package com.ms.learn.order.service;

import com.ms.learn.order.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> listAll();

    Order create(Order order, Long userId);
}
