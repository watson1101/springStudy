-- ============================================================
-- Simple Memo 数据表初始化脚本
-- 按 DDD 子模块划分表前缀：
--   user_  - 用户服务模块
--   memo_ - 备忘服务模块
-- ============================================================

USE `simple-memo`;

-- ============================================================
-- 用户服务模块 - 用户信息表
-- 表前缀：user_
-- ============================================================
DROP TABLE IF EXISTS `user_service_user_info`;
CREATE TABLE `user_service_user_info` (
    `id`                  BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `username`            VARCHAR(50)   NOT NULL                 COMMENT '用户名（唯一）',
    `password`            VARCHAR(255)  NOT NULL                 COMMENT '密码（BCrypt加密）',
    `nickname`            VARCHAR(50)   DEFAULT NULL             COMMENT '昵称',
    `email`               VARCHAR(100)  DEFAULT NULL             COMMENT '邮箱',
    `phone`               VARCHAR(20)   DEFAULT NULL             COMMENT '手机号',
    `avatar`              VARCHAR(500)  DEFAULT NULL             COMMENT '头像URL',
    `role`                VARCHAR(20)   NOT NULL DEFAULT 'user'  COMMENT '角色：admin-管理员 user-普通用户',
    `status`              TINYINT       NOT NULL DEFAULT 1       COMMENT '状态：1-启用 0-禁用',
    `multi_device_login`  TINYINT       NOT NULL DEFAULT 1       COMMENT '多端登录：1-允许 0-不允许',
    `deleted`             TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_by`          VARCHAR(50)   NOT NULL DEFAULT 'admin' COMMENT '创建人',
    `created_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`          VARCHAR(50)   NOT NULL DEFAULT 'admin' COMMENT '修改人',
    `updated_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ============================================================
-- 备忘服务模块 - 备忘信息表
-- 表前缀：memo_
-- 支持三种模式：
--   1-简单备忘录（仅标题+内容）
--   2-单次定时提醒（标题+内容+提醒时间）
--   3-循环定时提醒（标题+内容+Cron表达式）
-- ============================================================
DROP TABLE IF EXISTS `memo_service_memo`;
CREATE TABLE `memo_service_memo` (
    `id`                BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `user_id`           BIGINT        NOT NULL                 COMMENT '所属用户ID',
    `title`             VARCHAR(200)  DEFAULT NULL             COMMENT '备忘标题',
    `content`           TEXT          DEFAULT NULL             COMMENT '备忘内容',
    `memo_type`         TINYINT       NOT NULL DEFAULT 1       COMMENT '备忘类型：1-简单备忘 2-单次定时 3-循环定时',
    `background_image`  VARCHAR(500)  DEFAULT NULL             COMMENT '背景图片路径',
    `remind_time`       DATETIME      DEFAULT NULL             COMMENT '提醒时间（单次定时用）',
    `cron_expression`   VARCHAR(100)  DEFAULT NULL             COMMENT 'Cron表达式（循环定时用）',
    `status`            TINYINT       NOT NULL DEFAULT 1       COMMENT '状态：1-待办 2-已完成 3-已取消',
    `sort_order`        INT           DEFAULT 0                COMMENT '排序序号',
    `deleted`           TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-未删除 1-已删除',
    `created_by`        VARCHAR(50)   NOT NULL DEFAULT 'admin' COMMENT '创建人',
    `created_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`        VARCHAR(50)   NOT NULL DEFAULT 'admin' COMMENT '修改人',
    `updated_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_memo_type` (`memo_type`),
    KEY `idx_status` (`status`),
    KEY `idx_remind_time` (`remind_time`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_memo_user` FOREIGN KEY (`user_id`) REFERENCES `user_service_user_info` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备忘信息表';

-- ============================================================
-- 初始化数据
-- 1. 超级管理员：admin / admin（角色 admin）
-- 2. 普通用户：hong / 123456（角色 user）
-- 密码使用 BCrypt 加密后的值
-- 初始用户数据由 DatabaseInitializer 在项目启动时自动插入
