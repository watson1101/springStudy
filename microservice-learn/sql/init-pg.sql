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
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nickname      VARCHAR(64),
    email         VARCHAR(128),
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    create_time   TIMESTAMP    NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE t_user IS '用户表（service-user 对应 ms_ds_user）';

CREATE TABLE IF NOT EXISTS t_role (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(64)  NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_permission (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(128) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_user_role (
    user_id BIGINT NOT NULL REFERENCES t_user(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES t_role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS t_role_permission (
    role_id       BIGINT NOT NULL REFERENCES t_role(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES t_permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

INSERT INTO t_role (code, name) VALUES
    ('admin', '管理员'),
    ('user', '普通用户')
ON CONFLICT (code) DO NOTHING;

INSERT INTO t_permission (code, name) VALUES
    ('user:list', '查询用户列表'),
    ('user:read', '查询用户详情'),
    ('user:create', '创建用户'),
    ('user:self', '查询本人信息')
ON CONFLICT (code) DO NOTHING;

INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
CROSS JOIN t_permission p
WHERE r.code = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
JOIN t_permission p ON p.code = 'user:self'
WHERE r.code = 'user'
ON CONFLICT DO NOTHING;

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
