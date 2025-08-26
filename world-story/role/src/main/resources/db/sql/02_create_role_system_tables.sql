-- 创建角色分类表
drop table if exists t_role_category;
create table t_role_category (
    id bigserial primary key,
    name varchar(50) not null comment '分类名称：神位、官职、职务等',
    code varchar(50) not null unique comment '分类编码',
    parent_id bigint comment '父分类ID，自关联',
    realm_id bigint comment '所属界域ID，关联t_realm表',
    description text comment '分类描述',
    sort int default 0 comment '排序',
    status int default 1 comment '状态：0-禁用，1-启用',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);

-- 添加外键约束
alter table t_role_category add constraint fk_role_category_parent foreign key (parent_id) references t_role_category(id);
alter table t_role_category add constraint fk_role_category_realm foreign key (realm_id) references t_realm(id);

-- 创建角色表（扩展原有的t_role表功能）
-- 注意：如果t_role表已存在，可能需要根据实际情况调整此脚本
alter table t_role add column category_id bigint comment '角色分类ID，关联t_role_category表';
alter table t_role add column realm_id bigint comment '所属界域ID，关联t_realm表';
alter table t_role add column power_level int default 0 comment '角色权力等级';
alter table t_role add column responsibility text comment '角色职责';
alter table t_role add column inheritance_rule text comment '继承规则';

-- 添加外键约束
alter table t_role add constraint fk_role_category foreign key (category_id) references t_role_category(id);
alter table t_role add constraint fk_role_realm foreign key (realm_id) references t_realm(id);

-- 创建角色属性表
drop table if exists t_role_attribute;
create table t_role_attribute (
    id bigserial primary key,
    role_id bigint not null comment '角色ID，关联t_role表',
    attribute_key varchar(50) not null comment '属性键',
    attribute_value text comment '属性值',
    attribute_type varchar(20) default 'string' comment '属性类型：string、number、boolean等',
    description text comment '属性描述',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);

-- 添加外键约束
alter table t_role_attribute add constraint fk_role_attribute_role foreign key (role_id) references t_role(id);

-- 创建角色权限表
drop table if exists t_role_permission;
create table t_role_permission (
    id bigserial primary key,
    role_id bigint not null comment '角色ID，关联t_role表',
    permission_code varchar(100) not null comment '权限编码',
    permission_name varchar(100) not null comment '权限名称',
    permission_type varchar(50) not null comment '权限类型：功能权限、数据权限等',
    description text comment '权限描述',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);

-- 添加外键约束
alter table t_role_permission add constraint fk_role_permission_role foreign key (role_id) references t_role(id);

-- 创建生灵角色关联表
drop table if exists t_life_entity_role;
create table t_life_entity_role (
    id bigserial primary key,
    life_entity_id bigint not null comment '生灵ID，关联t_life_entity表',
    role_id bigint not null comment '角色ID，关联t_role表',
    start_time timestamp not null comment '担任角色开始时间',
    end_time timestamp comment '担任角色结束时间',
    status int default 1 comment '状态：0-无效，1-有效',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);

-- 添加外键约束
alter table t_life_entity_role add constraint fk_life_entity_role_life foreign key (life_entity_id) references t_life_entity(id);
alter table t_life_entity_role add constraint fk_life_entity_role_role foreign key (role_id) references t_role(id);

-- 创建唯一索引，确保一个生灵在同一时间不能担任同一个角色多次
alter table t_life_entity_role add constraint uk_life_entity_role unique (life_entity_id, role_id, start_time);