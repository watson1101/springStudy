package com.ms.learn.hotnews.consumer.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.hotnews.consumer.dto.HotNewsMessage;
import com.ms.learn.hotnews.consumer.entity.HotCollectLog;
import com.ms.learn.hotnews.consumer.entity.HotNews;
import com.ms.learn.hotnews.consumer.mapper.HotCollectLogMapper;
import com.ms.learn.hotnews.consumer.mapper.HotNewsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 热榜消费入库服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotNewsConsumeService {

    private final HotNewsMapper hotNewsMapper;
    private final HotCollectLogMapper hotCollectLogMapper;

    /**
     * 消费单条消息：解析单条 JSON → 幂等入库
     *
     * <p>采集端已改为「每条热点单独发送一条消息」，故此处按单条处理。</p>
     *
     * @param payload 采集端投递的单条消息（JSON 对象）
     * @return 入库条数（1=新增，0=已存在跳过）
     */
    @Transactional(rollbackFor = Exception.class)
    public int handleMessage(String payload) {
        long start = System.currentTimeMillis();
        String batchId = null;
        String clusterId = null;
        try {
            HotNewsMessage m = JSON.parseObject(payload, HotNewsMessage.class);
            if (m == null) {
                log.warn("收到空消息，忽略");
                return 0;
            }
            batchId = m.getBatchId();
            clusterId = m.getClusterId();

            // 幂等：同一批次内相同 clusterId 已入库则跳过（防止 MQ 重复投递）
            LambdaQueryWrapper<HotNews> dup = new LambdaQueryWrapper<HotNews>()
                    .eq(HotNews::getClusterId, clusterId)
                    .eq(batchId != null, HotNews::getBatchId, batchId);
            Long exists = hotNewsMapper.selectCount(dup);
            if (exists != null && exists > 0) {
                log.debug("消息已入库，跳过重复消费 clusterId={}, batchId={}", clusterId, batchId);
                return 0;
            }

            HotNews e = new HotNews();
            e.setClusterId(clusterId);
            e.setTitle(m.getTitle());
            e.setHotValue(m.getHotValue() == null ? 0L : m.getHotValue());
            e.setRankNo(m.getRankNo() == null ? 0 : m.getRankNo());
            e.setSource(m.getSource() == null ? "toutiao" : m.getSource());
            e.setUrl(m.getUrl());
            e.setBatchId(batchId);
            e.setCollectTime(m.getCollectTime() == null ? LocalDateTime.now() : m.getCollectTime());

            int inserted = hotNewsMapper.insert(e);
            log.debug("单条入库成功 clusterId={}, title={}, batchId={}", clusterId, m.getTitle(), batchId);
            return inserted;
        } catch (Exception e) {
            log.error("消费入库失败 clusterId={}, batchId={}", clusterId, batchId, e);
            throw e;
        } finally {
            // 记录消费耗时（仅统计有效入库的批次日志，失败也记录）
            if (clusterId != null) {
                saveLog(batchId, System.currentTimeMillis() - start);
            }
        }
    }

    /**
     * 记录单条消费日志
     * <p>注意：单条消息模式下每条都会产生一条日志，此处仅在 DEBUG 场景使用；
     * 为避免日志表膨胀，默认不逐条写库，可改用批量汇总（见 saveSummaryLog）。</p>
     */
    private void saveLog(String batchId, long costMs) {
        // 单条消息模式下不逐条写日志表，避免日志表暴涨；
        // 采集/消费汇总统计可由 hot_news 表按 batch_id 聚合得到。
        if (log.isDebugEnabled()) {
            log.debug("消费单条 batchId={}, cost={}ms", batchId, costMs);
        }
    }

    /** 查询最新一批热榜 */
    public List<HotNews> latest() {
        LambdaQueryWrapper<HotNews> w = new LambdaQueryWrapper<HotNews>()
                .orderByDesc(HotNews::getCreateTime)
                .last("LIMIT 50");
        return hotNewsMapper.selectList(w);
    }

    /** 按批次查询 */
    public List<HotNews> listByBatch(String batchId) {
        return hotNewsMapper.selectList(new LambdaQueryWrapper<HotNews>()
                .eq(HotNews::getBatchId, batchId)
                .orderByAsc(HotNews::getRankNo));
    }

    /** 按批次统计已入库条数 */
    public long countByBatch(String batchId) {
        Long c = hotNewsMapper.selectCount(new LambdaQueryWrapper<HotNews>()
                .eq(HotNews::getBatchId, batchId));
        return c == null ? 0L : c;
    }
}
