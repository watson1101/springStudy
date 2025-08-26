package com.hong.common.feign;

import com.hong.common.feign.fallback.UnderFeignFallback;
import com.hong.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 地府服务Feign客户端
 */
@FeignClient(name = "under-world", fallback = UnderFeignFallback.class)
public interface UnderFeignClient {

    /**
     * 获取地府信息
     */
    @GetMapping("/api/under/info")
    Result<String> getUnderInfo();
}