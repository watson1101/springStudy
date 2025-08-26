package com.hong.common.feign.fallback;

import com.hong.common.feign.UnderFeignClient;
import com.hong.common.result.Result;
import org.springframework.stereotype.Component;

/**
 * 地府服务Feign客户端熔断降级实现
 */
@Component
public class UnderFeignFallback implements UnderFeignClient {

    @Override
    public Result<String> getUnderInfo() {
        return Result.failed("地府服务暂时不可用，请稍后再试");
    }
}