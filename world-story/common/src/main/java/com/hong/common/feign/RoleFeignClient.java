package com.hong.common.feign;

import com.hong.common.feign.fallback.RoleFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 角色服务Feign客户端
 */
@FeignClient(name = "role-service", fallback = RoleFeignFallback.class)
public interface RoleFeignClient {
    
    /**
     * 获取角色信息
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/api/role/{id}")
    Map<String, Object> getRoleById(@PathVariable("id") Long id);
    
    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/api/role/user/{userId}")
    Map<String, Object> getRolesByUserId(@PathVariable("userId") Long userId);
}