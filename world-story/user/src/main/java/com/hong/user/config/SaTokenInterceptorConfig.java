package com.hong.user.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SA-Token拦截器配置类
 * 配置WebMvc的拦截器，用于处理登录验证
 */
@Configuration
public class SaTokenInterceptorConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册SA-Token拦截器，并排除公开接口
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 拦截规则：除了登录接口、注册接口等公开接口外，其他所有请求都需要登录
            SaRouter.match("/**", "!/**/auth/**", "!/**/user/username/**", "!/**/user/\{id\}", r -> {
                // 检查是否已登录
                StpUtil.checkLogin();
            });
        })).addPathPatterns("/**");
    }
}