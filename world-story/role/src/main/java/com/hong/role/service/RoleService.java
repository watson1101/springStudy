package com.hong.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hong.role.dto.RoleDTO;
import com.hong.role.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 根据角色编码查询角色
     * @param code 角色编码
     * @return 角色DTO对象
     */
    RoleDTO getRoleByCode(String code);
    
    /**
     * 创建新角色
     * @param roleDTO 角色DTO对象
     * @return 创建成功返回true，否则返回false
     */
    boolean createRole(RoleDTO roleDTO);
    
    /**
     * 更新角色信息
     * @param roleDTO 角色DTO对象
     * @return 更新成功返回true，否则返回false
     */
    boolean updateRole(RoleDTO roleDTO);
    
    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 分页查询角色列表
     * @param page 分页参数
     * @param name 角色名称（可选）
     * @param code 角色编码（可选）
     * @return 角色DTO分页对象
     */
    Page<RoleDTO> getRolePage(Page<Role> page, String name, String code);
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 分配成功返回true，否则返回false
     */
    boolean assignRolesToUser(Long userId, List<Long> roleIds);
    
    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色DTO列表
     */
    List<RoleDTO> getRolesByUserId(Long userId);
}