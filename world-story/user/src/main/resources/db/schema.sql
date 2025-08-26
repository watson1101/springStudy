-- 创建真灵表（元用户表）
CREATE TABLE IF NOT EXISTS meta_user (
    id BIGSERIAL PRIMARY KEY,                -- 真灵ID，自增主键
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    deleted INT2 DEFAULT 0                   -- 逻辑删除标志：0-未删除，1-已删除
);

-- 创建用户表（记录每个真灵的每一世信息）
CREATE TABLE IF NOT EXISTS t_user (
    id BIGSERIAL PRIMARY KEY,                -- 用户ID，自增主键
    meta_user_id BIGINT NOT NULL,           -- 真灵ID，外键关联meta_user表
    life_count INT4 NOT NULL,               -- 第几世
    world_type VARCHAR(20) NOT NULL,        -- 所属世界：haven-天界，human-人间，ghost-地府
    birth_date DATE,                         -- 出生日期
    birth_year_cn VARCHAR(10),              -- 出生年（天干地支格式，如甲辰年）
    birth_month_cn VARCHAR(10),             -- 出生月（农历）
    birth_day_cn VARCHAR(10),               -- 出生日（农历）
    birth_hour_cn VARCHAR(10),              -- 出生时辰（如子时）
    species VARCHAR(50) NOT NULL,           -- 物种
    destiny_value BIGINT DEFAULT 0,         -- 天命值
    misfortune_value BIGINT DEFAULT 0,      -- 厄运值
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    deleted INT2 DEFAULT 0,                 -- 逻辑删除标志：0-未删除，1-已删除
    
    -- 外键约束
    CONSTRAINT fk_meta_user FOREIGN KEY (meta_user_id) REFERENCES meta_user(id)
);

-- 创建索引
CREATE INDEX idx_user_meta_user_id ON t_user(meta_user_id);
CREATE INDEX idx_user_world_type ON t_user(world_type);
CREATE INDEX idx_user_species ON t_user(species);

-- 创建用户账号表（用于存储登录信息）
CREATE TABLE IF NOT EXISTS user_account (
    id BIGSERIAL PRIMARY KEY,                -- 账号ID，自增主键
    meta_user_id BIGINT NOT NULL,           -- 真灵ID，外键关联meta_user表
    username VARCHAR(50) NOT NULL UNIQUE,   -- 用户名，唯一
    password VARCHAR(100) NOT NULL,         -- 密码
    nickname VARCHAR(50),                   -- 昵称
    email VARCHAR(100),                     -- 邮箱
    phone VARCHAR(20),                      -- 手机号
    avatar VARCHAR(255),                    -- 头像URL
    gender INT2 DEFAULT 0,                  -- 性别：0-未知，1-男，2-女
    status INT2 DEFAULT 1,                  -- 账号状态：0-禁用，1-启用
    last_login_time TIMESTAMP,              -- 最后登录时间
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    deleted INT2 DEFAULT 0,                 -- 逻辑删除标志：0-未删除，1-已删除
    
    -- 外键约束
    CONSTRAINT fk_user_account_meta_user FOREIGN KEY (meta_user_id) REFERENCES meta_user(id)
);

-- 创建索引
CREATE INDEX idx_user_account_meta_user_id ON user_account(meta_user_id);
CREATE UNIQUE INDEX idx_user_account_username ON user_account(username);
CREATE INDEX idx_user_account_email ON user_account(email);
CREATE INDEX idx_user_account_phone ON user_account(phone);
CREATE INDEX idx_user_account_status ON user_account(status);