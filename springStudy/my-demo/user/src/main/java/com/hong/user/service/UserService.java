package com.hong.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hong.user.entity.User;
import java.util.List;

public interface UserService extends IService<User> {
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

    /**
     * 获取用户列表
     */
    List<User> getUserList();

    /**
     * 新增用户
     */
    boolean addUser(User user);

    /**
     * 删除用户（逻辑删除）
     */
    boolean deleteUser(Long id);

    /**
     * 禁用用户
     */
    boolean disableUser(Long id);

    /**
     * 启用用户
     */
    boolean enableUser(Long id);

    /**
     * 修改用户
     */
    boolean updateUser(User user);
}