package msdemo.hong.com.order.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单事件生产者
 *
 * <p>负责将订单相关的业务事件（如状态变更）发送到 Kafka 消息队列。</p>
 *
 * <h3>设计说明</h3>
 * <ul>
 *   <li><b>异步解耦</b> - 订单状态变更后不需要同步等待其他服务处理，通过消息队列异步通知</li>
 *   <li><b>有序消费</b> - 使用订单ID作为消息key，保证同一订单的消息按顺序发送到同一分区</li>
 *   <li><b>可靠投递</b> - 通过 {@link KafkaTemplate#send(String, Object, Object)} 的异步回调
 *   确认消息是否成功送达 Broker</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    /**
     * 订单事件主题名称
     *
     * <p>Kafka 主题（Topic）是消息的逻辑分类。所有订单相关的事件消息
     * 都发送到此主题，消费者按需订阅。</p>
     */
    public static final String TOPIC_ORDER_EVENTS = "order-events";

    /**
     * Kafka 消息模板
     *
     * <p>Spring Kafka 提供的核心模板类，封装了消息发送的底层细节。
     * 泛型参数：
     * <ul>
     *   <li>{@code String} - 消息key的类型（使用订单ID作为key）</li>
     *   <li>{@code Object} - 消息value的类型（使用 {@link OrderStatusChangeMessage} 对象）</li>
     * </ul>
     * value 会被配置的 {@link org.springframework.kafka.support.serializer.JsonSerializer}
     * 自动序列化为 JSON 格式。</p>
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送订单状态变更消息
     *
     * <p>将订单状态变更事件发送到 Kafka 的 {@code order-events} 主题。
     * 使用订单ID作为消息key，保证同一订单的消息有序消费。</p>
     *
     * <h4>发送流程</h4>
     * <ol>
     *   <li>构建消息体（{@link OrderStatusChangeMessage}）</li>
     *   <li>调用 {@link KafkaTemplate#send(String, Object, Object)} 发送消息</li>
     *   <li>通过 {@code whenComplete} 异步回调确认发送结果</li>
     *   <li>发送成功时记录消息被分配到的分区（partition）和偏移量（offset）</li>
     *   <li>发送失败时记录错误信息</li>
     * </ol>
     *
     * @param message 订单状态变更消息（不能为 null）
     */
    public void sendOrderStatusChangeMessage(OrderStatusChangeMessage message) {
        // 记录发送日志，方便追踪和排错
        log.info("▶ 发送订单状态变更消息到Kafka: topic={}, orderId={}, oldStatus={}, newStatus={}",
                TOPIC_ORDER_EVENTS, message.getOrderId(), message.getOldStatus(), message.getNewStatus());

        // 使用 orderId 作为消息 key，保证同一订单的消息按顺序在同一个分区消费
        // Kafka 根据 key 的 hash 值决定消息发送到哪个分区
        kafkaTemplate.send(TOPIC_ORDER_EVENTS, message.getOrderId(), message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        // 发送成功：记录元数据（分区、偏移量），方便后续追踪和排查问题
                        log.info("✓ 订单状态变更消息发送成功: topic={}, partition={}, offset={}, orderId={}",
                                TOPIC_ORDER_EVENTS,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                message.getOrderId());
                    } else {
                        // 发送失败：记录错误信息，需要人工介入排查
                        log.error("✗ 订单状态变更消息发送失败: orderId={}, error={}",
                                message.getOrderId(), ex.getMessage(), ex);
                    }
                });
    }
}