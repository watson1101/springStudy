package msdemo.hong.com.ssodemo.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.Result;
import msdemo.hong.com.ssodemo.common.SsoConstants;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import msdemo.hong.com.ssodemo.common.SsoUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * SSO Client控制器
 * 
 * <p>负责处理SSO服务提供者（SP）的HTTP请求，包括：
 * <ul>
 *   <li>SSO回调处理（接收SSO Server返回的令牌）</li>
 *   <li>受保护资源访问（需要登录才能访问）</li>
 *   <li>本地登出</li>
 *   <li>模拟其他模块登录演示</li>
 * </ul>
 * </p>
 * 
 * <p>SSO回调流程：
 * <pre>
 * 1. 用户在SSO Server登录成功
 * 2. SSO Server重定向到客户端回调地址，携带令牌
 * 3. 客户端验证令牌有效性
 * 4. 验证成功后，创建本地会话
 * 5. 重定向到用户最初访问的页面
 * </pre>
 * </p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "SSO Client", description = "单点登录客户端接口 - 服务提供者(SP)")
public class SsoClientController {

    /**
     * SSO Client服务层
     */
    private final SsoClientService ssoClientService;

    /**
     * SSO配置属性
     */
    private final SsoProperties ssoProperties;

    /**
     * SSO回调接口
     * 
     * <p>SSO Server登录成功后，会重定向到此接口，携带JWT令牌。
     * 此接口负责：
     * 1. 接收令牌参数
     * 2. 调用SSO Server验证令牌
     * 3. 验证成功后创建本地会话
     * 4. 重定向到用户最初访问的页面</p>
     * 
     * @param token SSO Server返回的JWT令牌
     * @param system 来源系统标识（用于模拟多系统登录）
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 重定向到受保护页面或返回错误信息
     */
    @GetMapping("/sso/callback")
    @Operation(summary = "SSO回调", description = "接收SSO Server返回的令牌，验证并创建本地会话")
    public String handleCallback(
            @Parameter(description = "SSO Server返回的JWT令牌") 
            @RequestParam("token") String token,
            @Parameter(description = "来源系统标识") 
            @RequestParam(value = "system", required = false) String system,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.info("处理SSO回调: token={}, system={}", 
                token != null ? token.substring(0, 20) + "..." : "null", system);
        
        // 1. 验证令牌
        Result<SsoUserDTO> validateResult = ssoClientService.validateToken(token);
        
        if (!validateResult.isSuccess()) {
            log.warn("令牌验证失败: {}", validateResult.getMessage());
            return "redirect:/sso/login?error=" + validateResult.getMessage();
        }
        
        // 2. 获取用户信息
        SsoUserDTO user = validateResult.getData();
        
        // 3. 创建本地会话
        HttpSession session = request.getSession();
        session.setAttribute(SsoConstants.SESSION_USER_ATTR, user);
        session.setAttribute(SsoConstants.SESSION_TOKEN_ATTR, token);
        
        log.info("创建本地会话成功: username={}, userId={}, system={}", 
                user.getUsername(), user.getUserId(), system);
        
        // 4. 重定向到用户最初访问的页面或首页
        String redirectUrl = "/";
        
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.error("重定向失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 首页（受保护资源）
     * 
     * <p>需要登录才能访问的页面，展示当前登录用户信息。</p>
     * 
     * @param request HttpServletRequest
     * @param model Spring MVC模型
     * @return 首页视图
     */
    @GetMapping("/")
    @Operation(summary = "首页", description = "受保护资源，展示当前登录用户信息")
    public String home(HttpServletRequest request, Model model) {
        log.info("访问首页");
        
        // 获取当前登录用户
        HttpSession session = request.getSession();
        SsoUserDTO user = (SsoUserDTO) session.getAttribute(SsoConstants.SESSION_USER_ATTR);
        
        if (user == null) {
            return "redirect:/sso/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("token", session.getAttribute(SsoConstants.SESSION_TOKEN_ATTR));
        
        return "home";
    }

    /**
     * 获取当前登录用户信息（API）
     * 
     * <p>RESTful接口，返回当前登录用户信息。</p>
     * 
     * @param request HttpServletRequest
     * @return 用户信息
     */
    @GetMapping("/api/userinfo")
    @ResponseBody
    @Operation(summary = "获取当前用户信息", description = "RESTful接口，返回当前登录用户信息")
    public Result<SsoUserDTO> getCurrentUser(HttpServletRequest request) {
        log.info("获取当前用户信息");
        
        HttpSession session = request.getSession();
        SsoUserDTO user = (SsoUserDTO) session.getAttribute(SsoConstants.SESSION_USER_ATTR);
        
        if (user == null) {
            return Result.unauthorized("用户未登录");
        }
        
        return Result.success(user);
    }

    /**
     * 本地登出
     * 
     * <p>清除本地会话，并可选调用SSO Server登出接口使令牌失效。</p>
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 重定向到登录页面
     */
    @GetMapping("/logout")
    @Operation(summary = "本地登出", description = "清除本地会话")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("处理本地登出");
        
        HttpSession session = request.getSession();
        
        // 获取令牌（用于调用SSO Server登出接口）
        String token = (String) session.getAttribute(SsoConstants.SESSION_TOKEN_ATTR);
        
        // 清除本地会话
        session.invalidate();
        
        log.info("本地会话已清除");
        
        // 重定向到SSO登录页面
        try {
            response.sendRedirect("/sso/login");
        } catch (IOException e) {
            log.error("登出重定向失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 全局登出（同时清除SSO Server令牌）
     * 
     * <p>清除本地会话，并调用SSO Server的登出接口使令牌失效。</p>
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 重定向到SSO登录页面
     */
    @GetMapping("/global-logout")
    @Operation(summary = "全局登出", description = "清除本地会话并使SSO令牌失效")
    public String globalLogout(HttpServletRequest request, HttpServletResponse response) {
        log.info("处理全局登出");
        
        HttpSession session = request.getSession();
        
        // 获取令牌
        String token = (String) session.getAttribute(SsoConstants.SESSION_TOKEN_ATTR);
        
        // 清除本地会话
        session.invalidate();
        
        // 构建SSO登出URL
        String logoutUrl = ssoClientService.buildLogoutUrl(token, null);
        
        log.info("全局登出，重定向到SSO Server: {}", logoutUrl);
        
        try {
            response.sendRedirect(logoutUrl);
        } catch (IOException e) {
            log.error("全局登出重定向失败: {}", e.getMessage());
            response.sendRedirect("/sso/login");
        }
        
        return null;
    }

    /**
     * 模拟其他模块登录演示
     * 
     * <p>模拟user-service、order-service等模块通过SSO登录当前系统。</p>
     * 
     * @param system 系统名称（user-service、order-service等）
     * @return 登录结果
     */
    @GetMapping("/simulate-login")
    @ResponseBody
    @Operation(summary = "模拟其他模块登录", description = "模拟user-service、order-service等模块通过SSO登录")
    public Result<String> simulateOtherSystemLogin(
            @Parameter(description = "系统名称，如user-service、order-service") 
            @RequestParam(value = "system", defaultValue = "user-service") String system) {
        
        log.info("模拟其他模块登录: system={}", system);
        
        // 使用演示账号
        String username = SsoConstants.DEMO_USERNAME;
        String password = SsoConstants.DEMO_PASSWORD;
        
        return ssoClientService.simulateOtherSystemLogin(system, username, password);
    }

    /**
     * 获取客户端配置信息
     * 
     * @return 客户端配置
     */
    @GetMapping("/api/client-config")
    @ResponseBody
    @Operation(summary = "获取客户端配置", description = "获取当前SSO客户端配置信息")
    public Result<SsoProperties.Client> getClientConfig() {
        return Result.success(ssoClientService.getClientConfig());
    }

}