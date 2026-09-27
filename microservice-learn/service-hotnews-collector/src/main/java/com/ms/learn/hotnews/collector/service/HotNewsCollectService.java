package com.ms.learn.hotnews.collector.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ms.learn.hotnews.collector.client.ToutiaoClient;
import com.ms.learn.hotnews.collector.config.CollectProperties;
import com.ms.learn.hotnews.collector.dto.HotNewsMessage;
import com.ms.learn.hotnews.collector.dto.ToutiaoHotItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 热榜采集核心服务：抓取 → 组装消息 → 投递 RocketMQ
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotNewsCollectService {

    private final ToutiaoClient toutiaoClient;
    private final CollectProperties props;
    private final RocketMQTemplate rocketMQTemplate;

    @Value("${hotnews.mq.topic:hotnews-topic}")
    private String topic;

    private static final DateTimeFormatter BATCH_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 执行一次采集并投递
     *
     * @return 本次采集条数
     */
    public int collectAndSend() {
        long start = System.currentTimeMillis();
        String batchId = "TT" + LocalDateTime.now().format(BATCH_FMT);
        try {
            List<ToutiaoHotItem> items = toutiaoClient.fetchHotBoard();
            List<HotNewsMessage> messages = new ArrayList<>();
            int rank = 1;
            for (ToutiaoHotItem item : items) {
                if (StrUtil.isBlank(item.getTitle())) {
                    continue;
                }
                HotNewsMessage m = new HotNewsMessage();
                m.setClusterId(item.getClusterId());
                m.setTitle(item.getTitle());
                m.setHotValue(parseHotValue(item.getHotValue()));
                m.setRankNo(rank++);
                m.setSource("toutiao");
                m.setUrl(cleanUrl(item.getUrl()));
                m.setBatchId(batchId);
                m.setCollectTime(LocalDateTime.now());
                messages.add(m);
            }

            if (messages.isEmpty()) {
                log.warn("本次未抓取到任何热榜数据");
                return 0;
            }

            // 每条热点作为独立的一条消息发送（消费端逐条处理）
            // 采用异步发送：50 条并发投递，避免同步阻塞导致的超时
            final java.util.concurrent.atomic.AtomicInteger okCount = new java.util.concurrent.atomic.AtomicInteger(0);
            final java.util.concurrent.atomic.AtomicInteger failCount = new java.util.concurrent.atomic.AtomicInteger(0);
            final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(messages.size());

            for (HotNewsMessage m : messages) {
                String payload = JSON.toJSONString(m);
                rocketMQTemplate.asyncSend(topic, payload, new org.apache.rocketmq.client.producer.SendCallback() {
                    @Override
                    public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                        okCount.incrementAndGet();
                        latch.countDown();
                    }

                    @Override
                    public void onException(Throwable e) {
                        failCount.incrementAndGet();
                        log.error("异步发送失败 clusterId={}", m.getClusterId(), e);
                        latch.countDown();
                    }
                });
            }

            // 最多等待 20 秒，等待全部回调完成
            boolean done = latch.await(20, java.util.concurrent.TimeUnit.SECONDS);
            if (!done) {
                log.warn("部分消息发送未在超时时间内完成回调");
            }
            int sent = okCount.get();

            long cost = System.currentTimeMillis() - start;
            log.info("采集完成 batchId={}, 成功={}, 失败={}, 耗时={}ms，已逐条异步投递 topic={}",
                    batchId, sent, failCount.get(), cost, topic);
            return sent;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("采集投递失败 batchId={}, 耗时={}ms", batchId, cost, e);
            throw new RuntimeException("采集投递失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清理热榜 URL，剔除跟踪参数。
     *
     * <p>头条热榜原始 URL 携带大量埋点参数（category_name / log_pb / impr_id /
     * style_id 等），单条约 800+ 字符，入库后既占空间又影响展示。
     * 实际有效的只有 {@code scheme://host/path} 部分，查询串与锚点均可丢弃。</p>
     *
     * @param url 原始 URL
     * @return 精简后的 URL；入参为空时返回空串
     */
    static String cleanUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return "";
        }
        String trimmed = url.trim();
        int idx = trimmed.indexOf('?');
        if (idx >= 0) {
            trimmed = trimmed.substring(0, idx);
        }
        idx = trimmed.indexOf('#');
        if (idx >= 0) {
            trimmed = trimmed.substring(0, idx);
        }
        return trimmed;
    }

    private Long parseHotValue(String hotValue) {
        if (StrUtil.isBlank(hotValue)) {
            return 0L;
        }
        try {
            return Long.parseLong(hotValue.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
