package com.hong.common.feign.fallback;

import com.hong.common.feign.RoleFeignClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色服务Feign客户端熔断降级实现
 */
@Component
public class RoleFeignFallback implements RoleFeignClient {

    @Override
    public Map<String, Object> getRoleById(Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "角色服务暂时不可用，请稍后再试");
        return result;
    }

    @Override
    public Map<String, Object> getRolesByUserId(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "角色服务暂时不可用，请稍后再试");
        return result;
    }
}