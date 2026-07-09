# flink-demo1 Flink 流处理学习示例

## 模块简介
本模块用于学习和验证 Apache Flink 流处理框架的核心技术点。Flink 已安装在本地，通过 `127.0.0.1` 即可访问 Web UI 和作业管理接口。

## Flink 环境信息
| 组件 | 地址 | 说明 |
|------|------|------|
| Web UI | http://127.0.0.1:8081 | Flink 仪表盘，查看作业状态 |
| JobManager | 127.0.0.1:6123 | 作业提交和管理 |
| TaskManager | 127.0.0.1:6124 | 任务执行 |

## 示例列表

| 分类 | 文件 | 说明 | 运行命令 |
|------|------|------|----------|
| **批处理** | `WordCountBatchDemo.java` | 批处理 WordCount，从内存集合读取数据 | `batch` |
| **流处理** | `WordCountStreamingDemo.java` | 流式 WordCount，从 Socket 读取数据 | `stream` |
| **窗口计算** | `WindowWordCountDemo.java` | 基于处理时间的滚动窗口词频统计（5秒窗口） | `window` |
| **Kafka 数据源** | `KafkaSourceDemo.java` | 从 Kafka 主题消费 JSON 消息并解析 | `kafka-source` |
| **Kafka 数据汇** | `KafkaSinkDemo.java` | 模拟生成订单数据，处理后写入 Kafka | `kafka-sink` |

## 前置准备

### 1. Flink 本地集群（可选，LocalEnvironment 模式不需要）
```bash
# 启动 Flink 本地集群
cd flink-1.18.0
./bin/start-cluster.sh          # Linux/Mac
bin/start-cluster.bat           # Windows

# 确认启动成功：访问 http://127.0.0.1:8081
```
> 注：本模块默认在 LocalEnvironment 模式下运行，不需要启动 Flink 集群。
> 集群模式仅在将作业提交到 Dashboard 管理时使用。

### 2. Kafka（仅用于 kafka-source / kafka-sink 演示）
```bash
# 启动 ZooKeeper
cd kafka_2.12-3.6.1
bin/zookeeper-server-start.sh config/zookeeper.properties

# 启动 Kafka Broker
bin/kafka-server-start.sh config/server.properties
```

### 3. Socket 数据源（仅用于 stream / window 演示）
```bash
# Windows（推荐使用 Git Bash 或安装 ncat）
ncat -lk 9999    # 用于 stream 演示
ncat -lk 9998    # 用于 window 演示

# Linux / Mac
nc -lk 9999
nc -lk 9998
```

---

## 快速开始

### 编译

```bash
cd C:\workspace\github\springStudy\ms-demo
mvn clean compile -pl flink-demo1 -am
```

编译成功后 `flink-demo1/target/classes/` 下会生成所有 class 文件。

---

## 演示一：批处理 WordCount（最简单，无需外部依赖）

这是唯一不需要外部数据源的 demo，编译完就能直接跑。

### 运行命令
```bash
mvn exec:java -pl flink-demo1 -Dexec.mainClass="msdemo.hong.com.flinkdemo1.FlinkDemoRunner" -Dexec.args="batch"
```

### 预期输出
```
═══════ Flink 批处理 WordCount（内存数据源） ═══════

  批处理 WordCount 结果：
    apache : 2
    flink : 2
    is : 2
    a : 2
    framework : 1
    also : 1
    distributed : 1
    processing : 1
    engine : 1
    hadoop : 1
    and : 1
    are : 1
    both : 1
    big : 1
    data : 1
    tools : 1

═══════ 批处理 WordCount 结束 ═══════
```

### 验证方式
看到 `批处理 WordCount 结果` 以及各单词的统计数量即为成功。
作业会自动执行完毕并退出，不需要手动停止。

---

## 演示二：流式 WordCount（需要 netcat）

从 Socket 端口实时读取文本，流式统计每个单词出现的次数。

### 步骤
**终端 1** — 启动 Socket 数据源：
```bash
ncat -lk 9999
```

**终端 2** — 启动 Flink 作业：
```bash
mvn exec:java -pl flink-demo1 -Dexec.mainClass="msdemo.hong.com.flinkdemo1.FlinkDemoRunner" -Dexec.args="stream"
```

在**终端 1**中逐行输入文本（每次回车发送一行）：
```
hello world
hello flink
flink stream processing
```

### 预期输出（终端 2）
```
═══════ Flink 流式 WordCount（Socket 数据源） ═══════
  请确保已运行: nc -lk 9999
  在 nc 终端输入文本即可看到统计结果...

WordCount 结果> (hello,1)
WordCount 结果> (world,1)
WordCount 结果> (hello,2)
WordCount 结果> (flink,1)
WordCount 结果> (flink,2)
WordCount 结果> (stream,1)
WordCount 结果> (processing,1)
```

### 验证方式
每次在 nc 终端中输入一行文本，Flink 端就实时输出更新后的词频统计结果。按 `Ctrl+C` 停止 Flink 作业。

---

## 演示三：窗口 WordCount（需要 netcat）

每 5 秒一个滚动窗口（Tumbling Window），统计当前窗口内的词频。

### 步骤
**终端 1** — 启动 Socket 数据源：
```bash
ncat -lk 9998
```

**终端 2** — 启动 Flink 作业：
```bash
mvn exec:java -pl flink-demo1 -Dexec.mainClass="msdemo.hong.com.flinkdemo1.FlinkDemoRunner" -Dexec.args="window"
```

在**终端 1**中，第一个 5 秒窗口内输入：
```
apache flink
flink stream
```
等待 5 秒后，终端 2 会输出第一个窗口的统计。再输入一批单词，Flink 会输出第二个窗口的统计。

### 预期输出（终端 2）
```
═══════ Flink 窗口 WordCount（每5秒滚动窗口） ═══════
  请确保已运行: nc -lk 9998
  每5秒输出一次窗口内的词频统计结果

窗口统计（5秒）> (apache,1)
窗口统计（5秒）> (flink,2)
窗口统计（5秒）> (stream,1)
```

### 与流式 WordCount 的区别
| 对比项 | 流式 WordCount | 窗口 WordCount |
|--------|---------------|----------------|
| 输出时机 | 每次输入立即输出 | 每 5 秒输出一批 |
| 统计范围 | 从启动到现在的总量 | 仅当前窗口内的增量 |
| 适用场景 | 实时监控总量 | 实时监控时段内的变化 |

---

## 演示四：Kafka 数据源（需要 Kafka 已启动）

从 Kafka `flink-input` 主题消费 JSON 格式的消息，解析后提取关键字段。

### 前置条件
Kafka 已运行在 `127.0.0.1:9092`。

### 步骤

**终端 1** — 创建主题并启动生产者：
```bash
# 创建输入主题（只需执行一次）
kafka-topics.bat --create --topic flink-input --bootstrap-server 127.0.0.1:9092

# 启动生产者
kafka-console-producer.bat --topic flink-input --bootstrap-server 127.0.0.1:9092
```

**终端 2** — 启动 Flink 作业：
```bash
mvn exec:java -pl flink-demo1 -Dexec.mainClass="msdemo.hong.com.flinkdemo1.FlinkDemoRunner" -Dexec.args="kafka-source"
```

在**终端 1**中逐行发送 JSON 消息：
```json
{"id":"1001","name":"iPhone 15","price":5999.00}
{"id":"1002","name":"MacBook Pro","price":16999.00}
{"id":"1003","name":"AirPods Pro","price":1999.00}
```

### 预期输出（终端 2）
```
═══════ Flink Kafka 数据源示例 ═══════
  从 Kafka topic 'flink-input' 消费消息...
  请在另一个终端启动 producer 发送消息...
  发送示例：{"id":"1001","name":"测试商品","price":99.5}

Kafka 消息> 商品{id=1001, name=iPhone 15, price=5999.00}
Kafka 消息> 商品{id=1002, name=MacBook Pro, price=16999.00}
Kafka 消息> 商品{id=1003, name=AirPods Pro, price=1999.00}
```

### 验证方式
生产者每发一条 JSON 消息，Flink 端就解析并打印一条 `商品{...}` 格式的信息。按 `Ctrl+C` 停止 Flink 作业。

---

## 演示五：Kafka 数据汇（需要 Kafka 已启动）

模拟订单生成程序，每秒生成一条订单数据，计算含税价格（13% 税率）后写入 Kafka。

### 前置条件
Kafka 已运行在 `127.0.0.1:9092`。

### 步骤

**终端 1** — 创建主题并启动消费者（查看 Flink 输出的结果）：
```bash
# 创建输出主题（只需执行一次）
kafka-topics.bat --create --topic flink-output --bootstrap-server 127.0.0.1:9092

# 启动消费者，实时查看 Flink 写入的数据
kafka-console-consumer.bat --topic flink-output --bootstrap-server 127.0.0.1:9092
```

**终端 2** — 启动 Flink 作业：
```bash
mvn exec:java -pl flink-demo1 -Dexec.mainClass="msdemo.hong.com.flinkdemo1.FlinkDemoRunner" -Dexec.args="kafka-sink"
```

### 预期输出

**终端 2（Flink 控制台）**：
```
═══════ Flink Kafka 数据汇示例 ═══════
  模拟生成订单数据 → 计算含税价格 → 写入 Kafka topic 'flink-output'

写入 Kafka 的数据> {"orderId":"ORD-00001","originalAmount":345.67,"taxRate":0.13,"taxAmount":44.94,"totalAmount":390.61,"processTime":1712345678000}
写入 Kafka 的数据> {"orderId":"ORD-00002","originalAmount":789.12,"taxRate":0.13,"taxAmount":102.59,"totalAmount":891.71,"processTime":1712345679000}
```

**终端 1（Kafka 消费者）**：
```json
{"orderId":"ORD-00001","originalAmount":345.67,"taxRate":0.13,"taxAmount":44.94,"totalAmount":390.61,"processTime":1712345678000}
{"orderId":"ORD-00002","originalAmount":789.12,"taxRate":0.13,"taxAmount":102.59,"totalAmount":891.71,"processTime":1712345679000}
```

### 验证方式
1. Kafka 消费者终端能显示 Flink 写入的 JSON 消息，每 1 秒显示一条
2. 对比 `originalAmount` 和 `totalAmount`，验证 `totalAmount = originalAmount × 1.13`
3. 按 `Ctrl+C` 停止 Flink 作业

---

## 提交到 Flink 集群

如果需要将作业提交到本地 Flink 集群（Web UI 地址 http://127.0.0.1:8081），可以打包后提交：

```bash
# 打包（包含依赖）
mvn clean package -pl flink-demo1 -am -DskipTests

# 提交到本地 Flink 集群
flink run -c msdemo.hong.com.flinkdemo1.FlinkDemoRunner ^
  flink-demo1/target/flink-demo1-1.0-SNAPSHOT.jar ^
  batch
```

在集群模式下，可以在 Web UI 中查看作业的运行状态、算子执行计划、反压等指标。

---

## Flink 核心概念

### 编程模型
```
Source（数据源） → Transformation（转换） → Sink（数据汇）
```

### 示例对应的 Flink 概念
| 概念 | 说明 | 对应示例 |
|------|------|----------|
| Source | 数据输入源 | Socket / Kafka / 集合 |
| Transformation | 数据处理逻辑 | flatMap / map / keyBy / sum |
| Window | 窗口计算 | TumblingProcessingTimeWindow |
| Sink | 结果输出 | print / Kafka Sink |
| Watermark | 事件时间处理 | WatermarkStrategy |

---

## 常见问题

| 问题 | 原因 | 解决 |
|------|------|------|
| `Connection refused: connect` | Socket 端口未开启或 Kafka 未启动 | 先按前置准备启动 nc 或 Kafka |
| `ClassNotFoundException` | classpath 缺少 Flink 依赖 | 使用 `mvn exec:java` 运行，不要直接用 `java -cp` |
| `Topic flink-input not present` | Kafka 主题未创建 | 执行 `kafka-topics --create` |
| `NoClassDefFoundError` | Flink 依赖版本不匹配 | 检查 pom.xml 中的 flink.version 是否与本地安装一致 |
| 作业启动后无任何输出 | 数据源未发送数据 | 检查 nc / Kafka producer 是否正在发送数据 |

---

## 包结构
```
msdemo.hong.com.flinkdemo1
├── FlinkDemoRunner.java              # 运行入口
├── wordcount/
│   ├── WordCountBatchDemo.java       # 批处理 WordCount
│   └── WordCountStreamingDemo.java   # 流式 WordCount
├── kafka/
│   ├── KafkaSourceDemo.java          # Kafka 数据源
│   └── KafkaSinkDemo.java            # Kafka 数据汇
└── window/
    └── WindowWordCountDemo.java      # 窗口词频统计
```

## 技术栈
- **Flink**: 1.18.0
- **Scala**: 2.12
- **Kafka Connector**: 3.2.0-1.18
- **JDK**: 21
---

## exec-maven-plugin 说明

`pom.xml` 中配置了 `exec-maven-plugin`，但 **未绑定任何生命周期阶段**，因此 `mvn package` 或 `mvn compile` 时不会自动执行。

如需手动运行任意演示，使用以下命令格式：

```bash
mvn exec:java -pl flink-demo1 `
  -Dexec.mainClass="msdemo.hong.com.flinkdemo1.FlinkDemoRunner" `
  -Dexec.args="batch"
```

参数 `exec.args` 可选值：`batch`、`stream`、`window`、`kafka-source`、`kafka-sink`、`help`。

> ⚠️ 注意：此插件仅供开发时快速运行使用。在生产环境部署时，应使用 `flink run` 命令将打包后的 JAR 提交到 Flink 集群。