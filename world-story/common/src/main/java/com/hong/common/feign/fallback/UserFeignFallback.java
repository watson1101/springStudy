package com.hong.common.feign.fallback;

import com.hong.common.feign.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务Feign客户端熔断降级实现
 */
@Component
public class UserFeignFallback implements UserFeignClient {

    @Override
    public Map<String, Object> getUserById(Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "用户服务暂时不可用，请稍后再试");
        return result;
    }

    @Override
    public Map<String, Object> getUserByUsername(String username) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "用户服务暂时不可用，请稍后再试");
        return result;
    }
}