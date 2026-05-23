package com.hong.etl.controller;

import com.hong.etl.entity.SyncTaskConfig;
import com.hong.etl.entity.SyncTaskStatus;
import com.hong.etl.service.EtlConfigService;
import com.hong.etl.service.EtlSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ETL数据同步控制器
 */
@RestController
@RequestMapping("/etl")
public class EtlController {

    @Autowired
    private EtlSyncService etlSyncService;

    @Autowired
    private EtlConfigService etlConfigService;

    /**
     * 创建或更新同步任务配置
     */
    @PostMapping("/config")
    public SyncTaskConfig saveConfig(@RequestBody SyncTaskConfig config) {
        return etlConfigService.saveTaskConfig(config);
    }

    /**
     * 获取任务配置
     */
    @GetMapping("/config/{taskId}")
    public SyncTaskConfig getConfig(@PathVariable String taskId) {
        return etlConfigService.getTaskConfig(taskId);
    }

    /**
     * 获取所有任务配置
     */
    @GetMapping("/configs")
    public List<SyncTaskConfig> getAllConfigs() {
        return etlConfigService.getAllTaskConfigs();
    }

    /**
     * 删除任务配置
     */
    @DeleteMapping("/config/{taskId}")
    public boolean deleteConfig(@PathVariable String taskId) {
        return etlConfigService.deleteTaskConfig(taskId);
    }

    /**
     * 启动全量同步
     */
    @PostMapping("/sync/full")
    public SyncTaskStatus startFullSync(@RequestBody SyncTaskConfig config) {
        return etlSyncService.startFullSync(config);
    }

    /**
     * 启动增量同步(实时同步)
     */
    @PostMapping("/sync/incremental")
    public SyncTaskStatus startIncrementalSync(@RequestBody SyncTaskConfig config) {
        return etlSyncService.startIncrementalSync(config);
    }

    /**
     * 启动全量+增量同步
     */
    @PostMapping("/sync/all")
    public SyncTaskStatus startFullAndIncrementalSync(@RequestBody SyncTaskConfig config) {
        return etlSyncService.startFullAndIncrementalSync(config);
    }

    /**
     * 停止同步任务
     */
    @PostMapping("/sync/stop/{taskId}")
    public boolean stopSync(@PathVariable String taskId) {
        return etlSyncService.stopSync(taskId);
    }

    /**
     * 获取任务状态
     */
    @GetMapping("/sync/status/{taskId}")
    public SyncTaskStatus getTaskStatus(@PathVariable String taskId) {
        return etlSyncService.getTaskStatus(taskId);
    }

    /**
     * 获取所有运行中的任务
     */
    @GetMapping("/sync/running")
    public Map<String, SyncTaskStatus> getAllRunningTasks() {
        return etlSyncService.getAllRunningTasks();
    }

    /**
     * 根据配置ID启动同步
     */
    @PostMapping("/sync/start/{configId}")
    public SyncTaskStatus startSyncByConfigId(@PathVariable String configId,
                                               @RequestParam(defaultValue = "FULL") String mode) {
        SyncTaskConfig config = etlConfigService.getTaskConfig(configId);
        if (config == null) {
            throw new IllegalArgumentException("Task config not found: " + configId);
        }

        return switch (mode.toUpperCase()) {
            case "INCREMENTAL" -> etlSyncService.startIncrementalSync(config);
            case "ALL" -> etlSyncService.startFullAndIncrementalSync(config);
            default -> etlSyncService.startFullSync(config);
        };
    }
}
