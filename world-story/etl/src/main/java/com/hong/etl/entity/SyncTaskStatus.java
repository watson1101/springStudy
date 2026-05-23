package com.hong.etl.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 同步任务状态
 */
@Data
public class SyncTaskStatus {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 状态: INIT, RUNNING, PAUSED, STOPPED, FAILED, COMPLETED
     */
    private String status;

    /**
     * 同步模式
     */
    private String mode;

    /**
     * 已同步记录数
     */
    private Long syncedRecords;

    /**
     * 错误记录数
     */
    private Long errorRecords;

    /**
     * 启动时间
     */
    private LocalDateTime startTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 错误信息
     */
    private String errorMessage;
}
