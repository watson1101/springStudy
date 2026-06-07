# ms-demo 微服务学习项目

## 项目简介

这是一个基于 Spring Boot 3.3 + Spring Cloud 2023 + Spring Cloud Alibaba 的微服务学习项目，涵盖了微服务架构的核心组件和常用功能，适合初学者学习和实践微服务开发。

## 技术栈

### 核心框架
- **Spring Boot**: 3.3.11
- **Spring Cloud**: 2023.0.1
- **Spring Cloud Alibaba**: 2023.0.1.2
- **JDK**: 21

### 微服务组件
- **Nacos**: 服务注册与发现、配置中心
- **Sentinel**: 流量控制、熔断降级
- **Gateway**: API 网关、路由转发
- **OpenFeign**: 服务间调用
- **LoadBalancer**: 客户端负载均衡

### 数据层
- **MyBatis Plus**: 3.5.12 (持久层框架)
- **PostgreSQL**: 关系型数据库
- **Redis**: 缓存中间件
- **Kafka**: 消息队列

### 工具类库
- **Hutool**: 5.8.37 (Java工具类库)
- **FastJSON**: 2.0.43 (JSON处理)
- **Lombok**: 代码简化

### 数据处理
- **Apache Flink**: 1.18.0 (流式数据处理)

## 项目结构

```
ms-demo/
├── pom.xml                          # 父级Maven配置
├── README.md                        # 项目说明文档
├── demand.md                        # 项目需求文档
├── env.md                           # 环境配置文档
│
├── common/                          # 公共模块
│   ├── pom.xml
│   └── src/main/
│       ├── java/msdemo/hong/com/common/
│       │   ├── config/              # 通用配置类
│       │   ├── constant/            # 常量定义
│       │   ├── enums/               # 枚举类
│       │   ├── exception/           # 自定义异常
│       │   ├── model/               # 通用模型
│       │   │   ├── dto/             # 数据传输对象
│       │   │   ├── vo/              # 视图对象
│       │   │   └── result/          # 统一返回结果
│       │   └── util/                # 工具类
│       └── resources/
│
├── gateway-service/                 # 网关服务
│   ├── pom.xml
│   └── src/main/
│       ├── java/msdemo/hong/com/gateway/
│       │   ├── config/              # 网关配置
│       │   ├── filter/              # 网关过滤器
│       │   └── handler/             # 处理器
│       │   └── GatewayApplication.java
│       └── resources/
│           ├── application.yml
│           ├── bootstrap.yml
│           └── logback-spring.xml
│
├── user-service/                    # 用户服务
│   ├── pom.xml
│   └── src/main/
│       ├── java/msdemo/hong/com/user/
│       │   ├── controller/          # 控制层
│       │   ├── service/             # 服务层
│       │   ├── mapper/              # 数据访问层
│       │   ├── model/               # 模型层
│       │   │   ├── entity/         # 实体类
│       │   │   ├── dto/            # 数据传输对象
│       │   │   └── vo/             # 视图对象
│       │   └── config/             # 配置类
│       └── resources/
│           ├── sql/                 # SQL脚本
│           ├── mapper/              # MyBatis映射文件
│           ├── application.yml
│           └── bootstrap.yml
│
├── order-service/                    # 订单服务
│   ├── pom.xml
│   └── src/main/
│       ├── java/msdemo/hong/com/order/
│       │   ├── controller/          # 控制层
│       │   ├── service/impl/        # 服务层及实现
│       │   ├── mapper/              # 数据访问层
│       │   ├── model/               # 模型层
│       │   │   ├── entity/         # 实体类
│       │   │   ├── dto/            # 数据传输对象
│       │   │   └── vo/             # 视图对象
│       │   └── config/             # 配置类
│       └── resources/
│           ├── sql/                 # SQL脚本
│           ├── mapper/              # MyBatis映射文件
│           ├── application.yml
│           └── bootstrap.yml
│
├── goods-service/                    # 商品服务
│   ├── pom.xml
│   └── src/main/
│       ├── java/msdemo/hong/com/goods/
│       │   ├── controller/          # 控制层
│       │   ├── service/impl/        # 服务层及实现
│       │   ├── mapper/              # 数据访问层
│       │   ├── model/               # 模型层
│       │   │   ├── entity/         # 实体类
│       │   │   ├── dto/            # 数据传输对象
│       │   │   └── vo/             # 视图对象
│       │   └── config/             # 配置类
│       └── resources/
│           ├── sql/                 # SQL脚本
│           ├── mapper/              # MyBatis映射文件
│           ├── application.yml
│           └── bootstrap.yml
│
├── role/                             # 角色权限服务
│   ├── pom.xml
│   └── src/main/
│       ├── java/msdemo/hong/com/role/
│       │   ├── controller/          # 控制层
│       │   ├── service/impl/        # 服务层及实现
│       │   ├── mapper/              # 数据访问层
│       │   ├── model/               # 模型层
│       │   │   ├── entity/         # 实体类
│       │   │   ├── dto/            # 数据传输对象
│       │   │   └── vo/             # 视图对象
│       │   └── config/             # 配置类
│       └── resources/
│           ├── sql/                 # SQL脚本
│           ├── mapper/              # MyBatis映射文件
│           ├── application.yml
│           └── bootstrap.yml
│
└── etl/                              # 数据处理服务
    ├── pom.xml
    └── src/main/
        ├── java/msdemo/hong/com/etl/
        │   ├── config/             # 配置类
        │   ├── utils/               # 工具类
        │   ├── flink/               # Flink流处理
        │   │   ├── source/          # 数据源
        │   │   ├── transform/       # 数据转换
        │   │   └── sink/            # 数据输出
        │   └── EtlApplication.java
        └── resources/
            ├── application.yml
            └── bootstrap.yml
```

## 模块说明

| 模块 | 端口 | 功能描述 |
|------|------|----------|
| gateway-service | 8000 | 统一网关入口、路由转发、限流熔断 |
| user-service | 8001 | 用户注册登录、信息管理、认证授权 |
| order-service | 8002 | 订单创建、查询、状态管理、支付对接 |
| goods-service | 8003 | 商品管理、库存管理、价格管理 |
| role | 8004 | 角色权限管理、RBAC权限控制 |
| etl | 8005 | 数据抽取、转换、加载、实时流处理 |
| common | - | 公共组件、工具类、通用配置 |

## 环境要求

### 基础环境
- JDK 21+
- Maven 3.6+
- IDE (IntelliJ IDEA / Eclipse)

### 中间件

#### MySQL 数据库
```
地址: localhost:3306
用户名: root
密码: 123456
数据库: ms-demo
```

#### Redis 缓存
```
地址: localhost:6379
```

#### Kafka 消息队列
```
Broker: localhost:9092
Controller: localhost:9093
```

#### Nacos 服务
```
地址: localhost:8848
命名空间: ms-demo
```

## 数据库设计规范

- 数据库名称: `ms-demo`
- 表名规范: `{模块简称}_{功能描述}`
  - 用户服务表前缀: `user_`
  - 订单服务表前缀: `order_`
  - 商品服务表前缀: `goods_`
  - 角色服务表前缀: `role_`

## 包名规范

统一包名: `msdemo.hong.com`

各模块包结构:
```
msdemo.hong.com.{模块名}
├── controller      // 控制器层
├── service         // 服务接口层
├── service.impl    // 服务实现层
├── mapper          // 数据访问层
├── model           // 模型层
│   ├── entity      // 数据库实体
│   ├── dto         // 数据传输对象
│   └── vo          // 视图对象
└── config          // 配置类
```

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd ms-demo
```

### 2. 启动基础服务

启动以下中间件:
- MySQL
- Redis
- Kafka
- Nacos

### 3. 创建数据库

```sql
CREATE DATABASE `ms-demo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 执行建表脚本

各模块的建表脚本位于:
```
{module}/src/main/resources/sql/*.sql
```

### 5. 配置Nacos

在 Nacos 控制台创建以下配置:

#### 公共配置 (common-config.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:3306/ms-demo
    username: root
    password: 123456
  redis:
    host: localhost
    port: 6379
```

### 6. 编译项目

```bash
mvn clean install
```

### 7. 启动服务

按以下顺序启动服务:
1. gateway-service
2. user-service
3. order-service
4. goods-service
5. role
6. etl

## 开发规范

### 1. 代码注释

项目需要提供详细的代码注释，便于新手学习:
- 类添加功能描述
- 方法添加参数和返回值说明
- 复杂逻辑添加详细注释

### 2. 接口规范

#### 统一返回格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

#### 异常处理
```json
{
  "code": 500,
  "message": "错误描述",
  "data": null
}
```

### 3. 日志规范

使用 SLF4J + Logback:
- DEBUG: 开发调试信息
- INFO: 关键业务流程
- WARN: 警告信息
- ERROR: 错误信息

## 学习路径

建议按以下顺序学习本项目:

1. **common 模块**: 了解公共组件和工具类
2. **user-service**: 学习基础的 CRUD 和认证
3. **gateway-service**: 学习网关路由和过滤器
4. **order-service**: 学习服务间调用和事务处理
5. **goods-service**: 学习库存管理和缓存应用
6. **role**: 学习 RBAC 权限控制
7. **etl**: 学习流式数据处理

## 常见问题

### 1. 端口冲突

如果端口被占用，可在 `application.yml` 中修改端口配置。

### 2. Nacos 连接失败

检查 Nacos 服务是否启动，以及配置的地址是否正确。

### 3. 数据库连接失败

检查数据库服务是否启动，以及用户名密码是否正确。

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue。
