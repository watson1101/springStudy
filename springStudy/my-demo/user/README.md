# user 模块

## 模块概述

user 模块是 my-demo 微服务工程的用户服务模块，负责用户相关的功能，如用户注册、登录、信息管理等。

## 功能说明

- **用户注册**：新用户注册
- **用户登录**：用户登录认证
- **用户信息管理**：查询和更新用户信息
- **用户权限管理**：管理用户权限

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
user/
├── src/main/java/com/hong/user/
│   ├── controller/      # 控制器
│   ├── service/         # 服务层
│   ├── mapper/          # 数据访问层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── vo/              # 视图对象
│   └── UserApplication.java  # 模块启动类
├── src/main/resources/
│   └── bootstrap.yaml   # 配置文件
├── pom.xml             # 模块依赖管理
└── README.md           # 模块说明
```

## 配置说明

### Nacos 配置

- **服务名称**：user-service
- **配置文件**：user-service.yaml

### 数据库配置

- **数据库名**：user_db
- **表名**：user

## 启动说明

1. 确保 Nacos 服务运行在 127.0.0.1:8848
2. 确保 MySQL 服务运行在 127.0.0.1:3306
3. 启动 user 服务

## API 文档

可通过 Swagger 访问 API 文档：http://localhost:8080/swagger-ui.html