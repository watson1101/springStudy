# World Story 微服务项目

## 项目简介

本项目是基于Spring Cloud Alibaba构建的微服务架构应用，模拟了三界（天界、人间、地府）的交互系统。项目采用了现代微服务架构设计，包含服务注册与发现、配置中心、API网关、服务间调用、熔断降级等核心功能。

## 技术栈

- **基础框架**：Spring Boot 3.3.11、Spring Cloud 2023.0.1
- **微服务组件**：Spring Cloud Alibaba 2022.0.0.0
- **服务注册与发现**：Nacos Discovery
- **配置中心**：Nacos Config
- **API网关**：Spring Cloud Gateway
- **服务调用**：OpenFeign
- **熔断降级**：Sentinel
- **负载均衡**：Spring Cloud LoadBalancer
- **数据库**：PostgreSQL
- **ORM框架**：MyBatis-Plus
- **缓存**：Redis
- **工具库**：Lombok、Hutool、FastJSON

## 项目结构

```
world-story/
├── common/                    # 公共模块，包含通用组件
│   ├── src/main/java/com/hong/common/
│   │   ├── config/            # 公共配置类
│   │   ├── exception/         # 全局异常处理
│   │   ├── feign/             # Feign客户端
│   │   └── result/            # 统一返回结果
│   └── pom.xml                # 公共模块依赖
│
├── gateway-service/           # 网关服务
│   ├── src/main/java/com/hong/gateway/
│   │   └── GatewayApplication.java  # 网关启动类
│   ├── src/main/resources/
│   │   ├── application-dev.yml      # 开发环境配置
│   │   └── bootstrap.yml            # 引导配置
│   └── pom.xml                # 网关服务依赖
│
├── haven-world/               # 天界服务
│   ├── src/main/java/com/hong/haven/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   └── HavenWorldApplication.java  # 天界服务启动类
│   ├── src/main/resources/
│   │   ├── application-dev.yml      # 开发环境配置
│   │   └── bootstrap.yml            # 引导配置
│   └── pom.xml                # 天界服务依赖
│
├── human-world/               # 人间服务
│   ├── src/main/java/com/hong/human/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   └── HumanWorldApplication.java  # 人间服务启动类
│   ├── src/main/resources/
│   │   ├── application-dev.yml      # 开发环境配置
│   │   └── bootstrap.yml            # 引导配置
│   └── pom.xml                # 人间服务依赖
│
├── under-world/               # 地府服务
│   ├── src/main/java/com/hong/under/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   └── UnderWorldApplication.java  # 地府服务启动类
│   ├── src/main/resources/
│   │   ├── application-dev.yml      # 开发环境配置
│   │   └── bootstrap.yml            # 引导配置
│   └── pom.xml                # 地府服务依赖
│
├── user/                      # 用户服务
│   ├── src/main/java/com/hong/user/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   └── UserApplication.java  # 用户服务启动类
│   ├── src/main/resources/
│   │   ├── application-dev.yml      # 开发环境配置
│   │   └── bootstrap.yml            # 引导配置
│   └── pom.xml                # 用户服务依赖
│
├── role/                      # 角色服务
│   ├── src/main/java/com/hong/role/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   └── RoleApplication.java  # 角色服务启动类
│   ├── src/main/resources/
│   │   ├── application-dev.yml      # 开发环境配置
│   │   └── bootstrap.yml            # 引导配置
│   └── pom.xml                # 角色服务依赖
│
└── pom.xml                    # 父项目依赖管理
```

## 模块说明

### 1. 公共模块 (common)

公共模块包含了所有服务共用的组件和工具类：

- **统一返回结果**：`Result<T>` 类封装了API的统一返回格式，包含状态码、消息和数据
- **全局异常处理**：`GlobalExceptionHandler` 统一处理各类异常，返回标准格式的错误信息
- **业务异常**：`BusinessException` 自定义业务异常类，用于业务逻辑异常处理
- **Feign客户端**：定义了各服务间的调用接口，包含熔断降级实现

### 2. 网关服务 (gateway-service)

网关服务是系统的统一入口，负责路由转发、负载均衡等功能：

- 端口：8080
- 路由规则：配置了对三个世界服务的路由转发
- 集成了Nacos服务发现，实现动态路由

### 3. 天界服务 (haven-world)

天界服务模拟天界的业务功能：

- 端口：8081
- 数据库：PostgreSQL (haven_world)
- 提供天界信息API
- 集成了Sentinel熔断降级

### 4. 人间服务 (human-world)

人间服务模拟人间的业务功能，同时作为三界信息的聚合点：

- 端口：8082
- 数据库：PostgreSQL (human_world)
- 提供人间信息API
- 通过Feign调用天界和地府服务，聚合三界信息

### 5. 地府服务 (under-world)

地府服务模拟地府的业务功能：

- 端口：8083
- 数据库：PostgreSQL (under_world)
- 提供地府信息API
- 集成了Sentinel熔断降级

### 6. 用户服务 (user)

用户服务提供用户管理相关功能，包含三种用户实体模型：

- **普通用户 (User)**：系统用户，提供基本的账号管理功能
- **生命用户 (LifeUser)**：代表在各个世界中存在的生命实体
- **真灵用户 (MetaUser)**：代表生命的本质，一个真灵可以对应多个生命用户

主要功能：

- 端口：8084
- 数据库：PostgreSQL (user_db)
- 提供普通用户的CRUD操作和分页查询
- 提供生命用户的管理，包括创建、查询、更新和删除
- 提供真灵用户的管理，支持查看真灵下的所有生命
- 与角色服务协同工作，实现用户权限管理
- 集成了Sentinel熔断降级

核心API：
- 用户基本信息管理：获取、创建、更新、删除用户
- 生命用户管理：创建不同世界的生命实体
- 真灵管理：查看真灵及其对应的所有生命

### 7. 角色服务 (role)

角色服务提供角色管理和用户角色关联功能：

- 端口：8085
- 数据库：PostgreSQL (role_db)
- 提供角色创建、分配、权限管理API
- 与用户服务协同工作，实现RBAC权限控制
- 集成了Sentinel熔断降级

## 组件配置说明

### Nacos配置

所有服务都通过Nacos进行服务注册与配置管理：

- 服务地址：xxx.hong.com:8848
- 命名空间：public
- 配置格式：YAML
- 配置分组：DEFAULT_GROUP

### Sentinel配置

所有服务都集成了Sentinel熔断降级功能：

- 控制台地址：xxx.hong.com:8858
- Feign整合：启用了Sentinel对Feign的支持
- 降级处理：为每个Feign客户端提供了降级实现

### 数据库配置

每个服务使用独立的PostgreSQL数据库：

- 数据库地址：xxx.hong.com:5432
- 数据库名称：分别为haven_world、human_world、under_world
- ORM框架：MyBatis-Plus

### Redis配置

所有服务共用Redis缓存，但使用不同的数据库编号：

- Redis地址：xxx.hong.com:6379
- 数据库编号：
  - 天界服务：0
  - 人间服务：1
  - 地府服务：2

## 服务间调用

服务间调用通过OpenFeign实现，主要调用关系：

- 人间服务 -> 天界服务：获取天界信息
- 人间服务 -> 地府服务：获取地府信息
- 用户服务 -> 角色服务：获取用户角色信息
- 网关服务 -> 用户服务：进行用户认证
- 各世界服务 -> 用户服务：获取用户信息和权限验证

所有调用都配置了熔断降级处理，确保系统的稳定性和可用性。

## 启动顺序

推荐的服务启动顺序：

1. 确保外部依赖（Nacos、Sentinel、PostgreSQL、Redis）已启动
2. 启动用户服务（user）和角色服务（role）
3. 启动三个世界服务（haven-world、human-world、under-world）
4. 启动网关服务（gateway-service）

## API示例

### 1. 获取天界信息

```
GET http://localhost:8080/haven-world/api/haven/info
```

### 2. 获取人间信息

```
GET http://localhost:8080/human-world/api/human/info
```

### 3. 获取地府信息

```
GET http://localhost:8080/under-world/api/under/info
```

### 4. 获取三界聚合信息

```
GET http://localhost:8080/human-world/api/world/info
```

### 5. 用户注册

```
POST http://localhost:8080/user/api/register
```

### 6. 用户登录

```
POST http://localhost:8080/user/api/login
```

### 7. 用户基本信息管理

```
GET http://localhost:8080/user/api/user/{id}          # 获取用户信息
GET http://localhost:8080/user/api/user/username/{username}  # 根据用户名获取用户信息
POST http://localhost:8080/user/api/user              # 创建用户
PUT http://localhost:8080/user/api/user/{id}          # 更新用户信息
DELETE http://localhost:8080/user/api/user/{id}       # 删除用户
GET http://localhost:8080/user/api/user/page          # 分页查询用户列表
```

### 8. 生命用户管理

```
GET http://localhost:8080/user/life-users/{id}            # 根据ID获取生命用户
GET http://localhost:8080/user/life-users/meta-user/{metaUserId}  # 根据真灵ID获取所有生命
POST http://localhost:8080/user/life-users                # 创建生命用户
PUT http://localhost:8080/user/life-users/{id}            # 更新生命用户
DELETE http://localhost:8080/user/life-users/{id}         # 删除生命用户
GET http://localhost:8080/user/life-users                 # 分页查询生命用户列表
```

### 9. 真灵管理

```
GET http://localhost:8080/user/meta-users/{id}        # 根据ID获取真灵
POST http://localhost:8080/user/meta-users            # 创建真灵
GET http://localhost:8080/user/meta-users             # 分页查询真灵列表
```

### 10. 角色管理

```
GET http://localhost:8080/role/api/roles
POST http://localhost:8080/role/api/roles
PUT http://localhost:8080/role/api/roles/{roleId}
```

### 11. 用户角色分配

```
POST http://localhost:8080/role/api/user-roles
```

## 未来扩展

1. 添加分布式事务支持（Seata）
2. 集成分布式链路追踪（SkyWalking）
3. 添加API文档（Swagger/Knife4j）
4. 实现统一认证授权（Spring Security + OAuth2）
5. 添加消息队列（RocketMQ）
6. 实现分布式任务调度（XXL-Job）