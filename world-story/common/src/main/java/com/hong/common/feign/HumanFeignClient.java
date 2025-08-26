package com.hong.common.feign;

import com.hong.common.feign.fallback.HumanFeignFallback;
import com.hong.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 人间服务Feign客户端
 */
@FeignClient(name = "human-world", fallback = HumanFeignFallback.class)
public interface HumanFeignClient {

    /**
     * 获取人间信息
     */
    @GetMapping("/api/human/info")
    Result<String> getHumanInfo();
}