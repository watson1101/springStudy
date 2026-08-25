-- ============================================================
-- PostgreSQL 初始化脚本：ms_ds_user / ms_ds_product
-- 在 Ubuntu 服务器执行：psql -h 127.0.0.1 -U postgres -f init-pg.sql
-- ============================================================

-- 创建数据库（库命名规则：ms_ds_模块名称）
CREATE DATABASE ms_ds_user;
CREATE DATABASE ms_ds_product;

-- 连接用户库，建表
\c ms_ds_user
CREATE TABLE IF NOT EXISTS t_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL UNIQUE,
    nickname    VARCHAR(64),
    email       VARCHAR(128),
    create_time TIMESTAMP DEFAULT NOW()
);
COMMENT ON TABLE t_user IS '用户表（service-user 对应 ms_ds_user）';

-- 连接商品库，建表
\c ms_ds_product
CREATE TABLE IF NOT EXISTS t_product (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    price       NUMERIC(12,2) DEFAULT 0,
    stock       INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT NOW()
);
COMMENT ON TABLE t_product IS '商品表（service-product 对应 ms_ds_product）';
