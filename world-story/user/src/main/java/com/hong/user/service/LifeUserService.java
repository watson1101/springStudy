package com.hong.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.LifeUserDTO;
import com.hong.user.entity.LifeUser;

import java.util.List;

/**
 * 生命用户服务接口
 */
public interface LifeUserService {
    
    /**
     * 根据ID查询生命用户
     *
     * @param id 用户ID
     * @return 生命用户DTO
     */
    LifeUserDTO getLifeUserById(Long id);
    
    /**
     * 根据真灵ID查询所有生命
     *
     * @param metaUserId 真灵ID
     * @return 生命用户DTO列表
     */
    List<LifeUserDTO> getLifeUsersByMetaUserId(Long metaUserId);
    
    /**
     * 创建生命用户
     *
     * @param lifeUserDTO 生命用户DTO
     * @return 创建的生命用户DTO
     */
    LifeUserDTO createLifeUser(LifeUserDTO lifeUserDTO);
    
    /**
     * 更新生命用户
     *
     * @param id 用户ID
     * @param lifeUserDTO 生命用户DTO
     * @return 更新后的生命用户DTO
     */
    LifeUserDTO updateLifeUser(Long id, LifeUserDTO lifeUserDTO);
    
    /**
     * 删除生命用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteLifeUser(Long id);
    
    /**
     * 分页查询生命用户列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param worldType 世界类型（可选）
     * @param species 物种（可选）
     * @return 生命用户DTO分页对象
     */
    Page<LifeUserDTO> listLifeUsers(long current, long size, String worldType, String species);
    
    /**
     * 将实体转换为DTO
     *
     * @param lifeUser 生命用户实体
     * @return 生命用户DTO
     */
    LifeUserDTO convertToDTO(LifeUser lifeUser);
    
    /**
     * 将DTO转换为实体
     *
     * @param lifeUserDTO 生命用户DTO
     * @return 生命用户实体
     */
    LifeUser convertToEntity(LifeUserDTO lifeUserDTO);
}