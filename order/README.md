# order 模块

## 模块概述

order 模块是 my-demo 微服务工程的订单服务模块，负责订单相关的功能，如订单创建、查询、支付等。

## 功能说明

- **订单创建**：创建新订单
- **订单查询**：查询订单详情和列表
- **订单支付**：处理订单支付
- **订单状态管理**：管理订单状态

## 技术栈

- **Spring Boot**：3.2.5
- **Spring Web**：Web 服务
- **Spring Cloud Alibaba Nacos Discovery**：服务注册与发现
- **Spring Cloud Alibaba Nacos Config**：配置管理
- **MyBatis Plus**：ORM 框架
- **MySQL**：数据库
- **Druid**：数据库连接池
- **Swagger**：API 文档生成

## 目录结构

```
order/
├── src/main/java/com/hong/order/
│   ├── controller/      # 控制器
│   ├── service/         # 服务层
│   ├── mapper/          # 数据访问层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── vo/              # 视图对象
│   └── OrderApplication.java  # 模块启动类
├── src/main/resources/
│   └── bootstrap.yaml   # 配置文件
├── pom.xml             # 模块依赖管理
└── README.md           # 模块说明
```

## 配置说明

### Nacos 配置

- **服务名称**：order-service
- **配置文件**：order-service.yaml

### 数据库配置

- **数据库名**：order_db
- **表名**：order

## 启动说明

1. 确保 Nacos 服务运行在 127.0.0.1:8848
2. 确保 MySQL 服务运行在 127.0.0.1:3306
3. 启动 order 服务

## API 文档

可通过 Swagger 访问 API 文档：http://localhost:8080/swagger-ui.html