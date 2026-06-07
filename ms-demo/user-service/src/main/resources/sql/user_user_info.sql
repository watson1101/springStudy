-- =============================================
-- 用户服务数据库表结构
-- 数据库: ms-demo
-- 表名: user_user_info (用户信息表)
-- 作者: hong
-- 创建时间: 2026-06-06
-- 修改说明: 主键ID使用BIGINT类型，通过雪花算法生成
-- =============================================

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS user_user_info;

-- 创建用户信息表
CREATE TABLE user_user_info (
    -- 用户ID，使用雪花算法生成64位Long型ID
    id BIGINT NOT NULL PRIMARY KEY COMMENT '用户ID（雪花算法生成）',

    -- 账号信息
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(200) NOT NULL COMMENT '登录密码（加密存储）',
    nickname VARCHAR(100) NOT NULL COMMENT '用户昵称',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '电子邮箱',
    avatar VARCHAR(500) COMMENT '用户头像URL',

    -- 用户信息
    real_name VARCHAR(100) COMMENT '真实姓名',
    gender SMALLINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    birthday DATE COMMENT '出生日期',

    -- 状态
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    is_deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',

    -- 时间戳
    last_login_time TIMESTAMP COMMENT '最后登录时间',

    -- 审计字段
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',

    -- 索引
    INDEX idx_username (username) COMMENT '用户名索引',
    INDEX idx_phone (phone) COMMENT '手机号索引',
    INDEX idx_status (status, is_deleted) COMMENT '状态索引',
    INDEX idx_create_time (create_time) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';