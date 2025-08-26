package com.hong.role.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hong.common.feign.UserFeignClient;
import com.hong.role.entity.UserRole;
import com.hong.role.mapper.UserRoleMapper;
import com.hong.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户角色关联控制器
 */
@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {

    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserFeignClient userFeignClient;
    
    /**
     * 获取用户的角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/user/{userId}/roles")
    public Map<String, Object> getRoleIdsByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 验证用户是否存在
        Map<String, Object> userResult = userFeignClient.getUserById(userId);
        if ((Integer) userResult.get("code") != 200) {
            result.put("code", 404);
            result.put("message", "用户不存在");
            return result;
        }
        
        // 查询用户角色关联
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        
        // 提取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();
        
        result.put("code", 200);
        result.put("message", "获取用户角色ID列表成功");
        result.put("data", roleIds);
        
        return result;
    }
    
    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 检查结果
     */
    @GetMapping("/check")
    public Map<String, Object> checkUserHasRole(
            @RequestParam Long userId,
            @RequestParam Long roleId) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 查询用户角色关联
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, roleId);
        
        UserRole userRole = userRoleMapper.selectOne(queryWrapper);
        
        result.put("code", 200);
        result.put("message", "检查用户角色成功");
        result.put("data", userRole != null);
        
        return result;
    }
}