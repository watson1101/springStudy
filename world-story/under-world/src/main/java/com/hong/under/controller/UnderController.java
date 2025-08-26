package com.hong.under.controller;

import com.hong.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地府控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/under")
public class UnderController {

    /**
     * 获取地府信息
     */
    @GetMapping("/info")
    public Result<String> getUnderInfo() {
        log.info("获取地府信息");
        return Result.success("这里是地府，亡灵居住的地方");
    }
}