-- 天道模块 - 随机事件测试数据

-- 插入天灾事件测试数据
INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    1, 
    '火山喷发', 
    '不周山火山喷发，岩浆四溢，周围百里生灵涂炭。', 
    '不周山', 
    '大范围', 
    CURRENT_TIMESTAMP - INTERVAL '2 hours', 
    CURRENT_TIMESTAMP + INTERVAL '22 hours', 
    1, 
    0.25, 
    '{"season": "summer", "region": "mountain", "weather": "sunny", "timeFactor": 0.75}'::jsonb
);

-- 插入宝物现世事件测试数据
INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    2, 
    '上古神器现世', 
    '昆仑山深处，上古神器现世，引动天地异象。', 
    '昆仑山', 
    '中范围', 
    CURRENT_TIMESTAMP - INTERVAL '1 day', 
    CURRENT_TIMESTAMP + INTERVAL '6 days', 
    1, 
    0.18, 
    '{"season": "spring", "region": "mountain", "weather": "cloudy", "timeFactor": 0.6}'::jsonb
);

-- 插入仙人降临事件测试数据
INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    3, 
    '道教仙人降临', 
    '东海之上，祥云缭绕，道教仙人降临凡尘，欲点化有缘人。', 
    '东海', 
    '小范围', 
    CURRENT_TIMESTAMP - INTERVAL '5 hours', 
    CURRENT_TIMESTAMP + INTERVAL '7 hours', 
    1, 
    0.12, 
    '{"season": "autumn", "region": "ocean", "weather": "windy", "timeFactor": 0.8}'::jsonb
);

-- 插入神兽出没事件测试数据
INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    4, 
    '青龙出没', 
    '北溟之中，青龙现世，天地变色，风雨交加。', 
    '北溟', 
    '大范围', 
    CURRENT_TIMESTAMP - INTERVAL '2 days', 
    CURRENT_TIMESTAMP + INTERVAL '2 days', 
    1, 
    0.08, 
    '{"season": "winter", "region": "ocean", "weather": "stormy", "timeFactor": 0.9}'::jsonb
);

-- 插入灵气潮汐事件测试数据
INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    5, 
    '灵气潮汐', 
    '天地灵气涌动，形成巨大潮汐，对所有修炼者大有裨益。', 
    '全区域', 
    '全区域', 
    CURRENT_TIMESTAMP - INTERVAL '3 days', 
    CURRENT_TIMESTAMP + INTERVAL '10 days', 
    1, 
    0.35, 
    '{"season": "spring", "region": "plain", "weather": "sunny", "timeFactor": 0.5}'::jsonb
);

-- 插入已结束的事件测试数据
INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    1, 
    '地震', 
    '西极之地发生强烈地震，山脉崩塌，河流改道。', 
    '西极', 
    '大范围', 
    CURRENT_TIMESTAMP - INTERVAL '3 days', 
    CURRENT_TIMESTAMP - INTERVAL '1 day', 
    2, 
    0.2, 
    '{"season": "summer", "region": "mountain", "weather": "cloudy", "timeFactor": 0.65}'::jsonb
);

INSERT INTO t_random_event (event_type, event_name, description, location, influence_range, occur_time, end_time, status, probability, influence_factors)
VALUES (
    2, 
    '珍稀药材现世', 
    '南荒深处，千年人参现世，引来了众多修炼者争夺。', 
    '南荒', 
    '小范围', 
    CURRENT_TIMESTAMP - INTERVAL '5 days', 
    CURRENT_TIMESTAMP - INTERVAL '3 days', 
    2, 
    0.15, 
    '{"season": "autumn", "region": "forest", "weather": "foggy", "timeFactor": 0.7}'::jsonb
);