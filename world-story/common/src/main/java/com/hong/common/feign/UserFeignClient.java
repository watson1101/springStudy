package com.hong.common.feign;

import com.hong.common.feign.fallback.UserFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", fallback = UserFeignFallback.class)
public interface UserFeignClient {
    
    /**
     * 获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/api/user/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/api/user/username/{username}")
    Map<String, Object> getUserByUsername(@PathVariable("username") String username);
}