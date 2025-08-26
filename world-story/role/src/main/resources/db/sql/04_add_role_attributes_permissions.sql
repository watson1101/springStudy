-- 为角色添加属性
delete from t_role_attribute;

-- 为天界角色添加属性
insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'divine_power', '10000', 'number', '神力值' from t_role r where r.code = 'ROLE_YUANSHI_TIANZUN';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'divine_artifact', '盘古幡', 'string', '法宝' from t_role r where r.code = 'ROLE_YUANSHI_TIANZUN';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'divine_power', '9800', 'number', '神力值' from t_role r where r.code = 'ROLE_LINGBAO_TIANZUN';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'divine_artifact', '太极图', 'string', '法宝' from t_role r where r.code = 'ROLE_DAODE_TIANZUN';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'divine_power', '9500', 'number', '神力值' from t_role r where r.code = 'ROLE_YUHuang_DADI';

-- 为人界角色添加属性
insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'authority_scope', '全国', 'string', '权力范围' from t_role r where r.code = 'ROLE_EMPEROR';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'reign_title', '贞观', 'string', '年号' from t_role r where r.code = 'ROLE_EMPEROR';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'military_rank', '正一品', 'string', '官阶' from t_role r where r.code = 'ROLE_GENERAL';

-- 为冥界角色添加属性
insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'underworld_power', '9500', 'number', '冥界法力' from t_role r where r.code = 'ROLE_FENDU_DADI';

insert into t_role_attribute (role_id, attribute_key, attribute_value, attribute_type, description) 
select r.id, 'judgment_authority', '生死簿', 'string', '审判权柄' from t_role r where r.code = 'ROLE_YAMA_KING';

-- 添加角色权限
delete from t_role_permission;

-- 天界角色权限
insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'CREATE_GOD', '创建神灵', '功能权限', '有权创建新的神灵角色' from t_role r where r.code in ('ROLE_YUANSHI_TIANZUN', 'ROLE_LINGBAO_TIANZUN', 'ROLE_DAODE_TIANZUN');

insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'MANAGE_HEAVEN', '管理天界', '功能权限', '有权管理天界事务' from t_role r where r.code = 'ROLE_YUHuang_DADI';

insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'COMMAND_ARMY', '指挥军队', '功能权限', '有权指挥天界军队' from t_role r where r.code in ('ROLE_TUOTA_TIANWANG', 'ROLE_NEZHA');

-- 人界角色权限
insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'ISSUE_DECREE', '发布诏令', '功能权限', '有权发布国家诏令' from t_role r where r.code = 'ROLE_EMPEROR';

insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'MANAGE_GOVERNMENT', '管理朝政', '功能权限', '有权管理国家政务' from t_role r where r.code = 'ROLE_PRIME_MINISTER';

insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'LEAD_TROOPS', '领军作战', '功能权限', '有权率领军队作战' from t_role r where r.code = 'ROLE_GENERAL';

-- 冥界角色权限
insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'JUDGE_SOUL', '审判灵魂', '功能权限', '有权审判亡灵' from t_role r where r.code in ('ROLE_FENDU_DADI', 'ROLE_YAMA_KING');

insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'ARREST_SOUL', '拘拿魂魄', '功能权限', '有权拘拿人间魂魄' from t_role r where r.code in ('ROLE_BLACK_WHITE_GHOST');

-- 地方神灵权限
insert into t_role_permission (role_id, permission_code, permission_name, permission_type, description) 
select r.id, 'PROTECT_TERRITORY', '保护领地', '功能权限', '有权保护所管辖的领地' from t_role r where r.code in ('ROLE_MOUNTAIN_GOD', 'ROLE_CHENGHUANG', 'ROLE_TUDIGONG');

-- 创建一些示例真灵数据
delete from t_meta_spirit;
insert into t_meta_spirit (name, code, origin_realm_id, description, power_level) 
values 
    ('元始真灵', 'META_YUANSHI', (select id from t_realm where code = 'REALM_HEAVEN'), '元始天尊的真灵', 100),
    ('灵宝真灵', 'META_LINGBAO', (select id from t_realm where code = 'REALM_HEAVEN'), '灵宝天尊的真灵', 100),
    ('道德真灵', 'META_DAODE', (select id from t_realm where code = 'REALM_HEAVEN'), '道德天尊的真灵', 100),
    ('玉皇真灵', 'META_YUHuang', (select id from t_realm where code = 'REALM_HEAVEN'), '玉皇大帝的真灵', 95),
    ('李世民真灵', 'META_LISHIMIN', (select id from t_realm where code = 'REALM_HUMAN'), '唐太宗李世民的真灵', 90),
    ('魏征真灵', 'META_WEIZHENG', (select id from t_realm where code = 'REALM_HUMAN'), '唐朝名相魏征的真灵', 85),
    ('秦琼真灵', 'META_QINQIONG', (select id from t_realm where code = 'REALM_HUMAN'), '唐朝名将秦琼的真灵', 85),
    ('酆都真灵', 'META_FENDU', (select id from t_realm where code = 'REALM_UNDERWORLD'), '酆都大帝的真灵', 95),
    ('阎罗真灵', 'META_YAMA', (select id from t_realm where code = 'REALM_UNDERWORLD'), '阎罗王的真灵', 90);

-- 创建一些示例生灵数据
delete from t_life_entity;
insert into t_life_entity (name, code, meta_spirit_id, current_realm_id, description, life_status, birth_time) 
values 
    ('元始天尊', 'LIFE_YUANSHI_TIANZUN', (select id from t_meta_spirit where code = 'META_YUANSHI'), (select id from t_realm where code = 'REALM_HEAVEN'), '三清之首，道教最高神之一', 1, '2000-01-01 00:00:00'),
    ('灵宝天尊', 'LIFE_LINGBAO_TIANZUN', (select id from t_meta_spirit where code = 'META_LINGBAO'), (select id from t_realm where code = 'REALM_HEAVEN'), '三清之一，道教最高神之一', 1, '2000-01-01 00:00:00'),
    ('道德天尊', 'LIFE_DAODE_TIANZUN', (select id from t_meta_spirit where code = 'META_DAODE'), (select id from t_realm where code = 'REALM_HEAVEN'), '三清之一，即太上老君', 1, '2000-01-01 00:00:00'),
    ('玉皇大帝', 'LIFE_YUHuang_DADI', (select id from t_meta_spirit where code = 'META_YUHuang'), (select id from t_realm where code = 'REALM_HEAVEN'), '天界之主，掌管三界', 1, '1800-01-01 00:00:00'),
    ('李世民', 'LIFE_LISHIMIN', (select id from t_meta_spirit where code = 'META_LISHIMIN'), (select id from t_realm where code = 'REALM_HUMAN'), '唐太宗，唐朝第二位皇帝', 0, '626-01-01 00:00:00'),
    ('魏征', 'LIFE_WEIZHENG', (select id from t_meta_spirit where code = 'META_WEIZHENG'), (select id from t_realm where code = 'REALM_HUMAN'), '唐朝名相，以直谏著称', 0, '627-01-01 00:00:00'),
    ('秦琼', 'LIFE_QINQIONG', (select id from t_meta_spirit where code = 'META_QINQIONG'), (select id from t_realm where code = 'REALM_HUMAN'), '唐朝开国名将', 0, '627-01-01 00:00:00'),
    ('酆都大帝', 'LIFE_FENDU_DADI', (select id from t_meta_spirit where code = 'META_FENDU'), (select id from t_realm where code = 'REALM_UNDERWORLD'), '冥界最高统治者', 1, '1500-01-01 00:00:00'),
    ('阎罗王', 'LIFE_YAMA_KING', (select id from t_meta_spirit where code = 'META_YAMA'), (select id from t_realm where code = 'REALM_UNDERWORLD'), '十殿阎王之首', 1, '1500-01-01 00:00:00');

-- 创建生灵与角色的关联
delete from t_life_entity_role;
insert into t_life_entity_role (life_entity_id, role_id, start_time) 
values 
    ((select id from t_life_entity where code = 'LIFE_YUANSHI_TIANZUN'), (select id from t_role where code = 'ROLE_YUANSHI_TIANZUN'), '2000-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_LINGBAO_TIANZUN'), (select id from t_role where code = 'ROLE_LINGBAO_TIANZUN'), '2000-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_DAODE_TIANZUN'), (select id from t_role where code = 'ROLE_DAODE_TIANZUN'), '2000-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_YUHuang_DADI'), (select id from t_role where code = 'ROLE_YUHuang_DADI'), '1800-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_LISHIMIN'), (select id from t_role where code = 'ROLE_EMPEROR'), '626-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_WEIZHENG'), (select id from t_role where code = 'ROLE_PRIME_MINISTER'), '627-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_QINQIONG'), (select id from t_role where code = 'ROLE_GENERAL'), '627-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_FENDU_DADI'), (select id from t_role where code = 'ROLE_FENDU_DADI'), '1500-01-01 00:00:00'),
    ((select id from t_life_entity where code = 'LIFE_YAMA_KING'), (select id from t_role where code = 'ROLE_YAMA_KING'), '1500-01-01 00:00:00');