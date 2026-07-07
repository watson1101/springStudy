package msdemo.hong.com.ssodemo.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.Result;
import msdemo.hong.com.ssodemo.common.SsoConstants;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import msdemo.hong.com.ssodemo.common.SsoUserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * SSO Client服务层
 * 
 * <p>负责处理SSO服务提供者（SP）的核心业务逻辑，包括：
 * <ul>
 *   <li>与SSO Server通信验证令牌</li>
 *   <li>管理本地会话</li>
 *   <li>构建SSO登录URL</li>
 *   <li>处理回调令牌</li>
 * </ul>
 * </p>
 * 
 * <p>在微服务架构中，每个需要接入SSO的模块（如user-service、order-service）
 * 都需要实现类似的客户端逻辑。本模块模拟了其他模块通过SSO登录当前系统的流程。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SsoClientService {

    /**
     * SSO配置属性
     */
    private final SsoProperties ssoProperties;

    /**
     * RestTemplate用于调用SSO Server接口
     */
    private final RestTemplate restTemplate;

    /**
     * 构建SSO登录URL
     * 
     * <p>当用户未登录时，客户端系统需要重定向到SSO Server的登录页面。
     * 此方法构建完整的登录URL，包含回调地址和客户端ID。</p>
     * 
     * @param redirectUri 登录成功后回调的地址
     * @param clientId 客户端ID
     * @return 完整的SSO登录URL
     */
    public String buildLoginUrl(String redirectUri, String clientId) {
        log.info("构建SSO登录URL: redirectUri={}, clientId={}", redirectUri, clientId);
        
        String serverUrl = ssoProperties.getClient().getServerUrl();
        String loginPath = SsoConstants.DEFAULT_LOGIN_PATH;
        
        // 使用配置的回调地址或传入的回调地址
        String callback = (redirectUri != null && !redirectUri.isEmpty()) 
                ? redirectUri 
                : ssoProperties.getClient().getCallbackUrl();
        
        // 使用配置的客户端ID或传入的客户端ID
        String cid = (clientId != null && !clientId.isEmpty()) 
                ? clientId 
                : ssoProperties.getClient().getClientId();
        
        // 构建完整URL
        String loginUrl = String.format("%s%s?redirectUri=%s&clientId=%s",
                serverUrl, loginPath, callback, cid);
        
        log.info("构建的SSO登录URL: {}", loginUrl);
        return loginUrl;
    }

    /**
     * 验证令牌（调用SSO Server的验证接口）
     * 
     * <p>客户端系统通过调用SSO Server的验证接口，验证令牌的有效性。
     * 这是SSO的核心验证机制，确保令牌由合法的SSO Server签发。</p>
     * 
     * @param token JWT令牌
     * @return 验证结果，成功返回用户信息
     */
    public Result<SsoUserDTO> validateToken(String token) {
        log.info("客户端验证令牌: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        
        if (token == null || token.isEmpty()) {
            return Result.unauthorized("令牌不能为空");
        }
        
        try {
            // 构建SSO Server验证接口URL
            String validateUrl = String.format("%s%s?token=%s",
                    ssoProperties.getClient().getServerUrl(),
                    SsoConstants.DEFAULT_VALIDATE_PATH,
                    token);
            
            // 调用SSO Server验证接口
            ResponseEntity<Result> response = restTemplate.exchange(
                    validateUrl,
                    HttpMethod.GET,
                    null,
                    Result.class
            );
            
            Result<SsoUserDTO> result = response.getBody();
            
            if (result == null) {
                return Result.error("SSO Server响应为空");
            }
            
            if (result.isSuccess()) {
                log.info("令牌验证成功: userId={}", 
                        ((SsoUserDTO) result.getData()).getUserId());
            } else {
                log.warn("令牌验证失败: {}", result.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("调用SSO Server验证接口失败: {}", e.getMessage());
            return Result.error("验证令牌失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息（通过令牌）
     * 
     * @param token JWT令牌
     * @return 用户信息
     */
    public Result<SsoUserDTO> getUserInfo(String token) {
        log.info("客户端获取用户信息: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        return validateToken(token);
    }

    /**
     * 构建登出URL
     * 
     * <p>当用户登出时，客户端系统需要调用SSO Server的登出接口，
     * 使令牌失效，并清除本地会话。</p>
     * 
     * @param token JWT令牌
     * @param redirectUri 登出后重定向的地址
     * @return 完整的SSO登出URL
     */
    public String buildLogoutUrl(String token, String redirectUri) {
        log.info("构建SSO登出URL: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        
        String serverUrl = ssoProperties.getClient().getServerUrl();
        String logoutPath = SsoConstants.DEFAULT_LOGOUT_PATH;
        
        StringBuilder logoutUrl = new StringBuilder();
        logoutUrl.append(serverUrl).append(logoutPath);
        logoutUrl.append("?token=").append(token);
        
        if (redirectUri != null && !redirectUri.isEmpty()) {
            logoutUrl.append("&redirectUri=").append(redirectUri);
        }
        
        log.info("构建的SSO登出URL: {}", logoutUrl);
        return logoutUrl.toString();
    }

    /**
     * 模拟其他模块接入SSO登录
     * 
     * <p>此方法模拟其他微服务模块（如user-service、order-service）
     * 通过SSO接入当前系统的流程。</p>
     * 
     * @param systemName 系统/模块名称
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public Result<String> simulateOtherSystemLogin(String systemName, String username, String password) {
        log.info("模拟其他模块登录: systemName={}, username={}", systemName, username);
        
        // 构建模拟登录的回调地址
        String callbackUrl = String.format("http://localhost:8006/sso/callback?system=%s", systemName);
        
        // 构建登录URL
        String loginUrl = buildLoginUrl(callbackUrl, systemName);
        
        log.info("{} 模块将重定向到SSO登录: {}", systemName, loginUrl);
        
        return Result.success(loginUrl, 
                String.format("%s 模块准备通过SSO登录，重定向地址: %s", systemName, loginUrl));
    }

    /**
     * 获取当前客户端配置信息
     * 
     * @return 客户端配置信息
     */
    public SsoProperties.Client getClientConfig() {
        return ssoProperties.getClient();
    }

}