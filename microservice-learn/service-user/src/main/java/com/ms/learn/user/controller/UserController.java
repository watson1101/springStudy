package com.ms.learn.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ms.learn.common.result.Result;
import com.ms.learn.user.dto.LoginRequest;
import com.ms.learn.user.dto.LoginResponse;
import com.ms.learn.user.dto.RegisterRequest;
import com.ms.learn.user.dto.UserProfile;
import com.ms.learn.user.service.AuthService;
import com.ms.learn.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务 REST 接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/list")
    @SaCheckPermission("user:list")
    public Result<List<UserProfile>> list() {
        return Result.success(userService.listAll());
    }

    @GetMapping("/{id}")
    @SaCheckPermission("user:read")
    public Result<UserProfile> get(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/{id}/exists")
    @SaCheckLogin
    public Result<Boolean> exists(@PathVariable Long id) {
        return Result.success(userService.exists(id));
    }

    @PostMapping
    @SaCheckPermission("user:create")
    public Result<UserProfile> create(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/auth/register")
    public Result<UserProfile> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success("注册成功", authService.register(request));
    }

    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success("登录成功", authService.login(request));
    }

    @PostMapping("/auth/logout")
    @SaCheckLogin
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/auth/me")
    @SaCheckLogin
    public Result<UserProfile> me() {
        return Result.success(authService.currentUser());
    }
}
