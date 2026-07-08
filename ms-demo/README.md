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

### API 文档
- **SpringDoc OpenAPI**: 2.6.0 (Swagger 接口文档)

### 工具类库
- **Hutool**: 5.8.37 (Java工具类库)
- **FastJSON**: 2.0.43 (JSON处理)
- **Lombok**: 代码简化

### 数据处理
- **Apache Flink**: 1.18.0 (流式数据处理)

### 安全认证
- **JWT**: 0.12.5 (令牌认证)
- **SSO**: 单点登录演示模块

### 工作流引擎
- **Flowable**: 7.0.1 (BPMN2.0业务流程管理)

## 项目结构

```
ms-demo/
├── pom.xml                          # 父级Maven配置
├── README.md                        # 项目说明文档
├── demand.md                        # 项目需求文档
├── env.md                           # 环境配置文档
├── common/                          # 公共模块
│   ├── pom.xml
│   └── src/main/
│       └── java/msdemo/hong/com/common/
│           ├── config/              # 通用配置类（含 SpringDoc 配置）
│           ├── constant/            # 常量定义
│           ├── enums/               # 枚举类
│           ├── exception/           # 自定义异常
│           ├── model/               # 通用模型
│           │   ├── dto/             # 数据传输对象
│           │   ├── vo/              # 视图对象
│           │   └── result/          # 统一返回结果
│           └── util/                # 工具类
├── gateway-service/                 # 网关服务
├── user-service/                    # 用户服务
├── order-service/                   # 订单服务
├── goods-service/                   # 商品服务
├── role/                            # 角色权限服务
├── etl/                             # 数据处理服务
├── multi-thread/                    # Java多线程验证模块
├── flink-demo1/                     # Flink流处理学习示例
├── sso-demo/                        # SSO单点登录演示模块
└── flowable-service/                # Flowable工作流演示模块
```

## 模块说明

| 模块 | 端口 | 功能描述 | Swagger UI |
|------|------|----------|-----------|
| gateway-service | 8000 | 统一网关入口、路由转发、限流熔断 | - |
| user-service | 8001 | 用户注册登录、信息管理、认证授权 | `/user-service/swagger-ui/index.html` |
| order-service | 8002 | 订单创建、查询、状态管理、支付对接 | `/order-service/swagger-ui/index.html` |
| goods-service | 8003 | 商品管理、库存管理、价格管理 | `/goods-service/swagger-ui/index.html` |
| role | 8004 | 角色权限管理、RBAC权限控制 | `/role/swagger-ui/index.html` |
| etl | 8005 | 数据抽取、转换、加载、实时流处理 | - |
| multi-thread | - | Java多线程技术验证模块 | - |
| flink-demo1 | - | Flink流处理学习示例 | - |
| sso-demo | 8006 | SSO单点登录演示模块 | `/sso-demo/swagger-ui/index.html` |
| flowable-service | 8007 | Flowable工作流演示模块 | `/flowable-service/swagger-ui.html` |
| common | - | 公共组件、工具类、通用配置 | - |

## 环境要求

### 基础环境
- JDK 21+
- Maven 3.6+
- IDE (IntelliJ IDEA / Eclipse)

### 中间件
#### PostgreSQL 数据库
- 地址: localhost:5432
- 用户名: postgres
- 密码: 123456
- 数据库: ms-demo

#### Redis 缓存
- 地址: localhost:6379

#### Kafka 消息队列
- Broker: localhost:9092
- Controller: localhost:9093

#### Nacos 服务
- 地址: localhost:8848
- 命名空间: ms-demo

## Swagger 接口文档

本项目使用 SpringDoc OpenAPI 2.6.0 作为接口文档框架（Spring Boot 3.x 的推荐方案）。

### 访问地址

各服务启动后，可以通过以下地址访问 Swagger UI：

| 服务 | 地址 |
|------|------|
| 商品服务 | http://localhost:8003/goods-service/swagger-ui/index.html |
| 订单服务 | http://localhost:8002/order-service/swagger-ui/index.html |
| 用户服务 | http://localhost:8001/user-service/swagger-ui/index.html |
| 角色服务 | http://localhost:8004/role/swagger-ui/index.html |

### API 文档 JSON

```bash
# 获取商品服务的 OpenAPI 规范（JSON 格式）
curl http://localhost:8003/goods-service/v3/api-docs

# 获取订单服务的 OpenAPI 规范（YAML 格式）
curl http://localhost:8002/order-service/v3/api-docs.yaml
```

### 常用注解说明

| 注解 | 作用 | 使用位置 |
|------|------|----------|
| `@Tag` | 标注模块分组 | Controller 类 |
| `@Operation` | 描述接口功能 | Controller 方法 |
| `@Parameter` | 描述请求参数 | 方法参数 |
| `@Schema` | 描述数据模型 | DTO/VO 类及字段 |
| `@ApiResponse` | 描述响应信息 | Controller 方法 |

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
├── config          // 配置类
└── Application.java // 启动类
```

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd ms-demo
```

### 2. 启动基础服务

启动以下中间件:
- PostgreSQL
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

### 5. 配置 Nacos

在 Nacos 控制台创建以下配置:

#### 公共配置 (common-config.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ms-demo
    username: postgres
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
1. gateway-service（端口 8000）
2. user-service（端口 8001）
3. order-service（端口 8002）
4. goods-service（端口 8003）
5. role（端口 8004）
6. etl（端口 8005）

## 开发规范

### 1. 代码注释

项目需要提供详细的代码注释，方便新手学习:
- 类添加功能描述注释
- 方法添加参数和返回值说明
- 复杂逻辑添加详细注释
- Controller 接口添加 Swagger 注解

### 2. 接口规范

#### 统一返回格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1700000000000
}
```

#### 异常处理
```json
{
  "code": 500,
  "message": "错误描述",
  "data": null,
  "timestamp": 1700000000000
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
8. **multi-thread**: 学习 Java 多线程技术
9. **flink-demo1**: 学习 Flink 流处理框架
10. **sso-demo**: 学习单点登录（SSO）原理与实现
11. **flowable-service**: 学习 Flowable 工作流引擎与 BPMN2.0 流程定义

## Flowable 工作流模块 (flowable-service)

### 模块简介

`flowable-service` 模块演示了基于 Flowable 引擎的业务流程管理（BPM）功能，包含两个典型的业务流程示例：

- **请假申请流程**: 员工提交请假申请 -> 直属领导审批 -> 部门经理审批（超过3天）-> HR归档
- **费用报销流程**: 员工提交报销申请 -> 财务审核 -> 部门经理审批 -> 财务付款

通过该模块，可以学习如何使用 Flowable 引擎实现复杂的业务流程自动化。

### 技术实现

- **Flowable**: 7.0.1 版本，基于 Spring Boot 的流程引擎
- **BPMN2.0**: 标准化的业务流程建模语言
- **MySQL**: 数据库存储流程定义和运行时数据
- **Spring MVC**: 提供 RESTful API 接口

### 核心文件结构

```
flowable-service/
├── src/main/java/msdemo/hong/com/flowableservice/
│   ├── FlowableServiceApplication.java  # 启动类
│   ├── controller/                       # 控制器层
│   │   └── FlowableController.java       # 流程管理控制器
│   ├── service/                          # 服务层
│   │   └── FlowableService.java          # 流程服务类
│   └── config/                           # 配置类
│       └── FlowableConfig.java           # Flowable配置类
├── src/main/resources/
│   ├── application.yml                   # 应用配置
│   └── processes/                        # 流程定义文件
│       ├── leave-request.bpmn20.xml      # 请假申请流程
│       └── expense-reimbursement.bpmn20.xml  # 费用报销流程
└── pom.xml                               # Maven配置
```

### 流程定义说明

#### 1. 请假申请流程 (leaveRequest)

**流程节点**:
- `开始` → 员工提交请假申请
- `直属领导审批` → 直属领导审批请假申请
- `领导审批决策` → 根据审批结果和请假天数决定流程走向
  - 批准且 ≤3天 → HR归档
  - 批准且 >3天 → 部门经理审批
  - 拒绝 → 流程结束（审批拒绝）
- `部门经理审批` → 部门经理审批超过3天的请假申请
- `经理审批决策` → 根据审批结果决定流程走向
  - 批准 → HR归档
  - 拒绝 → 流程结束（审批拒绝）
- `HR归档` → HR部门归档已批准的请假申请
- `审批通过/审批拒绝` → 流程结束

**流程变量**:
| 变量名 | 类型 | 说明 |
|--------|------|------|
| employeeName | String | 员工姓名 |
| employeeId | String | 员工ID |
| leaveType | String | 请假类型（annual/sick/personal/maternity） |
| startDate | String | 开始日期 |
| endDate | String | 结束日期 |
| days | Long | 请假天数 |
| reason | String | 请假原因 |
| leader | String | 直属领导（任务负责人） |
| manager | String | 部门经理（任务负责人） |
| hr | String | HR人员（任务负责人） |

#### 2. 费用报销流程 (expenseReimbursement)

**流程节点**:
- `开始` → 员工提交报销申请
- `财务审核` → 财务人员审核报销票据
- `财务审核决策` → 根据审核结果决定流程走向
  - 通过 → 部门经理审批
  - 拒绝 → 流程结束（审批拒绝）
- `部门经理审批` → 部门经理审批报销申请
- `经理审批决策` → 根据审批结果决定流程走向
  - 批准 → 财务付款
  - 拒绝 → 流程结束（审批拒绝）
- `财务付款` → 财务部门执行付款操作
- `流程结束/审批拒绝` → 流程结束

**流程变量**:
| 变量名 | 类型 | 说明 |
|--------|------|------|
| applicantName | String | 申请人姓名 |
| applicantId | String | 申请人ID |
| amount | Double | 报销金额 |
| expenseType | String | 费用类型（travel/entertainment/office/other） |
| description | String | 费用说明 |
| receiptCount | Long | 票据数量 |
| finance | String | 财务人员（任务负责人） |
| manager | String | 部门经理（任务负责人） |

### API 接口列表

#### 流程定义管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/flowable/process-definitions` | GET | 获取所有流程定义列表 |
| `/api/flowable/process-definitions/{key}` | GET | 根据key获取流程定义详情 |
| `/api/flowable/process-definitions/xml/{id}` | GET | 获取流程定义XML内容 |
| `/api/flowable/deploy` | POST | 部署新的流程定义 |
| `/api/flowable/deployments/{id}` | DELETE | 删除流程部署 |

#### 流程实例管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/flowable/process-instances` | GET | 获取所有运行中的流程实例 |
| `/api/flowable/process-instances/key/{key}` | GET | 根据流程定义key获取流程实例 |
| `/api/flowable/process-instances/{id}` | GET | 获取流程实例详情 |
| `/api/flowable/process-instances/{id}/variables` | GET | 获取流程实例变量 |
| `/api/flowable/process-instances/{id}/variables` | PUT | 设置流程实例变量 |
| `/api/flowable/process-instances/{id}/suspend` | PUT | 挂起流程实例 |
| `/api/flowable/process-instances/{id}/activate` | PUT | 激活流程实例 |
| `/api/flowable/start` | POST | 通用启动流程实例 |
| `/api/flowable/start/leave-request` | POST | 启动请假申请流程 |
| `/api/flowable/start/expense-reimbursement` | POST | 启动费用报销流程 |

#### 任务管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/flowable/tasks/process-instance/{id}` | GET | 获取流程实例的任务列表 |
| `/api/flowable/tasks/assignee/{user}` | GET | 获取个人待办任务 |
| `/api/flowable/tasks/{id}` | GET | 获取任务详情 |
| `/api/flowable/tasks/{id}/claim` | PUT | 认领任务 |
| `/api/flowable/tasks/{id}/unclaim` | PUT | 取消认领任务 |
| `/api/flowable/tasks/{id}/complete` | POST | 完成任务 |

#### 历史查询

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/flowable/history` | GET | 获取已结束的流程实例历史记录 |

### 启动方式

```bash
# 1. 创建MySQL数据库
mysql -u root -p123456 -e "CREATE DATABASE flowable DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 确保MySQL服务已启动
mysql -u root -p123456

# 3. 进入 flowable-service 目录
cd flowable-service

# 4. 编译并启动
mvn spring-boot:run
```

启动后访问: http://localhost:8007/flowable-service

### Swagger 文档

启动后访问: http://localhost:8007/flowable-service/swagger-ui.html

### 使用示例

#### 1. 启动请假申请流程

```bash
curl -X POST http://localhost:8007/flowable-service/api/flowable/start/leave-request \
  -H "Content-Type: application/json" \
  -d '{
    "employeeName": "张三",
    "employeeId": "EMP001",
    "leaveType": "annual",
    "startDate": "2026-07-15",
    "endDate": "2026-07-17",
    "days": 3,
    "reason": "年假",
    "leader": "leader001",
    "manager": "manager001",
    "hr": "hr001",
    "businessKey": "LEAVE-2026-001"
  }'
```

#### 2. 获取领导的待办任务

```bash
curl http://localhost:8007/flowable-service/api/flowable/tasks/assignee/leader001
```

#### 3. 领导审批（批准）

```bash
curl -X POST http://localhost:8007/flowable-service/api/flowable/tasks/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "leaderComment": "同意请假"
  }'
```

#### 4. HR归档

```bash
curl -X POST http://localhost:8007/flowable-service/api/flowable/tasks/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "archiveNumber": "ARCH-2026-001"
  }'
```

### 配置说明

主要配置项（application.yml）：

```yaml
server:
  port: 8007
  servlet:
    context-path: /flowable-service

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/flowable?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true
    username: root
    password: 123456

flowable:
  process:
    database-schema-update: true                    # 自动更新数据库表结构
    definition-location-prefix: classpath:/processes/  # 流程定义文件位置
    async-executor-activate: false                  # 禁用异步执行器（演示环境）
```

### Flowable 数据库表说明

Flowable 启动时会自动创建以下表：

| 表名前缀 | 说明 |
|----------|------|
| ACT_RE_* | 流程定义相关表（Repository） |
| ACT_RU_* | 运行时数据相关表（Runtime） |
| ACT_HI_* | 历史数据相关表（History） |
| ACT_ID_* | 身份认证相关表（Identity） |
| ACT_GE_* | 通用数据表（General） |

## SSO 单点登录模块 (sso-demo)

### 模块简介

`sso-demo` 模块模拟了单点登录（Single Sign-On）的完整流程，包含两个核心角色：

- **SSO Server (身份提供者/IdP)**: 负责用户认证和令牌发放
- **SSO Client (服务提供者/SP)**: 负责令牌验证和资源保护

通过该模块，可以学习如何实现其他模块（如 user-service、order-service）通过 SSO 登录当前系统。

### 技术实现

- **JWT**: 使用 jjwt 库生成和验证令牌
- **Redis**: 存储令牌和会话信息
- **Spring MVC**: 处理 HTTP 请求和拦截器
- **Thymeleaf**: 渲染登录页面和首页

### 核心文件结构

```
sso-demo/
├── src/main/java/msdemo/hong/com/ssodemo/
│   ├── SsoDemoApplication.java      # 启动类
│   ├── common/                      # 公共层
│   │   ├── JwtUtil.java             # JWT工具类
│   │   ├── SsoConstants.java        # 常量定义
│   │   ├── SsoProperties.java       # 配置属性类
│   │   ├── SsoUserDTO.java          # 用户数据传输对象
│   │   └── Result.java              # 统一返回结果类
│   ├── server/                      # SSO Server层（身份提供者）
│   │   ├── SsoServerController.java # 服务端控制器
│   │   └── SsoServerService.java    # 服务端业务逻辑
│   └── client/                      # SSO Client层（服务提供者）
│       ├── SsoClientController.java # 客户端控制器
│       ├── SsoClientService.java    # 客户端业务逻辑
│       ├── SsoClientConfig.java     # 客户端配置类
│       └── SsoAuthInterceptor.java  # 认证拦截器
├── src/main/resources/
│   ├── application.yml              # 应用配置
│   └── templates/                   # Thymeleaf模板
│       ├── login.html               # SSO登录页面
│       └── home.html                # 首页（受保护资源）
└── pom.xml                          # Maven配置
```

### SSO 登录流程

```
1. 用户访问客户端系统（如 http://localhost:8006/）
2. 客户端拦截器检测到用户未登录
3. 重定向到 SSO Server 登录页面（/sso/login）
4. 用户输入用户名密码（演示账号: admin/123456）
5. SSO Server 验证凭据并签发 JWT 令牌
6. SSO Server 重定向回客户端回调地址（/sso/callback），携带令牌
7. 客户端调用 SSO Server 验证令牌有效性
8. 验证通过后，客户端创建本地会话
9. 用户成功访问受保护的资源
```

### API 接口列表

#### SSO Server 接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/sso/login` | GET | 显示登录页面 |
| `/sso/login` | POST | 处理登录请求 |
| `/sso/api/login` | POST | RESTful 登录接口 |
| `/sso/validate` | GET | 验证令牌有效性 |
| `/sso/userinfo` | GET | 获取用户信息 |
| `/sso/logout` | GET | 登出（使令牌失效） |

#### SSO Client 接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/` | GET | 首页（受保护资源） |
| `/sso/callback` | GET | SSO 回调接口 |
| `/api/userinfo` | GET | 获取当前用户信息 |
| `/logout` | GET | 本地登出 |
| `/global-logout` | GET | 全局登出 |
| `/simulate-login` | GET | 模拟其他模块登录 |
| `/api/client-config` | GET | 获取客户端配置 |

### 演示账号

- **用户名**: `admin`
- **密码**: `123456`

### 启动方式

```bash
# 确保 Redis 已启动
redis-server

# 进入 sso-demo 目录
cd sso-demo

# 编译并启动
mvn spring-boot:run
```

启动后访问: http://localhost:8006

### Swagger 文档

启动后访问: http://localhost:8006/swagger-ui.html

### 模拟其他模块登录

模块提供了模拟其他微服务模块通过 SSO 登录的功能：

```bash
# 模拟 user-service 登录
curl "http://localhost:8006/simulate-login?system=user-service"

# 模拟 order-service 登录
curl "http://localhost:8006/simulate-login?system=order-service"

# 模拟 goods-service 登录
curl "http://localhost:8006/simulate-login?system=goods-service"
```

### 配置说明

主要配置项（application.yml）：

```yaml
sso:
  server:
    url: http://localhost:8006          # SSO Server 地址
    jwt-secret: sso-demo-jwt-secret-key  # JWT 签名密钥
    token-expire-seconds: 7200           # 令牌过期时间（秒）
    persist-token: true                  # 是否持久化令牌到 Redis
  
  client:
    client-id: sso-demo-client           # 客户端 ID
    client-secret: sso-demo-secret       # 客户端密钥
    server-url: http://localhost:8006    # SSO Server 地址
    callback-url: http://localhost:8006/sso/callback  # 回调地址
    ignore-paths:                        # 白名单路径（不需要认证）
      - /sso/login
      - /sso/callback
      - /swagger-ui/**
```

## 常见问题

### 1. 端口冲突

如果端口被占用，可在 `application.yml` 中修改端口配置。

### 2. Nacos 连接失败

检查 Nacos 服务是否启动，以及配置的地址是否正确。

### 3. 数据库连接失败

检查数据库服务是否启动，以及用户名密码是否正确。

## 贡献指南

欢迎提交 Issue 和 Pull Request。

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue。