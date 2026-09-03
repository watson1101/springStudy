package com.ms.learn.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.points.dto.EarnByConsumeRequest;
import com.ms.learn.points.dto.EarnResponse;
import com.ms.learn.points.entity.PointsAccount;
import com.ms.learn.points.entity.PointsRecord;
import com.ms.learn.points.mapper.PointsAccountMapper;
import com.ms.learn.points.mapper.PointsRecordMapper;
import com.ms.learn.points.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final PointsAccountMapper accountMapper;
    private final PointsRecordMapper recordMapper;

    /** 兜底发放比例：规则表不可用时按该值发放，默认 1 = 每 1 分钱 = 1 积分 */
    @Value("${points.earn.fallback-rate:1}")
    private int fallbackRate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EarnResponse earnByConsume(EarnByConsumeRequest request) {
        // 1) 参数校验
        if (request.getUserId() == null) {
            throw new BizException("userId 必填");
        }
        long amountCent = resolveAmountCent(request);
        if (amountCent < 0) {
            throw new BizException("消费金额不能为负");
        }

        // 2) 幂等：相同 refType+refNo 只发放一次
        if (request.getRefType() != null && request.getRefNo() != null) {
            PointsRecord exist = recordMapper.selectOne(new LambdaQueryWrapper<PointsRecord>()
                    .eq(PointsRecord::getRefType, request.getRefType())
                    .eq(PointsRecord::getRefNo, request.getRefNo())
                    .eq(PointsRecord::getBizType, "EARN"));
            if (exist != null) {
                log.info("[earnByConsume] 幂等命中：refType={}, refNo={}，直接返回已发放记录",
                        request.getRefType(), request.getRefNo());
                PointsAccount acc = getOrCreateAccount(request.getUserId());
                return EarnResponse.builder()
                        .userId(exist.getUserId())
                        .recordNo(exist.getRecordNo())
                        .points(exist.getPoints())
                        .earnRate(getEarnRate())
                        .amountCent(exist.getAmountCent())
                        .balanceAfter(acc.getBalance())
                        .refNo(exist.getRefNo())
                        .build();
            }
        }

        // 3) 查询积分规则 earn_rate（默认 1，1 分钱 = 1 积分）
        int earnRate = getEarnRate();

        // 4) 计算发放积分 = amountCent * earn_rate；核心：1 分钱 = 1 积分
        long points = amountCent * earnRate;
        log.info("[earnByConsume] 计算积分：amountCent={} * earnRate={} => points={}, userId={}",
                amountCent, earnRate, points, request.getUserId());

        // 5) 获取/创建用户账户，并加余额（乐观锁更新，避免超发）
        PointsAccount account = getOrCreateAccount(request.getUserId());
        long newBalance = (account.getBalance() == null ? 0L : account.getBalance()) + points;
        long newTotalEarned = (account.getTotalEarned() == null ? 0L : account.getTotalEarned()) + points;
        account.setBalance(newBalance);
        account.setTotalEarned(newTotalEarned);
        account.setLastEarnTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        int updated = accountMapper.updateById(account);
        if (updated != 1) {
            throw new BizException("积分账户更新失败：乐观锁冲突，请稍后重试");
        }

        // 6) 记录发放流水
        PointsRecord record = new PointsRecord();
        record.setUserId(request.getUserId());
        record.setRecordNo("R" + UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase());
        record.setBizType("EARN");
        record.setChangeType("IN");
        record.setPoints(points);
        record.setBalanceAft(newBalance);
        record.setRefType(request.getRefType());
        record.setRefNo(request.getRefNo());
        record.setAmountCent(amountCent);
        record.setRemark(request.getRemark());
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);

        log.info("[earnByConsume] 发放成功：userId={}, recordNo={}, points={}, balanceAfter={}",
                request.getUserId(), record.getRecordNo(), points, newBalance);

        return EarnResponse.builder()
                .userId(request.getUserId())
                .recordNo(record.getRecordNo())
                .points(points)
                .earnRate(earnRate)
                .amountCent(amountCent)
                .balanceAfter(newBalance)
                .refNo(record.getRefNo())
                .build();
    }

    @Override
    public PointsAccount getAccount(Long userId) {
        if (userId == null) return null;
        return getOrCreateAccount(userId);
    }

    @Override
    public List<PointsRecord> listRecords(Long userId) {
        if (userId == null) return List.of();
        return recordMapper.selectList(new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreateTime));
    }

    // ================== private helpers ==================

    private long resolveAmountCent(EarnByConsumeRequest request) {
        if (request.getAmountCent() != null && request.getAmountCent() > 0) {
            return request.getAmountCent();
        }
        if (request.getAmountYuan() != null && request.getAmountYuan().signum() > 0) {
            return request.getAmountYuan()
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();
        }
        return 0L;
    }

    /**
     * 积分发放比例 earn_rate
     * 业务约定：1 分钱 = 1 积分，默认 earn_rate=1。
     * 后续可以从 t_points_rule 表读取 EARN_DEFAULT；这里为了让服务在没有初始化规则数据时也能工作，先使用配置兜底 + 常量 1。
     */
    private int getEarnRate() {
        // TODO 如需动态化，可查表：SELECT earn_rate FROM t_points_rule WHERE rule_code='EARN_DEFAULT' AND enabled=1
        return Math.max(1, fallbackRate);
    }

    private PointsAccount getOrCreateAccount(Long userId) {
        PointsAccount account = accountMapper.selectOne(
                new LambdaQueryWrapper<PointsAccount>().eq(PointsAccount::getUserId, userId));
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setTotalEarned(0L);
            account.setTotalSpent(0L);
            account.setTotalExpired(0L);
            account.setBalance(0L);
            account.setVersion(0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            accountMapper.insert(account);
        }
        return account;
    }
}
