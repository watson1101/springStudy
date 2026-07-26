package com.hong.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hong.user.entity.User;
import com.hong.user.mapper.UserMapper;
import com.hong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean register(String username, String password) {
        // 验证用户名格式
        if (!username.matches("^[a-zA-Z0-9_]{1,32}$")) {
            return false;
        }

        // 验证密码格式
        if (password.length() < 8 || password.length() > 32) {
            return false;
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser != null) {
            return false;
        }

        // 生成盐值
        String salt = generateSalt();

        // 对密码进行加盐加密
        String encryptedPassword = encryptPassword(password, salt);

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setSalt(salt);
        user.setStatus(1); // 默认为启用状态
        user.setIsAdmin(0); // 默认为普通用户
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        return userMapper.insert(user) > 0;
    }

    @Override
    public String login(String username, String password) {
        // 根据用户名查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            return null;
        }

        // 验证密码
        String encryptedPassword = encryptPassword(password, user.getSalt());
        if (!encryptedPassword.equals(user.getPassword())) {
            return null;
        }

        // 生成 token
        StpUtil.login(user.getId());
        return StpUtil.getTokenValue();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> getUserList() {
        return userMapper.selectList(null);
    }

    @Override
    public boolean addUser(User user) {
        // 验证用户名格式
        if (!user.getUsername().matches("^[a-zA-Z0-9_]{1,32}$")) {
            return false;
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }

        // 生成盐值
        String salt = generateSalt();

        // 对密码进行加盐加密
        String encryptedPassword = encryptPassword(user.getPassword(), salt);

        // 设置用户信息
        user.setSalt(salt);
        user.setPassword(encryptedPassword);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        // 逻辑删除用户
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean disableUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setStatus(0); // 禁用
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean enableUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setStatus(1); // 启用
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        // 如果更新密码，需要重新加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String salt = generateSalt();
            String encryptedPassword = encryptPassword(user.getPassword(), salt);
            user.setSalt(salt);
            user.setPassword(encryptedPassword);
        }

        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    /**
     * 生成盐值
     */
    private String generateSalt() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            salt.append(chars.charAt(random.nextInt(chars.length())));
        }
        return salt.toString();
    }

    /**
     * 对密码进行加盐加密
     */
    private String encryptPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] bytes = md.digest(saltedPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}