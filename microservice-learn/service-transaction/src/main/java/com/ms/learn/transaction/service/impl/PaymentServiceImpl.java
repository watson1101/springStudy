package com.ms.learn.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.common.result.Result;
import com.ms.learn.transaction.dto.PaymentRequest;
import com.ms.learn.transaction.entity.Payment;
import com.ms.learn.transaction.feign.PointsFeignClient;
import com.ms.learn.transaction.mapper.PaymentMapper;
import com.ms.learn.transaction.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PointsFeignClient pointsFeignClient;

    @Override
    public Payment pay(PaymentRequest request) {
        // 1) 幂等：若 reqNo 已有记录，直接返回（不重复处理）
        if (request.getReqNo() != null && !request.getReqNo().isBlank()) {
            Payment exist = paymentMapper.selectOne(
                    new LambdaQueryWrapper<Payment>().eq(Payment::getReqNo, request.getReqNo()));
            if (exist != null) {
                log.info("[pay] 幂等命中，reqNo={}，直接返回 payNo={}", request.getReqNo(), exist.getPayNo());
                return exist;
            }
        }

        // 2) 落库：PENDING
        Payment payment = new Payment();
        payment.setPayNo("PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase());
        payment.setUserId(request.getUserId());
        payment.setOrderNo(request.getOrderNo());
        payment.setAmount(request.getAmount() == null ? BigDecimal.ZERO : request.getAmount());
        payment.setChannel(request.getChannel() == null ? "MOCK" : request.getChannel());
        payment.setStatus("PENDING");
        payment.setReqNo(request.getReqNo());
        payment.setPointsGranted(0);
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.insert(payment);
        log.info("[pay] 已创建交易 PENDING，payNo={}, userId={}, amount={}",
                payment.getPayNo(), payment.getUserId(), payment.getAmount());

        // 3) 模拟支付网关处理：2s 延迟
        try {
            log.info("[pay] 模拟支付网关处理中... sleep 2s，payNo={}", payment.getPayNo());
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[pay] 模拟2s延迟被中断，payNo={}", payment.getPayNo());
        }

        // 4) 交易成功（Mock：全部走成功；如需失败率模拟，可后续扩展）
        payment.setStatus("SUCCESS");
        payment.setPayTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.updateById(payment);
        log.info("[pay] 交易 SUCCESS，payNo={}，准备发放积分", payment.getPayNo());

        // 5) 调用积分模块：每消费 1 分钱 = 获得 1 积分
        //    amountYuan -> amountCent（四舍五入到分，避免浮点误差）
        long amountCent = payment.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        try {
            Map<String, Object> earnReq = new HashMap<>();
            earnReq.put("userId", payment.getUserId());
            earnReq.put("refType", "PAYMENT");
            earnReq.put("refNo", payment.getPayNo());
            earnReq.put("amountCent", amountCent);
            earnReq.put("amountYuan", payment.getAmount());
            earnReq.put("remark", "交易模块支付成功发放积分：1分=1积分");

            Result<Map<String, Object>> earn = pointsFeignClient.earnByConsume(earnReq);
            log.info("[pay] 调用积分模块返回：payNo={}, earnResp={}", payment.getPayNo(), earn);

            if (earn != null && Integer.valueOf(200).equals(earn.getCode())) {
                payment.setPointsGranted(1);
                payment.setUpdateTime(LocalDateTime.now());
                paymentMapper.updateById(payment);
            } else {
                log.warn("[pay] 积分发放返回非成功，payNo={}，pointsGranted=0，可由补偿任务重试", payment.getPayNo());
            }
        } catch (Exception e) {
            // 积分发放异常不应回滚交易本身，交易已完成；记录日志，后续可通过补偿任务重试
            log.error("[pay] Feign 调用积分模块异常，payNo={}，message={}",
                    payment.getPayNo(), e.getMessage(), e);
        }

        return payment;
    }

    @Override
    public List<Payment> listByUser(Long userId) {
        return paymentMapper.selectList(
                new LambdaQueryWrapper<Payment>()
                        .eq(userId != null, Payment::getUserId, userId)
                        .orderByDesc(Payment::getId));
    }

    @Override
    public Payment getByPayNo(String payNo) {
        return paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getPayNo, payNo));
    }
}
