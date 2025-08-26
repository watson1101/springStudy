package com.hong.common.feign.fallback;

import com.hong.common.feign.HavenFeignClient;
import com.hong.common.result.Result;
import org.springframework.stereotype.Component;

/**
 * 天界服务Feign客户端熔断降级实现
 */
@Component
public class HavenFeignFallback implements HavenFeignClient {

    @Override
    public Result<String> getHavenInfo() {
        return Result.failed("天界服务暂时不可用，请稍后再试");
    }
}