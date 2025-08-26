-- 创建界域表
drop table if exists t_realm;
create table t_realm (
    id bigserial primary key,
    name varchar(50) not null comment '界域名称：天界、人界、冥界',
    code varchar(50) not null unique comment '界域编码',
    description text comment '界域描述',
    status int default 1 comment '状态：0-禁用，1-启用',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);

-- 创建真灵表
drop table if exists t_meta_spirit;
create table t_meta_spirit (
    id bigserial primary key,
    name varchar(100) not null comment '真灵名称',
    code varchar(100) not null unique comment '真灵编码',
    origin_realm_id bigint comment '起源界域ID，关联t_realm表',
    description text comment '真灵描述',
    power_level int default 1 comment '真灵等级/实力',
    status int default 1 comment '状态：0-禁用，1-启用',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp,
    deleted int default 0 comment '逻辑删除：0-未删除，1-已删除'
);

-- 添加外键约束
alter table t_meta_spirit add constraint fk_meta_spirit_realm foreign key (origin_realm_id) references t_realm(id);

-- 创建生灵表
drop table if exists t_life_entity;
create table t_life_entity (
    id bigserial primary key,
    name varchar(100) not null comment '生灵名称',
    code varchar(100) not null unique comment '生灵编码',
    meta_spirit_id bigint not null comment '关联真灵ID，关联t_meta_spirit表',
    current_realm_id bigint not null comment '当前界域ID，关联t_realm表',
    description text comment '生灵描述',
    life_status int default 1 comment '生命状态：0-死亡，1-存活',
    birth_time timestamp comment '诞生时间',
    death_time timestamp comment '死亡时间',
    status int default 1 comment '状态：0-禁用，1-启用',
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp,
    deleted int default 0 comment '逻辑删除：0-未删除，1-已删除'
);

-- 添加外键约束
alter table t_life_entity add constraint fk_life_entity_meta_spirit foreign key (meta_spirit_id) references t_meta_spirit(id);
alter table t_life_entity add constraint fk_life_entity_realm foreign key (current_realm_id) references t_realm(id);