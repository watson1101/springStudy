package msdemo.hong.com.order.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单状态变更消息实体
 *
 * <p>当订单状态发生变更时（例如：创建订单、支付成功、发货、完成、取消等），
 * 订单服务会通过 Kafka 将此消息发送到消息队列，用于：
 * </p>
 * <ul>
 *   <li>通知其他微服务（如商品服务更新销量统计）</li>
 *   <li>触发后续业务处理（如发送短信/邮件通知用户）</li>
 *   <li>记录审计日志</li>
 * </ul>
 *
 * <p>该类实现了 {@link Serializable} 接口，确保对象可以在网络中传输
 * 或经过序列化/反序列化操作。使用 {@link Builder} 模式构建对象，
 * 代码更简洁易读。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "订单状态变更消息实体，通过Kafka传输")
public class OrderStatusChangeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     *
     * <p>全局唯一标识，用于唯一确定一个订单。
     * 同时也是 Kafka 消息的 key，保证同一订单的消息按顺序在同一个分区消费。</p>
     */
        @Schema(description = "订单ID", example = "ORD-001")
    private String orderId;

    /**
     * 订单编号
     *
     * <p>用户可读的订单号，通常包含日期序列等信息，格式如 {@code ORDER-20240101-0001}。
     * 用于在前端展示和客服查询。</p>
     */
        @Schema(description = "订单编号", example = "ORDER-ORD-001")
    private String orderNo;

    /**
     * 用户ID
     *
     * <p>下单用户的唯一标识，用于关联用户服务和订单数据。</p>
     */
    private String userId;

    /**
     * 原状态
     *
     * <p>订单变更前的状态值，例如：{@code CREATED}（已创建）、{@code PAID}（已支付）。</p>
     */
    private String oldStatus;

    /**
     * 新状态
     *
     * <p>订单变更后的状态值，例如：{@code PAID}（已支付）、{@code SHIPPED}（已发货）。</p>
     */
        @Schema(description = "新状态", example = "PAID")
    private String newStatus;

    /**
     * 状态变更描述信息
     *
     * <p>对本次状态变更的简要说明，例如："订单已支付成功，等待发货"。</p>
     */
    private String message;

    /**
     * 状态变更时间
     *
     * <p>记录状态变更发生的具体时间，使用 {@link LocalDateTime} 类型，
     * 包含日期和时间信息，精度到纳秒。</p>
     */
    private LocalDateTime timestamp;
}