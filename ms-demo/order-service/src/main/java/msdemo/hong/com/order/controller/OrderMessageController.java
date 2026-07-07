package msdemo.hong.com.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.common.model.result.Result;
import msdemo.hong.com.order.message.OrderEventProducer;
import msdemo.hong.com.order.message.OrderStatusChangeMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 订单消息测试控制器
 *
 * <p>提供 REST 接口触发订单状态变更消息的发送，用于演示
 * Kafka 消息的发送和消费流程。该控制器主要用于开发和测试阶段，
 * 方便开发人员快速验证消息队列的功能是否正常。</p>
 *
 * <h3>提供的接口</h3>
 * <ul>
 *   <li>{@code POST /order/message/send} - 发送默认的订单状态变更消息</li>
 *   <li>{@code POST /order/message/send/custom} - 发送自定义参数的订单状态变更消息</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "订单消息", description = "Kafka 消息发送测试接口，用于演示订单状态变更消息的发送和消费")
@RestController
@RequestMapping("/order/message")
@RequiredArgsConstructor
public class OrderMessageController {

    /**
     * 订单事件生产者
     *
     * <p>通过构造器注入（{@link RequiredArgsConstructor}），
     * Spring 会自动注入 {@link OrderEventProducer} 的实例。</p>
     */
    private final OrderEventProducer orderEventProducer;

    /**
     * 发送订单状态变更消息（测试接口）
     *
     * <p>模拟一个订单从「已创建（CREATED）」变更为「已支付（PAID）」的场景，
     * 将状态变更事件发送到 Kafka 消息队列。</p>
     *
     * <h4>接口说明</h4>
     * <ul>
     *   <li><b>请求方式</b>：POST</li>
     *   <li><b>使用场景</b>：开发测试阶段，验证 Kafka 消息发送和消费链路是否正常</li>
     *   <li><b>默认值</b>：orderId 默认为 {@code ORD-001}，userId 默认为 {@code USER-001}</li>
     * </ul>
     *
     * @param orderId 订单ID（可选，默认 {@code ORD-001}）
     * @param userId  用户ID（可选，默认 {@code USER-001}）
     * @return 发送结果，包含订单ID和提示信息
     */
    @Operation(summary = "发送订单状态变更消息", description = "模拟订单从「已创建(CREATED)」变更为「已支付(PAID)」的场景，将消息发送到Kafka")
    @PostMapping("/send")
    public Result<String> sendOrderStatusChangeMessage(
            @Parameter(description = "订单ID", example = "ORD-001")
            @RequestParam(value = "orderId", required = false, defaultValue = "ORD-001") String orderId,
            @Parameter(description = "用户ID", example = "USER-001")
            @RequestParam(value = "userId", required = false, defaultValue = "USER-001") String userId) {

        log.info("请求发送订单状态变更消息: orderId={}, userId={}", orderId, userId);

        // 构造订单状态变更消息
        // 使用 Builder 模式构建对象，代码更加简洁
        OrderStatusChangeMessage message = OrderStatusChangeMessage.builder()
                .orderId(orderId)
                .orderNo("ORDER-" + orderId)
                .userId(userId)
                .oldStatus("CREATED")    // 原状态：已创建
                .newStatus("PAID")       // 新状态：已支付
                .message("订单已支付成功，等待发货")
                .timestamp(LocalDateTime.now())
                .build();

        // 发送消息到 Kafka，异步操作，不阻塞当前线程
        orderEventProducer.sendOrderStatusChangeMessage(message);

        log.info("订单状态变更消息已发送: orderId={}", orderId);
        return Result.success("订单状态变更消息发送成功，请查看消费者日志", orderId);
    }

    /**
     * 发送自定义订单状态变更消息
     *
     * <p>允许客户端自定义订单ID、新旧状态等信息，用于灵活测试
     * 不同状态变更场景（如：已支付 → 已发货、已发货 → 已完成等）。</p>
     *
     * <h4>接口说明</h4>
     * <ul>
     *   <li><b>请求方式</b>：POST</li>
     *   <li><b>使用场景</b>：测试不同的订单状态流转路径</li>
     *   <li><b>参数说明</b>：所有参数均为必填或带有合理的默认值</li>
     * </ul>
     *
     * <h4>示例</h4>
     * <pre>{@code
     * POST /order/message/send/custom?orderId=ORD-002&oldStatus=PAID&newStatus=SHIPPED&desc=订单已发货
     * }</pre>
     *
     * @param orderId    订单ID（必填）
     * @param orderNo    订单编号（可选，为空时自动生成）
     * @param userId     用户ID（可选，默认 {@code USER-001}）
     * @param oldStatus  原状态（可选，默认 {@code CREATED}）
     * @param newStatus  新状态（可选，默认 {@code PAID}）
     * @param desc       变更描述（可选，默认 "订单状态已变更"）
     * @return 发送结果，包含订单ID和提示信息
     */
    @Operation(summary = "发送自定义订单状态变更消息", description = "允许自定义订单ID、新旧状态等参数，灵活测试不同状态变更场景")
    @PostMapping("/send/custom")
    public Result<String> sendCustomMessage(
            @Parameter(description = "订单ID", required = true) @RequestParam("orderId") String orderId,
            @Parameter(description = "订单编号") @RequestParam(value = "orderNo", required = false, defaultValue = "") String orderNo,
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false, defaultValue = "USER-001") String userId,
            @Parameter(description = "原状态", example = "CREATED") @RequestParam(value = "oldStatus", required = false, defaultValue = "CREATED") String oldStatus,
            @Parameter(description = "新状态", example = "PAID") @RequestParam(value = "newStatus", required = false, defaultValue = "PAID") String newStatus,
            @Parameter(description = "变更描述", example = "订单已支付成功") @RequestParam(value = "desc", required = false, defaultValue = "订单状态已变更") String desc) {

        log.info("请求发送自定义订单状态变更消息: orderId={}, {}→{}", orderId, oldStatus, newStatus);

        // 构造自定义订单状态变更消息
        // 如果订单编号为空，则自动生成：ORDER- + 订单ID
        OrderStatusChangeMessage message = OrderStatusChangeMessage.builder()
                .orderId(orderId)
                .orderNo(orderNo.isEmpty() ? "ORDER-" + orderId : orderNo)
                .userId(userId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .message(desc)
                .timestamp(LocalDateTime.now())
                .build();

        // 发送消息到 Kafka
        orderEventProducer.sendOrderStatusChangeMessage(message);

        log.info("自定义订单状态变更消息已发送: orderId={}", orderId);
        return Result.success("自定义订单状态变更消息发送成功，请查看消费者日志", orderId);
    }
}