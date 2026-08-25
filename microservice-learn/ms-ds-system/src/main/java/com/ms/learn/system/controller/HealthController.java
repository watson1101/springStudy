package com.ms.learn.system.controller;

import com.ms.learn.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理服务 - 健康检查
 */
@RestController
public class HealthController {

    @GetMapping("/api/system/health")
    public Result<String> health() {
        return Result.success("ms-ds-system is running");
    }
}
