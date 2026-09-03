-- ============================================================
-- 用户模块数据库 MySQL 初始化脚本：ms_ds_user
-- 说明：service-user 由 PostgreSQL 迁移到 MySQL 的建库建表脚本
--   与 sql/init-mysql-2.sql 中的 ms_ds_user 段保持一致；额外提供独立子目录便于按库单独执行
-- 执行：mysql -uroot -p < sql/ms_ds_user/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_user
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_user;

-- 按依赖顺序删除表：关联表 -> 基础表
DROP TABLE IF EXISTS t_role_permission;
DROP TABLE IF EXISTS t_user_role;
DROP TABLE IF EXISTS t_permission;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_user;

-- ============================================================
-- 1) 用户表 t_user
-- ============================================================
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
  COMMENT = '用户表（service-user 对应 ms_ds_user.t_user）';

-- ============================================================
-- 2) 角色表 t_role
-- ============================================================
CREATE TABLE t_role (
    id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code VARCHAR(64)  NOT NULL COMMENT '角色编码（唯一）',
    name VARCHAR(128) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '角色表';

-- ============================================================
-- 3) 权限表 t_permission
-- ============================================================
CREATE TABLE t_permission (
    id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code VARCHAR(128) NOT NULL COMMENT '权限编码（唯一）',
    name VARCHAR(128) NOT NULL COMMENT '权限名称',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '权限表';

-- ============================================================
-- 4) 用户-角色 关联表 t_user_role
-- ============================================================
CREATE TABLE t_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID（关联 t_user.id）',
    role_id BIGINT NOT NULL COMMENT '角色ID（关联 t_role.id）',
    PRIMARY KEY (user_id, role_id),
    KEY idx_user_role_role_id (role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户-角色关联表';

-- ============================================================
-- 5) 角色-权限 关联表 t_role_permission
-- ============================================================
CREATE TABLE t_role_permission (
    role_id       BIGINT NOT NULL COMMENT '角色ID（关联 t_role.id）',
    permission_id BIGINT NOT NULL COMMENT '权限ID（关联 t_permission.id）',
    PRIMARY KEY (role_id, permission_id),
    KEY idx_role_perm_perm_id (permission_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '角色-权限关联表';

-- ============================================================
-- 初始化数据（与 init-mysql-2.sql / 原 PG 脚本保持一致）
-- ============================================================
INSERT INTO t_role (code, name)
VALUES ('admin', '管理员'),
       ('user', '普通用户')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO t_permission (code, name)
VALUES ('user:list', '查询用户列表'),
       ('user:read', '查询用户详情'),
       ('user:create', '创建用户'),
       ('user:self', '查询本人信息')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT IGNORE INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
CROSS JOIN t_permission p
WHERE r.code = 'admin';

INSERT IGNORE INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
JOIN t_permission p ON p.code = 'user:self'
WHERE r.code = 'user';
