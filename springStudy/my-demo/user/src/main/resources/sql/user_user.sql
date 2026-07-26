CREATE TABLE `user_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `salt` varchar(64) NOT NULL COMMENT '盐值',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `is_admin` int(11) NOT NULL DEFAULT '0' COMMENT '是否管理员：0普通用户，1管理员',
  `deleted` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除，1已删除',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
