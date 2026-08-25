-- ============================================================
-- MySQL 初始化脚本：ms_ds_order
-- 在 Ubuntu 服务器执行：mysql -uroot -p < init-mysql.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ms_ds_order;

CREATE TABLE IF NOT EXISTS t_order (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL COMMENT '关联用户ID（来自 service-user）',
    order_no    VARCHAR(64) NOT NULL COMMENT '订单号',
    amount      DECIMAL(12,2) DEFAULT 0,
    status      VARCHAR(20) DEFAULT 'NEW',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表（service-order 对应 ms_ds_order）';
