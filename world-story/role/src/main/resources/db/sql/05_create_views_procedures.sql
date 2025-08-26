-- 创建视图：获取生灵的完整角色信息
create or replace view v_life_entity_full_role_info as
select 
    le.id as life_entity_id,
    le.name as life_entity_name,
    le.code as life_entity_code,
    ms.id as meta_spirit_id,
    ms.name as meta_spirit_name,
    r.id as realm_id,
    r.name as realm_name,
    r.code as realm_code,
    role.id as role_id,
    role.name as role_name,
    role.code as role_code,
    role.description as role_description,
    rc.id as category_id,
    rc.name as category_name,
    rc.code as category_code,
    ler.start_time,
    ler.end_time,
    ler.status as relation_status
from t_life_entity le
join t_meta_spirit ms on le.meta_spirit_id = ms.id
join t_realm r on le.current_realm_id = r.id
join t_life_entity_role ler on le.id = ler.life_entity_id
join t_role role on ler.role_id = role.id
join t_role_category rc on role.category_id = rc.id;

-- 创建视图：获取角色的完整信息（包含权限和属性）
create or replace view v_role_full_info as
select 
    r.id as role_id,
    r.name as role_name,
    r.code as role_code,
    r.description as role_description,
    r.power_level,
    r.responsibility,
    r.inheritance_rule,
    rc.id as category_id,
    rc.name as category_name,
    rc.code as category_code,
    realm.id as realm_id,
    realm.name as realm_name,
    realm.code as realm_code,
    array_agg(distinct rp.permission_code) as permissions,
    json_agg(distinct jsonb_build_object(
        'key', ra.attribute_key,
        'value', ra.attribute_value,
        'type', ra.attribute_type,
        'description', ra.description
    )) as attributes
from t_role r
join t_role_category rc on r.category_id = rc.id
join t_realm realm on r.realm_id = realm.id
left join t_role_permission rp on r.id = rp.role_id
left join t_role_attribute ra on r.id = ra.role_id
where r.deleted = 0
group by r.id, rc.id, realm.id;

-- 创建视图：获取界域中的所有角色分布
create or replace view v_realm_role_distribution as
select 
    r.id as realm_id,
    r.name as realm_name,
    r.code as realm_code,
    rc.name as category_name,
    rc.code as category_code,
    count(role.id) as role_count
from t_realm r
left join t_role_category rc on r.id = rc.realm_id
left join t_role role on rc.id = role.category_id and role.deleted = 0
group by r.id, rc.id
order by r.id, rc.id;

-- 创建存储过程：根据界域ID获取所有角色
create or replace function get_roles_by_realm_id(p_realm_id bigint)
returns table (
    role_id bigint,
    role_name varchar(50),
    role_code varchar(50),
    category_name varchar(50),
    power_level int,
    responsibility text
)
as $$
begin
    return query
    select 
        r.id,
        r.name,
        r.code,
        rc.name,
        r.power_level,
        r.responsibility
    from t_role r
    join t_role_category rc on r.category_id = rc.id
    where r.realm_id = p_realm_id and r.deleted = 0 and r.status = 1
    order by r.power_level desc;
end;
$$ language plpgsql;

-- 创建存储过程：为生灵分配角色
create or replace function assign_role_to_life_entity(
    p_life_entity_id bigint,
    p_role_id bigint,
    p_start_time timestamp
)
returns boolean
as $$
declare
    v_life_entity_exists int;
    v_role_exists int;
    v_current_assignment int;
begin
    -- 检查生灵是否存在
    select count(*) into v_life_entity_exists from t_life_entity where id = p_life_entity_id and deleted = 0;
    if v_life_entity_exists = 0 then
        raise exception '生灵不存在';
        return false;
    end if;
    
    -- 检查角色是否存在
    select count(*) into v_role_exists from t_role where id = p_role_id and deleted = 0 and status = 1;
    if v_role_exists = 0 then
        raise exception '角色不存在或已禁用';
        return false;
    end if;
    
    -- 检查是否已经有相同的角色分配（未结束的）
    select count(*) into v_current_assignment 
    from t_life_entity_role 
    where life_entity_id = p_life_entity_id 
      and role_id = p_role_id 
      and end_time is null 
      and status = 1;
    
    if v_current_assignment > 0 then
        raise exception '该生灵已经被分配了此角色且尚未结束';
        return false;
    end if;
    
    -- 分配角色
    insert into t_life_entity_role (life_entity_id, role_id, start_time, status) 
    values (p_life_entity_id, p_role_id, p_start_time, 1);
    
    return true;
exception
    when others then
        raise notice '分配角色时发生错误: %', SQLERRM;
        return false;
end;
$$ language plpgsql;

-- 创建存储过程：结束生灵的角色任期
create or replace function end_life_entity_role(
    p_life_entity_id bigint,
    p_role_id bigint,
    p_end_time timestamp
)
returns boolean
as $$
begin
    -- 检查是否存在有效的角色分配
    if not exists (
        select 1 
        from t_life_entity_role 
        where life_entity_id = p_life_entity_id 
          and role_id = p_role_id 
          and end_time is null 
          and status = 1
    ) then
        raise exception '未找到该生灵的有效角色分配';
        return false;
    end if;
    
    -- 更新角色任期结束时间
    update t_life_entity_role 
    set end_time = p_end_time, status = 0
    where life_entity_id = p_life_entity_id 
      and role_id = p_role_id 
      and end_time is null 
      and status = 1;
    
    return true;
exception
    when others then
        raise notice '结束角色任期时发生错误: %', SQLERRM;
        return false;
end;
$$ language plpgsql;

-- 创建存储过程：查询真灵的转世历史
create or replace function get_meta_spirit_reincarnation_history(p_meta_spirit_id bigint)
returns table (
    life_entity_id bigint,
    life_entity_name varchar(100),
    life_entity_code varchar(100),
    realm_name varchar(50),
    birth_time timestamp,
    death_time timestamp,
    life_status int
)
as $$
begin
    return query
    select 
        le.id,
        le.name,
        le.code,
        r.name,
        le.birth_time,
        le.death_time,
        le.life_status
    from t_life_entity le
    join t_realm r on le.current_realm_id = r.id
    where le.meta_spirit_id = p_meta_spirit_id and le.deleted = 0
    order by le.birth_time desc;
end;
$$ language plpgsql;