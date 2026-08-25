package com.ms.learn.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.user.entity.User;
import com.ms.learn.user.mapper.UserMapper;
import com.ms.learn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在: " + id);
        }
        return user;
    }

    @Override
    public User create(User user) {
        user.setId(null);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }
}
