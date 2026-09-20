# microservice-learn

> **项目定位**：Spring Cloud Alibaba 微服务学习 & 练手工程（用户/订单/商品 + 系统配置中心 + 多中间件对照学习）
>
> **文档生成时间**：2026-09-03
> **最后基于版本**：`com.ms.learn:microservice-learn:1.0.0`（根 pom.xml 10 个 Maven 子模块）

---

## 一、技术栈总览

| 层级 | 选型 |
|------|------|
| 语言 / 构建 | Java 17 + Maven（多模块） |
| 基础框架 | Spring Boot **4.0.0** |
| 微服务体系 | Spring Cloud 2025.1.3 + Spring Cloud Alibaba 2025.1.0.0 |
| 注册 / 配置中心 | Nacos（单机，`8848`） |
| 限流 / 熔断 | Sentinel Dashboard（`8858`），service-product 已接入 |
| API 网关 | Spring Cloud Gateway（WebFlux 版，`8000`） + LoadBalancer（`lb://`） |
| 服务间调用 | Spring Cloud OpenFeign |
| ORM | MyBatis-Plus 3.5.17 |
| 数据库（异构） | PostgreSQL（用户/商品） + MySQL（订单/系统/工作流） |
| 认证 / SSO | Sa-Token 1.46 + Sa-Token SSO（service-user 作为 SSO Server） |
| 工作流 | Flowable（flowable-service，端口 8007） |
| CDC 数据同步 | mysql-binlog-connector 0.29（ms-ds-system 内置 Binlog 同步器） |
| 消息 / 流处理对比 | RocketMQ 5.3.1 + Kafka 3.9（CP Kafka 7.7.1） + Apache Flink Demo |
| 缓存 / 文档存储 | Redis 7 + MongoDB 8 |
| 前端 | Vue 3 + Vite 5 + Vue Router 4 + Element Plus + Axios |
| 容器化 | Docker Compose v2（多份 compose 按阶段使用） + 私有 Registry（`5000`） |
| CI / CD 基建 | Jenkins LTS（`8080`） |
| K8s 底座（可选） | 测试服务器已部署 flannel / cni0（10.42.0.x），可迁移到 K8s |

---

## 二、项目结构

```
microservice-learn/                     ← 根 POM (pom)
├── pom.xml                             ← 父 POM，统一依赖版本
├── common/                             ← 公共包：Result 统一返回、BizException、全局异常捕获+异常日志（详见 common/README.md）
├── gateway/                            ← Spring Cloud Gateway（8000），含 Dockerfile
│   └── src/main/resources/application.yml
├── service-user/                       ← 用户 & SSO 中心（8001，PostgreSQL），含 Dockerfile
├── service-order/                      ← 订单服务（8002，MySQL + OpenFeign 调 user），含 Dockerfile
├── service-product/                    ← 商品服务（8003，PostgreSQL + Sentinel），含 Dockerfile
├── service-goods/                      ← 商品管理服务（8006，MySQL + Nacos + Sentinel + Sa-Token + Feign 调字典），含 Dockerfile
├── ms-ds-system/                       ← 系统配置 + Binlog CDC（8090，MySQL），含 Dockerfile
│   └── sql/                            ← sys_config / sys_dict 表 & A股 目标表 SQL
├── flowable-service/                   ← Flowable 工作流（8007 /flowable-service，MySQL）
│   └── src/main/resources/processes/   ← 请假 / 报销 BPMN20 XML
├── flink-demo/                         ← Flink 学习：WordCount / Window / Kafka Source-Sink
├── multi-thread/                       ← Java 并发编程 Demo
├── frontend/                           ← Vue 3 SPA（用户/订单/商品/Home 四页）
│   ├── package.json
│   └── src/{api,views,router.js,App.vue,main.js}
├── sql/                                ← 初始化 SQL：init-mysql.sql / init-pg.sql / migrate-user-auth.sql
├── docker-compose.yml                  ← 【起步】仅基础设施：Nacos + Sentinel
├── docker-compose.full.yml             ← 【全量】基础设施 + 5 个服务 Jar build
├── docker-compose.ms-ds-system.yml     ← 【独立】ms-ds-system 单独部署
├── docker-compose.kafka.yml            ← 【消息】ZooKeeper + Kafka + Kafka UI
└── Dockerfile                          ← 根 Dockerfile（供 full compose 引用）
```

### 2.1 核心模块端口与数据库映射

| 模块 | 服务注册名 | 端口 | 数据库 / 库名 |
|------|-----------|------|--------------|
| gateway | ms-gateway | 8000 | 无 |
| service-user | service-user | 8001 | PostgreSQL `ms_ds_user` |
| service-order | service-order | 8002 | MySQL `ms_ds_order` |
| service-product | service-product | 8003 | PostgreSQL `ms_ds_product` |
| service-goods | service-goods | 8006 | MySQL `ms_ds_goods`（分类字典在 ms-ds-system） |
| ms-ds-system | ms-ds-system | 8090 | MySQL `ms_ds_sys_config` + `OPENCLAW_A_STOCK` (CDC 目标) |
| flowable-service | flowable-service (当前未注册到 Nacos) | 8007 | MySQL `flowable` |

### 2.2 辅助模块定位

| 模块 | 性质 | 说明 |
|------|------|------|
| common | 依赖库 (jar) | 统一响应 `Result`、业务异常 `BizException`、**全局异常捕获 + 异常日志（文件/数据库双写，2026-09-21 新增）**；其他模块通过 `com.ms.learn:common:${project.version}` 依赖，详见 `common/README.md` |
| flink-demo | 学习示例 (jar) | Flink 基础/窗口/Kafka 接入演示，独立 `run.bat` / `main()` 启动，不进主 compose |
| multi-thread | 学习示例 (jar) | Java 并发基础、同步、阻塞队列、线程池、CompletableFuture、CountDownLatch 等 |
| flowable-service | 准业务服务 (jar) | 工作流流程定义（请假/报销），含 Swagger UI；目前数据源走 `localhost:3306`、未接入 Nacos，独立启动 |

---

## 三、逻辑架构分层

```
┌───────────────────────────────────────────────────────────────────────┐
│                        用户 / 浏览器 / 第三方客户端                     │
└──────────────────────────┬────────────────────────────────────────────┘
                           │ HTTP + JSON + Bearer Token (Sa-Token)
┌──────────────────────────▼────────────────────────────────────────────┐
│  接入层    Spring Cloud Gateway (8000)                                 │
│           路由 (lb:// 从 Nacos 拉实例 + LoadBalancer 客户端侧均衡)      │
│            /api/user/* /sso/*      → service-user                     │
│            /api/order/*             → service-order                    │
│            /api/product/*           → service-product                  │
│           全局 CORS: allowedOriginPatterns=*                          │
└──────┬───────────┬────────────────────┬───────────────────────────────┘
       │           │                    │
       ▼           ▼                    ▼
 ┌─────────┐  ┌───────────┐      ┌───────────────┐       ┌───────────────┐
 │ service │  │ service   │      │ service       │       │ ms-ds-system  │
 │ -user   │  │ -order    │      │ -product      │       │ 系统配置+CDC  │
 │ 8001 PG │  │ 8002 MySQL│      │ 8003 PG       │       │ 8090 MySQL    │
 │ SSO Server│ │ └─Feign──┼──────► getUser(id)  │       │               │
 │ Sa-Token │  └───────────┘      │ + Sentinel    │       │ Binlog 监听器│
 └────┬────┘                       └───────┬───────┘       └───────┬───────┘
      │  Sa-Token 票据/校验                │ Sentinel 规则上报         │
      └──────────────────────────┐         │ (8858)                  │ 增量同步 A股
                                 ▼         ▼                         │ (Mac 192.168.0.40:3306
                          ┌─────────────────────────────┐            │  → 目标库 192.168.0.27)
                          │   前端 (Vue 3, 5173 Vite)   │            │
                          │   Home/Users/Orders/Products│◄───────────┘ (REST APIs)
                          └─────────────────────────────┘

 ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ 基础设施层 (注册到 Nacos:192.168.0.27:8848) ─ ─ ─ ─ ─ ─ ─ ─ ─
  Nacos (服务注册 + 配置预留 import-check off)    Sentinel Dashboard
  Kafka + ZK + Kafka UI   RocketMQ Name/Broker    Redis    MongoDB    Jenkins    Registry
 ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ 数据层 (测试服务器 192.168.0.27) ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
  PostgreSQL 5432 :  ms_ds_user, ms_ds_product
  MySQL 3306      :  ms_ds_order, ms_ds_sys_config, OPENCLAW_A_STOCK, flowable
  MongoDB 27017   :  (部署中待用)
  Redis 6379      :  (部署中待用)
```

---

## 四、关键调用链路

### 4.1 前端 → 网关 → 微服务
```
frontend (Axios, vite:5173)
        │ http://{gateway-host}:8000/api/<domain>/...
        ▼
Gateway (/api/user|order|product/*  +  /sso/*)
        │ lb://<service-name>  (Nacos 服务发现 + Spring Cloud LoadBalancer)
        ▼
对应业务微服务 (Controller → Service → Mapper → DB)
```

### 4.2 跨服务调用（订单 → 用户）
- `service-order` 通过 `@FeignClient(name="service-user")` `UserFeignClient#getUser(Long id)` 调用
- 底层走 Nacos 服务发现 + 客户端侧负载均衡 + OpenFeign 编解码
- **典型链路**：创建订单 → 校验下单用户存在性 / 拉取用户档案

### 4.3 登录 / SSO（Sa-Token）
- service-user 作为 **SSO Server**，暴露 `/sso/**` 入口（Gateway 直通）
- 前端（clientId=`microservice-frontend`）通过 ticket 换 Token，`Authorization: Bearer <token>` 用于后续请求
- Token 风格：`random-64`，超时 7200s，活跃超时 1800s

### 4.4 CDC 数据同步（ms-ds-system 独有）
- 源：`192.168.0.40:3306/OPENCLAW_A_STOCK` → Binlog 订阅（server-id=65530）
- 目标：`192.168.0.27:3306/OPENCLAW_A_STOCK`
- 入口：`BinlogSyncRunner`（Spring Boot 启动后拉线程）+ `BinlogSyncService`
- 开关 & 分块大小由 `sys_config` 表控制（通过 SysConfig API 可热切换）

---

## 五、部署拓扑（测试服务器 192.168.0.27）

> 服务器 OS：Ubuntu 26.04 LTS；Docker 29.1.3；Docker Compose 2.40.3；K8s 网络已就绪（flannel/cni0）

### 5.1 四份 Docker Compose 对照

| Compose 文件 | 作用域 | 包含组件 | 典型启动命令 |
|--------------|--------|---------|-------------|
| `docker-compose.yml` | **起步基础设施** | Nacos + Sentinel | `docker compose up -d` |
| `docker-compose.full.yml` | **全量（推荐生产/演示）** | Nacos + Sentinel + Gateway + service-user/order/product + ms-ds-system | `mvn clean package -DskipTests` 之后：`docker compose -f docker-compose.full.yml up -d --build` |
| `docker-compose.ms-ds-system.yml` | **系统配置服务独立** | ms-ds-system 单体 | `docker compose -f docker-compose.ms-ds-system.yml up -d --build` |
| `docker-compose.kafka.yml` | **Kafka 三件套** | ZooKeeper 3.9 + Kafka (CP 7.7.1) + Kafka UI (9100) | `cd /home/hong/docker/kafka && docker compose up -d` |

### 5.2 对外端口总表

| 端口 | 组件 | 备注 |
|------|------|------|
| 5173 | Frontend Vite dev | 本地开发 |
| 8000 | Spring Cloud Gateway | **唯一前端入口** |
| 8001 | service-user | 可独立暴露调试 |
| 8002 | service-order | 可独立暴露调试 |
| 8003 | service-product | 可独立暴露调试 |
| 8006 | service-goods | 商品管理（分类字典走 ms-ds-system） |
| 8007 | flowable-service | 未纳入 compose，需手动启动；`/flowable-service` context-path |
| 8090 | ms-ds-system | 系统配置 + CDC |
| 8848 / 9848 | Nacos 控制台 / gRPC | 默认账号 `nacos/nacos`，URL：http://192.168.0.27:8848/nacos |
| 8858 | Sentinel Dashboard | service-product 已配置上报（dashboard:127.0.0.1:8858，按需改为网关可访问地址） |
| 9100 | Kafka UI | http://192.168.0.27:9100 |
| 9094 | Kafka EXTERNAL Listener | **局域网 Spring/SDK 接入推荐使用**，不要用 9092 |
| 2181 | ZooKeeper | 供 Kafka 用；其他服务如需独立 ZK 可复用 |
| 9876 / 10911 | RocketMQ NameServer / Broker | 学习与 Kafka 对照 |
| 5432 / 3306 | PostgreSQL / MySQL | 直连数据库 |
| 6379 / 27017 | Redis / MongoDB | 缓存与文档存储（代码接入为下一步） |
| 8080 / 50000 | Jenkins | 流水线 |
| 5000 | Docker 私有 Registry | `curl http://192.168.0.27:5000/v2/_catalog` |

### 5.3 持久化
- **Nacos / Kafka / ZooKeeper**：Docker 命名卷（`docker compose down` 默认不删卷，加 `-v` 才清空）
- **业务数据库**：部署在 192.168.0.27 本地 PostgreSQL / MySQL 实例（非 Docker 化），初始化脚本见 `sql/` 目录：
  - `init-mysql.sql`：MySQL 侧（order / sys_config / flowable 等）
  - `init-pg.sql`：PostgreSQL 侧（user / product）
  - `migrate-user-auth.sql`：用户认证补充迁移

---

## 六、架构特征 & 学习点清单

| 维度 | 说明 |
|------|------|
| ✅ 服务拆分 | 按业务域拆成 user/order/product + 系统配置，共 4 个核心微服务 + 1 网关 |
| ✅ 服务发现 | 4 个业务服务 + 网关都注册到 Nacos；Gateway 路由使用 `lb://<service>` |
| ⚠️ 配置中心 | 已接入 Nacos Config 依赖，但 `import-check.enabled=false`，当前仍用本地 yml；**推荐下一练习**：把数据库连接、Sentinel 地址迁到 Nacos，并练习多 profile 切换 |
| ✅ 服务调用 | service-order → service-user 使用 OpenFeign，演示声明式 RPC |
| ✅ 限流 / 熔断 | service-product 接入 Sentinel Dashboard；下一练习：在 Gateway 统一加限流，在 order 加降级规则 |
| ✅ 认证 | Sa-Token SSO 模式已打通，前端可直接走 `/sso/*` 完成登录 |
| ✅ 多数据库 | PostgreSQL + MySQL 双库对照，练手多数据源 / 跨库事务思维 |
| ✅ 消息双体系 | RocketMQ + Kafka 同机部署，对照学习 Push/Pull、顺序、事务消息差异；并能直连 Flink |
| ✅ CDC | 自研 Binlog 监听 + 可热配置启停，练习 "日志型 ETL" 思维 |
| ✅ 工作流 | Flowable 提供 BPMN 可视化能力，下一练习：与订单服务联动（下单 → 审批流） |
| ✅ 前端联动 | Vue 3 四页 SPA，练习 "前端→网关→微服务→DB" 完整闭环 |
| ✅ 全局异常处理 | common 统一提供异常捕获 + 异常日志（文件/数据库双写，开关默认全开）；服务依赖 common 即生效，无需逐服务改代码 |
| ✅ 容器化 | 所有核心模块含 Dockerfile；full compose 支持一把梭全量启动 |
| 🚀 进阶方向 K8s | flannel + cni0 已就绪，下一练习：helm/kubectl 把 core services 从 compose 迁入 K8s Deployment + Service + Ingress |

---

## 七、快速启动（三步跑起来）

### 第 1 步：启动基础设施（Nacos、Sentinel、Redis、MongoDB、Kafka、RocketMQ 等）
测试服务器上基础设施容器已经在跑（参考 `docker ps`），如果需要在 **本地从零起**：
```bash
# 基础设施：Nacos + Sentinel
docker compose up -d

# Kafka 三件套（或把 docker-compose.kafka.yml 合并进 full 一起启动）
docker compose -f docker-compose.kafka.yml up -d
```

### 第 2 步：准备数据库
```bash
# 1) 在 PostgreSQL 中执行 sql/init-pg.sql.bak ---- 排除pg库，改为使用mysql
# 2) 在 MySQL 中执行 sql/init-mysql.sql
# 3) 如需用户认证字段补充，执行 sql/migrate-user-auth.sql
```
> 默认数据库地址全部指向 `192.168.0.27`（见各服务 application.yml）。本地跑建议用 `SPRING_PROFILES_ACTIVE=docker` 并配套调整本地 hosts 或本地 profile。

### 第 3 步：打包并启动
```bash
# 根目录
mvn clean package -DskipTests

# 方式 A：全量容器化
docker compose -f docker-compose.full.yml up -d --build

# 方式 B：本地 Idea 逐个启动（先起 nacos，再起 gateway→user→order→product→ms-ds-system）
java -jar gateway/target/gateway.jar           # 8000
java -jar service-user/target/service-user.jar  # 8001
java -jar service-order/target/service-order.jar # 8002
java -jar service-product/target/service-product.jar # 8003
java -jar ms-ds-system/target/ms-ds-system.jar  # 8090

# 前端（另一个终端）
cd frontend && npm i && npm run dev   # http://localhost:5173
```

### 快速冒烟测试
```bash
# 1. 网关路由可达
curl http://192.168.0.27:8000/api/user/1

# 2. Nacos 控制台可打开
#    浏览器打开 http://192.168.0.27:8848/nacos  → 服务列表应看到 gateway + 4 个服务

# 3. Kafka UI 可打开
#    浏览器打开 http://192.168.0.27:9100 → 创建 Topic 并发送消息

# 4. 前端 CRUD
#    浏览器打开 http://localhost:5173 → Users / Orders / Products 页依次尝试增删改查
```

---

## 八、Kubernetes 部署（测试服务器 192.168.0.27）

> 目标：将 8 个 JAR 微服务 + 前端，全部以 **Deployment `replicas=2`** 方式部署到服务器已有 K8s 集群，统一放 namespace `ms-learn`。
> 交付物：仓库 `k8s/` 目录下的 YAML 清单 + `k8s/scripts/` 下三个一键脚本。

### 8.1 环境前置（一次性完成）

在服务器 192.168.0.27 终端执行（root 权限）：

```bash
# ---- 1. 工具链 ----
# Java 17（运行已具备，构建阶段要求 JDK 17）
java -version    # 期望 openjdk version "17.0.x"
mvn -version      # 期望 Maven 3.9.x；若无：sudo apt install -y maven
docker --version  # 期望 29.x

# ---- 2. 私有 Registry insecure 放行 ----
# 服务器本机 Docker Registry 跑在 5000 (http)，docker daemon 默认会因为无证书拒绝 push/pull
sudo tee /etc/docker/daemon.json <<'JSON'
{
  "insecure-registries": ["192.168.0.27:5000", "localhost:5000"]
}
JSON
sudo systemctl daemon-reload
sudo systemctl restart docker

# 验证
curl -fsS http://localhost:5000/v2/_catalog   # 应至少返回 {"repositories":[...]}，若已推送 ms-ds-system 等旧镜像

# ---- 3. K8s 可用性 ----
kubectl cluster-info
kubectl get nodes   # 期望 1 个 node Ready（单节点集群）
kubectl get pods -A # 期望 coredns / kube-flannel / kube-proxy 等系统 Pod Running

# ---- 4. 数据库准备 ----
# 所有 ms_ds_* 库必须已在宿主机 MySQL (192.168.0.27:3306) 创建并导入表
# 一键执行全部建库建表（含 ms_ds_user/order/product/transaction/points/flowable/goods）：
for f in sql/ms_ds_*/init.sql; do mysql -uroot -p123456 < "$f"; done
# ms-ds-system 的字典表（sys_dict/sys_dict_item）建在 ms_ds_sys_config 库：
mysql -uroot -p123456 < ms-ds-system/sql/ms_ds_sys_config.sql
```

### 8.2 上传项目到服务器

在 **本机 Windows 终端**（当前开发机）执行：

```powershell
# 方式 A：rsync over ssh（推荐，增量快；Windows 没有 rsync 用方式 B）
# 若 ssh 密钥已配好
# rsync -avz --exclude target/ --exclude node_modules/ --exclude dist/ \
#   /f/workspace/github/springStudy/microservice-learn/ hong@192.168.0.27:~/microservice-learn/

# 方式 B：sftp 上传压缩包（更稳，不依赖 ssh 免密）
cd f:\workspace\github\springStudy\
tar czf microservice-learn.tgz --exclude=target --exclude=node_modules --exclude=dist microservice-learn
sftp hong@192.168.0.27 <<'EOF'
put microservice-learn.tgz ~/
bye
EOF
# 然后在服务器解包：
#   ssh hong@192.168.0.27
#   cd ~ && rm -rf microservice-learn && tar xzf microservice-learn.tgz && cd microservice-learn
```

### 8.3 构建 & 推送镜像

服务器终端执行：

```bash
cd ~/microservice-learn

# 脚本需要可执行
chmod +x k8s/scripts/*.sh

# 一键：mvn package -> docker build -> docker push
bash k8s/scripts/build-push.sh
# 成功后末尾会枚举 Registry 中 ms-learn/gateway, service-user, ..., frontend 共 10 个镜像
```

常见失败 & 修复：

| 症状 | 处理 |
|---|---|
| `http: server gave HTTP response to HTTPS client` | 重做 8.1 第 2 步 daemon.json，并 `sudo systemctl restart docker` |
| mvn 报 `Could not resolve dependencies` | 检查 maven 源，~/.m2/settings.xml 是否有 aliyun mirror 配置 |
| npm ERR（前端构建阶段） | 检查服务器能否访问 `registry.npmmirror.com`；若网络不通，先在本机构建 `frontend/dist/` 一起上传，改成单阶段 nginx COPY dist 即可 |

### 8.4 生成 Secret + 部署

服务器终端执行：

```bash
cd ~/microservice-learn

# 1) 生成 k8s/02-app-secret.yaml（默认值：MySQL=123456, Nacos=nacos/nacos, SSO dev-only key）
bash k8s/scripts/gen-secret.sh
# 若密码与默认不同，用环境变量覆盖：
#   MYSQL_PASSWORD='xxx' NACOS_PASSWORD='xxx' bash k8s/scripts/gen-secret.sh --overwrite

# 2) 一键 kubectl apply（按顺序 ns -> cm -> secret -> deploy/svc）
bash k8s/scripts/deploy-apply.sh
# 成功会输出 "下一步：bash k8s/scripts/verify-deploy.sh"
```

### 8.5 验证

服务器终端执行：

```bash
bash k8s/scripts/verify-deploy.sh
```

**期望结果**：最后一行 `K8s Deploy: OK`，exit code=0。

验证清单：

| 编号 | 检查项 | 验收标准 |
|---|---|---|
| (1) | Namespace | `kubectl get ns ms-learn` 显示 Active |
| (2) | 9 Deployment 2/2 Ready | `kubectl -n ms-learn get deploy` 全部 READY 列 = `2/2` |
| (3) | Pod 健康 | 所有 Pod restart<=1，0 个 probe Unhealthy 事件 |
| (4) | Service 端口/类型 | 网关 NodePort 30080、前端 30081，其他 ClusterIP + 正确 containerPort |
| (5) | Registry 镜像 | `curl -s localhost:5000/v2/_catalog \| jq '.repositories\|length'` ≥9 |
| (6) | 网关 → 用户服务 | `curl -I http://192.168.0.27:30080/api/user/list` 返回 200/401/403 |
| (7) | 前端主页 | `curl -I http://192.168.0.27:30081/` 返回 HTTP 200 + Content-Type: text/html |
| (8) | 外部访问 | 浏览器打开 http://192.168.0.27:30081/users ，前端通过 nginx 反代接口，不再有 CORS 报错 |

### 8.6 回滚 / 日常维护

```bash
# 单服务回滚到上一版本
kubectl -n ms-learn rollout undo deploy/service-user

# 查看升级历史
kubectl -n ms-learn rollout history deploy/ms-gateway

# 实时日志（多副本用 -l app=<svc> 同时观察两个 Pod）
kubectl -n ms-learn logs -f -l app=flowable-service --max-log-requests=10

# 临时缩到 1 副本（比如 ms-ds-system 要开启 binlog 同步防止双写）
kubectl -n ms-learn scale deploy ms-ds-system --replicas=1

# 卸载整个 ns（危险）
# kubectl delete ns ms-learn
```

### 8.7 常见问题（实际踩坑记录；待部署后补充）

> 部署完 verify 失败后把真实报错贴回作者，本节将按实际情况补齐。

- **Pod CrashLoopBackOff：**先 `kubectl -n ms-learn describe pod <name>` 看 Events，再 `kubectl -n ms-learn logs <name> --previous` 看上一次崩溃日志。
- **Pod OOMKilled：**容器 limits.memory 偏小，先临时 `kubectl -n ms-learn edit deploy xxx` 调到 1Gi 再观察，后续补丁更新到 YAML。
- **flowable-service 启动慢 / ready 超时：**初次启动 Flowable 会建 ACT_* 表，默认 initialDelaySeconds 已给到 40/120。若还是 probe 失败，临时调 readiness failureThreshold=8 再观察。
- **service-user /api/user/list 返回 401：**正常现象（该接口要求 `SaCheckPermission("user:list")` 权限），verify 脚本把 401/403 均视为 PASS（证明网关成功路由到 service-user 并返回业务响应）。
- **ms-frontend 访问 `/api/*` 404：**`nginx.conf` 反代 `ms-gateway.ms-learn.svc.cluster.local:8000/` 失败 → 先 `kubectl -n ms-learn get svc ms-gateway` 看 CLUSTER-IP，进入 Pod 里 `wget http://ms-gateway.ms-learn.svc.cluster.local:8000` 能否通。

---

## 九、相关文档

- 公共模块说明（含全局异常捕获与异常日志）：仓库内 `common/README.md`
- 异常日志设计说明：仓库内 `docs/exception-log-design.md`
- 异常日志建表脚本：仓库内 `sql/exception_log.sql`
- 热点资讯模块设计：仓库内 `docs/hotnews-design.md`
- 测试服务器 Kafka 部署说明：服务器上 `/home/hong/Documents/KAFKA_DEPLOYMENT.md`
- Sentinel K8s 部署（NodePort 30858）：仓库内 `SENTINEL_K8S_DEPLOY.md`
- 数据库脚本：仓库内 `sql/*.sql`、`sql/ms_ds_*/init.sql`
- ms-ds-system 的 A股 CDC 表定义：仓库内 `ms-ds-system/sql/openclaw_a_stock_schema.sql`
- flowable BPMN：仓库内 `flowable-service/src/main/resources/processes/*.bpmn20.xml`
- K8s 规格/任务/评审文档：仓库内 `.trae/specs/k8s-deploy/{spec,tasks,review}.md`

---

*本文档根据仓库当前代码、配置与测试服务器实际部署归纳生成。若新增模块/中间件，请同步更新本 README。*
