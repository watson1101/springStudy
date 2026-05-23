package com.hong.etl.job;

import com.hong.etl.entity.DataSourceConfig;
import com.hong.etl.entity.SyncTaskConfig;
import com.hong.etl.entity.SyncTaskStatus;
import com.hong.etl.entity.TableConfig;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.config.ServerIdRange;
import com.ververica.cdc.connectors.mysql.source.startup.StartupMode;
import com.ververica.cdc.connectors.mysql.source.startup.StartupOptions;
import com.ververica.cdc.connectors.postgres.source.PostgresSource;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.ververica.cdc.debezium.DebeziumSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Flink CDC同步作业
 * 支持MySQL和PostgreSQL的CDC数据同步
 */
public class CdcSyncJob {
    private static final Logger LOG = LoggerFactory.getLogger(CdcSyncJob.class);

    private final SyncTaskConfig taskConfig;
    private final SyncTaskStatus taskStatus;
    private StreamExecutionEnvironment env;
    private boolean isRunning = false;
    private CompletableFuture<Void> executionFuture;

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
     * 启动全量同步
     */
    public void startFullSync() throws Exception {
        LOG.info("Starting full sync for task: {}", taskConfig.getName());
        taskStatus.setStatus("RUNNING");
        taskStatus.setStartTime(java.time.LocalDateTime.now());
        isRunning = true;

        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(taskConfig.getParallelism());
        env.setRestartStrategy(RestartStrategies.noRestart());

        if (taskConfig.getEnableCheckpoint()) {
            env.enableCheckpointing(taskConfig.getCheckpointInterval());
        }

        for (TableConfig tableConfig : taskConfig.getTables()) {
            syncTableFullData(tableConfig);
        }

        executionFuture = env.executeAsync();
        LOG.info("Full sync started for task: {}", taskConfig.getName());
    }

    /**
     * 启动增量同步(实时CDC)
     */
    public void startIncrementalSync() throws Exception {
        LOG.info("Starting incremental sync for task: {}", taskConfig.getName());
        taskStatus.setStatus("RUNNING");
        taskStatus.setStartTime(java.time.LocalDateTime.now());
        isRunning = true;

        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(taskConfig.getParallelism());
        env.setRestartStrategy(RestartStrategies.noRestart());

        if (taskConfig.getEnableCheckpoint()) {
            env.enableCheckpointing(taskConfig.getCheckpointInterval());
        }

        for (TableConfig tableConfig : taskConfig.getTables()) {
            syncTableIncremental(tableConfig);
        }

        executionFuture = env.executeAsync();
        LOG.info("Incremental sync started for task: {}", taskConfig.getName());
    }

    /**
     * 全量同步单张表
     */
    private void syncTableFullData(TableConfig tableConfig) throws Exception {
        DataSourceConfig source = taskConfig.getSource();
        DataSourceConfig target = taskConfig.getTarget();

        SourceFunction<String> sourceFunction = createSourceFunction(source, tableConfig);

        DataStreamSource<String> streamSource = env.addSource(sourceFunction)
                .name("Full Sync Source - " + tableConfig.getSourceTable());

        SingleOutputStreamOperator<String> processedStream = streamSource
                .process(new FullSyncProcessFunction())
                .name("Full Sync Processor - " + tableConfig.getSourceTable());

        writeToTarget(processedStream, tableConfig, target);
    }

    /**
     * 增量同步单张表(CDC)
     */
    private void syncTableIncremental(TableConfig tableConfig) throws Exception {
        DataSourceConfig source = taskConfig.getSource();

        DebeziumSource<String> cdcSource = createDebeziumSource(source, tableConfig);

        DataStream<String> cdcStream = env.fromSource(
                cdcSource,
                WatermarkStrategy.noWatermarks(),
                "CDC Source - " + tableConfig.getSourceTable()
        );

        SingleOutputStreamOperator<String> processedStream = cdcStream
                .process(new CdcProcessFunction())
                .name("CDC Processor - " + tableConfig.getSourceTable());

        writeToTargetIncremental(processedStream, tableConfig, source);
    }

    /**
     * 创建SourceFunction
     */
    private SourceFunction<String> createSourceFunction(DataSourceConfig source, TableConfig tableConfig) {
        switch (source.getType().toUpperCase()) {
            case "MYSQL":
                return createMySqlSourceFunction(source, tableConfig);
            case "POSTGRESQL":
                return createPostgresSourceFunction(source, tableConfig);
            default:
                throw new IllegalArgumentException("不支持的数据库类型: " + source.getType());
        }
    }

    /**
     * 创建MySQL Source
     */
    private SourceFunction<String> createMySqlSourceFunction(DataSourceConfig source, TableConfig tableConfig) {
        MySqlSource.Builder<String> builder = MySqlSource.builder()
                .hostname(source.getHost())
                .port(source.getPort())
                .databaseList(source.getDatabase())
                .tableList(source.getDatabase() + "." + tableConfig.getSourceTable())
                .username(source.getUsername())
                .password(source.getPassword())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .serverIdRange(ServerIdRange.valueOf("500000-500099"));

        if (tableConfig.getColumns() != null && !tableConfig.getColumns().isEmpty()) {
            builder.columnSelect(List.of(tableConfig.getColumns().split(",")));
        }

        return builder.build();
    }

    /**
     * 创建PostgreSQL Source
     */
    private SourceFunction<String> createPostgresSourceFunction(DataSourceConfig source, TableConfig tableConfig) {
        PostgresSource.Builder<String> builder = PostgresSource.builder()
                .hostname(source.getHost())
                .port(source.getPort())
                .database(source.getDatabase())
                .schemaList("public")
                .tableList(tableConfig.getSourceTable())
                .username(source.getUsername())
                .password(source.getPassword())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .slotName("flink_cdc_" + UUID.randomUUID().toString().replace("-", ""));

        return builder.build();
    }

    /**
     * 创建Debezium Source(用于增量同步)
     */
    private DebeziumSource<String> createDebeziumSource(DataSourceConfig source, TableConfig tableConfig) {
        switch (source.getType().toUpperCase()) {
            case "MYSQL":
                return createMySqlDebeziumSource(source, tableConfig);
            case "POSTGRESQL":
                return createPostgresDebeziumSource(source, tableConfig);
            default:
                throw new IllegalArgumentException("不支持的数据库类型: " + source.getType());
        }
    }

    private DebeziumSource<String> createMySqlDebeziumSource(DataSourceConfig source, TableConfig tableConfig) {
        StartupOptions startupOptions = new StartupOptions();
        startupOptions.startupMode = StartupMode.INITIAL;

        return MySqlSource.<String>builder()
                .hostname(source.getHost())
                .port(source.getPort())
                .databaseList(source.getDatabase())
                .tableList(source.getDatabase() + "." + tableConfig.getSourceTable())
                .username(source.getUsername())
                .password(source.getPassword())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .serverIdRange(ServerIdRange.valueOf("500000-500099"))
                .startupOptions(startupOptions)
                .build();
    }

    private DebeziumSource<String> createPostgresDebeziumSource(DataSourceConfig source, TableConfig tableConfig) {
        StartupOptions startupOptions = new StartupOptions();
        startupOptions.startupMode = StartupMode.INITIAL;

        return PostgresSource.<String>builder()
                .hostname(source.getHost())
                .port(source.getPort())
                .database(source.getDatabase())
                .schemaList("public")
                .tableList(tableConfig.getSourceTable())
                .username(source.getUsername())
                .password(source.getPassword())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .slotName("flink_cdc_" + UUID.randomUUID().toString().replace("-", ""))
                .startupOptions(startupOptions)
                .build();
    }

    /**
     * 写入目标数据库(全量同步)
     */
    private void writeToTarget(DataStream<String> stream, TableConfig tableConfig, DataSourceConfig target) {
        String insertSql = buildInsertSql(tableConfig);
        String jdbcUrl = target.buildJdbcUrl();

        stream.addSink(JdbcSink.sink(
                insertSql,
                new JdbcStatementBuilder<String>() {
                    @Override
                    public void accept(PreparedStatement statement, String data) throws SQLException {
                        parseAndFillParameters(statement, data, tableConfig);
                    }
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(taskConfig.getBatchSize())
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(jdbcUrl)
                        .withDriverName(getDriverName(target.getType()))
                        .withUsername(target.getUsername())
                        .withPassword(target.getPassword())
                        .build()
        ));
    }

    /**
     * 写入目标数据库(增量同步)
     */
    private void writeToTargetIncremental(DataStream<String> stream, TableConfig tableConfig, DataSourceConfig source) {
        String targetJdbcUrl = taskConfig.getTarget().buildJdbcUrl();

        stream.addSink(JdbcSink.<String>builder()
                .setSqlOptions(JdbcExecutionOptions.builder()
                        .withBatchSize(taskConfig.getBatchSize())
                        .build())
                .setJdbcOptions(new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(targetJdbcUrl)
                        .withDriverName(getDriverName(taskConfig.getTarget().getType()))
                        .withUsername(taskConfig.getTarget().getUsername())
                        .withPassword(taskConfig.getTarget().getPassword())
                        .build())
                .setInsertOrUpdateExecutor(new JdbcExactlyOnceOptions.JdbcExactlyOnceOptionsBuilder()
                        .withXa(true)
                        .build())
                .build());
    }

    /**
     * 构建插入SQL
     */
    private String buildInsertSql(TableConfig tableConfig) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableConfig.getTargetTable()).append(" (");

        String[] columns = tableConfig.getColumns() != null && !tableConfig.getColumns().isEmpty()
                ? tableConfig.getColumns().split(",")
                : new String[0];

        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i].trim());
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            sql.append("?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") ON DUPLICATE KEY UPDATE ");

        String[] primaryKeys = tableConfig.getPrimaryKey().split(",");
        for (int i = 0; i < primaryKeys.length; i++) {
            sql.append(primaryKeys[i].trim()).append("=VALUES(").append(primaryKeys[i].trim()).append(")");
            if (i < primaryKeys.length - 1) {
                sql.append(", ");
            }
        }

        return sql.toString();
    }

    /**
     * 解析JSON并填充参数
     */
    private void parseAndFillParameters(PreparedStatement statement, String data, TableConfig tableConfig) throws SQLException {
        try {
            String json = data;
            json = json.replace("{", "").replace("}", "").replace("\"", "");

            String[] columns = tableConfig.getColumns() != null && !tableConfig.getColumns().isEmpty()
                    ? tableConfig.getColumns().split(",")
                    : json.split(",");

            for (int i = 0; i < columns.length; i++) {
                String colName = columns[i].trim();
                String value = extractJsonValue(json, colName);
                statement.setString(i + 1, value);
            }
        } catch (Exception e) {
            LOG.error("Error parsing data: {}", data, e);
            taskStatus.setErrorRecords(taskStatus.getErrorRecords() + 1);
        }
    }

    private String extractJsonValue(String json, String key) {
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2 && kv[0].trim().equals(key)) {
                return kv[1].trim();
            }
        }
        return null;
    }

    private String getDriverName(String dbType) {
        switch (dbType.toUpperCase()) {
            case "MYSQL":
                return "com.mysql.cj.jdbc.Driver";
            case "POSTGRESQL":
                return "org.postgresql.Driver";
            default:
                throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
        }
    }

    /**
     * 停止同步
     */
    public void stop() {
        isRunning = false;
        taskStatus.setStatus("STOPPED");
        taskStatus.setLastUpdateTime(java.time.LocalDateTime.now());
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
     * 全量同步处理函数
     */
    private static class FullSyncProcessFunction extends ProcessFunction<String, String> {
        @Override
        public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
            out.collect(value);
        }
    }

    /**
     * CDC处理函数
     */
    private static class CdcProcessFunction extends ProcessFunction<String, String> {
        @Override
        public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
            out.collect(value);
        }
    }

    /**
     * JSON Debezium反序列化器
     */
    private static class JsonDebeziumDeserializationSchema implements DebeziumDeserializationSchema<String> {
        @Override
        public void deserialize(String record, Collector<String> out) throws Exception {
            out.collect(record);
        }

        @Override
        public TypeInformation<String> getProducedType() {
            return TypeInformation.of(String.class);
        }
    }
}
