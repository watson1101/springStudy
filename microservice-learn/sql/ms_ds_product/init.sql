-- ============================================================
-- 商品模块数据库 MySQL 初始化脚本：ms_ds_product
-- 说明：service-product 由 PostgreSQL 迁移到 MySQL 的建库建表脚本
--   与 sql/init-mysql-2.sql 中的 ms_ds_product 段保持一致；额外提供独立子目录便于按库单独执行
-- 执行：mysql -uroot -p < sql/ms_ds_product/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_product
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_product;

DROP TABLE IF EXISTS t_product;

CREATE TABLE t_product (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(128)  NOT NULL COMMENT '商品名称',
    price       DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '单价（元）',
    stock       INT           NOT NULL DEFAULT 0 COMMENT '库存数量',
    create_time DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_product_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '商品表（service-product 对应 ms_ds_product.t_product）';

-- ============================================================
-- 示例数据：少量商品便于验证
-- ============================================================
INSERT INTO t_product(name, price, stock)
VALUES ('华为 Mate 100 Pro', 9999.00, 100),
       ('小米 15 Ultra',   7999.00, 200),
       ('SKILL 积累水杯',   99.99,  9999)
ON DUPLICATE KEY UPDATE name = VALUES(name);
