CREATE TABLE `goods_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态：0下架，1上架',
  `deleted` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
