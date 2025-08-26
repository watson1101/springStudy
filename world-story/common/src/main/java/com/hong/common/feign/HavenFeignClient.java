package com.hong.common.feign;

import com.hong.common.feign.fallback.HavenFeignFallback;
import com.hong.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 天界服务Feign客户端
 * @author h
 */
@FeignClient(name = "haven-world", fallback = HavenFeignFallback.class)
public interface HavenFeignClient {

    /**
     * 获取天界信息
     */
    @GetMapping("/api/haven/info")
    Result<String> getHavenInfo();
}