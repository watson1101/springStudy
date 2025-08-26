package com.hong.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.UserDTO;
import com.hong.user.entity.User;
import com.hong.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * 获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getById(id);
        
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            org.springframework.beans.BeanUtils.copyProperties(user, userDTO);
            
            result.put("code", 200);
            result.put("message", "获取用户信息成功");
            result.put("data", userDTO);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        
        return result;
    }
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        UserDTO userDTO = userService.getUserByUsername(username);
        
        if (userDTO != null) {
            result.put("code", 200);
            result.put("message", "获取用户信息成功");
            result.put("data", userDTO);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        
        return result;
    }
    
    /**
     * 创建用户
     * @param userDTO 用户信息
     * @return 创建结果
     */
    @PostMapping
    public Map<String, Object> createUser(@RequestBody UserDTO userDTO) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = userService.createUser(userDTO);
        if (success) {
            result.put("code", 200);
            result.put("message", "创建用户成功");
        } else {
            result.put("code", 400);
            result.put("message", "创建用户失败，用户名可能已存在");
        }
        
        return result;
    }
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        Map<String, Object> result = new HashMap<>();
        
        userDTO.setId(id);
        boolean success = userService.updateUser(userDTO);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "更新用户信息成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新用户信息失败，用户可能不存在");
        }
        
        return result;
    }
    
    /**
     * 用户本人修改密码
     * @param request 修改密码请求体
     * @return 修改结果
     */
    @PutMapping("/password/self")
    public Map<String, Object> updatePasswordBySelf(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取当前登录用户ID
        Object userIdObj = StpUtil.getLoginIdDefaultNull();
        if (userIdObj == null) {
            result.put("code", 401);
            result.put("message", "用户未登录");
            return result;
        }
        
        Long userId = Long.parseLong(userIdObj.toString());
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            result.put("code", 400);
            result.put("message", "旧密码和新密码不能为空");
            return result;
        }
        
        boolean success = userService.updatePasswordByUser(userId, oldPassword, newPassword);
        
        if (success) {
            // 密码修改成功后，强制用户重新登录
            StpUtil.logout();
            result.put("code", 200);
            result.put("message", "密码修改成功，请重新登录");
        } else {
            result.put("code", 400);
            result.put("message", "密码修改失败，旧密码不正确或用户不存在");
        }
        
        return result;
    }
    
    /**
     * 管理员直接修改指定用户密码
     * @param userId 用户ID
     * @param request 修改密码请求体
     * @return 修改结果
     */
    @PutMapping("/{userId}/password/admin")
    public Map<String, Object> updatePasswordByAdmin(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 这里应该添加管理员权限验证
        
        String newPassword = request.get("newPassword");
        if (!StringUtils.hasText(newPassword)) {
            result.put("code", 400);
            result.put("message", "新密码不能为空");
            return result;
        }
        
        boolean success = userService.updatePasswordByAdmin(userId, newPassword);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "密码修改成功");
        } else {
            result.put("code", 400);
            result.put("message", "密码修改失败，用户不存在");
        }
        
        return result;
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = userService.deleteUser(id);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "删除用户成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除用户失败，用户可能不存在");
        }
        
        return result;
    }
    
    /**
     * 分页查询用户列表
     * @param current 当前页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param phone 手机号（可选）
     * @param email 邮箱（可选）
     * @return 用户列表分页数据
     */
    @GetMapping("/page")
    public Map<String, Object> getUserPage(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {
        
        Map<String, Object> result = new HashMap<>();
        
        Page<User> page = new Page<>(current, size);
        Page<UserDTO> userPage = userService.getUserPage(page, username, phone, email);
        
        result.put("code", 200);
        result.put("message", "获取用户列表成功");
        result.put("data", userPage);
        
        return result;
    }
}