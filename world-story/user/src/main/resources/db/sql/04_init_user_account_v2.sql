-- 更新初始化用户账号数据（使用加盐MD5加密）
-- 注意：密码使用加盐MD5加密存储
-- 例如：admin123 加盐 world-story-salt-2024 的MD5值为：
-- MD5("admin123world-story-salt-2024") = 7c9b7d9d33b4a8748f760b17423c723e

-- 插入管理员账号
INSERT INTO user_account (
    meta_user_id, username, password, nickname, email, phone, avatar, gender, status
) VALUES (
    1, 'admin', '7c9b7d9d33b4a8748f760b17423c723e', '管理员', 'admin@hong.com', '13800138000', 'default_admin_avatar.png', 1, 1
);

-- 插入普通用户账号
INSERT INTO user_account (
    meta_user_id, username, password, nickname, email, phone, avatar, gender, status
) VALUES (
    2, 'user', '7c9b7d9d33b4a8748f760b17423c723e', '普通用户', 'user@hong.com', '13800138001', 'default_user_avatar.png', 1, 1
);

-- 插入测试账号（用于开发测试）
INSERT INTO user_account (
    meta_user_id, username, password, nickname, email, phone, avatar, gender, status
) VALUES (
    3, 'test', '7c9b7d9d33b4a8748f760b17423c723e', '测试用户', 'test@hong.com', '13800138002', 'default_test_avatar.png', 1, 1
);