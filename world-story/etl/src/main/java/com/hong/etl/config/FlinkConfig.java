package com.hong.etl.config;

import org.springframework.context.annotation.Configuration;

/**
 * Flink配置类
 * 注意: Flink CDC使用嵌入式模式运行，不需要单独的Flink集群
 * StreamExecutionEnvironment会在CdcSyncJob中直接创建
 */
@Configuration
public class FlinkConfig {
    // Flink CDC使用嵌入式模式运行
}
