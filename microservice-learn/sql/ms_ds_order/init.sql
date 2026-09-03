-- ============================================================
-- 订单模块数据库 MySQL 初始化脚本：ms_ds_order
-- 说明：service-order 对应数据库；内容与 sql/init-mysql.sql 中的 ms_ds_order 段保持一致；
--   单独提供子目录便于按库单独执行，与其他库 sql/ms_ds_*/init.sql 规范统一。
-- 执行：mysql -uroot -p < sql/ms_ds_order/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_order
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_order;

-- 按依赖顺序删除表：当前仅 1 张表
DROP TABLE IF EXISTS t_order;

-- ============================================================
-- 1) 订单表 t_order（对应 Order.java + service-order.controller.OrderController）
-- ============================================================
CREATE TABLE IF NOT EXISTS t_order (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT       NOT NULL COMMENT '关联用户ID（来自 service-user.t_user.id）',
    order_no    VARCHAR(64)  NOT NULL COMMENT '订单号（唯一）',
    amount      DECIMAL(12,2)          DEFAULT 0 COMMENT '订单金额',
    status      VARCHAR(20)            DEFAULT 'NEW' COMMENT '订单状态：NEW/PAID/CANCELLED 等',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '订单表（service-order 对应 ms_ds_order.t_order）';
