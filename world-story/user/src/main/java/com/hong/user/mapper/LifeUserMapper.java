package com.hong.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.user.entity.LifeUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生命用户Mapper接口
 */
@Mapper
public interface LifeUserMapper extends BaseMapper<LifeUser> {
}