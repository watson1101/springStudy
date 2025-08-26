package com.hong.haven.controller;

import com.hong.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天界控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/haven")
public class HavenController {

    /**
     * 获取天界信息
     */
    @GetMapping("/info")
    public Result<String> getHavenInfo() {
        log.info("获取天界信息");
        return Result.success("这里是天界，神仙居住的地方");
    }
}