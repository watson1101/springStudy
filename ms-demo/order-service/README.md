# order-service 订单服务

## 模块简介
订单服务是微服务架构中的核心业务服务之一，负责订单的全生命周期管理，包括订单创建、查询、状态管理和消息通知等功能。

## 功能特性

### 核心功能
- **订单创建** - 接收前端下单请求，校验商品信息、价格和库存，生成订单
- **订单查询** - 支持按订单ID、用户ID、时间范围等多维度查询订单详情和列表
- **订单状态管理** - 管理订单状态流转（已创建 → 已支付 → 已发货 → 已完成 → 已取消）
- **订单取消** - 支持取消未支付的订单，释放冻结的库存
- **支付对接** - 对接支付网关，处理支付回调，更新订单状态

### 消息通知（Kafka）
- **订单事件推送** - 订单状态变更时通过 Kafka 发送消息（`order-events` 主题）
- **异步解耦** - 订单创建/支付/取消等操作通过消息队列通知其他服务，提高系统响应速度
- **有序消费** - 使用订单ID作为消息key，保证同一订单的消息被发送到同一分区，按顺序消费

## 技术栈

- **Spring Boot** 3.3.11
- **Spring Cloud** 2023.0.1
- **MyBatis Plus** 3.5.12（持久层框架）
- **PostgreSQL**（关系型数据库）
- **Redis**（缓存）
- **Kafka**（消息队列）
- **Nacos**（服务发现、配置中心）
- **OpenFeign**（服务间调用）

## 数据库表

### order_order_info（订单信息表）
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(64) | 订单ID（主键） |
| order_no | VARCHAR(100) | 订单编号（唯一） |
| user_id | VARCHAR(64) | 用户ID |
| user_name | VARCHAR(100) | 用户名称 |
| total_amount | DECIMAL(10,2) | 订单总金额 |
| pay_amount | DECIMAL(10,2) | 实付金额 |
| discount_amount | DECIMAL(10,2) | 优惠金额 |
| freight_amount | DECIMAL(10,2) | 运费 |
| status | SMALLINT | 订单状态（0-待支付 1-已支付 2-已发货 3-已完成 4-已取消） |
| pay_type | SMALLINT | 支付方式（1-微信支付 2-支付宝 3-银联） |
| pay_time | TIMESTAMP | 支付时间 |
| delivery_time | TIMESTAMP | 发货时间 |
| receive_time | TIMESTAMP | 收货时间 |
| receiver_name | VARCHAR(100) | 收货人姓名 |
| receiver_phone | VARCHAR(20) | 收货人电话 |
| receiver_address | VARCHAR(500) | 收货地址 |
| remark | VARCHAR(500) | 订单备注 |
| is_deleted | SMALLINT | 是否删除（0-否 1-是） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_by | VARCHAR(64) | 更新人 |

### order_order_item（订单明细表）
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(64) | 明细ID（主键） |
| order_id | VARCHAR(64) | 订单ID（关联 order_order_info） |
| product_id | VARCHAR(64) | 商品ID |
| product_name | VARCHAR(200) | 商品名称 |
| product_code | VARCHAR(100) | 商品编码 |
| product_image | VARCHAR(500) | 商品图片 |
| category_id | VARCHAR(64) | 分类ID |
| specification | VARCHAR(500) | 商品规格 |
| unit_price | DECIMAL(10,2) | 单价 |
| quantity | INT | 数量 |
| subtotal | DECIMAL(10,2) | 小计金额 |
| is_deleted | SMALLINT | 是否删除（0-否 1-是） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_by | VARCHAR(64) | 更新人 |

## API 接口清单

### 订单状态消息接口（Kafka 演示）

#### 1. 发送订单状态变更消息（基础测试）

```http
POST /order/message/send
```

**请求参数**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| orderId | String | 否 | ORD-001 | 订单ID |
| userId | String | 否 | USER-001 | 用户ID |

**请求示例**
```bash
curl -X POST "http://localhost:8002/order-service/order/message/send?orderId=ORD-001&userId=USER-001"
```

**响应**
```json
{
  "code": 200,
  "message": "订单状态变更消息发送成功，请查看消费者日志",
  "data": "ORD-001",
  "timestamp": 1717689600000
}
```

#### 2. 发送自定义订单状态变更消息

```http
POST /order/message/send/custom
```

**请求参数**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| orderId | String | 是 | - | 订单ID |
| orderNo | String | 否 | ORDER-{orderId} | 订单编号 |
| userId | String | 否 | USER-001 | 用户ID |
| oldStatus | String | 否 | CREATED | 原状态 |
| newStatus | String | 否 | PAID | 新状态 |
| desc | String | 否 | 订单状态已变更 | 变更描述 |

**请求示例**
```bash
curl -X POST "http://localhost:8002/order-service/order/message/send/custom?orderId=ORD-002&oldStatus=PAID&newStatus=SHIPPED&desc=订单已发货"
```

**响应**
```json
{
  "code": 200,
  "message": "自定义订单状态变更消息发送成功，请查看消费者日志",
  "data": "ORD-002",
  "timestamp": 1717689600000
}
```

### 订单核心接口（待实现）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/order/create` | POST | 创建订单 |
| `/order/{orderId}` | GET | 查询订单详情 |
| `/order/page` | POST | 分页查询订单列表 |
| `/order/{orderId}/pay` | POST | 支付订单 |
| `/order/{orderId}/cancel` | POST | 取消订单 |

## Kafka 消息机制说明

### 消息流程

```
┌──────────────┐     POST /order/message/send     ┌─────────────────────┐
│  客户端/测试  │ ──────────────────────────────────▶  OrderMessageController │
└──────────────┘                                    └─────────┬───────────┘
                                                              │
                                                              │ 构建 OrderStatusChangeMessage
                                                              ▼
                                                    ┌─────────────────────┐
                                                    │  OrderEventProducer  │
                                                    │  (Kafka 生产者)      │
                                                    └─────────┬───────────┘
                                                              │
                                                              │ kafkaTemplate.send("order-events", key, message)
                                                              ▼
                                                    ┌─────────────────────┐
                                                    │   Kafka Broker       │
                                                    │   Topic: order-events │
                                                    └─────────┬───────────┘
                                                              │
                                                              │ 自动消费
                                                              ▼
                                                    ┌─────────────────────┐
                                                    │  OrderEventConsumer  │
                                                    │  (Kafka 消费者)      │
                                                    └─────────┬───────────┘
                                                              │
                                                              │ 打印日志（不写数据库）
                                                              ▼
                                                    ┌─────────────────────┐
                                                    │   控制台日志输出      │
                                                    └─────────────────────┘
```

### Topic 说明

| 属性 | 值 | 说明 |
|------|------|------|
| Topic 名称 | `order-events` | 订单事件主题 |
| 分区数 | 3（默认） | 支持并行消费 |
| 副本因子 | 1（默认） | 单副本，开发环境使用 |
| 消息 Key | 订单ID | 保证同一订单有序消费 |
| 消息 Value | JSON 格式 | OrderStatusChangeMessage 的 JSON 序列化 |
| 消费者组 | `order-service-group` | 订单服务消费者组 |

### 消息格式

```json
{
  "orderId": "ORD-001",
  "orderNo": "ORDER-ORD-001",
  "userId": "USER-001",
  "oldStatus": "CREATED",
  "newStatus": "PAID",
  "message": "订单已支付成功，等待发货",
  "timestamp": "2024-06-06T10:30:00"
}
```

### 消费者日志输出

```
═══════════════════════════════════════════════
  接收到订单状态变更消息
  Topic:      order-events
  Partition:  0
  Offset:     15
  Key:        ORD-001
  OrderId:    ORD-001
  OrderNo:    ORDER-ORD-001
  UserId:     USER-001
  状态变更:    CREATED → PAID
  描述:       订单已支付成功，等待发货
  变更时间:   2024-06-06T10:30:00
═══════════════════════════════════════════════
```

## 订单状态说明

| 状态值 | 状态码 | 说明 |
|--------|--------|------|
| 0 | CREATED | 已创建（待支付） |
| 1 | PAID | 已支付（待发货） |
| 2 | SHIPPED | 已发货（待收货） |
| 3 | COMPLETED | 已完成 |
| 4 | CANCELLED | 已取消 |

### 状态流转图
```
CREATED ──▶ PAID ──▶ SHIPPED ──▶ COMPLETED
    │                                      │
    └──── CANCELLED ◀──────────────────────┘
```

## 配置说明

### 端口配置
- **服务端口**: 8002
- **上下文路径**: /order-service

### 数据库配置
- **数据库**: PostgreSQL
- **数据库名**: ms-demo
- **表前缀**: order_

### Nacos 配置
- **命名空间**: ms-demo
- **分组**: DEFAULT_GROUP

### Kafka 配置
- **Broker**: localhost:9092
- **消费者组**: order-service-group
- **事件主题**: order-events

## 快速开始

### 1. 启动依赖服务

```bash
# 启动 Kafka（需先启动 ZooKeeper）
# Kafka 安装目录下执行：
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

# 启动 Nacos
sh startup.sh -m standalone

# 启动 Redis
redis-server

# 启动 PostgreSQL
pg_ctl start
```

### 2. 启动订单服务

```bash
# 进入项目根目录
cd ms-demo

# 编译项目
mvn clean install

# 启动订单服务
java -jar order-service/target/order-service-1.0-SNAPSHOT.jar

# 或使用 Maven 启动
mvn spring-boot:run -pl order-service
```

### 3. 测试消息发送

```bash
# 发送默认订单状态变更消息
curl -X POST "http://localhost:8002/order-service/order/message/send?orderId=ORD-001"

# 查看订单服务控制台，应该能看到消费者打印的日志
```

### 4. 验证消息消费

查看订单服务的控制台输出，如果配置正确，应该能看到类似以下的日志：

```
2024-06-06 10:30:00.123 [org.springframework.kafka.KafkaMessageListenerContainer-0] INFO  msdemo.hong.com.order.message.OrderEventConsumer - 接收到订单状态变更消息...
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 30001 | 订单不存在 |
| 30002 | 订单创建失败 |
| 30003 | 订单支付失败 |
| 30004 | 订单取消失败 |
| 30005 | 订单状态错误 |
| 30006 | 订单已超时 |
| 30007 | 订单已支付 |

## 开发规范

### 包结构
```
msdemo.hong.com.order
├── controller      // 控制器层
├── service         // 服务接口层
├── service.impl    // 服务实现层
├── mapper          // 数据访问层
├── model           // 模型层
│   ├── entity      // 数据库实体
│   ├── dto         // 数据传输对象
│   └── vo          // 视图对象
├── message         // Kafka 消息相关
│   ├── OrderEventProducer.java    // 消息生产者
│   ├── OrderEventConsumer.java    // 消息消费者
│   └── OrderStatusChangeMessage.java  // 消息实体
├── config          // 配置类
└── OrderServiceApplication.java   // 启动类
```

### 代码注释规范
- 所有类添加功能描述注释
- 所有方法添加参数和返回值说明
- 复杂逻辑添加详细注释
- 注释使用中文，便于新手学习

## 更新日志

### v1.0.0 (2024-06-06)
- 初始版本发布
- 实现 Kafka 消息发送和消费演示功能
- 提供订单状态变更消息测试接口
- 完整的代码注释和文档