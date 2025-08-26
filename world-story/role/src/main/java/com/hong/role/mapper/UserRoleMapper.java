package com.hong.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.role.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    // 继承BaseMapper，拥有基本的CRUD方法
}