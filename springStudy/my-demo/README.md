# my-demo 微服务工程

## 项目概述

my-demo 是一个基于 Spring Cloud Alibaba 的微服务工程，包含以下模块：

- **common**：公共模块，包含共享的工具类和配置
- **user**：用户服务模块，负责用户相关功能
- **gateway**：网关服务模块，负责请求路由和过滤
- **order**：订单服务模块，负责订单相关功能
- **goods**：商品服务模块，负责商品相关功能

## 技术栈

- **Spring Boot**：3.2.5
- **Spring Cloud**：2023.0.1
- **Spring Cloud Alibaba**：2023.0.1.0
- **MyBatis Plus**：3.5.5
- **MySQL**：8.0.33
- **Druid**：1.2.18
- **Swagger**：3.0.0
- **Redis**：6.x
- **Nacos**：服务注册与配置中心

## 架构设计

- **微服务架构**：基于 Spring Cloud Alibaba 构建
- **服务注册与发现**：使用 Nacos
- **配置管理**：使用 Nacos 配置中心
- **API 网关**：使用 Spring Cloud Gateway
- **数据库**：MySQL
- **缓存**：Redis
- **连接池**：Druid

## 环境要求

- JDK 17+
- Maven 3.6+
- Docker
- Nacos 2.x
- MySQL 8.0+
- Redis 6.x

## 快速开始

### 1. 启动依赖服务

- **Nacos**：运行在 127.0.0.1:8848
- **MySQL**：运行在 127.0.0.1:3306
- **Redis**：运行在 127.0.0.1:6379

### 2. 编译项目

```bash
mvn clean install
```

### 3. 启动服务

按照以下顺序启动服务：
1. gateway
2. user
3. goods
4. order

## 模块说明

- **common**：提供共享的工具类和配置
- **user**：用户服务，处理用户注册、登录等功能
- **gateway**：API 网关，负责请求路由和过滤
- **order**：订单服务，处理订单创建、查询等功能
- **goods**：商品服务，处理商品查询、管理等功能

## 配置说明

所有配置都在 Nacos 上配置，服务启动时会从 Nacos 拉取配置。

### Nacos 配置

- **服务地址**：127.0.0.1:8848
- **命名空间**：默认
- **配置格式**：YAML

### 数据库配置

- **地址**：127.0.0.1:3306
- **数据库名**：根据服务模块创建对应数据库
- **用户名**：root
- **密码**：根据实际情况设置

### Redis 配置

- **地址**：127.0.0.1:6379
- **密码**：无
- **数据库**：0

## 项目结构

```
my-demo/
├── common/           # 公共模块
├── user/             # 用户服务模块
├── gateway/          # 网关服务模块
├── order/            # 订单服务模块
├── goods/            # 商品服务模块
├── pom.xml           # 父工程依赖管理
└── README.md         # 项目说明
```