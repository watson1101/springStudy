package com.hong.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.role.dto.RoleDTO;
import com.hong.role.entity.Role;
import com.hong.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    /**
     * 获取角色信息
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getRoleById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Role role = roleService.getById(id);
        
        if (role != null) {
            RoleDTO roleDTO = new RoleDTO();
            org.springframework.beans.BeanUtils.copyProperties(role, roleDTO);
            
            result.put("code", 200);
            result.put("message", "获取角色信息成功");
            result.put("data", roleDTO);
        } else {
            result.put("code", 404);
            result.put("message", "角色不存在");
        }
        
        return result;
    }
    
    /**
     * 根据角色编码获取角色信息
     * @param code 角色编码
     * @return 角色信息
     */
    @GetMapping("/code/{code}")
    public Map<String, Object> getRoleByCode(@PathVariable String code) {
        Map<String, Object> result = new HashMap<>();
        RoleDTO roleDTO = roleService.getRoleByCode(code);
        
        if (roleDTO != null) {
            result.put("code", 200);
            result.put("message", "获取角色信息成功");
            result.put("data", roleDTO);
        } else {
            result.put("code", 404);
            result.put("message", "角色不存在");
        }
        
        return result;
    }
    
    /**
     * 创建角色
     * @param roleDTO 角色信息
     * @return 创建结果
     */
    @PostMapping
    public Map<String, Object> createRole(@RequestBody RoleDTO roleDTO) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = roleService.createRole(roleDTO);
        if (success) {
            result.put("code", 200);
            result.put("message", "创建角色成功");
        } else {
            result.put("code", 400);
            result.put("message", "创建角色失败，角色编码可能已存在");
        }
        
        return result;
    }
    
    /**
     * 更新角色信息
     * @param id 角色ID
     * @param roleDTO 角色信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        Map<String, Object> result = new HashMap<>();
        
        roleDTO.setId(id);
        boolean success = roleService.updateRole(roleDTO);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "更新角色信息成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新角色信息失败，角色可能不存在或编码已被使用");
        }
        
        return result;
    }
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteRole(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = roleService.deleteRole(id);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "删除角色成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除角色失败，角色可能不存在");
        }
        
        return result;
    }
    
    /**
     * 分页查询角色列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 角色名称（可选）
     * @param code 角色编码（可选）
     * @return 角色列表分页数据
     */
    @GetMapping("/page")
    public Map<String, Object> getRolePage(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
        
        Map<String, Object> result = new HashMap<>();
        
        Page<Role> page = new Page<>(current, size);
        Page<RoleDTO> rolePage = roleService.getRolePage(page, name, code);
        
        result.put("code", 200);
        result.put("message", "获取角色列表成功");
        result.put("data", rolePage);
        
        return result;
    }
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 分配结果
     */
    @PostMapping("/assign/{userId}")
    public Map<String, Object> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        
        Map<String, Object> result = new HashMap<>();
        
        boolean success = roleService.assignRolesToUser(userId, roleIds);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "分配角色成功");
        } else {
            result.put("code", 400);
            result.put("message", "分配角色失败");
        }
        
        return result;
    }
    
    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getRolesByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        List<RoleDTO> roles = roleService.getRolesByUserId(userId);
        
        result.put("code", 200);
        result.put("message", "获取用户角色列表成功");
        result.put("data", roles);
        
        return result;
    }
}