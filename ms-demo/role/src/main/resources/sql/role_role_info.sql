-- =============================================
-- 角色服务数据库表结构
-- 数据库: ms-demo
-- 主键ID使用BIGINT类型，通过雪花算法生成
-- =============================================

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS role_user_role;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS role_role_info;

-- =============================================
-- 角色信息表
-- =============================================
CREATE TABLE role_role_info (
    id BIGINT NOT NULL PRIMARY KEY COMMENT '角色ID（雪花算法生成）',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_desc VARCHAR(500) COMMENT '角色描述',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    is_deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',
    INDEX idx_role_code (role_code) COMMENT '角色编码索引',
    INDEX idx_status (status, is_deleted) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- =============================================
-- 权限信息表
-- =============================================
CREATE TABLE role_permission (
    id BIGINT NOT NULL PRIMARY KEY COMMENT '权限ID（雪花算法生成）',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_type SMALLINT NOT NULL COMMENT '权限类型：1-菜单 2-按钮 3-API',
    parent_id BIGINT DEFAULT NULL COMMENT '父权限ID',
    path VARCHAR(500) COMMENT '路径',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    is_deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',
    INDEX idx_permission_code (permission_code) COMMENT '权限编码索引',
    INDEX idx_parent_id (parent_id) COMMENT '父权限ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限信息表';

-- =============================================
-- 用户角色关联表
-- =============================================
CREATE TABLE role_user_role (
    id BIGINT NOT NULL PRIMARY KEY COMMENT '关联ID（雪花算法生成）',
    user_id BIGINT NOT NULL COMMENT '用户ID（关联user_user_info）',
    role_id BIGINT NOT NULL COMMENT '角色ID（关联role_role_info）',
    is_deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',
    UNIQUE INDEX idx_user_role (user_id, role_id) COMMENT '用户角色唯一索引',
    INDEX idx_role_id (role_id) COMMENT '角色ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';