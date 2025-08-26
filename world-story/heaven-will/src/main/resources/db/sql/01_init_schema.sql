-- 天道模块 - 随机事件表结构

-- 创建随机事件表
CREATE TABLE IF NOT EXISTS t_random_event (
    id BIGSERIAL PRIMARY KEY COMMENT '主键ID',
    event_type INTEGER NOT NULL COMMENT '事件类型：1-天灾，2-宝物现世，3-仙人降临，4-神兽出没，5-灵气潮汐',
    event_name VARCHAR(100) NOT NULL COMMENT '事件名称',
    description TEXT COMMENT '事件描述',
    location VARCHAR(100) COMMENT '事件发生地点',
    influence_range VARCHAR(50) COMMENT '影响范围',
    occur_time TIMESTAMP COMMENT '发生时间',
    end_time TIMESTAMP COMMENT '结束时间',
    status INTEGER DEFAULT 0 COMMENT '事件状态：0-未发生，1-活跃中，2-已结束',
    probability DOUBLE PRECISION DEFAULT 0.0 COMMENT '事件概率',
    influence_factors JSONB COMMENT '影响因子(JSON格式)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除'
);

-- 添加表注释
COMMENT ON TABLE t_random_event IS '随机事件表 - 记录天道模块生成的各种随机事件';

-- 添加索引
CREATE INDEX idx_event_type ON t_random_event (event_type);
CREATE INDEX idx_status ON t_random_event (status);
CREATE INDEX idx_occur_time ON t_random_event (occur_time);
CREATE INDEX idx_end_time ON t_random_event (end_time);
CREATE INDEX idx_deleted ON t_random_event (deleted);