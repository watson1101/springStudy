# 热点新闻采集与消费 - 设计方案

> 项目：microservice-learn（Spring Cloud Alibaba 微服务学习项目）
> 设计日期：2026-09-20
> 目标：定时抓取今日头条热榜 → 投递 RocketMQ → 消费入库 MySQL

---

## 一、总体架构

```
┌─────────────────────────┐     ┌──────────────┐     ┌─────────────────────────┐
│ service-hotnews-collector│────▶│  RocketMQ    │────▶│ service-hotnews-consumer│
│  (定时采集, 每30分钟)     │     │ 192.168.0.27 │     │   (@RocketMQMessageListener)│
│  @Scheduled + HTTP抓取   │     │  :9876       │     │                         │
└─────────────────────────┘     └──────────────┘     └───────────┬─────────────┘
                                                                  ▼
                                                     ┌─────────────────────────┐
                                                     │ MySQL 192.168.0.27      │
                                                     │ 库: ms_ds_hotnews       │
                                                     └─────────────────────────┘
```

- 全部使用 **Java + Spring Boot**（与项目技术栈统一）
- 采集端与消费端为**两个独立微服务模块**

---

## 二、新增模块

| 模块 | 端口 | 职责 | Maven artifactId |
| --- | --- | --- | --- |
| 采集服务 | 8008 | 定时抓取头条热榜，投递 RocketMQ | `service-hotnews-collector` |
| 消费服务 | 8009 | 消费 RocketMQ，写入 MySQL | `service-hotnews-consumer` |

- 均注册到 Nacos（192.168.0.27:8848）
- 均继承父 pom（Spring Boot 4.0.0 / Spring Cloud 2025.1.3 / Alibaba 2025.1.0.0）

---

## 三、数据源

### 抓取目标
- **今日头条热榜**：`https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc`
- 返回 JSON，字段：`Title`（标题）、`HotValue`（热度）、`Url`（链接）、`ClusterId`（唯一ID）

### 数据库（新建）
- 库名：`ms_ds_hotnews`
- 字符集：`utf8mb4` / `utf8mb4_unicode_ci`
- 表名规范：沿用既有模块风格（如 `hot_news`），字段含 `create_time`

---

## 四、数据库表设计（ms_ds_hotnews）

### 表 1：hot_news（热榜数据表）

```sql
CREATE TABLE IF NOT EXISTS `hot_news` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cluster_id`   VARCHAR(64)  NOT NULL                COMMENT '头条热榜唯一ID',
  `title`        VARCHAR(512) NOT NULL                COMMENT '热点标题',
  `hot_value`    BIGINT       NOT NULL DEFAULT 0      COMMENT '热度值',
  `rank_no`      INT          NOT NULL DEFAULT 0      COMMENT '榜单排名(从1开始)',
  `source`       VARCHAR(32)  NOT NULL DEFAULT 'toutiao' COMMENT '来源(头条等)',
  `url`          VARCHAR(1024) DEFAULT NULL           COMMENT '详情链接',
  `batch_id`     VARCHAR(64)  DEFAULT NULL            COMMENT '采集批次ID',
  `collect_time` DATETIME     NOT NULL                COMMENT '本条采集时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0      COMMENT '软删除标记 0正常 1删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cluster_batch` (`cluster_id`, `batch_id`),
  KEY `idx_collect_time` (`collect_time`),
  KEY `idx_batch_id` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='今日头条热榜数据表';
```

### 表 2：hot_collect_log（采集批次日志表，可选但推荐）

```sql
CREATE TABLE IF NOT EXISTS `hot_collect_log` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id`      VARCHAR(64)  NOT NULL                COMMENT '采集批次ID',
  `source`        VARCHAR(32)  NOT NULL DEFAULT 'toutiao' COMMENT '来源',
  `total_count`   INT          NOT NULL DEFAULT 0      COMMENT '本次采集条数',
  `success`       TINYINT      NOT NULL DEFAULT 1      COMMENT '是否成功 1成功 0失败',
  `error_msg`     VARCHAR(1024) DEFAULT NULL           COMMENT '错误信息',
  `collect_time`  DATETIME     NOT NULL                COMMENT '采集时间',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_collect_time` (`collect_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热榜采集批次日志表';
```

---

## 五、RocketMQ 设计

| 项 | 值 |
| --- | --- |
| NameServer | `192.168.0.27:9876` |
| Topic | `hotnews-topic` |
| Producer Group | `hotnews-producer-group` |
| Consumer Group | `hotnews-consumer-group` |
| 消息体 | JSON 数组（一批热榜数据）或单条 JSON |
| 投递保证 | 至少一次 |
| 消费模式 | 集群消费（CLUSTERING） |

---

## 六、技术选型

| 层 | 技术 | 版本 | 说明 |
| --- | --- | --- | --- |
| 框架 | Spring Boot | 4.0.0 | 继承父 pom |
| 微服务 | Spring Cloud Alibaba | 2025.1.0.0 | Nacos 注册/配置 |
| MQ 客户端 | `rocketmq-spring-boot-starter` | 与 RocketMQ 5.3.1 兼容版本 | 生产/消费注解化 |
| 持久层 | MyBatis-Plus | 3.5.17 | 项目已在用 |
| 数据库 | MySQL | 8.4 | 服务器已装 |
| HTTP 抓取 | Java 内置 `HttpClient`（JDK17+）或 Hutool | — | 无额外重依赖优先 |
| JSON | fastjson / Jackson | — | 项目已在用 |
| 参数校验 | Hibernate Validator | — | 可选 |

---

## 七、关键实现要点

### 采集端 service-hotnews-collector
1. **定时任务 cron 从 Nacos 动态读取**（不写死，见下方「十、定时任务 Nacos 配置」）
2. 用 `HttpClient` 请求头条接口（带 `User-Agent`）
3. 解析 JSON → 组装 `HotNews` 列表 + 生成 `batchId`（如 `yyyyMMddHHmmss`）
4. 通过 `RocketMQTemplate` 投递到 `hotnews-topic`
5. 记录采集日志（可选：直接写 hot_collect_log）
6. 提供手动触发接口（便于测试）：`POST /api/hotnews/collect`

### 定时任务执行方式（动态 cron）
- 不使用 `@Scheduled(cron="...")` 写死表达式
- 改用 **`ScheduledTaskRegistrar` + `Trigger`**（或 `ThreadPoolTaskScheduler`）
  在运行时从配置读取 cron，实现**无需重启即可调整频率**
- 配合 `@RefreshScope` / Nacos 配置监听，配置变更后自动重载 cron

### 消费端 service-hotnews-consumer
1. `@RocketMQMessageListener(topic="hotnews-topic", consumerGroup="hotnews-consumer-group")`
2. 反序列化消息 → 批量入库 `hot_news`（MyBatis-Plus 批量插入）
3. 幂等：靠 `uk_cluster_batch` 唯一键去重（`INSERT IGNORE` 或存在则跳过）
4. 查询接口：`GET /api/hotnews/latest`（最新一批）、`GET /api/hotnews/list?date=`

---

## 八、目录结构（拟）

```
microservice-learn/
├── service-hotnews-collector/
│   ├── pom.xml
│   └── src/main/java/com/ms/learn/hotnews/collector/
│       ├── HotNewsCollectorApplication.java
│       ├── config/         (RocketMQ 配置)
│       ├── job/            (定时任务)
│       ├── client/         (头条 HTTP 抓取)
│       ├── dto/            (抓取响应对象)
│       └── service/
├── service-hotnews-consumer/
│   ├── pom.xml
│   └── src/main/java/com/ms/learn/hotnews/consumer/
│       ├── HotNewsConsumerApplication.java
│       ├── listener/       (@RocketMQMessageListener)
│       ├── entity/         (HotNews)
│       ├── mapper/
│       ├── service/
│       └── controller/
└── sql/ms_ds_hotnews/init.sql
```

---

## 九、待确认事项

1. **消息粒度**：每批投递一条「批量消息」还是循环投递「单条消息」？
   - 建议：批量消息（减少 MQ 连接开销），消费端解析数组
2. ~~采集时机~~ → 改为**从 Nacos 动态读取**（见第十节）
3. **是否保留 hot_collect_log 表**（采集日志）？
4. **是否需要手动触发接口**（便于测试）？
5. **RocketMQ 版本兼容**：服务器是 5.3.1，客户端 starter 版本待定（需实测）

---

## 十、定时任务 Nacos 配置（动态 cron）

### Nacos 配置项（DataId: `service-hotnews-collector.yaml`）

```yaml
hotnews:
  collect:
    # 定时任务 cron 表达式（默认每 30 分钟）
    cron: "0 0/30 * * * ?"
    # 是否启用定时采集
    enabled: true
    # 抓取目标地址
    url: "https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc"
    # 抓取超时（毫秒）
    timeout: 10000
  mq:
    topic: hotnews-topic
    producer-group: hotnews-producer-group
```

### 实现方式（二选一，推荐 A）

**方案 A：`ScheduledTaskRegistrar` + `Trigger`（推荐）**
- 定义一个 `SchedulingConfigurer`，用 `Trigger` 从 `@Value("${hotnews.collect.cron}")` 读取 cron
- 配合 `@RefreshScope`，Nacos 配置变更时可动态更新 cron
- 无需重启，改 Nacos 配置即生效

**方案 B：`ThreadPoolTaskScheduler` + 配置监听**
- 用 `NacosConfigManager` / `@NacosConfigListener` 手动监听配置变化
- 变化时 `scheduler.schedule(task, new CronTrigger(newCron))` 重新注册

### 关键点
- **cron 不写死**，全部来自 Nacos 配置
- `enabled=false` 时可一键关闭定时采集（便于维护）
- 手动触发接口不受 cron 限制，测试用
