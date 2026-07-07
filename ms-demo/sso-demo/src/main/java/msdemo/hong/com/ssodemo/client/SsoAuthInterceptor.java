package msdemo.hong.com.ssodemo.client;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.Result;
import msdemo.hong.com.ssodemo.common.SsoConstants;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import msdemo.hong.com.ssodemo.common.SsoUserDTO;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * SSO认证拦截器
 * 
 * <p>负责拦截客户端系统的HTTP请求，检查用户是否已登录。
 * 如果未登录，则重定向到SSO Server的登录页面。</p>
 * 
 * <p>拦截流程：
 * <pre>
 * 1. 请求到达
 * 2. 检查是否在白名单中（如登录页面、回调接口等）
 * 3. 如果在白名单中，直接放行
 * 4. 如果不在白名单中，检查Session中是否有登录用户
 * 5. 如果有登录用户，放行
 * 6. 如果没有登录用户，重定向到SSO Server登录页面
 * </pre>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SsoAuthInterceptor implements HandlerInterceptor {

    /**
     * SSO Client服务层
     */
    private final SsoClientService ssoClientService;

    /**
     * SSO配置属性
     */
    private final SsoProperties ssoProperties;

    /**
     * 拦截请求前处理
     * 
     * <p>检查请求是否需要认证，如果需要但用户未登录，则重定向到SSO登录页面。</p>
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler 处理器
     * @return true表示放行，false表示拦截
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        
        log.info("拦截请求: method={}, uri={}", requestMethod, requestUri);
        
        // 1. 检查是否为OPTIONS请求（CORS预检请求），直接放行
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(requestMethod)) {
            log.debug("OPTIONS请求，直接放行");
            return true;
        }
        
        // 2. 检查是否在白名单中
        if (isInWhitelist(requestUri)) {
            log.debug("请求路径在白名单中，放行: {}", requestUri);
            return true;
        }
        
        // 3. 检查Session中是否有登录用户
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute(SsoConstants.SESSION_USER_ATTR);
        
        if (userObj instanceof SsoUserDTO) {
            SsoUserDTO user = (SsoUserDTO) userObj;
            log.debug("用户已登录，放行: username={}, userId={}", user.getUsername(), user.getUserId());
            return true;
        }
        
        // 4. 用户未登录，重定向到SSO Server登录页面
        log.info("用户未登录，重定向到SSO登录页面: {}", requestUri);
        
        // 构建当前请求的完整URL作为回调地址
        String currentUrl = buildCurrentUrl(request);
        
        // 构建SSO登录URL
        String loginUrl = ssoClientService.buildLoginUrl(currentUrl, null);
        
        // 重定向到SSO登录页面
        response.sendRedirect(loginUrl);
        
        return false;
    }

    /**
     * 检查请求路径是否在白名单中
     * 
     * <p>白名单中的路径不需要认证，直接放行。</p>
     * 
     * @param requestUri 请求路径
     * @return true表示在白名单中
     */
    private boolean isInWhitelist(String requestUri) {
        String[] ignorePaths = ssoProperties.getClient().getIgnorePaths();
        
        if (ignorePaths == null || ignorePaths.length == 0) {
            return false;
        }
        
        List<String> whitelist = Arrays.asList(ignorePaths);
        
        // 精确匹配或前缀匹配
        for (String path : whitelist) {
            if (path.contains("*")) {
                // 前缀匹配（如 /swagger-ui/**）
                String prefix = path.replace("**", "");
                if (requestUri.startsWith(prefix)) {
                    return true;
                }
            } else {
                // 精确匹配
                if (requestUri.equals(path)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 构建当前请求的完整URL
     * 
     * <p>用于作为SSO登录成功后的回调地址。</p>
     * 
     * @param request HttpServletRequest
     * @return 完整的请求URL
     */
    private String buildCurrentUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        
        // 获取协议（http或https）
        String scheme = request.getScheme();
        url.append(scheme).append("://");
        
        // 获取服务器名称
        String serverName = request.getServerName();
        url.append(serverName);
        
        // 获取端口（如果不是默认端口）
        int serverPort = request.getServerPort();
        if (!(("http".equals(scheme) && serverPort == 80) 
                || ("https".equals(scheme) && serverPort == 443))) {
            url.append(":").append(serverPort);
        }
        
        // 获取上下文路径
        String contextPath = request.getContextPath();
        if (StrUtil.isNotBlank(contextPath)) {
            url.append(contextPath);
        }
        
        // 获取请求路径
        String requestUri = request.getRequestURI();
        url.append(requestUri);
        
        // 获取查询参数
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            url.append("?").append(queryString);
        }
        
        return url.toString();
    }

}