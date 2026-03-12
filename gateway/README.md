# gateway 模块

## 模块概述

gateway 模块是 my-demo 微服务工程的网关服务模块，负责请求路由、过滤和负载均衡等功能。

## 功能说明

- **请求路由**：将请求路由到相应的微服务
- **请求过滤**：对请求进行过滤和处理
- **负载均衡**：在多个服务实例之间进行负载均衡
- **认证授权**：对请求进行认证和授权

## 技术栈

- **Spring Boot**：3.2.5
- **Spring WebFlux**：响应式 Web 框架
- **Spring Cloud Gateway**：API 网关
- **Spring Cloud Alibaba Nacos Discovery**：服务注册与发现
- **Spring Cloud Alibaba Nacos Config**：配置管理

## 目录结构

```
gateway/
├── src/main/java/com/hong/gateway/
│   ├── filter/          # 过滤器
│   ├── config/          # 配置类
│   └── GatewayApplication.java  # 模块启动类
├── src/main/resources/
│   └── bootstrap.yaml   # 配置文件
├── pom.xml             # 模块依赖管理
└── README.md           # 模块说明
```

## 配置说明

### Nacos 配置

- **服务名称**：gateway-service
- **配置文件**：gateway-service.yaml

### 路由配置

在 Nacos 配置中心配置路由规则，将请求路由到相应的微服务。

## 启动说明

1. 确保 Nacos 服务运行在 127.0.0.1:8848
2. 启动 gateway 服务
3. gateway 服务将自动从 Nacos 发现其他微服务

## 访问说明

所有微服务的请求都通过 gateway 进行访问，例如：
- 用户服务：http://localhost:8080/user/**
- 商品服务：http://localhost:8080/goods/**
- 订单服务：http://localhost:8080/order/**