package com.hong.etl.service;

import com.hong.etl.entity.SyncTaskConfig;
import com.hong.etl.entity.SyncTaskStatus;
import com.hong.etl.job.CdcSyncJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ETL同步服务
 */
@Service
public class EtlSyncService {
    private static final Logger LOG = LoggerFactory.getLogger(EtlSyncService.class);

    private final Map<String, CdcSyncJob> runningJobs = new HashMap<>();

    /**
     * 创建并启动全量同步任务
     */
    public SyncTaskStatus startFullSync(SyncTaskConfig config) {
        try {
            String taskId = config.getId() != null ? config.getId() : UUID.randomUUID().toString();
            config.setId(taskId);

            CdcSyncJob job = new CdcSyncJob(config);
            job.startFullSync();

            runningJobs.put(taskId, job);
            LOG.info("Full sync task started: {}", taskId);

            return job.getTaskStatus();
        } catch (Exception e) {
            LOG.error("Failed to start full sync task", e);
            SyncTaskStatus status = new SyncTaskStatus();
            status.setTaskId(config.getId());
            status.setTaskName(config.getName());
            status.setStatus("FAILED");
            status.setErrorMessage(e.getMessage());
            return status;
        }
    }

    /**
     * 创建并启动增量同步任务(实时同步)
     */
    public SyncTaskStatus startIncrementalSync(SyncTaskConfig config) {
        try {
            String taskId = config.getId() != null ? config.getId() : UUID.randomUUID().toString();
            config.setId(taskId);

            CdcSyncJob job = new CdcSyncJob(config);
            job.startIncrementalSync();

            runningJobs.put(taskId, job);
            LOG.info("Incremental sync task started: {}", taskId);

            return job.getTaskStatus();
        } catch (Exception e) {
            LOG.error("Failed to start incremental sync task", e);
            SyncTaskStatus status = new SyncTaskStatus();
            status.setTaskId(config.getId());
            status.setTaskName(config.getName());
            status.setStatus("FAILED");
            status.setErrorMessage(e.getMessage());
            return status;
        }
    }

    /**
     * 创建并启动全量+增量同步任务
     */
    public SyncTaskStatus startFullAndIncrementalSync(SyncTaskConfig config) {
        try {
            String taskId = config.getId() != null ? config.getId() : UUID.randomUUID().toString();
            config.setId(taskId);

            CdcSyncJob job = new CdcSyncJob(config);
            job.startFullSync();
            job.startIncrementalSync();

            runningJobs.put(taskId, job);
            LOG.info("Full and incremental sync task started: {}", taskId);

            return job.getTaskStatus();
        } catch (Exception e) {
            LOG.error("Failed to start full and incremental sync task", e);
            SyncTaskStatus status = new SyncTaskStatus();
            status.setTaskId(config.getId());
            status.setTaskName(config.getName());
            status.setStatus("FAILED");
            status.setErrorMessage(e.getMessage());
            return status;
        }
    }

    /**
     * 停止同步任务
     */
    public boolean stopSync(String taskId) {
        CdcSyncJob job = runningJobs.get(taskId);
        if (job != null) {
            job.stop();
            runningJobs.remove(taskId);
            LOG.info("Sync task stopped: {}", taskId);
            return true;
        }
        return false;
    }

    /**
     * 获取任务状态
     */
    public SyncTaskStatus getTaskStatus(String taskId) {
        CdcSyncJob job = runningJobs.get(taskId);
        if (job != null) {
            return job.getTaskStatus();
        }
        return null;
    }

    /**
     * 获取所有运行中的任务
     */
    public Map<String, SyncTaskStatus> getAllRunningTasks() {
        Map<String, SyncTaskStatus> result = new HashMap<>();
        for (Map.Entry<String, CdcSyncJob> entry : runningJobs.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getTaskStatus());
        }
        return result;
    }
}
