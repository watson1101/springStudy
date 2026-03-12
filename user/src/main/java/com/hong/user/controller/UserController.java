package com.hong.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.hong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 测试接口，允许未登录访问
     */
    @SaIgnore
    @GetMapping("/test")
    public String test() {
        return "nice to see you.";
    }

    /**
     * 用户注册
     */
    @SaIgnore
    @PostMapping("/register")
    public boolean register(@RequestParam String username, @RequestParam String password) {
        return userService.register(username, password);
    }

    /**
     * 用户登录
     */
    @SaIgnore
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password);
    }

    /**
     * 获取当前登录用户信息
     */
    @SaCheckLogin
    @GetMapping("/info")
    public String info() {
        return "当前登录用户：" + cn.dev33.satoken.stp.StpUtil.getLoginId();
    }
}