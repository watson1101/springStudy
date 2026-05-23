package com.hong.etl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * ETL配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "etl")
public class EtlProperties {
    /**
     * 默认Flink REST端口
     */
    private Integer restPort = 8081;

    /**
     * 任务状态存储(内存Map)
     */
    private Map<String, String> taskStatus = new HashMap<>();

    /**
     * 同步任务配置
     */
    private Map<String, TaskConfig> tasks = new HashMap<>();

    @Data
    public static class TaskConfig {
        private String name;
        private String mode;
        private SourceConfig source;
        private TargetConfig target;
        private java.util.List<TableMapping> tables;

        @Data
        public static class SourceConfig {
            private String type;
            private String host;
            private Integer port;
            private String database;
            private String username;
            private String password;
            private String parameters;
        }

        @Data
        public static class TargetConfig {
            private String type;
            private String host;
            private Integer port;
            private String database;
            private String username;
            private String password;
            private String parameters;
        }

        @Data
        public static class TableMapping {
            private String sourceTable;
            private String targetTable;
            private String primaryKey;
            private String columns;
            private String filter;
        }
    }
}
