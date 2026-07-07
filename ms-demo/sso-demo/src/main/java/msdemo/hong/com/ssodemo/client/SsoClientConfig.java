package msdemo.hong.com.ssodemo.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SSO Client配置类
 * 
 * <p>负责配置SSO客户端相关的Bean和拦截器：
 * <ul>
 *   <li>注册RestTemplate用于调用SSO Server接口</li>
 *   <li>注册SSO认证拦截器</li>
 *   <li>配置拦截器的拦截路径和白名单</li>
 * </ul>
 * </p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SsoClientConfig implements WebMvcConfigurer {

    /**
     * SSO认证拦截器
     */
    private final SsoAuthInterceptor ssoAuthInterceptor;

    /**
     * SSO配置属性
     */
    private final SsoProperties ssoProperties;

    /**
     * 注册RestTemplate Bean
     * 
     * <p>用于客户端系统调用SSO Server的验证接口。</p>
     * 
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        log.info("注册RestTemplate Bean");
        return new RestTemplate();
    }

    /**
     * 注册拦截器
     * 
     * <p>配置SSO认证拦截器的拦截路径和白名单。</p>
     * 
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册SSO认证拦截器");
        
        registry.addInterceptor(ssoAuthInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除白名单路径
                .excludePathPatterns(ssoProperties.getClient().getIgnorePaths());
        
        log.info("SSO认证拦截器注册完成，排除路径: {}", 
                java.util.Arrays.toString(ssoProperties.getClient().getIgnorePaths()));
    }

}