package com.ms.learn.system.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.system.service.SysConfigService;
import com.ms.learn.system.sync.BinlogSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统管理服务 - A股资讯增量同步开关控制
 */
@RestController
@RequestMapping("/api/system/sync")
@RequiredArgsConstructor
public class SyncController {

    private final BinlogSyncService binlogSyncService;
    private final SysConfigService configService;

    /**
     * 查询同步状态(开关值 + 运行状态)
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        boolean enabled = configService.getBoolean("sync.a_stock.enabled", false);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enabled", enabled);
        data.put("running", binlogSyncService.isRunning());
        data.put("sourceHost", configService.getValue("sync.a_stock.source.host"));
        data.put("targetDatabase", configService.getValue("sync.a_stock.target.database"));
        return Result.success(data);
    }

    /**
     * 开启同步
     */
    @PostMapping("/enable")
    public Result<Map<String, Object>> enable() {
        configService.setValue("sync.a_stock.enabled", "true", "系统管理模块手动开启同步");
        binlogSyncService.syncWithConfig();
        return Result.success("同步已开启", statusBody());
    }

    /**
     * 关闭同步
     */
    @PostMapping("/disable")
    public Result<Map<String, Object>> disable() {
        configService.setValue("sync.a_stock.enabled", "false", "系统管理模块手动关闭同步");
        binlogSyncService.syncWithConfig();
        return Result.success("同步已关闭", statusBody());
    }

    private Map<String, Object> statusBody() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("enabled", configService.getBoolean("sync.a_stock.enabled", false));
        data.put("running", binlogSyncService.isRunning());
        return data;
    }
}
