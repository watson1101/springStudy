package com.ms.learn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
