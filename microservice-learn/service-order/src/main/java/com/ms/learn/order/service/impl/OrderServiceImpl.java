package com.ms.learn.order.service.impl;

import com.ms.learn.common.exception.BizException;
import com.ms.learn.common.result.Result;
import com.ms.learn.order.entity.Order;
import com.ms.learn.order.feign.UserFeignClient;
import com.ms.learn.order.mapper.OrderMapper;
import com.ms.learn.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务：演示 OpenFeign 调用用户服务校验用户是否存在
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final UserFeignClient userFeignClient;

    @Override
    public List<Order> listAll() {
        return orderMapper.selectList(null);
    }

    @Override
    public Order create(Order order, Long userId) {
        Result<Boolean> result = userFeignClient.userExists(userId);
        if (!Integer.valueOf(200).equals(result.getCode()) || !Boolean.TRUE.equals(result.getData())) {
            throw new BizException(404, "用户不存在: " + userId);
        }

        order.setUserId(userId);
        order.setOrderNo("ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        order.setCreateTime(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus("NEW");
        }
        orderMapper.insert(order);
        return order;
    }
}
