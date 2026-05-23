package com.hong.etl.entity;

import lombok.Data;
import java.util.List;

/**
 * ETL同步任务配置
 */
@Data
public class SyncTaskConfig {
    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务ID
     */
    private String id;

    /**
     * 同步模式: FULL(全量同步), INCREMENTAL(增量同步), ALL(全量+增量)
     */
    private String mode;

    /**
     * 源数据库配置
     */
    private DataSourceConfig source;

    /**
     * 目标数据库配置
     */
    private DataSourceConfig target;

    /**
     * 要同步的表配置列表
     */
    private List<TableConfig> tables;

    /**
     * 批处理大小
     */
    private Integer batchSize = 1000;

    /**
     * 并行度
     */
    private Integer parallelism = 1;

    /**
     * 是否启用检查点
     */
    private Boolean enableCheckpoint = true;

    /**
     * 检查点间隔(毫秒)
     */
    private Long checkpointInterval = 60000L;
}
