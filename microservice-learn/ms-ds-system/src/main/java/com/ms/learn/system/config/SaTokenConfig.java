package com.ms.learn.system.config;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (isPublicReadRequest(request)) {
                    return true;
                }
                StpUtil.checkLogin();
                return true;
            }
        }).addPathPatterns("/api/**");
    }

    private boolean isPublicReadRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String uri = request.getRequestURI();
        return "/api/system/health".equals(uri)
                || uri.matches("/api/system/dict/type/[^/]+")
                || uri.matches("/api/system/dict/tree/[^/]+")
                || uri.matches("/api/system/dict/item/[^/]+");
    }
}
