package com.hong.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;

@Configuration
// 使配置类生效
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KeyPair keyPair(JwtProperties jwtProperties) {
        // 获取密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                jwtProperties.getLocation(), jwtProperties.getPassword().toCharArray());

        // 读取钥匙对
        return keyStoreKeyFactory.getKeyPair(jwtProperties.getAlias(),
                jwtProperties.getPassword().toCharArray());

    }


}
