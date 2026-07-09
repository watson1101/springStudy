# ms-demo 微服务学习项目

## 项目简介
这是一个基于 Spring Boot 3.3 + Spring Cloud 2023 + Spring Cloud Alibaba 的微服务学习项目，涵盖了微服务架构的核心组件和常用功能。

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

### 数据层
- **MyBatis Plus**: 3.5.12（持久层框架）
- **PostgreSQL**: 关系型数据库
- **Redis**: 缓存
- **Kafka**: 消息队列

### API 文档
- **SpringDoc OpenAPI**: 2.6.0（Swagger 接口文档）

## 项目结构

```
ms-demo/
├── pom.xml              # 父级 Maven 配置
├── README.md            # 项目说明
├── SSH_SETUP.md         # SSH 免密配置指南
├── deploy.bat           # 一键部署脚本（Windows）
├── deploy-wsl.sh        # 部署脚本（WSL 内运行）
├── common/              # 公共模块（工具类、配置）
├── gateway-service/     # 网关服务（端口 8000）
├── user-service/        # 用户服务（端口 8001）
├── order-service/       # 订单服务（端口 8002）
├── goods-service/       # 商品服务（端口 8003）
├── role/                # 角色权限服务（端口 8004）
├── etl/                 # 数据处理服务（端口 8005）
├── multi-thread/        # Java 多线程验证模块
├── flink-demo1/         # Flink 流处理学习示例
├── sso-demo/            # SSO 单点登录示例
└── flowable-service/    # Flowable 工作流示例
```

## Swagger 接口文档

各服务启动后可通过以下地址访问 API 文档：

| 服务 | Swagger UI |
|------|-----------|
| goods-service | http://localhost:8003/goods-service/swagger-ui/index.html |
| order-service | http://localhost:8002/order-service/swagger-ui/index.html |
| user-service | http://localhost:8001/user-service/swagger-ui/index.html |
| role | http://localhost:8004/role/swagger-ui/index.html |

## 学习路径

1. **common** — 公共组件和工具类
2. **user-service** — CRUD 和认证
3. **gateway-service** — 网关路由
4. **order-service** — 服务间调用和事务
5. **goods-service** — 库存管理和缓存
6. **role** — RBAC 权限控制
7. **etl** — 流式数据处理
8. **multi-thread** — Java 多线程
9. **flink-demo1** — Flink 流处理
10. **sso-demo** — 单点登录
11. **flowable-service** — 工作流引擎

## 一键部署（SSH）

项目提供了 `deploy` Maven profile，用于将打包好的 JAR 通过 SSH 部署到远程服务器并自动重启。

### 前置条件

1. **SSH 免密登录**（推荐）— 参照 [SSH_SETUP.md](SSH_SETUP.md) 配置
2. 服务器已安装 Java 和 `pkill` 命令
3. 服务器目录 `/home/hong/apps/msdemo` 可写

### IDEA 中使用方式

1. 右侧 Maven 面板 → 勾选 **Profiles** → `deploy`
2. 选择目标模块（如 `goods-service`）
3. 点击 **Lifecycle** → `deploy` ▶

Maven 会自动完成：编译 → 打包 → 执行 deploy.bat（SCP 上传 → 关闭旧进程 → 启动新服务）

### 命令行

```bash
mvn deploy -P deploy -pl goods-service -am -DskipTests
```

### 验证

```bash
ssh hong@localhost "ps aux | grep java"
ssh hong@localhost "tail -f /home/hong/apps/msdemo/app.log"
```

### 修改配置

编辑 `deploy.bat` 顶部：

```batch
set "SSH_USER=hong"
set "SSH_HOST=localhost"
set "REMOTE_PATH=/home/hong/apps/msdemo"
```

### SSH 免密配置

一条命令完成（首次需输入密码 `123456`）：

```bash
type "%USERPROFILE%\.ssh\id_rsa.pub" | ssh hong@localhost "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"
```

详细步骤见 [SSH_SETUP.md](SSH_SETUP.md)。
