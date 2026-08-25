package com.ms.learn.order.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.order.entity.Order;
import com.ms.learn.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单服务 REST 接口
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    public Result<List<Order>> list() {
        return Result.success(orderService.listAll());
    }

    @PostMapping("/{userId}")
    public Result<Order> create(@PathVariable Long userId, @RequestBody Order order) {
        return Result.success(orderService.create(order, userId));
    }
}
