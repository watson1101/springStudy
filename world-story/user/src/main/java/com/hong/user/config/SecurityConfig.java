package com.hong.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 安全配置类
 * 用于读取安全相关的配置信息
 */
@Configuration
public class SecurityConfig {
    
    /**
     * 密码加盐值
     */
    @Value("${user.security.salt}")
    private String salt;

    public String getSalt() {
        return salt;
    }
}