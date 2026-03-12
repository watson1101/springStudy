CREATE TABLE `order_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '订单状态：0待支付，1已支付，2已发货，3已完成，4已取消',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `shipping_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `deleted` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
