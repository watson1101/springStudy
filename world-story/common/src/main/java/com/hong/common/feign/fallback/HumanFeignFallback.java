package com.hong.common.feign.fallback;

import com.hong.common.feign.HumanFeignClient;
import com.hong.common.result.Result;
import org.springframework.stereotype.Component;

/**
 * 人间服务Feign客户端熔断降级实现
 */
@Component
public class HumanFeignFallback implements HumanFeignClient {

    @Override
    public Result<String> getHumanInfo() {
        return Result.failed("人间服务暂时不可用，请稍后再试");
    }
}