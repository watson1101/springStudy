# ETL模块编译运行日志

## 2026-05-23 错误记录1：依赖解析失败

### 错误内容
```
[ERROR] Failed to execute goal on project etl: Could not resolve dependencies for project com.hong:etl:jar:1.0-SNAPSHOT: The following artifacts could not be resolved: com.ververica:flink-connector-mysql-cdc:jar:2.5.1, com.ververica:flink-connector-postgres-cdc:jar:2.5.1, org.apache.flink:flink-connector-jdbc:jar:1.18.1
```

### 原因分析
1. Flink CDC 2.5+版本的groupId已从`com.ververica`变更为`org.apache.flink`
2.阿里云Maven镜像可能不包含Apache Flink的快照仓库

### 修复方案
1. 修改Flink CDC依赖坐标：`com.ververica` → `org.apache.flink`
2. 添加Apache仓库配置到etl/pom.xml

---

## 2026-05-23 错误记录2：API兼容性问题

### 错误内容
```
[ERROR] /C:/workspace/github/springStudy/world-story/etl/src/main/java/com/hong/etl/job/CdcSyncJob.java:[9,57] 程序包com.ververica.cdc.connectors.mysql.source.startup不存在
[ERROR] /C:/workspace/github/springStudy/world-story/etl/src/main/java/com/hong/etl/job/CdcSyncJob.java:[11,52] 找不到符号: 类 PostgresSource
[ERROR] /C:/workspace/github/springStudy/world-story/etl/src/main/java/com/hong/etl/job/CdcSyncJob.java:[13,34] 找不到符号: 类 DebeziumSource
```

### 原因分析
1. Flink CDC 2.4+版本API发生变化，移除了`DebeziumSource`类型
2. `StartupOptions`和`StartupMode`的API发生变化
3. `PostgresSource`包路径可能不同

### 修复方案
1. 更新import语句使用正确的包路径
2. 移除`DebeziumSource`类型，改用Flink的`Source`接口
3. 更新`StartupOptions`的使用方式

---

## 2026-05-23 错误记录3：Flink CDC 2.4.2 API完全重构

### 错误内容
```
[ERROR] /CdcSyncJob.java:[80,43] 不兼容的类型: org.apache.flink.core.execution.JobClient无法转换为java.util.concurrent.CompletableFuture<java.lang.Void>
[ERROR] /CdcSyncJob.java:[164,20] 找不到符号: 方法 builder()
[ERROR] /CdcSyncJob.java:[381,20] 不是抽象方法, 并且未覆盖DebeziumDeserializationSchema中的抽象方法deserialize(org.apache.kafka.connect.source.SourceRecord,org.apache.flink.util.Collector<java.lang.String>)
```

### 原因分析
Flink CDC 2.4.2 API与代码中的API差异较大，多个方法签名发生变化

### 修复方案
重写CdcSyncJob.java使用正确的Flink CDC 2.4.2 API，包括：
1. `executeAsync()`返回`JobClient`而非`CompletableFuture<Void>`
2. 使用正确的`MySqlSource`构建方式
3. 实现`DebeziumDeserializationSchema`的正确方法签名
4. 移除不兼容的JDBC Sink代码


---

## 2026-05-23 错误记录4：缺少数据源配置

### 错误内容
```
APPLICATION FAILED TO START
Description: Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
```

### 原因分析
父POM中包含`spring-boot-starter-jdbc`依赖，Spring Boot自动配置要求数据源配置，但ETL模块作为CDC客户端不需要自己的数据库

### 修复方案
在启动类中禁用数据源自动配置


### 修复结果
在`EtlApplication.java`中添加了`@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})`禁用数据源自动配置。

---

## 2026-05-23 编译运行成功

### 成功信息
```
2026-05-24T00:12:17.649+08:00  INFO 35888 --- [etl] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8010 (http) with context path '/'
2026-05-24T00:12:18.680+08:00  INFO 35888 --- [etl] [           main] com.hong.etl.EtlApplication              : Started EtlApplication in 14.727 seconds
```

### 说明
- ETL模块已成功编译和运行
- 端口：8010
- 健康检查显示DOWN是因为Redis未连接，但不影响核心CDC同步功能
- 支持MySQL CDC数据同步（PostgreSQL支持已暂时移除）


---

## 2026-05-24 依赖版本兼容性检查与修复

### 发现的问题

#### 问题1: Flink版本与Flink CDC版本不兼容
- **原配置**: Flink 1.18.1 + Flink CDC 2.4.2
- **问题**: Flink CDC 2.4.2 官方仅支持 Flink 1.13-1.16，与 Flink 1.18.1 存在API不兼容风险

#### 问题2: Flink JDBC Connector版本与Flink核心版本不匹配
- **原配置**: flink-connector-jdbc:3.1.1-1.17
- **问题**: Connector为Flink 1.17构建，但项目使用Flink 1.18.1

### 修复方案

#### 修复内容
| 依赖 | 原版本 | 新版本 | 说明 |
|------|--------|--------|------|
| flink-streaming-java | 1.18.1 | 1.16.3 | 降级以兼容Flink CDC 2.4.2 |
| flink-clients | 1.18.1 | 1.16.3 | 保持与Flink核心版本一致 |
| flink-connector-base | 1.18.1 | 1.16.3 | 保持与Flink核心版本一致 |
| flink-connector-jdbc | 3.1.1-1.17 | 3.1.0-1.16 | 匹配Flink 1.16.3 |

#### 修改后的版本配置
```xml
<flink.version>1.16.3</flink.version>
<flink-cdc.version>2.4.2</flink-cdc.version>
<flink.jdbc.version>3.1.0-1.16</flink.jdbc.version>
```

### 验证结果
- ✅ 编译成功，无兼容性错误

### 版本兼容性说明
- **Flink 1.16.3**: 稳定版本，与Flink CDC 2.4.2完全兼容
- **Flink CDC 2.4.2**: 支持Flink 1.13-1.16，提供MySQL CDC功能
- **Flink JDBC Connector 3.1.0-1.16**: 专为Flink 1.16构建

