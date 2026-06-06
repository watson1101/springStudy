package com.hong.etl.job;

import com.hong.etl.entity.DataSourceConfig;
import com.hong.etl.entity.SyncTaskConfig;
import com.hong.etl.entity.SyncTaskStatus;
import com.hong.etl.entity.TableConfig;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

/**
 * Flink CDC同步作业 - 简化版
 */
public class CdcSyncJob {
    private static final Logger LOG = LoggerFactory.getLogger(CdcSyncJob.class);

    private final SyncTaskConfig taskConfig;
    private final SyncTaskStatus taskStatus;
    private StreamExecutionEnvironment env;
    private boolean isRunning = false;

    // 保存配置供Sink使用
    private static class SyncConfig implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        String jdbcUrl;
        String username;
        String password;
        String targetTable;
        String primaryKey;
    }

    public CdcSyncJob(SyncTaskConfig taskConfig) {
        this.taskConfig = taskConfig;
        this.taskStatus = new SyncTaskStatus();
        this.taskStatus.setTaskId(taskConfig.getId());
        this.taskStatus.setTaskName(taskConfig.getName());
        this.taskStatus.setStatus("INIT");
        this.taskStatus.setMode(taskConfig.getMode());
        this.taskStatus.setSyncedRecords(0L);
        this.taskStatus.setErrorRecords(0L);
    }

    /**
     * 启动增量同步
     */
    public void startIncrementalSync() throws Exception {
        LOG.info("Starting incremental sync for task: {}", taskConfig.getName());
        taskStatus.setStatus("RUNNING");
        taskStatus.setStartTime(LocalDateTime.now());
        isRunning = true;

        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setRestartStrategy(RestartStrategies.noRestart());

        // 先执行初始快照同步
        performInitialSnapshot();

        // 获取表配置
        TableConfig tableConfig = taskConfig.getTables().get(0);
        DataSourceConfig source = taskConfig.getSource();
        DataSourceConfig target = taskConfig.getTarget();

        // 准备同步配置
        SyncConfig syncConfig = new SyncConfig();
        syncConfig.jdbcUrl = target.buildJdbcUrl();
        syncConfig.username = target.getUsername();
        syncConfig.password = target.getPassword();
        syncConfig.targetTable = target.getDatabase() + "." + tableConfig.getTargetTable();
        syncConfig.primaryKey = tableConfig.getPrimaryKey();

        // 创建CDC Source
        Source<String, ?, ?> cdcSource = MySqlSource.<String>builder()
                .hostname(source.getHost())
                .port(source.getPort())
                .databaseList(source.getDatabase())
                .tableList(source.getDatabase() + "." + tableConfig.getSourceTable())
                .username(source.getUsername())
                .password(source.getPassword())
                .deserializer(new CdcDebeziumDeserializationSchema())
                .includeSchemaChanges(false)
                .build();

        DataStream<String> cdcStream = env.fromSource(
                cdcSource,
                WatermarkStrategy.noWatermarks(),
                "CDC Source"
        );

        // 添加自定义Sink
        cdcStream.addSink(new SimpleJdbcSink(syncConfig));

        env.executeAsync("CDC Sync - " + taskConfig.getName());
        LOG.info("Incremental sync started for task: {}", taskConfig.getName());
    }

    /**
     * 执行初始快照同步
     */
    private void performInitialSnapshot() {
        DataSourceConfig source = taskConfig.getSource();
        DataSourceConfig target = taskConfig.getTarget();
        TableConfig tableConfig = taskConfig.getTables().get(0);

        LOG.info("Performing initial snapshot from {} to {}", source.getDatabase(), target.getDatabase());

        try (Connection sourceConn = DriverManager.getConnection(
                source.buildJdbcUrl(), source.getUsername(), source.getPassword());
             Connection targetConn = DriverManager.getConnection(
                target.buildJdbcUrl(), target.getUsername(), target.getPassword())) {

            String sourceTable = source.getDatabase() + "." + tableConfig.getSourceTable();
            String targetTable = target.getDatabase() + "." + tableConfig.getTargetTable();

            // 清空目标表
            try (java.sql.Statement stmt = targetConn.createStatement()) {
                stmt.execute("DELETE FROM " + targetTable);
            }

            // 复制数据
            String insertSql = "INSERT INTO " + targetTable + " SELECT * FROM " + sourceTable;
            try (java.sql.Statement stmt = targetConn.createStatement()) {
                int rows = stmt.executeUpdate(insertSql);
                taskStatus.setSyncedRecords(taskStatus.getSyncedRecords() + (long) rows);
                LOG.info("Initial snapshot completed: {} rows synced", rows);
            }
        } catch (Exception e) {
            LOG.error("Error performing initial snapshot", e);
        }
    }

    /**
     * 简单的JDBC Sink
     */
    private static class SimpleJdbcSink implements SinkFunction<String> {
        private static final Logger LOG = LoggerFactory.getLogger(SimpleJdbcSink.class);
        private static final long serialVersionUID = 1L;

        private final SyncConfig config;
        private transient Connection connection;

        public SimpleJdbcSink(SyncConfig config) {
            this.config = config;
        }

        /**
         * 转换时间格式
         * 从: 2026-05-23T19:24:32Z (ISO 8601)
         * 到: 2026-05-23 19:24:32 (MySQL DATETIME)
         */
        private String convertTimestamp(String ts) {
            if (ts == null) return null;
            // 去掉T和Z
            return ts.replace("T", " ").replace("Z", "");
        }

        @Override
        public void invoke(String record) {
            try {
                LOG.debug("CDC Event received: {}", record.substring(0, Math.min(200, record.length())));

                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(config.jdbcUrl, config.username, config.password);
                    LOG.info("JDBC connection established");
                }

                // 解析Struct格式的CDC事件
                // 格式: Struct{after=Struct{id=5,username=eve,...},source=Struct{...},op=c,...}

                // 判断操作类型
                String op = "r";
                if (record.contains("op=c")) {
                    op = "c";
                } else if (record.contains("op=u")) {
                    op = "u";
                } else if (record.contains("op=d")) {
                    op = "d";
                }

                // 提取数据部分 (after或before)
                String section = "d".equals(op) ? "before" : "after";
                String dataSection = extractStructSection(record, section);

                if (dataSection == null || dataSection.isEmpty()) {
                    LOG.debug("No data section found for op: {}", op);
                    return;
                }

                // 提取字段值
                String id = extractStructField(dataSection, "id");
                String username = extractStructField(dataSection, "username");
                String email = extractStructField(dataSection, "email");
                String age = extractStructField(dataSection, "age");
                String createdAt = convertTimestamp(extractStructField(dataSection, "created_at"));
                String updatedAt = convertTimestamp(extractStructField(dataSection, "updated_at"));

                if (id == null) {
                    LOG.warn("Could not extract id from: {}", dataSection);
                    return;
                }

                if ("c".equals(op) || "r".equals(op)) {
                    // INSERT
                    String sql = String.format(
                        "INSERT INTO %s (id,username,email,age,created_at,updated_at) VALUES (?,?,?,?,?,?)",
                        config.targetTable
                    );
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(id));
                        ps.setString(2, username);
                        ps.setString(3, email);
                        ps.setInt(4, Integer.parseInt(age != null ? age : "0"));
                        ps.setString(5, createdAt);
                        ps.setString(6, updatedAt);
                        ps.executeUpdate();
                        LOG.info("Inserted record: id={}", id);
                    }
                } else if ("u".equals(op)) {
                    // UPDATE
                    String sql = String.format(
                        "UPDATE %s SET username=?, email=?, age=?, updated_at=? WHERE id=?",
                        config.targetTable
                    );
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setString(1, username);
                        ps.setString(2, email);
                        ps.setInt(3, Integer.parseInt(age != null ? age : "0"));
                        ps.setString(4, updatedAt);
                        ps.setInt(5, Integer.parseInt(id));
                        ps.executeUpdate();
                        LOG.info("Updated record: id={}", id);
                    }
                } else if ("d".equals(op)) {
                    // DELETE
                    String sql = String.format("DELETE FROM %s WHERE %s = ?", config.targetTable, config.primaryKey);
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(id));
                        ps.executeUpdate();
                        LOG.info("Deleted record: id={}", id);
                    }
                }
            } catch (Exception e) {
                LOG.error("Error processing CDC record", e);
            }
        }

        /**
         * 从Struct字符串中提取某个section (after或before)
         */
        private String extractStructSection(String record, String section) {
            try {
                // 查找 section=Struct{...}
                String pattern = section + "=Struct\\{([^}]+)\\}";
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher m = p.matcher(record);
                if (m.find()) {
                    return m.group(1);
                }
            } catch (Exception e) {
                LOG.error("Error extracting section: {}", section, e);
            }
            return null;
        }

        /**
         * 从Struct数据中提取字段值
         */
        private String extractStructField(String data, String fieldName) {
            try {
                // 查找 fieldName=value
                String pattern = fieldName + "=([^,}]+)";
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher m = p.matcher(data);
                if (m.find()) {
                    return m.group(1).trim();
                }
            } catch (Exception e) {
                LOG.error("Error extracting field: {}", fieldName, e);
            }
            return null;
        }
    }

    /**
     * 停止同步
     */
    public void stop() {
        isRunning = false;
        taskStatus.setStatus("STOPPED");
        taskStatus.setLastUpdateTime(LocalDateTime.now());
        LOG.info("Task {} stopped", taskConfig.getName());
    }

    /**
     * 获取任务状态
     */
    public SyncTaskStatus getTaskStatus() {
        return taskStatus;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * CDC Debezium反序列化器
     */
    public static class CdcDebeziumDeserializationSchema implements DebeziumDeserializationSchema<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void deserialize(SourceRecord record, Collector<String> out) {
            Struct value = (Struct) record.value();
            String jsonString = value.toString();
            out.collect(jsonString);
        }

        @Override
        public TypeInformation<String> getProducedType() {
            return TypeInformation.of(String.class);
        }
    }
}
