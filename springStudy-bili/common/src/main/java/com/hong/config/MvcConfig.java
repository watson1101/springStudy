package com.hong.config;

import com.hong.interceptors.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 光在业务模块引入common包还不够，还需要配置类被扫描到配置嘞才能生效，尤其不同的模块，对应的包名可能还不一致，所以还需要定一个文件记录这些配置类 <br/>
 * 记录文件： resources/META-INF/spring.factories  <br/>
 * 注意： gateway也引入了common模块， 而gateway模块是基于webflux的，不包含springmvc，gateway启动时会提示file not found ,xxx/WebMvcConfigurer.class，
 * 因为 WebMvcConfigurer 依赖springmvc，所以需要加一个条件判断，避免在 webflux 环境下加载，判断 springmvc的核心类DispatcherServlet是否存在
 *
 */
@Configuration
@ConditionalOnClass(DispatcherServlet.class)
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 默认拦截所有路径，如果需要拦截特定路径，还需要在后面添加路径  .addPathPatterns("/**")
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
