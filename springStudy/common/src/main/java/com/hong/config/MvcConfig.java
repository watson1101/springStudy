package com.hong.config;

import com.hong.interceptors.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 默认拦截所有路径，如果需要拦截特定路径，还需要在后面添加路径  .addPathPatterns("/**")
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
