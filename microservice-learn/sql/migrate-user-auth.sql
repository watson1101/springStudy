-- service-user 认证与 RBAC 增量迁移
-- 执行示例：psql -h 127.0.0.1 -U postgres -d ms_ds_user -f migrate-user-auth.sql

BEGIN;

ALTER TABLE t_user
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(100),
    ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE IF NOT EXISTS t_role (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(64)  NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_permission (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(128) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_user_role (
    user_id BIGINT NOT NULL REFERENCES t_user(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES t_role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS t_role_permission (
    role_id       BIGINT NOT NULL REFERENCES t_role(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES t_permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

INSERT INTO t_role (code, name) VALUES
    ('admin', '管理员'),
    ('user', '普通用户')
ON CONFLICT (code) DO NOTHING;

INSERT INTO t_permission (code, name) VALUES
    ('user:list', '查询用户列表'),
    ('user:read', '查询用户详情'),
    ('user:create', '创建用户'),
    ('user:self', '查询本人信息')
ON CONFLICT (code) DO NOTHING;

INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
CROSS JOIN t_permission p
WHERE r.code = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM t_role r
JOIN t_permission p ON p.code = 'user:self'
WHERE r.code = 'user'
ON CONFLICT DO NOTHING;

COMMIT;

-- 旧数据没有可迁移的明文密码，需要通过注册接口重建账号或由管理员写入 BCrypt 哈希。
-- 为用户授予管理员角色：
-- INSERT INTO t_user_role (user_id, role_id)
-- SELECT u.id, r.id FROM t_user u JOIN t_role r ON r.code = 'admin'
-- WHERE u.username = 'your-admin' ON CONFLICT DO NOTHING;
