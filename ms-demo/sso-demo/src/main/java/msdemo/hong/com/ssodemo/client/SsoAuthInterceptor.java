package msdemo.hong.com.ssodemo.client;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.Result;
import msdemo.hong.com.ssodemo.common.SsoConstants;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import msdemo.hong.com.ssodemo.common.SsoUserDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class SsoAuthInterceptor implements HandlerInterceptor {

    private final SsoClientService ssoClientService;
    private final SsoProperties ssoProperties;

    public SsoAuthInterceptor(@Lazy SsoClientService ssoClientService, SsoProperties ssoProperties) {
        this.ssoClientService = ssoClientService;
        this.ssoProperties = ssoProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        log.info("拦截请求: method={}, uri={}", requestMethod, requestUri);

        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(requestMethod)) {
            log.debug("OPTIONS请求，直接放行");
            return true;
        }

        if (isInWhitelist(requestUri)) {
            log.debug("请求路径在白名单中，放行: {}", requestUri);
            return true;
        }

        HttpSession session = request.getSession();
        Object userObj = session.getAttribute(SsoConstants.SESSION_USER_ATTR);

        if (userObj instanceof SsoUserDTO) {
            SsoUserDTO user = (SsoUserDTO) userObj;
            log.debug("用户已登录，放行: username={}, userId={}", user.getUsername(), user.getUserId());
            return true;
        }

        log.info("用户未登录，重定向到SSO登录页面: {}", requestUri);

        String currentUrl = buildCurrentUrl(request);
        String loginUrl = ssoClientService.buildLoginUrl(currentUrl, null);
        response.sendRedirect(loginUrl);
        return false;
    }

    private boolean isInWhitelist(String requestUri) {
        String[] ignorePaths = ssoProperties.getClient().getIgnorePaths();
        if (ignorePaths == null || ignorePaths.length == 0) {
            return false;
        }
        List<String> whitelist = Arrays.asList(ignorePaths);
        for (String path : whitelist) {
            if (path.contains("*")) {
                String prefix = path.replace("**", "");
                if (requestUri.startsWith(prefix)) {
                    return true;
                }
            } else {
                if (requestUri.equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String buildCurrentUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        String scheme = request.getScheme();
        url.append(scheme).append("://");
        String serverName = request.getServerName();
        url.append(serverName);
        int serverPort = request.getServerPort();
        if (!(("http".equals(scheme) && serverPort == 80)
                || ("https".equals(scheme) && serverPort == 443))) {
            url.append(":").append(serverPort);
        }
        String contextPath = request.getContextPath();
        if (StrUtil.isNotBlank(contextPath)) {
            url.append(contextPath);
        }
        String requestUri = request.getRequestURI();
        url.append(requestUri);
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }
}