-- =====================================================================
-- 系统管理模块 - 建库建表脚本
-- 数据库名: ms_ds_sys_config
-- 说明: 用于管理系统相关设置,当前仅搭骨架,不含具体业务功能
-- 环境: MySQL 8.x (192.168.0.27 Ubuntu)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `ms_ds_sys_config`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `ms_ds_sys_config`;

-- ---------------------------------------------------------------------
-- 配置分组表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_config_group` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_code`  VARCHAR(64)  NOT NULL                COMMENT '分组编码(唯一)',
  `group_name`  VARCHAR(128) NOT NULL                COMMENT '分组名称',
  `sort`        INT          NOT NULL DEFAULT 0      COMMENT '排序,越小越靠前',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `remark`      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
  `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '软删除标记 0正常 1删除',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_code` (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置分组表';

-- ---------------------------------------------------------------------
-- 系统配置主表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id`     BIGINT UNSIGNED NOT NULL                COMMENT '所属分组ID(system_config_group.id)',
  `config_key`   VARCHAR(128) NOT NULL                   COMMENT '配置键(唯一)',
  `config_name`  VARCHAR(128) NOT NULL                   COMMENT '配置名称',
  `config_value` TEXT                                    COMMENT '配置值',
  `value_type`   VARCHAR(32)  NOT NULL DEFAULT 'string'  COMMENT '值类型 string/int/bool/json',
  `status`       TINYINT      NOT NULL DEFAULT 1         COMMENT '状态 1启用 0停用',
  `remark`       VARCHAR(255) DEFAULT NULL               COMMENT '备注',
  `deleted`      TINYINT      NOT NULL DEFAULT 0         COMMENT '软删除标记 0正常 1删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置主表';

-- ---------------------------------------------------------------------
-- 配置变更日志表(审计)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_config_log` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_id`   BIGINT UNSIGNED NOT NULL                COMMENT '配置ID(sys_config.id)',
  `old_value`   TEXT                                    COMMENT '修改前值',
  `new_value`   TEXT                                    COMMENT '修改后值',
  `operator`    VARCHAR(64)  DEFAULT NULL               COMMENT '操作人',
  `op_type`     VARCHAR(16)  NOT NULL                   COMMENT '操作类型 add/update/delete',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置变更日志表';

-- ---------------------------------------------------------------------
-- 初始化示例: 默认分组
-- ---------------------------------------------------------------------
INSERT INTO `sys_config_group` (`group_code`, `group_name`, `sort`) VALUES
  ('basic', '基础设置', 1),
  ('system', '系统设置', 2)
ON DUPLICATE KEY UPDATE `group_name` = VALUES(`group_name`);

-- =====================================================================
-- 数据字典模块（供 service-goods 等业务模块通过 Feign 调用）
--   sys_dict      字典类型表
--   sys_dict_item 字典项表（支持 parent_id 三级层级：level 1/2/3）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 字典类型表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_dict` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type`   VARCHAR(64)  NOT NULL                COMMENT '字典类型编码(唯一), 如 GOODS_CATEGORY',
  `dict_name`   VARCHAR(128) NOT NULL                COMMENT '字典名称, 如 商品分类',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `remark`      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
  `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '软删除标记 0正常 1删除',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典类型表';

-- ---------------------------------------------------------------------
-- 字典项表（支持三级层级，level 1/2/3；parent_id=0 表示一级）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_item` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_id`     BIGINT UNSIGNED NOT NULL                COMMENT '所属字典类型ID(sys_dict.id)',
  `item_value`  VARCHAR(128) NOT NULL                   COMMENT '字典项值(业务键)',
  `item_label`  VARCHAR(255) NOT NULL                   COMMENT '字典项显示名称',
  `parent_id`   BIGINT UNSIGNED NOT NULL DEFAULT 0      COMMENT '父级ID, 0=一级',
  `level`       TINYINT      NOT NULL DEFAULT 1         COMMENT '层级 1/2/3',
  `sort`        INT          NOT NULL DEFAULT 0         COMMENT '排序,越小越靠前',
  `status`      TINYINT      NOT NULL DEFAULT 1         COMMENT '状态 1启用 0停用',
  `deleted`     TINYINT      NOT NULL DEFAULT 0         COMMENT '软删除标记 0正常 1删除',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dict_id` (`dict_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典项表(支持三级层级)';

-- ---------------------------------------------------------------------
-- 初始化字典：商品分类 GOODS_CATEGORY（三级示例）
-- ---------------------------------------------------------------------
INSERT INTO `sys_dict` (`dict_type`, `dict_name`, `remark`) VALUES
  ('GOODS_CATEGORY', '商品分类', 'service-goods 商品三级分类字典')
ON DUPLICATE KEY UPDATE `dict_name` = VALUES(`dict_name`);

-- 一级分类
INSERT INTO `sys_dict_item` (`dict_id`, `item_value`, `item_label`, `parent_id`, `level`, `sort`)
SELECT id, 'ELECTRONICS', '电子产品', 0, 1, 1 FROM `sys_dict` WHERE `dict_type` = 'GOODS_CATEGORY'
ON DUPLICATE KEY UPDATE `item_label` = VALUES(`item_label`);

INSERT INTO `sys_dict_item` (`dict_id`, `item_value`, `item_label`, `parent_id`, `level`, `sort`)
SELECT id, 'CLOTHING', '服装鞋帽', 0, 1, 2 FROM `sys_dict` WHERE `dict_type` = 'GOODS_CATEGORY'
ON DUPLICATE KEY UPDATE `item_label` = VALUES(`item_label`);

-- 二级分类（电子产品下）
INSERT INTO `sys_dict_item` (`dict_id`, `item_value`, `item_label`, `parent_id`, `level`, `sort`)
SELECT d.id, 'PHONE', '手机', i.id, 2, 1
FROM `sys_dict` d JOIN `sys_dict_item` i ON i.dict_id = d.id AND i.item_value = 'ELECTRONICS'
WHERE d.dict_type = 'GOODS_CATEGORY'
ON DUPLICATE KEY UPDATE `item_label` = VALUES(`item_label`);

INSERT INTO `sys_dict_item` (`dict_id`, `item_value`, `item_label`, `parent_id`, `level`, `sort`)
SELECT d.id, 'COMPUTER', '电脑', i.id, 2, 2
FROM `sys_dict` d JOIN `sys_dict_item` i ON i.dict_id = d.id AND i.item_value = 'ELECTRONICS'
WHERE d.dict_type = 'GOODS_CATEGORY'
ON DUPLICATE KEY UPDATE `item_label` = VALUES(`item_label`);

-- 三级分类（手机下）
INSERT INTO `sys_dict_item` (`dict_id`, `item_value`, `item_label`, `parent_id`, `level`, `sort`)
SELECT d.id, 'SMARTPHONE', '智能手机', i.id, 3, 1
FROM `sys_dict` d JOIN `sys_dict_item` i ON i.dict_id = d.id AND i.item_value = 'PHONE'
WHERE d.dict_type = 'GOODS_CATEGORY'
ON DUPLICATE KEY UPDATE `item_label` = VALUES(`item_label`);

INSERT INTO `sys_dict_item` (`dict_id`, `item_value`, `item_label`, `parent_id`, `level`, `sort`)
SELECT d.id, 'FEATUREPHONE', '功能机', i.id, 3, 2
FROM `sys_dict` d JOIN `sys_dict_item` i ON i.dict_id = d.id AND i.item_value = 'PHONE'
WHERE d.dict_type = 'GOODS_CATEGORY'
ON DUPLICATE KEY UPDATE `item_label` = VALUES(`item_label`);
