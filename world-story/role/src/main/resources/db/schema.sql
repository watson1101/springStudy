-- role模块数据库模式定义文件
-- 该文件引用了所有SQL脚本，按执行顺序排列

-- 基础表结构
i-- nclude 'sql/01_create_basic_tables.sql';

-- 角色体系表结构
i-- nclude 'sql/02_create_role_system_tables.sql';

-- 初始化角色数据
i-- nclude 'sql/03_init_role_data.sql';

-- 添加角色属性和权限
i-- nclude 'sql/04_add_role_attributes_permissions.sql';

-- 创建视图和存储过程
i-- nclude 'sql/05_create_views_procedures.sql';