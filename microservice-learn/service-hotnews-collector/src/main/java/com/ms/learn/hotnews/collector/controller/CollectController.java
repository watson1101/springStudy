package com.ms.learn.hotnews.collector.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.hotnews.collector.config.CollectProperties;
import com.ms.learn.hotnews.collector.service.HotNewsCollectService;import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 热榜采集服务 REST 接口
 * <ul>
 *   <li>POST /api/hotnews/collect —— 手动触发一次采集（不受 cron 限制，测试用）</li>
 *   <li>GET  /api/hotnews/config  —— 查看当前采集配置（来自 Nacos）</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/hotnews")
@RequiredArgsConstructor
public class CollectController {

    private final HotNewsCollectService collectService;
    private final CollectProperties props;

    /** 手动触发一次采集 */
    @PostMapping("/collect")
    public Result<Map<String, Object>> collect() {
        int count = collectService.collectAndSend();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success("采集完成", data);
    }

    /** 查看当前采集配置（验证 Nacos 动态配置是否生效） */
    @GetMapping("/config")
    public Result<Map<String, Object>> config() {
        Map<String, Object> data = new HashMap<>();
        data.put("cron", props.getCron());
        data.put("enabled", props.isEnabled());
        data.put("url", props.getUrl());
        data.put("timeout", props.getTimeout());
        return Result.success(data);
    }
}
