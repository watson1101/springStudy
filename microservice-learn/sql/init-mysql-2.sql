-- ============================================================
-- MySQL 初始化脚本：ms_ds_user / ms_ds_product（由 init-pg.sql 转换而来）
-- 在 Ubuntu 服务器执行：mysql -uroot -p < init-mysql-2.sql
-- 说明：
--   1. PostgreSQL BIGSERIAL   -> MySQL BIGINT AUTO_INCREMENT
--   2. PostgreSQL BOOLEAN     -> MySQL TINYINT(1)
--   3. PostgreSQL NOW()       -> MySQL CURRENT_TIMESTAMP
--   4. PostgreSQL \c <db>     -> MySQL USE <db>
--   5. PostgreSQL ON CONFLICT -> MySQL INSERT IGNORE / ON DUPLICATE KEY UPDATE
--   6. 所有库/表默认字符集 utf8mb4、排序规则 utf8mb4_unicode_ci、引擎 InnoDB
-- ============================================================

-- ------------------------------------------------------------
-- 1) 创建数据库 ms_ds_user / ms_ds_product
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS ms_ds_user
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS ms_ds_product
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- 2) 用户库 ms_ds_user 建表 + 初始化数据
-- ============================================================
USE ms_ds_user;

-- 2.1 用户表 t_user
DROP TABLE IF EXISTS t_role_permission;
DROP TABLE IF EXISTS t_user_role;
DROP TABLE IF EXISTS t_permission;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username      VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希（bcrypt等）',
    nickname      VARCHAR(64)           DEFAULT NULL COMMENT '昵称',
    email         VARCHAR(128)          DEFAULT NULL COMMENT '邮箱',
    enabled       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用：1=启用，0=禁用',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户表（service-user 对应 ms_ds_user）';

-- 2.2 角色表 t_role
CREATE TABLE t_role (
    id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code VARCHAR(64)  NOT NULL COMMENT '角色编码（唯一）',
    name VARCHAR(128) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '角色表';

-- 2.3 权限表 t_permission
CREATE TABLE t_permission (
    id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code VARCHAR(128) NOT NULL COMMENT '权限编码（唯一）',
    name VARCHAR(128) NOT NULL COMMENT '权限名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '权限表';

-- 2.4 用户-角色 关联表 t_user_role
CREATE TABLE t_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID（关联 t_user.id）',
    role_id BIGINT NOT NULL COMMENT '角色ID（关联 t_role.id）',
    PRIMARY KEY (user_id, role_id),
    KEY idx_user_role_role_id (role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户-角色关联表';

-- 2.5 角色-权限 关联表 t_role_permission
CREATE TABLE t_role_permission (
    role_id       BIGINT NOT NULL COMMENT '角色ID（关联 t_role.id）',
    permission_id BIGINT NOT NULL COMMENT '权限ID（关联 t_permission.id）',
    PRIMARY KEY (role_id, permission_id),
    KEY idx_role_perm_perm_id (permission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '角色-权限关联表';

-- ------------------------------------------------------------
-- 2.6 初始化：角色数据
-- ------------------------------------------------------------
INSERT INTO t_role (code, name)
VALUES ('admin', '管理员'),
       ('user', '普通用户')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ------------------------------------------------------------
-- 2.7 初始化：权限数据
-- ------------------------------------------------------------
INSERT INTO t_permission (code, name)
VALUES ('user:list', '查询用户列表'),
       ('user:read', '查询用户详情'),
       ('user:create', '创建用户'),
       ('user:self', '查询本人信息')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ------------------------------------------------------------
-- 2.8 初始化：admin 角色授予全部权限
-- ------------------------------------------------------------
INSERT IGNORE INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
CROSS JOIN t_permission p
WHERE r.code = 'admin';

-- ------------------------------------------------------------
-- 2.9 初始化：user 角色授予 user:self 权限
-- ------------------------------------------------------------
INSERT IGNORE INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
JOIN t_permission p ON p.code = 'user:self'
WHERE r.code = 'user';

-- ============================================================
-- 3) 商品库 ms_ds_product 建表
-- ============================================================
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
  COMMENT = '商品表（service-product 对应 ms_ds_product）';
