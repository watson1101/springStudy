package com.hong.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.hong.user.entity.User;
import com.hong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 获取用户列表（管理员功能）
     */
    @SaCheckLogin
    @GetMapping("/list")
    public List<User> getUserList() {
        return userService.getUserList();
    }

    /**
     * 新增用户（管理员功能）
     */
    @SaCheckLogin
    @PostMapping("/add")
    public boolean addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * 删除用户（管理员功能，逻辑删除）
     */
    @SaCheckLogin
    @DeleteMapping("/delete/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 禁用用户（管理员功能）
     */
    @SaCheckLogin
    @PutMapping("/disable/{id}")
    public boolean disableUser(@PathVariable Long id) {
        return userService.disableUser(id);
    }

    /**
     * 启用用户（管理员功能）
     */
    @SaCheckLogin
    @PutMapping("/enable/{id}")
    public boolean enableUser(@PathVariable Long id) {
        return userService.enableUser(id);
    }

    /**
     * 修改用户（管理员功能）
     */
    @SaCheckLogin
    @PutMapping("/update")
    public boolean updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}