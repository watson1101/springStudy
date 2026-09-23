package com.ms.learn.user.service.impl;

import com.ms.learn.common.exception.BizException;
import com.ms.learn.user.dto.UserProfile;
import com.ms.learn.user.entity.User;
import com.ms.learn.user.mapper.UserMapper;
import com.ms.learn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<UserProfile> listAll() {
        return userMapper.selectList(null).stream()
                .map(this::toProfile)
                .toList();
    }

    @Override
    public UserProfile getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在: " + id);
        }
        return toProfile(user);
    }

    @Override
    public boolean exists(Long id) {
        return userMapper.selectById(id) != null;
    }

    private UserProfile toProfile(User user) {
        return new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getEnabled(),
                user.getCreateTime(),
                userMapper.selectRoleCodesByUserId(user.getId()),
                userMapper.selectPermissionCodesByUserId(user.getId()));
    }
}
