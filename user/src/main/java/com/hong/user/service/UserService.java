package com.hong.user.service;

import com.hong.user.entity.User;

public interface UserService {
    /**
     * 用户注册
     */
    boolean register(String username, String password);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
}