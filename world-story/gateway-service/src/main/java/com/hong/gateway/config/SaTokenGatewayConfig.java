package com.hong.gateway.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 网关SA-Token配置类
 * 用于配置网关的全局拦截器和Redis存储
 */
@Configuration
public class SaTokenGatewayConfig {

    /**
     * 配置Sa-Token的Redis存储
     * @param redisTemplate Redis操作模板
     * @return SaTokenDao实例
     */
    @Bean
    public SaTokenDao saTokenDao(RedisTemplate<String, Object> redisTemplate) {
        SaTokenDaoRedisJackson saTokenDaoRedisJackson = new SaTokenDaoRedisJackson();
        saTokenDaoRedisJackson.setRedisTemplate(redisTemplate);
        return saTokenDaoRedisJackson;
    }

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
        // token临时有效期（指定时间内无操作就视为token过期）
        config.setActiveTimeout(-1);
        // 是否允许同一账号多处登录
        config.setIsConcurrent(false);
        // 同一账号最大登录数量
        config.setMaxLoginCount(1);
        // 是否在账号被顶下线时发送消息给被顶下线的用户
        config.setIsSendConcurrentMessage(false);
        // token风格
        config.setTokenStyle("uuid");
        // token前缀
        config.setTokenPrefix("satoken:");
        return config;
    }

    /**
     * 配置全局拦截器
     * @return SaReactorFilter实例
     */
    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                // 拦截规则：除了登录接口、注册接口等公开接口外，其他所有请求都需要登录
                .setIncludeList("/**")
                // 排除规则：不需要登录的接口
                .setExcludeList(
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/getUserInfo",
                        "/api/user/username/**",
                        "/api/user/\{id\}",
                        "/api/role/**",
                        "/meta-users/**",
                        "/life-users/**"
                )
                // 认证处理
                .setAuth(obj -> {
                    // 获取当前请求路径
                    String path = SaRouter.getRequest().getUrl();
                    // 打印请求信息，方便调试
                    System.out.println("请求路径: " + path);
                    // 检查是否需要登录
                    SaRouter.match("/**", r -> {
                        // 检查是否已登录
                        StpUtil.checkLogin();
                    });
                })
                // 未登录处理
                .setError(e -> {
                    // 未登录时返回401状态码
                    return "未登录，请先登录";
                });
    }
}