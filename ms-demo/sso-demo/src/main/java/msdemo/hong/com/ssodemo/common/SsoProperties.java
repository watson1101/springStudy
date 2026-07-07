package msdemo.hong.com.ssodemo.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SSO单点登录配置属性类
 * 
 * <p>该类通过Spring Boot的配置属性机制，从application.yml中读取SSO相关配置，
 * 支持SSO Server和SSO Client的配置参数。</p>
 * 
 * <p>配置前缀：sso</p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sso")
public class SsoProperties {

    /**
     * SSO Server配置
     */
    private Server server = new Server();

    /**
     * SSO Client配置
     */
    private Client client = new Client();

    /**
     * SSO Server配置类
     * 
     * <p>定义SSO身份提供者（IdP）的配置参数</p>
     */
    @Data
    public static class Server {

        /**
         * SSO Server地址，如：http://localhost:8006
         */
        private String url = SsoConstants.DEFAULT_SERVER_URL;

        /**
         * JWT签名密钥，用于签发和验证令牌
         */
        private String jwtSecret = "sso-demo-jwt-secret-key-2026";

        /**
         * 令牌过期时间（秒），默认2小时
         */
        private long tokenExpireSeconds = SsoConstants.TOKEN_EXPIRE_SECONDS;

        /**
         * 是否启用令牌持久化到Redis
         */
        private boolean persistToken = true;
    }

    /**
     * SSO Client配置类
     * 
     * <p>定义SSO服务提供者（SP）的配置参数，模拟其他模块接入SSO</p>
     */
    @Data
    public static class Client {

        /**
         * 客户端ID，用于标识接入SSO的系统
         */
        private String clientId = "sso-demo-client";

        /**
         * 客户端密钥，用于客户端与服务端的认证
         */
        private String clientSecret = "sso-demo-client-secret";

        /**
         * SSO Server登录地址
         */
        private String serverUrl = SsoConstants.DEFAULT_SERVER_URL;

        /**
         * 客户端回调地址，SSO Server登录成功后重定向到此地址
         */
        private String callbackUrl = "http://localhost:8006/sso/callback";

        /**
         * 需要拦截保护的URL模式列表
         */
        private String[] protectedPaths = {"/**"};

        /**
         * 不需要拦截的URL模式列表（白名单）
         */
        private String[] ignorePaths = {
            "/sso/login",
            "/sso/callback",
            "/sso/validate",
            "/sso/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
        };
    }

}