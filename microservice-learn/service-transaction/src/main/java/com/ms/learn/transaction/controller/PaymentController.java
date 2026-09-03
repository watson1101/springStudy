package com.ms.learn.transaction.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.transaction.dto.PaymentRequest;
import com.ms.learn.transaction.entity.Payment;
import com.ms.learn.transaction.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易服务 REST 接口
 * <p>POST /api/payment/pay  发起支付（模拟2s后交易成功，并发积分）</p>
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 发起支付请求
     * 示例请求体：
     * { "userId": 1, "orderNo": "ORDXXXX", "amount": 99.99, "channel": "MOCK", "reqNo": "REQXXXX" }
     *
     * 行为：
     * 1) 落库 PENDING
     * 2) sleep 2s（模拟支付网关处理）
     * 3) 状态改为 SUCCESS
     * 4) Feign 调 service-points 发放积分（1 分钱 = 1 积分）
     */
    @PostMapping("/pay")
    public Result<Payment> pay(@RequestBody PaymentRequest request) {
        if (request.getUserId() == null || request.getAmount() == null) {
            return Result.fail("userId 和 amount 必填");
        }
        if (request.getAmount().signum() <= 0) {
            return Result.fail("amount 必须大于 0");
        }
        return Result.success(paymentService.pay(request));
    }

    /** 查询某用户交易列表 */
    @GetMapping("/list/{userId}")
    public Result<List<Payment>> listByUser(@PathVariable Long userId) {
        return Result.success(paymentService.listByUser(userId));
    }

    /** 根据交易流水号查单 */
    @GetMapping("/{payNo}")
    public Result<Payment> getByPayNo(@PathVariable String payNo) {
        Payment p = paymentService.getByPayNo(payNo);
        return p == null ? Result.fail("交易不存在") : Result.success(p);
    }
}
