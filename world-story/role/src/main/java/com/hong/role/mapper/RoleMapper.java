package com.hong.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    // 继承BaseMapper，拥有基本的CRUD方法
}