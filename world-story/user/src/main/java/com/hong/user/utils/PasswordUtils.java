package com.hong.user.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 密码工具类
 * 提供密码加密和验证功能
 */
@Component
public class PasswordUtils {

    private static String salt;

    @Autowired
    private com.hong.user.config.SecurityConfig securityConfig;

    @PostConstruct
    public void init() {
        salt = securityConfig.getSalt();
    }

    /**
     * 生成加盐密码
     * @param password 原始密码
     * @return 加盐加密后的密码
     */
    public static String generatePassword(String password) {
        // 组合密码和盐值
        String saltedPassword = password + salt;
        // 使用MD5加密
        return DigestUtils.md5DigestAsHex(saltedPassword.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean validatePassword(String rawPassword, String encodedPassword) {
        // 生成加盐密码并与存储的密码比较
        return generatePassword(rawPassword).equals(encodedPassword);
    }

    /**
     * 生成随机密码
     * @return 随机密码
     */
    public static String generateRandomPassword() {
        // 生成8位随机密码
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid.substring(0, 8);
    }
}