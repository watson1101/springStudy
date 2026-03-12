package com.hong.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.user.entity.User;

public interface UserMapper extends BaseMapper<User> {
    User selectByUsername(String username);
}