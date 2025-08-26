-- 初始化界域数据
delete from t_realm;
insert into t_realm (name, code, description) values
    ('天界', 'REALM_HEAVEN', '三界之首，神仙居住之地'),
    ('人界', 'REALM_HUMAN', '凡人居住之地，万物生灵繁衍生息'),
    ('冥界', 'REALM_UNDERWORLD', '亡灵归宿，阴司地府所在');

-- 初始化角色分类数据
delete from t_role_category;
-- 天界角色分类
insert into t_role_category (name, code, parent_id, realm_id, description) 
select '神位', 'CATEGORY_GOD', null, id, '天界神灵职位' from t_realm where code = 'REALM_HEAVEN';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '三清', 'CATEGORY_SANQING', id, (select id from t_realm where code = 'REALM_HEAVEN'), '道教最高神团' from t_role_category where code = 'CATEGORY_GOD';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '四御', 'CATEGORY_SIYU', id, (select id from t_realm where code = 'REALM_HEAVEN'), '辅佐三清的四位天帝' from t_role_category where code = 'CATEGORY_GOD';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '星宿', 'CATEGORY_STAR', id, (select id from t_realm where code = 'REALM_HEAVEN'), '天界星宿神位' from t_role_category where code = 'CATEGORY_GOD';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '天兵天将', 'CATEGORY_SOLDIER', id, (select id from t_realm where code = 'REALM_HEAVEN'), '天界守卫和战斗力量' from t_role_category where code = 'CATEGORY_GOD';

-- 人界角色分类
insert into t_role_category (name, code, parent_id, realm_id, description) 
select '官职', 'CATEGORY_OFFICIAL', null, id, '人间世俗官职' from t_realm where code = 'REALM_HUMAN';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '诸侯', 'CATEGORY_LORD', id, (select id from t_realm where code = 'REALM_HUMAN'), '诸侯藩王' from t_role_category where code = 'CATEGORY_OFFICIAL';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '文官', 'CATEGORY_CIVIL', id, (select id from t_realm where code = 'REALM_HUMAN'), '文职官员' from t_role_category where code = 'CATEGORY_OFFICIAL';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '武将', 'CATEGORY_MILITARY', id, (select id from t_realm where code = 'REALM_HUMAN'), '军事将领' from t_role_category where code = 'CATEGORY_OFFICIAL';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '平民', 'CATEGORY_COMMONER', null, id, '普通百姓' from t_realm where code = 'REALM_HUMAN';

-- 冥界角色分类
insert into t_role_category (name, code, parent_id, realm_id, description) 
select '阴司', 'CATEGORY_YINSI', null, id, '冥界官职' from t_realm where code = 'REALM_UNDERWORLD';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '十殿阎王', 'CATEGORY_YAMA', id, (select id from t_realm where code = 'REALM_UNDERWORLD'), '冥界十位阎王' from t_role_category where code = 'CATEGORY_YINSI';

insert into t_role_category (name, code, parent_id, realm_id, description) 
select '鬼差', 'CATEGORY_GHOST', id, (select id from t_realm where code = 'REALM_UNDERWORLD'), '冥界差役' from t_role_category where code = 'CATEGORY_YINSI';

-- 初始化基础角色数据
delete from t_role;
-- 天界角色
insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility, inheritance_rule) 
select '元始天尊', 'ROLE_YUANSHI_TIANZUN', '三清之首，道教最高神之一', id, (select id from t_realm where code = 'REALM_HEAVEN'), 100, '主持天界大事，教化众生', '道教传承' from t_role_category where code = 'CATEGORY_SANQING';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility, inheritance_rule) 
select '灵宝天尊', 'ROLE_LINGBAO_TIANZUN', '三清之一，道教最高神之一', id, (select id from t_realm where code = 'REALM_HEAVEN'), 100, '掌管灵宝，度化世人', '道教传承' from t_role_category where code = 'CATEGORY_SANQING';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility, inheritance_rule) 
select '道德天尊', 'ROLE_DAODE_TIANZUN', '三清之一，即太上老君', id, (select id from t_realm where code = 'REALM_HEAVEN'), 100, '传授道德，炼制丹药', '道教传承' from t_role_category where code = 'CATEGORY_SANQING';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility, inheritance_rule) 
select '玉皇大帝', 'ROLE_YUHuang_DADI', '天界之主，掌管三界', id, (select id from t_realm where code = 'REALM_HEAVEN'), 95, '统御三界，管理诸神', '道教传承' from t_role_category where code = 'CATEGORY_SIYU';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '托塔天王', 'ROLE_TUOTA_TIANWANG', '天界武将，掌管天兵天将', id, (select id from t_realm where code = 'REALM_HEAVEN'), 85, '统领天界军队，降妖除魔' from t_role_category where code = 'CATEGORY_SOLDIER';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '哪吒三太子', 'ROLE_NEZHA', '托塔天王之子，三坛海会大神', id, (select id from t_realm where code = 'REALM_HEAVEN'), 80, '辅助托塔天王，降妖伏魔' from t_role_category where code = 'CATEGORY_SOLDIER';

-- 人界角色
insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '皇帝', 'ROLE_EMPEROR', '人间最高统治者', id, (select id from t_realm where code = 'REALM_HUMAN'), 90, '统治天下，治理万民' from t_role_category where code = 'CATEGORY_OFFICIAL';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '丞相', 'ROLE_PRIME_MINISTER', '皇帝之下的最高文官', id, (select id from t_realm where code = 'REALM_HUMAN'), 80, '辅佐皇帝，管理朝政' from t_role_category where code = 'CATEGORY_CIVIL';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '大将军', 'ROLE_GENERAL', '最高军事指挥官', id, (select id from t_realm where code = 'REALM_HUMAN'), 80, '统领军队，保卫国家' from t_role_category where code = 'CATEGORY_MILITARY';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '平民百姓', 'ROLE_COMMON_PEOPLE', '普通民众', id, (select id from t_realm where code = 'REALM_HUMAN'), 10, '劳作生产，遵守法律' from t_role_category where code = 'CATEGORY_COMMONER';

-- 冥界角色
insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '酆都大帝', 'ROLE_FENDU_DADI', '冥界最高统治者', id, (select id from t_realm where code = 'REALM_UNDERWORLD'), 95, '统治冥界，审判亡灵' from t_role_category where code = 'CATEGORY_YINSI';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '阎罗王', 'ROLE_YAMA_KING', '十殿阎王之首', id, (select id from t_realm where code = 'REALM_UNDERWORLD'), 90, '审判善恶，决定轮回' from t_role_category where code = 'CATEGORY_YAMA';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '黑白无常', 'ROLE_BLACK_WHITE_GHOST', '勾魂使者', id, (select id from t_realm where code = 'REALM_UNDERWORLD'), 70, '拘拿魂魄，引领亡灵' from t_role_category where code = 'CATEGORY_GHOST';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '牛头马面', 'ROLE_BULL_HEAD_HORSE_FACE', '冥界差役', id, (select id from t_realm where code = 'REALM_UNDERWORLD'), 65, '看管地狱，维持秩序' from t_role_category where code = 'CATEGORY_GHOST';

-- 初始化地方神灵角色（跨界域）
insert into t_role_category (name, code, parent_id, realm_id, description) 
select '地方神灵', 'CATEGORY_LOCAL_GOD', null, (select id from t_realm where code = 'REALM_HEAVEN'), '负责一方事务的神灵';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '山神', 'ROLE_MOUNTAIN_GOD', '掌管山岳的神灵', id, (select id from t_realm where code = 'REALM_HEAVEN'), 60, '守护山脉，庇护山民' from t_role_category where code = 'CATEGORY_LOCAL_GOD';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '城隍', 'ROLE_CHENGHUANG', '守护城池的神灵', id, (select id from t_realm where code = 'REALM_HEAVEN'), 65, '守护城池，管理阴魂' from t_role_category where code = 'CATEGORY_LOCAL_GOD';

insert into t_role (name, code, description, category_id, realm_id, power_level, responsibility) 
select '土地公', 'ROLE_TUDIGONG', '掌管一方土地的神灵', id, (select id from t_realm where code = 'REALM_HEAVEN'), 55, '守护乡土，保佑丰收' from t_role_category where code = 'CATEGORY_LOCAL_GOD';