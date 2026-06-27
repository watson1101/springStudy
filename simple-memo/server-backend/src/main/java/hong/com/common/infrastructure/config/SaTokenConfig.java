package hong.com.common.infrastructure.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证配置，配置路由拦截规则
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/user/reset-password",
                        "/api/user/send-code",
                        "/favicon.ico",
                        "/error"
                );
    }
}
