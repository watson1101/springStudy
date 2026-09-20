package com.ms.learn.hotnews.consumer.listener;

import com.ms.learn.hotnews.consumer.service.HotNewsConsumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 热榜消息消费者
 *
 * <p>监听 topic=hotnews-topic，消费采集端投递的热榜批量消息并入库。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${hotnews.mq.topic:hotnews-topic}",
        consumerGroup = "${hotnews.mq.consumer-group:hotnews-consumer-group}",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING
)
public class HotNewsListener implements RocketMQListener<String> {

    private final HotNewsConsumeService consumeService;

    @Override
    public void onMessage(String message) {
        log.debug("收到热榜消息，长度={}", message == null ? 0 : message.length());
        consumeService.handleMessage(message);
    }
}
