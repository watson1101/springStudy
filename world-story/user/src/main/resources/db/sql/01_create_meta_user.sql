-- 创建真灵表（元用户表）
CREATE TABLE IF NOT EXISTS meta_user (
    id BIGSERIAL PRIMARY KEY,                -- 真灵ID，自增主键
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    deleted INT2 DEFAULT 0                   -- 逻辑删除标志：0-未删除，1-已删除
);