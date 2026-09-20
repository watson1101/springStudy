package com.ms.learn.hotnews.consumer.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.hotnews.consumer.entity.HotNews;
import com.ms.learn.hotnews.consumer.service.HotNewsConsumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 热榜查询接口
 * <ul>
 *   <li>GET /api/hotnews/latest —— 最新热榜（最多 50 条）</li>
 *   <li>GET /api/hotnews/batch/{batchId} —— 按批次查询</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/hotnews")
@RequiredArgsConstructor
public class HotNewsController {

    private final HotNewsConsumeService consumeService;

    /** 最新热榜 */
    @GetMapping("/latest")
    public Result<List<HotNews>> latest() {
        return Result.success(consumeService.latest());
    }

    /** 按批次查询 */
    @GetMapping("/batch/{batchId}")
    public Result<List<HotNews>> byBatch(@PathVariable String batchId) {
        return Result.success(consumeService.listByBatch(batchId));
    }
}
