package com.ms.learn.order.feign;

import com.ms.learn.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 声明式调用用户服务（OpenFeign 学习点）
 * <p>通过 Nacos 服务名 service-user 找到实例并调用。</p>
 */
@FeignClient(name = "service-user")
public interface UserFeignClient {

    @GetMapping("/api/user/{id}/exists")
    Result<Boolean> userExists(@PathVariable("id") Long id);
}
