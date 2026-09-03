package com.ms.learn.user.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.user.dto.LoginRequest;
import com.ms.learn.user.dto.LoginResponse;
import com.ms.learn.user.dto.RegisterRequest;
import com.ms.learn.user.dto.UserProfile;
import com.ms.learn.user.entity.User;
import com.ms.learn.user.mapper.UserMapper;
import com.ms.learn.user.service.AuthService;
import com.ms.learn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "user";

    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserProfile register(RegisterRequest request) {
        String username = request.getUsername().trim();
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (count > 0) {
            throw new BizException(409, "用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(trimToNull(request.getNickname()));
        user.setEmail(trimToNull(request.getEmail()));
        user.setEnabled(true);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        Long roleId = userMapper.selectRoleIdByCode(DEFAULT_ROLE);
        if (roleId == null) {
            throw new BizException(500, "默认角色未初始化，请先执行用户认证数据库迁移");
        }
        userMapper.insertUserRole(user.getId(), roleId);
        return userService.getById(user.getId());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BizException(400, "用户名和密码不能为空");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername().trim())
                .last("LIMIT 1"));
        if (!passwordMatches(request.getPassword(), user)) {
            throw new BizException(401, "用户名或密码错误");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BizException(403, "用户已被禁用");
        }

        SaLoginParameter loginParameter = new SaLoginParameter();
        if (StringUtils.hasText(request.getDeviceId())) {
            loginParameter.setDeviceId(request.getDeviceId().trim());
        }
        StpUtil.login(user.getId(), loginParameter);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return new LoginResponse(
                tokenInfo.getTokenName(),
                tokenInfo.getTokenValue(),
                tokenInfo.getTokenTimeout(),
                userService.getById(user.getId()));
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserProfile currentUser() {
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());
        return userService.getById(userId);
    }

    private boolean passwordMatches(String rawPassword, User user) {
        if (user == null || !StringUtils.hasText(user.getPasswordHash())) {
            return false;
        }
        try {
            return passwordEncoder.matches(rawPassword, user.getPasswordHash());
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
