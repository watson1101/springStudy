package com.hong.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper，拥有基本的CRUD方法
}