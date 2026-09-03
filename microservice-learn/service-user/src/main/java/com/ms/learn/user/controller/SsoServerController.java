package com.ms.learn.user.controller;

import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.util.SaResult;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.user.dto.LoginRequest;
import com.ms.learn.user.dto.LoginResponse;
import com.ms.learn.user.service.AuthService;
import com.ms.learn.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SsoServerController {

    private final SaSsoServerTemplate ssoServerTemplate;
    private final AuthService authService;
    private final UserService userService;

    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoServerProcessor.instance.dister();
    }

    @PostConstruct
    public void configureSso() {
        ssoServerTemplate.strategy.notLoginView = () ->
                SaResult.code(401).setMsg("SSO 认证中心未登录");

        ssoServerTemplate.strategy.doLoginHandle = (username, password) -> {
            LoginRequest request = new LoginRequest();
            request.setUsername(username);
            request.setPassword(password);
            try {
                LoginResponse response = authService.login(request);
                return SaResult.ok("登录成功").setData(response);
            } catch (BizException exception) {
                return SaResult.error(exception.getMessage()).setCode(exception.getCode());
            }
        };

        ssoServerTemplate.messageHolder.addHandle("userinfo", (template, message) -> {
            Long userId = Long.valueOf(String.valueOf(message.get("loginId")));
            return SaResult.ok().setData(userService.getById(userId));
        });
    }
}
