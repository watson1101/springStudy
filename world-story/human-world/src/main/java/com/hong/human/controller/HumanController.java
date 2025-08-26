package com.hong.human.controller;

import com.hong.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 人间控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/human")
public class HumanController {

    /**
     * 获取人间信息
     */
    @GetMapping("/info")
    public Result<String> getHumanInfo() {
        log.info("获取人间信息");
        return Result.success("这里是人间，凡人居住的地方");
    }
}