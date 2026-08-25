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
