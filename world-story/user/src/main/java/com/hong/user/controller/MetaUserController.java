package com.hong.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.MetaUserDTO;
import com.hong.user.service.MetaUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 真灵控制器
 */
@RestController
@RequestMapping("/meta-users")
public class MetaUserController {
    
    @Resource
    private MetaUserService metaUserService;
    
    /**
     * 根据ID获取真灵
     *
     * @param id 真灵ID
     * @return 真灵信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getMetaUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        MetaUserDTO metaUserDTO = metaUserService.getMetaUserById(id);
        
        if (metaUserDTO != null) {
            result.put("code", 200);
            result.put("data", metaUserDTO);
        } else {
            result.put("code", 404);
            result.put("message", "真灵不存在");
        }
        
        return result;
    }
    
    /**
     * 创建真灵
     *
     * @return 创建的真灵信息
     */
    @PostMapping
    public Map<String, Object> createMetaUser() {
        Map<String, Object> result = new HashMap<>();
        MetaUserDTO metaUserDTO = metaUserService.createMetaUser();
        
        result.put("code", 200);
        result.put("data", metaUserDTO);
        result.put("message", "真灵创建成功");
        
        return result;
    }
    
    /**
     * 删除真灵
     *
     * @param id 真灵ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteMetaUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = metaUserService.deleteMetaUser(id);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "真灵删除成功");
        } else {
            result.put("code", 404);
            result.put("message", "真灵不存在或删除失败");
        }
        
        return result;
    }
    
    /**
     * 分页查询真灵列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 真灵列表
     */
    @GetMapping
    public Map<String, Object> listMetaUsers(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Map<String, Object> result = new HashMap<>();
        Page<MetaUserDTO> page = metaUserService.listMetaUsers(current, size);
        
        result.put("code", 200);
        result.put("data", page);
        
        return result;
    }
}