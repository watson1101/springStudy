package com.hong.user.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.hong.user.dto.LoginDTO;
import com.hong.user.dto.UserDTO;
import com.hong.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 * 处理用户登录、注销等操作
 */
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public SaResult login(@RequestBody LoginDTO loginDTO) {
        // 调用登录服务进行验证
        UserDTO userDTO = loginService.login(loginDTO);
        
        if (userDTO != null) {
            // 登录成功，获取token信息
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            
            // 构建返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("userInfo", userDTO);
            data.put("tokenInfo", tokenInfo);
            
            return SaResult.ok("登录成功").setData(data);
        } else {
            return SaResult.error("用户名或密码错误");
        }
    }

    /**
     * 用户注销
     * @return 注销结果
     */
    @PostMapping("/logout")
    public SaResult logout() {
        // 调用SA-Token的注销方法
        StpUtil.logout();
        return SaResult.ok("注销成功");
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @PostMapping("/getUserInfo")
    public SaResult getUserInfo() {
        if (StpUtil.isLogin()) {
            // 获取当前登录用户ID
            Object userId = StpUtil.getLoginId();
            // 根据用户ID获取用户信息
            UserDTO userDTO = loginService.getUserInfoByUserId(userId.toString());
            return SaResult.ok().setData(userDTO);
        } else {
            return SaResult.error("用户未登录");
        }
    }
}