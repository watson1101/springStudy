package com.hong.etl.entity;

import lombok.Data;

/**
 * 表配置
 */
@Data
public class TableConfig {
    /**
     * 源表名称
     */
    private String sourceTable;

    /**
     * 目标表名称
     */
    private String targetTable;

    /**
     * 源表主键字段(多个用逗号分隔)
     */
    private String primaryKey;

    /**
     * 要同步的列(空表示全部)
     */
    private String columns;

    /**
     * 过滤条件
     */
    private String filter;
}
