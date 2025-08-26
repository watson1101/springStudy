package com.hong.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hong.role.dto.RoleDTO;
import com.hong.role.entity.Role;
import com.hong.role.entity.UserRole;
import com.hong.role.mapper.RoleMapper;
import com.hong.role.mapper.UserRoleMapper;
import com.hong.role.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public RoleDTO getRoleByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getCode, code);
        
        Role role = getOne(queryWrapper);
        if (role == null) {
            return null;
        }
        
        return convertToDTO(role);
    }

    @Override
    public boolean createRole(RoleDTO roleDTO) {
        if (roleDTO == null || !StringUtils.hasText(roleDTO.getCode())) {
            return false;
        }
        
        // 检查角色编码是否已存在
        if (getRoleByCode(roleDTO.getCode()) != null) {
            return false;
        }
        
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        role.setCreateTime(now);
        role.setUpdateTime(now);
        
        // 设置默认状态
        if (role.getStatus() == null) {
            role.setStatus(1); // 默认启用
        }
        
        return save(role);
    }

    @Override
    public boolean updateRole(RoleDTO roleDTO) {
        if (roleDTO == null || roleDTO.getId() == null) {
            return false;
        }
        
        Role role = getById(roleDTO.getId());
        if (role == null) {
            return false;
        }
        
        // 如果修改了角色编码，需要检查新编码是否已存在
        if (StringUtils.hasText(roleDTO.getCode()) && !roleDTO.getCode().equals(role.getCode())) {
            RoleDTO existingRole = getRoleByCode(roleDTO.getCode());
            if (existingRole != null && !existingRole.getId().equals(roleDTO.getId())) {
                return false;
            }
        }
        
        BeanUtils.copyProperties(roleDTO, role);
        role.setUpdateTime(LocalDateTime.now());
        
        return updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        if (roleId == null) {
            return false;
        }
        
        // 删除角色与用户的关联关系
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getRoleId, roleId);
        userRoleMapper.delete(queryWrapper);
        
        // 删除角色
        return removeById(roleId);
    }

    @Override
    public Page<RoleDTO> getRolePage(Page<Role> page, String name, String code) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Role::getName, name);
        }
        
        if (StringUtils.hasText(code)) {
            queryWrapper.like(Role::getCode, code);
        }
        
        // 按排序字段升序排序
        queryWrapper.orderByAsc(Role::getSort);
        
        // 执行分页查询
        Page<Role> rolePage = page(page, queryWrapper);
        
        // 转换为DTO对象
        List<RoleDTO> roleDTOList = rolePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 创建DTO分页对象
        Page<RoleDTO> roleDTOPage = new Page<>();
        roleDTOPage.setRecords(roleDTOList);
        roleDTOPage.setCurrent(rolePage.getCurrent());
        roleDTOPage.setSize(rolePage.getSize());
        roleDTOPage.setTotal(rolePage.getTotal());
        
        return roleDTOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null) {
            return false;
        }
        
        // 先删除用户已有的角色关联
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(queryWrapper);
        
        // 如果角色ID列表为空，则只是清空用户角色，直接返回成功
        if (roleIds.isEmpty()) {
            return true;
        }
        
        // 批量添加用户角色关联
        List<UserRole> userRoles = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(now);
            userRoles.add(userRole);
        }
        
        // 批量插入用户角色关联
        return userRoleMapper.insertBatchSomeColumn(userRoles) > 0;
    }

    @Override
    public List<RoleDTO> getRolesByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        // 查询用户角色关联
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        // 查询角色信息
        List<Role> roles = listByIds(roleIds);
        
        // 转换为DTO对象
        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将Role实体转换为RoleDTO
     * @param role Role实体
     * @return RoleDTO对象
     */
    private RoleDTO convertToDTO(Role role) {
        if (role == null) {
            return null;
        }
        
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        return roleDTO;
    }
}