package com.hong.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.LifeUserDTO;
import com.hong.user.service.LifeUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生命用户控制器
 */
@RestController
@RequestMapping("/life-users")
public class LifeUserController {
    
    @Resource
    private LifeUserService lifeUserService;
    
    /**
     * 根据ID获取生命用户
     *
     * @param id 用户ID
     * @return 生命用户信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getLifeUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        LifeUserDTO lifeUserDTO = lifeUserService.getLifeUserById(id);
        
        if (lifeUserDTO != null) {
            result.put("code", 200);
            result.put("data", lifeUserDTO);
        } else {
            result.put("code", 404);
            result.put("message", "生命用户不存在");
        }
        
        return result;
    }
    
    /**
     * 根据真灵ID获取所有生命
     *
     * @param metaUserId 真灵ID
     * @return 生命用户列表
     */
    @GetMapping("/meta-user/{metaUserId}")
    public Map<String, Object> getLifeUsersByMetaUserId(@PathVariable Long metaUserId) {
        Map<String, Object> result = new HashMap<>();
        List<LifeUserDTO> lifeUserDTOs = lifeUserService.getLifeUsersByMetaUserId(metaUserId);
        
        result.put("code", 200);
        result.put("data", lifeUserDTOs);
        
        return result;
    }
    
    /**
     * 创建生命用户
     *
     * @param lifeUserDTO 生命用户DTO
     * @return 创建的生命用户信息
     */
    @PostMapping
    public Map<String, Object> createLifeUser(@RequestBody LifeUserDTO lifeUserDTO) {
        Map<String, Object> result = new HashMap<>();
        
        if (lifeUserDTO.getMetaUserId() == null) {
            result.put("code", 400);
            result.put("message", "真灵ID不能为空");
            return result;
        }
        
        LifeUserDTO createdLifeUserDTO = lifeUserService.createLifeUser(lifeUserDTO);
        
        result.put("code", 200);
        result.put("data", createdLifeUserDTO);
        result.put("message", "生命用户创建成功");
        
        return result;
    }
    
    /**
     * 更新生命用户
     *
     * @param id 用户ID
     * @param lifeUserDTO 生命用户DTO
     * @return 更新后的生命用户信息
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateLifeUser(
            @PathVariable Long id,
            @RequestBody LifeUserDTO lifeUserDTO) {
        Map<String, Object> result = new HashMap<>();
        LifeUserDTO updatedLifeUserDTO = lifeUserService.updateLifeUser(id, lifeUserDTO);
        
        if (updatedLifeUserDTO != null) {
            result.put("code", 200);
            result.put("data", updatedLifeUserDTO);
            result.put("message", "生命用户更新成功");
        } else {
            result.put("code", 404);
            result.put("message", "生命用户不存在");
        }
        
        return result;
    }
    
    /**
     * 删除生命用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteLifeUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = lifeUserService.deleteLifeUser(id);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "生命用户删除成功");
        } else {
            result.put("code", 404);
            result.put("message", "生命用户不存在或删除失败");
        }
        
        return result;
    }
    
    /**
     * 分页查询生命用户列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param worldType 世界类型（可选）
     * @param species 物种（可选）
     * @return 生命用户列表
     */
    @GetMapping
    public Map<String, Object> listLifeUsers(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String worldType,
            @RequestParam(required = false) String species) {
        Map<String, Object> result = new HashMap<>();
        Page<LifeUserDTO> page = lifeUserService.listLifeUsers(current, size, worldType, species);
        
        result.put("code", 200);
        result.put("data", page);
        
        return result;
    }
}