-- 初始化用户账号数据
-- 注意：密码使用MD5加密存储
-- 例如：admin123 的MD5值为：0192023a7bbd73250516f069df18b500

-- 插入管理员账号
INSERT INTO user_account (
    meta_user_id, username, password, nickname, email, phone, avatar, gender, status
) VALUES (
    1, 'admin', '0192023a7bbd73250516f069df18b500', '管理员', 'admin@hong.com', '13800138000', 'default_admin_avatar.png', 1, 1
);

-- 插入普通用户账号
INSERT INTO user_account (
    meta_user_id, username, password, nickname, email, phone, avatar, gender, status
) VALUES (
    2, 'user', '0192023a7bbd73250516f069df18b500', '普通用户', 'user@hong.com', '13800138001', 'default_user_avatar.png', 1, 1
);

-- 插入测试账号（用于开发测试）
INSERT INTO user_account (
    meta_user_id, username, password, nickname, email, phone, avatar, gender, status
) VALUES (
    3, 'test', '0192023a7bbd73250516f069df18b500', '测试用户', 'test@hong.com', '13800138002', 'default_test_avatar.png', 1, 1
);