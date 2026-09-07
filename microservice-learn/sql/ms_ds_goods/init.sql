-- ============================================================
-- 商品管理模块数据库 MySQL 初始化脚本：ms_ds_goods
-- 说明：service-goods 建库建表脚本，商品分类存在 ms-ds-system 的字典表中
-- 执行：mysql -uroot -p < sql/ms_ds_goods/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_goods
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_goods;

DROP TABLE IF EXISTS ms_ds_goods;

CREATE TABLE ms_ds_goods (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name         VARCHAR(128)  NOT NULL COMMENT '商品名称',
    category_id  BIGINT        NOT NULL COMMENT '三级分类ID（对应 ms-ds-system 字典 GOODS_CATEGORY 的 level=3 字典项ID）',
    price        DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '价格（元）',
    main_image   VARCHAR(512)  DEFAULT NULL COMMENT '主图 URL',
    images       TEXT          DEFAULT NULL COMMENT '多图列表（JSON 数组字符串）',
    specs        TEXT          DEFAULT NULL COMMENT '规格（JSON 字符串，如 {"颜色":"红色","尺寸":"L"}）',
    description  TEXT          DEFAULT NULL COMMENT '商品描述',
    status       TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0=草稿 1=上架 2=下架',
    stock        INT           NOT NULL DEFAULT 0 COMMENT '库存数量',
    create_time  DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '商品表（service-goods 对应 ms_ds_goods.ms_ds_goods）';

-- ============================================================
-- 示例数据：少量商品便于验证（category_id 需替换为实际三级字典项ID）
-- ============================================================
-- 注意：category_id 依赖 ms-ds-system 字典 GOODS_CATEGORY 的三级项ID，
--       请先执行 ms-ds-system/sql/ms_ds_sys_config.sql 初始化字典后，
--       将下列 category_id 替换为实际值（示例中三级项"智能手机"的ID）。
INSERT INTO ms_ds_goods(name, category_id, price, main_image, images, specs, description, status, stock)
VALUES ('示例智能手机 X1', 0, 2999.00, 'https://example.com/x1.jpg',
        '["https://example.com/x1-1.jpg","https://example.com/x1-2.jpg"]',
        '{"颜色":"幻夜黑","存储":"256GB"}', '示例商品，用于联调验证', 1, 100)
ON DUPLICATE KEY UPDATE name = VALUES(name);
