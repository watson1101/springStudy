package com.hong.gateway.config;

import cn.dev33.satoken.config.SaTokenConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关SA-Token配置类
 * 用于配置网关的全局拦截器和Redis存储
 */
@Configuration
public class SaTokenGatewayConfig {

    /**
     * 配置Sa-Token的基本参数
     * @return SaTokenConfig实例
     */
    @Bean
    public SaTokenConfig getSaTokenConfig() {
        SaTokenConfig config = new SaTokenConfig();
        // token名称（同时也是cookie名称）
        config.setTokenName("satoken");
        // token有效期，单位秒，默认30天
        config.setTimeout(60 * 60 * 24 * 30);
        // 是否允许同一账号多处登录
        config.setIsConcurrent(false);
        // 同一账号最大登录数量
        config.setMaxLoginCount(1);
        // token风格
        config.setTokenStyle("uuid");
        // token前缀
        config.setTokenPrefix("satoken:");
        return config;
    }
}