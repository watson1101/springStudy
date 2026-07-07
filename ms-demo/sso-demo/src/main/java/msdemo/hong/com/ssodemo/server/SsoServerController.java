package msdemo.hong.com.ssodemo.server;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.Result;
import msdemo.hong.com.ssodemo.common.SsoConstants;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import msdemo.hong.com.ssodemo.common.SsoUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * SSO Server控制器
 * 
 * <p>负责处理SSO身份提供者（IdP）的HTTP请求，包括：
 * <ul>
 *   <li>登录页面展示</li>
 *   <li>用户认证与令牌签发</li>
 *   <li>令牌验证接口</li>
 *   <li>登出接口</li>
 * </ul>
 * </p>
 * 
 * <p>SSO登录流程：
 * <pre>
 * 1. 客户端系统（SP）拦截未认证请求
 * 2. 客户端重定向到SSO Server登录页面
 * 3. 用户输入用户名密码
 * 4. SSO Server验证凭据并签发JWT令牌
 * 5. SSO Server重定向回客户端回调地址，携带令牌
 * 6. 客户端验证令牌并创建本地会话
 * </pre>
 * </p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "SSO Server", description = "单点登录服务端接口 - 身份提供者(IdP)")
public class SsoServerController {

    /**
     * SSO Server服务层
     */
    private final SsoServerService ssoServerService;

    /**
     * SSO配置属性
     */
    private final SsoProperties ssoProperties;

    /**
     * 显示SSO登录页面
     * 
     * <p>当客户端系统（SP）用户未登录时，会重定向到此页面。
     * 登录页面需要接收redirectUri参数，登录成功后重定向回该地址。</p>
     * 
     * @param redirectUri 登录成功后的回调地址
     * @param clientId 客户端ID
     * @param model Spring MVC模型
     * @return 登录页面视图名称
     */
    @GetMapping("/sso/login")
    @Operation(summary = "显示登录页面", description = "SSO登录页面，接收redirectUri和clientId参数")
    public String showLoginPage(
            @Parameter(description = "登录成功后重定向的回调地址") 
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @Parameter(description = "客户端系统ID") 
            @RequestParam(value = "clientId", required = false) String clientId,
            Model model) {
        
        log.info("访问SSO登录页面: redirectUri={}, clientId={}", redirectUri, clientId);
        
        // 将参数传递给前端页面
        model.addAttribute("redirectUri", redirectUri);
        model.addAttribute("clientId", clientId);
        
        return "login";
    }

    /**
     * 处理用户登录请求（表单提交）
     * 
     * <p>验证用户名密码，签发JWT令牌，并重定向回客户端回调地址。</p>
     * 
     * @param username 用户名
     * @param password 密码
     * @param redirectUri 登录成功后的回调地址
     * @param clientId 客户端ID
     * @param response HttpServletResponse用于重定向
     * @return 如果验证失败返回登录页面，成功则重定向
     */
    @PostMapping("/sso/login")
    @Operation(summary = "处理登录请求", description = "验证用户名密码，签发令牌并跳转回客户端")
    public String handleLogin(
            @Parameter(description = "用户名") 
            @RequestParam String username,
            @Parameter(description = "密码") 
            @RequestParam String password,
            @Parameter(description = "登录成功后重定向的回调地址") 
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            @Parameter(description = "客户端系统ID") 
            @RequestParam(value = "clientId", required = false) String clientId,
            HttpServletResponse response) {
        
        log.info("处理登录请求: username={}, redirectUri={}", username, redirectUri);
        
        // 1. 验证用户凭据并签发令牌
        Result<String> result = ssoServerService.issueToken(username, password);
        
        if (!result.isSuccess()) {
            // 登录失败，返回登录页面并显示错误信息
            log.warn("登录失败: {}", result.getMessage());
            return "redirect:/sso/login?redirectUri=" + encodeUrl(redirectUri) 
                    + "&clientId=" + encodeUrl(clientId)
                    + "&error=" + encodeUrl(result.getMessage());
        }
        
        // 2. 登录成功，重定向回客户端回调地址
        String token = result.getData();
        String callbackUrl = buildCallbackUrl(redirectUri, token);
        
        log.info("登录成功，重定向到: {}", callbackUrl);
        
        try {
            response.sendRedirect(callbackUrl);
        } catch (IOException e) {
            log.error("重定向失败: {}", e.getMessage());
            return "redirect:/sso/login?error=" + encodeUrl("重定向失败");
        }
        
        return null;
    }

    /**
     * 处理用户登录请求（JSON API）
     * 
     * <p>提供RESTful API方式的登录接口，返回JSON格式结果。</p>
     * 
     * @param username 用户名
     * @param password 密码
     * @return 包含令牌的JSON结果
     */
    @PostMapping("/sso/api/login")
    @ResponseBody
    @Operation(summary = "登录API（JSON）", description = "RESTful登录接口，返回JWT令牌")
    public Result<String> apiLogin(
            @Parameter(description = "用户名") 
            @RequestParam String username,
            @Parameter(description = "密码") 
            @RequestParam String password) {
        
        log.info("API登录请求: username={}", username);
        return ssoServerService.issueToken(username, password);
    }

    /**
     * 验证SSO令牌
     * 
     * <p>供客户端系统（SP）调用，验证令牌的有效性并获取用户信息。</p>
     * 
     * @param token JWT令牌
     * @return 包含用户信息的验证结果
     */
    @GetMapping("/sso/validate")
    @ResponseBody
    @Operation(summary = "验证令牌", description = "验证JWT令牌有效性，返回用户信息")
    public Result<SsoUserDTO> validateToken(
            @Parameter(description = "JWT令牌") 
            @RequestParam("token") String token) {
        
        log.info("验证令牌请求: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        return ssoServerService.validateToken(token);
    }

    /**
     * 获取当前登录用户信息
     * 
     * <p>通过请求中的令牌获取用户信息。</p>
     * 
     * @param token JWT令牌
     * @return 用户信息
     */
    @GetMapping("/sso/userinfo")
    @ResponseBody
    @Operation(summary = "获取用户信息", description = "通过令牌获取当前登录用户信息")
    public Result<SsoUserDTO> getUserInfo(
            @Parameter(description = "JWT令牌") 
            @RequestParam("token") String token) {
        
        log.info("获取用户信息请求: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        return ssoServerService.getUserInfo(token);
    }

    /**
     * 处理登出请求
     * 
     * <p>使令牌失效，并清除会话。</p>
     * 
     * @param token JWT令牌
     * @param redirectUri 登出后重定向的地址
     * @param response HttpServletResponse用于重定向
     * @return 重定向结果
     */
    @GetMapping("/sso/logout")
    @Operation(summary = "登出", description = "使令牌失效并清除会话")
    public String logout(
            @Parameter(description = "JWT令牌") 
            @RequestParam(value = "token", required = false) String token,
            @Parameter(description = "登出后重定向地址") 
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            HttpServletResponse response) {
        
        log.info("处理登出请求: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        
        // 使令牌失效
        if (token != null && !token.isEmpty()) {
            ssoServerService.invalidateToken(token);
        }
        
        // 重定向到指定地址或登录页面
        String targetUrl = (redirectUri != null && !redirectUri.isEmpty()) 
                ? redirectUri 
                : "/sso/login";
        
        try {
            response.sendRedirect(targetUrl);
        } catch (IOException e) {
            log.error("登出重定向失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 构建回调URL
     * 
     * <p>将令牌附加到回调地址上，供客户端系统接收。</p>
     * 
     * @param redirectUri 原始回调地址
     * @param token JWT令牌
     * @return 包含令牌的完整回调URL
     */
    private String buildCallbackUrl(String redirectUri, String token) {
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = ssoProperties.getClient().getCallbackUrl();
        }
        
        // 判断是否需要添加参数分隔符
        String separator = redirectUri.contains("?") ? "&" : "?";
        
        return redirectUri + separator + "token=" + encodeUrl(token);
    }

    /**
     * URL编码
     * 
     * @param url URL字符串
     * @return 编码后的URL
     */
    private String encodeUrl(String url) {
        if (url == null) {
            return "";
        }
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

}