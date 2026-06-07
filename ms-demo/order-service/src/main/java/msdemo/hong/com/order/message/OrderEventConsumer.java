package msdemo.hong.com.order.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 订单事件消费者
 *
 * <p>监听 Kafka 中 {@code order-events} 主题的消息，接收订单状态变更事件
 * 并进行异步处理。</p>
 *
 * <h3>设计说明</h3>
 * <ul>
 *   <li><b>异步处理</b> - 消费者独立于生产者运行，订单创建后无需等待消费完成即可返回</li>
 *   <li><b>自动确认</b> - 默认使用自动提交偏移量（auto-commit），消息处理完成后自动提交</li>
 *   <li><b>可扩展</b> - 当前仅为日志记录，后续可扩展为发送通知、审计日志、更新缓存等</li>
 * </ul>
 *
 * <p>使用 {@link KafkaListener} 注解标记监听方法，Spring 容器启动时会自动创建
 * 对应的消息监听容器（MessageListenerContainer），持续监听指定主题。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
@Component
public class OrderEventConsumer {

    /**
     * 消费订单状态变更消息
     *
     * <p>当有新的订单状态变更消息到达 {@code order-events} 主题时，
     * 该方法会被自动调用并处理消息。</p>
     *
     * <h4>处理逻辑</h4>
     * <ol>
     *   <li>从 Kafka 消息中提取完整的订单状态变更信息</li>
     *   <li>打印结构化的日志（包含 topic、partition、offset 等元数据）</li>
     *   <li>当前仅演示消息消费，不进行数据库写入操作</li>
     * </ol>
     *
     * <h4>可扩展场景</h4>
     * <ul>
     *   <li><b>发送通知</b> - 根据订单状态发送短信/邮件/站内信通知用户</li>
     *   <li><b>记录审计日志</b> - 将订单操作记录保存到审计表中，满足合规要求</li>
     *   <li><b>更新缓存</b> - 订单状态变更后及时更新 Redis 缓存</li>
     *   <li><b>触发下游流程</b> - 支付成功后触发发货流程，完成后触发评价流程</li>
     * </ul>
     *
     * @param message   订单状态变更消息（JSON 反序列化后的 {@link OrderStatusChangeMessage} 对象）
     * @param key       消息 key（通常是订单ID，用于分区路由）
     * @param topic     消息来源主题
     * @param partition 消息所在分区号（0 ~ 分区数-1）
     * @param offset    消息在分区中的偏移量（唯一确定一条消息的位置）
     */
    @KafkaListener(
            topics = OrderEventProducer.TOPIC_ORDER_EVENTS,
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderStatusChangeMessage(
            @Payload OrderStatusChangeMessage message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        // ========== 打印消息元数据 ==========
        // 打印分隔线，使日志更清晰易读
        log.info("═══════════════════════════════════════════════");
        log.info("  接收到订单状态变更消息");
        log.info("  Topic:      {}", topic);          // 消息主题名称
        log.info("  Partition:  {}", partition);       // 消息所在分区
        log.info("  Offset:     {}", offset);          // 消息偏移量（用于精确追踪）
        log.info("  Key:        {}", key);             // 消息key（订单ID）

        // ========== 打印业务数据 ==========
        log.info("  OrderId:    {}", message.getOrderId());      // 订单唯一标识
        log.info("  OrderNo:    {}", message.getOrderNo());      // 订单编号（用户可读）
        log.info("  UserId:     {}", message.getUserId());       // 用户标识
        log.info("  状态变更:    {} → {}", message.getOldStatus(), message.getNewStatus());  // 状态变更路径
        log.info("  描述:       {}", message.getMessage());      // 变更描述
        log.info("  变更时间:   {}", message.getTimestamp());     // 变更发生时间
        log.info("═══════════════════════════════════════════════");

        // 演示说明：此处仅为演示消息消费，不进行数据库写入操作
        // 实际业务中可以在下面扩展具体的业务逻辑
    }
}