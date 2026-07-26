# goods 模块

## 模块概述

goods 模块是 my-demo 微服务工程的商品服务模块，负责商品相关的功能，如商品查询、管理、库存等。

## 功能说明

- **商品查询**：查询商品详情和列表
- **商品管理**：添加、修改、删除商品
- **库存管理**：管理商品库存
- **商品分类**：商品分类管理

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
goods/
├── src/main/java/com/hong/goods/
│   ├── controller/      # 控制器
│   ├── service/         # 服务层
│   ├── mapper/          # 数据访问层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── vo/              # 视图对象
│   └── GoodsApplication.java  # 模块启动类
├── src/main/resources/
│   └── bootstrap.yaml   # 配置文件
├── pom.xml             # 模块依赖管理
└── README.md           # 模块说明
```

## 配置说明

### Nacos 配置

- **服务名称**：goods-service
- **配置文件**：goods-service.yaml

### 数据库配置

- **数据库名**：goods_db
- **表名**：goods

## 启动说明

1. 确保 Nacos 服务运行在 127.0.0.1:8848
2. 确保 MySQL 服务运行在 127.0.0.1:3306
3. 启动 goods 服务

## API 文档

可通过 Swagger 访问 API 文档：http://localhost:8080/swagger-ui.html