# ETL数据同步模块 - 面试文档

## 目录
1. [项目概述](#项目概述)
2. [技术栈详解](#技术栈详解)
3. [架构设计](#架构设计)
4. [执行流程](#执行流程)
5. [Flink快速入门](#flink快速入门)
6. [面试题与答案](#面试题与答案)
7. [开发流程](#开发流程)

---

## 项目概述

### 项目背景
这是一个基于 **Flink CDC** 的实时数据同步ETL模块，支持将源数据库的数据变更实时捕获并同步到目标数据库。

### 核心功能
- **全量同步**: 将源表数据一次性全量同步到目标表
- **增量同步(CDC)**: 实时捕获源数据库的变更操作(INSERT/UPDATE/DELETE)并同步
- **多数据库支持**: 支持MySQL、PostgreSQL等主流数据库
- **任务管理**: 支持任务的创建、启动、停止、状态监控

### 技术亮点
- 使用 **Change Data Capture (CDC)** 技术实现低延迟的数据同步
- 基于 **Flink** 分布式计算框架，支持高吞吐量数据处理
- **RESTful API** 接口，便于集成和调度

---

## 技术栈详解

### 核心技术栈

| 技术组件 | 版本 | 用途 |
|----------|------|------|
| **Apache Flink** | 1.16.3 | 分布式流处理引擎 |
| **Flink CDC** | 2.4.2 | 变更数据捕获连接器 |
| **Spring Boot** | 3.3.11 | 应用框架 |
| **Spring Cloud** | 2023.0.1 | 微服务框架 |
| **Nacos** | 2023.0.3.2 | 服务发现与配置中心 |
| **MySQL Connector** | 8.0.33 | MySQL JDBC驱动 |

### 版本兼容性说明
```
Flink 1.16.3 + Flink CDC 2.4.2 + Flink JDBC Connector 3.1.0-1.16
```
这个版本组合经过测试，完全兼容且稳定。

---

## 架构设计

### 整体架构图
```
┌─────────────────────────────────────────────────────────────────┐
│                         ETL Module                               │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌──────────────┐    ┌─────────────────┐    │
│  │ EtlController│───▶│ EtlSyncService│───▶│   CdcSyncJob    │    │
│  │  (REST API) │    │ (任务管理)    │    │  (Flink Job)    │    │
│  └─────────────┘    └──────────────┘    └─────────────────┘    │
│                             │                     │              │
│                             ▼                     ▼              │
│                    ┌──────────────┐    ┌─────────────────┐      │
│                    │EtlConfigService│   │ MySqlSource     │      │
│                    │  (配置管理)   │   │ (CDC Source)    │      │
│                    └──────────────┘    └─────────────────┘      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Source Database                             │
│                      (MySQL/PostgreSQL)                          │
└─────────────────────────────────────────────────────────────────┘
```

### 核心类说明

#### 1. EtlApplication
- **职责**: Spring Boot启动类
- **关键配置**: 排除数据源自动配置 `@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})`
- **原因**: ETL模块作为CDC客户端，不需要自己的数据库

#### 2. EtlController
- **职责**: 暴露RESTful API接口
- **核心接口**:
  - `POST /etl/sync/full` - 启动全量同步
  - `POST /etl/sync/incremental` - 启动增量同步
  - `POST /etl/sync/stop/{taskId}` - 停止同步任务
  - `GET /etl/sync/status/{taskId}` - 获取任务状态

#### 3. CdcSyncJob
- **职责**: Flink CDC同步任务的核心实现
- **关键方法**:
  - `startIncrementalSync()`: 启动CDC增量同步
  - `createMySqlDebeziumSource()`: 创建MySQL CDC数据源
  - `JsonDebeziumDeserializationSchema`: 自定义反序列化器

#### 4. SyncTaskConfig
- **职责**: 同步任务配置实体
- **核心属性**:
  - `mode`: 同步模式 (FULL/INCREMENTAL/ALL)
  - `source`: 源数据库配置
  - `target`: 目标数据库配置
  - `tables`: 要同步的表配置列表

---

## 执行流程

### CDC增量同步流程

```
1. 客户端请求
   │
   ▼
2. EtlController.startIncrementalSync()
   │
   ▼
3. EtlSyncService.startIncrementalSync()
   │  └── 创建 CdcSyncJob 实例
   │
   ▼
4. CdcSyncJob.startIncrementalSync()
   │  ├── 创建 Flink StreamExecutionEnvironment
   │  ├── 配置并行度和重启策略
   │  └── 为每个表创建 CDC Source
   │
   ▼
5. MySqlSource Builder 构建
   │  ├── 配置数据库连接信息
   │  ├── 配置要捕获的表
   │  └── 设置反序列化器
   │
   ▼
6. Flink执行环境
   │  ├── fromSource() - 创建数据流
   │  ├── process() - 数据处理
   │  └── executeAsync() - 异步执行
   │
   ▼
7. MySQL Binlog监听
   │  ├── Debezium捕获Binlog事件
   │  ├── 解析为SourceRecord
   │  └── 反序列化为JSON
   │
   ▼
8. 数据输出
   └── 通过Collector输出到下游
```

### 全量同步流程
全量同步实际通过CDC的**初始快照**功能实现：
```
MySqlSource配置 ──▶ StartupOptions.initial()
                        │
                        ▼
              ┌─────────────────────┐
              │  1. 读取当前表结构   │
              │  2. 全表数据扫描     │
              │  3. 生成一致性快照   │
              │  4. 继续读取Binlog   │
              └─────────────────────┘
                        │
                        ▼
                   实时增量同步
```

---

## Flink快速入门

### 什么是Apache Flink?

Apache Flink是一个**分布式流处理引擎**，支持：
- **有界流**（批处理）和**无界流**（流处理）的统一处理
- **精确一次**（Exactly-Once）语义保证
- **低延迟**（毫秒级）和高吞吐量
- **状态管理**和**容错机制**

### Flink核心概念

#### 1. DataStream（数据流）
```java
// 数据流是Flink处理的基本单元
DataStream<String> stream = env.fromSource(source, strategy, "name");
```

#### 2. Source（数据源）
```java
// Source是数据流的来源
Source<String, ?, ?> source = MySqlSource.<String>builder()
    .hostname("localhost")
    .port(3306)
    .databaseList("mydb")
    .tableList("mydb.users")
    .username("root")
    .password("password")
    .deserializer(new JsonDebeziumDeserializationSchema())
    .build();
```

#### 3. Transformation（转换操作）
```java
// ProcessFunction是最底层的转换函数
stream.process(new ProcessFunction<String, String>() {
    @Override
    public void processElement(String value, Context ctx, Collector<String> out) {
        // 处理每一条记录
        out.collect(value);
    }
});
```

#### 4. Sink（输出）
```java
// Sink是数据流的目的地
stream.addSink(sink);
```

### Flink执行环境
```java
// 创建流执行环境
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

// 设置并行度
env.setParallelism(2);

// 设置重启策略
env.setRestartStrategy(RestartStrategies.noRestart());

// 启用Checkpoint
env.enableCheckpointing(60000); // 60秒
```

### Flink CDC核心概念

#### CDC（Change Data Capture）
CDC是一种**数据变更捕获技术**，可以实时捕获数据库的增删改操作。

#### Debezium
Debezium是Flink CDC的底层实现，通过解析**Binlog**（MySQL）或**WAL**（PostgreSQL）来捕获变更。

#### Binlog（Binary Log）
MySQL的二进制日志，记录所有数据变更操作：
- **Statement格式**: 记录SQL语句
- **Row格式**: 记录每一行的变更（CDC使用此格式）
- **Mixed格式**: 混合模式

### Flink编程模型
```
Source ──▶ Transformation ──▶ Sink
  │             │                │
  ▼             ▼                ▼
数据输入      数据处理        数据输出
```

---

## 面试题与答案

### Flink相关面试题

#### Q1: Flink和Spark Streaming的区别?

| 特性 | Flink | Spark Streaming |
|------|-------|-----------------|
| **处理模型** | 真正的流处理，一条一条处理 | 微批处理，一批一批处理 |
| **延迟** | 毫秒级 | 秒级 |
| **窗口机制** | 原生支持，灵活 | 基于批处理 |
| **状态管理** | 强大的状态后端 | 较弱 |
| **容错** | Checkpoint机制 | RDD血统 lineage |
| **吞吐量** | 高 | 极高 |

**答案要点**:
- Flink是**真正的流处理引擎**，事件一到即处理
- Spark Streaming是**微批处理**，将流切成小批次处理
- Flink延迟更低（毫秒级 vs 秒级），适合实时性要求高的场景

#### Q2: 什么是Flink的Checkpoint机制?

**答案**:
Checkpoint是Flink的**容错机制**，通过**定期保存状态快照**实现精确一次语义。

**工作原理**:
```
1. Flink定期触发Checkpoint（如每60秒）
2. 将所有算子的状态保存到外部存储（如HDFS）
3. 记录数据源的消费位置（如Kafka offset）
4. 如果任务失败，从最近的Checkpoint恢复

Checkpoint Barrier流动:
Source ──[Barrier]──▶ Operator1 ──[Barrier]──▶ Operator2
       │                 │                    │
       ▼                 ▼                    ▼
   保存Offset       保存状态1             保存状态2
```

#### Q3: Flink的Watermark是什么?

**答案**:
Watermark是**时间语义**的核心，用于处理**迟到数据**。

```
事件时间 vs 处理时间 vs 摄入时间:
- 事件时间(Event Time): 事件实际发生的时间
- 处理时间(Processing Time): 事件被处理的时间
- 摄入时间(Ingestion Time): 事件进入Flink的时间

Watermark = 当前最大事件时间 - 允许的延迟时间

例如: 最大事件时间 10:00:00，允许延迟5秒
      Watermark = 09:59:55

所有事件时间 < 09:59:55 的数据都已到达，可以触发窗口计算
```

#### Q4: Flink的并行度如何设置?

**答案**:
```java
// 全局并行度
env.setParallelism(4);

// 单个算子并行度
stream.map(...).setParallelism(2);

// 并行度设置原则:
// 1. 通常设置为集群CPU核心数的1-2倍
// 2. IO密集型任务可设置更高
// 3. 需要通过性能测试确定最优值
```

#### Q5: 什么是Flink的状态?

**答案**:
状态是**算子在处理过程中需要记住的信息**。

```
状态类型:
1. Keyed State: 按Key分区的状态（如每个用户的访问次数）
2. Operator State: 算子状态（如Kafka的offset）

状态后端:
1. MemoryStateBackend: 内存，适合开发测试
2. FsStateBackend: 文件系统，适合生产
3. RocksDBStateBackend: 嵌入式数据库，支持大状态
```

### CDC相关面试题

#### Q6: 什么是CDC?有哪些实现方式?

**答案**:
CDC（Change Data Capture）变更数据捕获技术。

```
实现方式:
1. 基于查询: 定时查询数据库比较（低效，不推荐）
2. 基于触发器: 数据库触发器捕获（影响数据库性能）
3. 基于日志: 解析事务日志（Binlog/WAL，推荐）

CDC工具:
- Debezium (开源，支持多种数据库)
- Flink CDC (基于Debezium)
- Canal (阿里巴巴开源)
- Maxwell
```

#### Q7: MySQL的Binlog有几种格式?

**答案**:
```
1. STATEMENT: 记录SQL语句
   优点: 日志量小
   缺点: 可能导致主从数据不一致

2. ROW: 记录每一行的变更
   优点: 数据最准确，CDC必须使用此格式
   缺点: 日志量大

3. MIXED: 混合模式
   一般语句用STATEMENT，不确定的用ROW

配置: binlog_format=ROW
```

#### Q8: Flink CDC如何保证数据不丢失、不重复?

**答案**:
```
不丢失 - Checkpoint机制:
1. 定期保存消费位置（Binlog position）
2. 任务失败从Checkpoint恢复

不重复 - 精确一次语义:
1. Checkpoint保存状态和位置
2. 恢复时从Checkpoint位置重新消费
3. 配合幂等性写入实现端到端精确一次
```

#### Q9: CDC同步延迟如何监控?

**答案**:
```
监控指标:
1. Binlog延迟: 当前同步位置与最新位置的时间差
2. 吞吐量: 每秒处理的事件数
3. 任务状态: 运行中/失败/重启

Flink Metrics:
- currentEmitEventTimeLag: 事件时间延迟
- numRecordsIn: 输入记录数
- numRecordsOut: 输出记录数
```

### Spring Boot相关面试题

#### Q10: 为什么EtlApplication要排除DataSource自动配置?

**答案**:
```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
```

**原因**:
1. 父POM包含`spring-boot-starter-jdbc`依赖
2. Spring Boot会自动配置DataSource，要求数据库连接配置
3. ETL模块是**CDC客户端**，不需要自己的数据库
4. 排除自动配置避免启动失败

#### Q11: @RestController和@Controller的区别?

**答案**:
```
@RestController = @Controller + @ResponseBody

@Controller:
- 返回View视图
- 需要配合@ResponseBody返回JSON

@RestController:
- 所有方法默认返回JSON
- 不需要每个方法加@ResponseBody
```

### 项目实战面试题

#### Q12: 如何实现CDC任务的热重启?

**答案**:
```java
// 1. 保存Checkpoint到外部存储
env.getCheckpointConfig().setCheckpointStorage("hdfs:///checkpoints/");

// 2. 任务失败从Checkpoint恢复
// Flink会自动从最近的Checkpoint恢复

// 3. 通过Savepoint手动触发保存
./flink savepoint <jobId> <targetPath>
```

#### Q13: 如何处理同步过程中的数据类型转换?

**答案**:
```
1. 使用Debezium的数据类型映射
2. 自定义DeserializationSchema处理
3. 配置字段映射规则

本项目采用JSON格式:
- MySQL数据转为JSON
- 保持数据类型信息
- 下游解析JSON时处理类型转换
```

#### Q14: 如何保证跨库事务的一致性?

**答案**:
```
分布式事务挑战:
1. 两阶段提交(2PC): 性能差，不推荐
2. 最终一致性: 通过补偿机制实现
3. CDC方式: 源库提交后才同步，保证源库一致

本方案:
- 只同步成功提交的事务
- Binlog只记录已提交事务
- 目标库失败可重试
```

---

## 开发流程

### 环境准备

#### 1. 开发环境要求
```bash
# JDK版本
Java 21

# Maven版本
Maven 3.6+

# 数据库准备
MySQL 8.0+ (需要开启Binlog)
```

#### 2. MySQL Binlog配置
```sql
-- 检查Binlog是否开启
SHOW VARIABLES LIKE 'log_bin';

-- 检查Binlog格式（必须是ROW）
SHOW VARIABLES LIKE 'binlog_format';

-- my.cnf配置
[mysqld]
server-id=1
log-bin=mysql-bin
binlog_format=ROW
binlog_row_image=FULL
expire_logs_days=7
```

### 本地运行

#### 1. 编译打包
```bash
cd etl
mvn clean package -DskipTests
```

#### 2. 运行应用
```bash
# 禁用Nacos运行（本地测试）
java -Dspring.cloud.nacos.discovery.enabled=false \
     -Dspring.cloud.nacos.config.enabled=false \
     -jar target/etl-1.0-SNAPSHOT.jar
```

#### 3. API测试
```bash
# 启动增量同步
curl -X POST http://localhost:8010/etl/sync/incremental \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-sync",
    "mode": "INCREMENTAL",
    "source": {
      "type": "MYSQL",
      "host": "localhost",
      "port": 3306,
      "database": "source_db",
      "username": "root",
      "password": "password"
    },
    "target": {
      "type": "MYSQL",
      "host": "localhost",
      "port": 3306,
      "database": "target_db",
      "username": "root",
      "password": "password"
    },
    "tables": [{
      "sourceTable": "users",
      "targetTable": "users",
      "primaryKey": "id"
    }]
  }'

# 查询任务状态
curl http://localhost:8010/etl/sync/status/{taskId}

# 停止任务
curl -X POST http://localhost:8010/etl/sync/stop/{taskId}
```

### 调试技巧

#### 1. 日志级别调整
```yaml
# application.yml
logging:
  level:
    com.hong.etl: DEBUG
    org.apache.flink: INFO
    com.ververica.cdc: DEBUG
```

#### 2. Flink Web UI
```bash
# Flink CDC会启动本地Web UI
# 默认端口: 8081
# 访问: http://localhost:8081
```

#### 3. 常见问题排查
```
问题1: 连接MySQL失败
解决: 检查MySQL服务、用户权限、防火墙

问题2: 没有捕获到变更
解决: 检查Binlog格式是否为ROW，表是否有主键

问题3: 任务启动失败
解决: 检查parallelism设置，资源是否足够
```

### 部署流程

#### 1. 构建Docker镜像
```dockerfile
FROM openjdk:21-slim
COPY target/etl-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 2. Kubernetes部署
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: etl-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: etl
  template:
    metadata:
      labels:
        app: etl
    spec:
      containers:
      - name: etl
        image: etl:latest
        ports:
        - containerPort: 8010
```

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2024-05-23 | 初始版本，支持MySQL CDC |

---

## 常用链接

- [Apache Flink官方文档](https://flink.apache.org/)
- [Flink CDC GitHub](https://github.com/ververica/flink-cdc-connectors)
- [Debezium官方文档](https://debezium.io/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)

---

**祝面试顺利！** 🚀
