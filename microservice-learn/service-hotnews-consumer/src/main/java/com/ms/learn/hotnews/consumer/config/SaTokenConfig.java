package com.ms.learn.hotnews.consumer.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 鉴权配置。
 *
 * <p>除下列公开接口外，其余 /api/** 均需登录。</p>
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/api/**")
                // 热点资讯为公开数据：前端首页展示与定时推送均需匿名读取，
                // 因此查询类接口不要求登录。
                .excludePathPatterns(
                        "/api/hotnews/latest",
                        "/api/hotnews/batch/**"
                );
    }
}
