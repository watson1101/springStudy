package com.hong.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.MetaUserDTO;
import com.hong.user.entity.MetaUser;

/**
 * 真灵服务接口
 */
public interface MetaUserService {
    
    /**
     * 根据ID查询真灵
     *
     * @param id 真灵ID
     * @return 真灵DTO
     */
    MetaUserDTO getMetaUserById(Long id);
    
    /**
     * 创建真灵
     *
     * @return 创建的真灵DTO
     */
    MetaUserDTO createMetaUser();
    
    /**
     * 删除真灵
     *
     * @param id 真灵ID
     * @return 是否成功
     */
    boolean deleteMetaUser(Long id);
    
    /**
     * 分页查询真灵列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 真灵DTO分页对象
     */
    Page<MetaUserDTO> listMetaUsers(long current, long size);
    
    /**
     * 将实体转换为DTO
     *
     * @param metaUser 真灵实体
     * @return 真灵DTO
     */
    MetaUserDTO convertToDTO(MetaUser metaUser);
}