# common 模块

## 模块概述

common 模块是 my-demo 微服务工程的公共模块，提供共享的工具类、配置和依赖管理。

## 功能说明

- **工具类**：提供项目中常用的工具方法
- **配置**：提供共享的配置类和常量
- **依赖管理**：管理公共依赖，供其他模块使用

## 技术栈

- **Spring Boot**：3.2.5
- **Spring Cloud Alibaba Nacos Discovery**：服务注册与发现
- **MyBatis Plus**：ORM 框架
- **MySQL Connector**：数据库驱动
- **Druid**：数据库连接池
- **Swagger**：API 文档生成
- **Lombok**：代码简化工具

## 目录结构

```
common/
├── src/main/java/com/hong/common/
│   ├── utils/          # 工具类
│   └── CommonApplication.java  # 模块启动类
├── pom.xml             # 模块依赖管理
└── README.md           # 模块说明
```

## 主要工具类

- **CommonUtils**：提供通用的工具方法，如随机字符串生成等

## 依赖说明

该模块被其他所有模块依赖，提供基础的工具和配置支持。