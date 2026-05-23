package com.hong.etl.service;

import com.hong.etl.entity.SyncTaskConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ETL配置管理服务
 */
@Service
public class EtlConfigService {

    private final Map<String, SyncTaskConfig> taskConfigs = new HashMap<>();

    /**
     * 保存任务配置
     */
    public SyncTaskConfig saveTaskConfig(SyncTaskConfig config) {
        if (config.getId() == null || config.getId().isEmpty()) {
            config.setId(java.util.UUID.randomUUID().toString());
        }
        taskConfigs.put(config.getId(), config);
        return config;
    }

    /**
     * 获取任务配置
     */
    public SyncTaskConfig getTaskConfig(String taskId) {
        return taskConfigs.get(taskId);
    }

    /**
     * 获取所有任务配置
     */
    public List<SyncTaskConfig> getAllTaskConfigs() {
        return new ArrayList<>(taskConfigs.values());
    }

    /**
     * 删除任务配置
     */
    public boolean deleteTaskConfig(String taskId) {
        return taskConfigs.remove(taskId) != null;
    }

    /**
     * 检查任务是否存在
     */
    public boolean taskExists(String taskId) {
        return taskConfigs.containsKey(taskId);
    }
}
