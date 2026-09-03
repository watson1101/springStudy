# Nacos 配置中心迁移 + 负载均衡完善 + 全服务注册 实施计划

## Context（为什么要做）

当前项目存在三个问题：
1. **Nacos 配置中心未启用**：所有模块都设置了 `import-check.enabled: false`，配置只从本地 `application.yml` 读取，无法集中管理
2. **负载均衡缺失**：`service-order` 使用 `@FeignClient(name="service-user")` 但 pom 中缺少 `spring-cloud-starter-loadbalancer`，无法解析服务名
3. **flowable-service 是孤岛**：无 Nacos 注册、无 Nacos 配置、数据源指向 `localhost:3306` 而非服务器 `192.168.0.27:3306`

**目标**：所有服务注册到 Nacos；本地 `application.yml` 保留作为兜底；运行时优先从 Nacos 读取配置；Sentinel 地址更新为 K8s NodePort `192.168.0.27:30858`。

---

## 技术方案：Spring Cloud 2025.x `spring.config.import`

Spring Cloud 2025.x（Spring Boot 4.0）不再使用 `bootstrap.yml`，改为 `spring.config.import` 机制：
```yaml
spring:
  config:
    import:
      - optional:nacos:<service-name>.yaml
```
- `optional:` 前缀 → Nacos 不可用时用本地配置兜底，不阻断启动
- Nacos 中的配置 **优先级高于** 本地 `application.yml`，满足"优先从 Nacos 读取"要求

---

## 实施步骤

### 步骤 1：POM 依赖补全（3 个文件）

| 文件 | 改动 |
|------|------|
| [gateway/pom.xml](file:///f:/workspace/github/springStudy/microservice-learn/gateway/pom.xml) | 添加 `spring-cloud-starter-alibaba-nacos-config` 依赖 |
| [service-order/pom.xml](file:///f:/workspace/github/springStudy/microservice-learn/service-order/pom.xml) | 添加 `spring-cloud-starter-loadbalancer` 依赖（Feign 需要它解析服务名） |
| [flowable-service/pom.xml](file:///f:/workspace/github/springStudy/microservice-learn/flowable-service/pom.xml) | 添加 `nacos-discovery` + `nacos-config` + `loadbalancer` 三个依赖 |

> `service-user`、`service-product`、`ms-ds-system` 的 pom 不需要改动（已有 nacos-discovery + nacos-config）。

### 步骤 2：重构 6 个 application.yml

每个 `application.yml` 按统一模式改造：

**保留在本地**（连接 Nacos 必需的 + 服务身份）：
- `server.port`、`spring.application.name`、`spring.profiles.active`
- `spring.cloud.nacos.server-addr / username / password / discovery.namespace / config.namespace / config.group / config.file-extension`
- `spring.config.import`

**保留在本地作为兜底**（Nacos 中也有同 key 时被覆盖）：
- `spring.datasource.*`、`mybatis-plus.*`、`sa-token.*`、`logging.*`、`sync.a-stock.*`、`flowable.*` 等

**每个文件的具体改动**：

| 文件 | 改动点 |
|------|--------|
| [gateway/application.yml](file:///f:/workspace/github/springStudy/microservice-learn/gateway/src/main/resources/application.yml) | +`spring.config.import: optional:nacos:ms-gateway.yaml`；+`config.namespace/group/file-extension`；username/password 提到 `spring.cloud.nacos` 公共层；删除 `import-check.enabled: false` |
| [service-user/application.yml](file:///f:/workspace/github/springStudy/microservice-learn/service-user/src/main/resources/application.yml) | 同上模式，dataId=`service-user.yaml` |
| [service-order/application.yml](file:///f:/workspace/github/springStudy/microservice-learn/service-order/src/main/resources/application.yml) | 同上模式，dataId=`service-order.yaml` |
| [service-product/application.yml](file:///f:/workspace/github/springStudy/microservice-learn/service-product/src/main/resources/application.yml) | 同上 + **Sentinel 地址改为 `192.168.0.27:30858`** |
| [ms-ds-system/application.yml](file:///f:/workspace/github/springStudy/microservice-learn/ms-ds-system/src/main/resources/application.yml) | 同上模式，dataId=`ms-ds-system.yaml` |
| [flowable-service/application.yml](file:///f:/workspace/github/springStudy/microservice-learn/flowable-service/src/main/resources/application.yml) | 全量新增 Nacos 配置块 + `spring.config.import`；**数据源 localhost→192.168.0.27** |

### 步骤 3：在 Nacos 控制台创建 6 个 dataId

在 `http://192.168.0.27:8848/nacos`（namespace=public, group=DEFAULT_GROUP, format=yaml）创建：

| dataId | 内容（运行时可覆盖的配置） |
|--------|--------------------------|
| `ms-gateway.yaml` | logging 级别 |
| `service-user.yaml` | datasource、mybatis-plus、sa-token、logging |
| `service-order.yaml` | datasource、mybatis-plus、logging |
| `service-product.yaml` | datasource、sentinel(dashboard:192.168.0.27:30858)、mybatis-plus、logging |
| `ms-ds-system.yaml` | datasource、mybatis-plus、sync.a-stock、logging |
| `flowable-service.yaml` | datasource(192.168.0.27)、flowable、springdoc、logging |

> 这些 dataId 的内容与本地 application.yml 中的对应段落一致，保证初始行为不变；之后可通过 Nacos 控制台集中修改。

### 步骤 4：创建 flowable-service/Dockerfile

新建 [flowable-service/Dockerfile](file:///f:/workspace/github/springStudy/microservice-learn/flowable-service/Dockerfile)，参照现有 service-order/Dockerfile 模式。

### 步骤 5：更新 docker-compose.full.yml

- 添加 `flowable-service` 服务块（build + port 8007 + depends_on nacos）
- 注释掉 `sentinel-dashboard` 块（已由 K8s NodePort 30858 替代）
- 同步更新 `docker-compose.yml`（基础版也注释掉 sentinel-dashboard）

### 步骤 6：不需要改动的文件

- **所有 `*Application.java` 主启动类**：不需要添加 `@EnableDiscoveryClient`（Spring Cloud 2025.x 有 discovery starter 在 classpath 即自动注册）
- **`service-user`、`service-product`、`ms-ds-system` 的 pom.xml**：已有完整依赖

---

## 验证方式

1. **编译验证**：`mvn clean compile -DskipTests` → BUILD SUCCESS
2. **Nacos 服务列表**：启动所有服务后 Nacos 控制台应显示 6 个服务（含新增的 `flowable-service`）
3. **Nacos 配置覆盖**：在 Nacos 修改某 dataId 的 logging 级别，服务日志级别立即变化
4. **负载均衡**：启动两个 `service-user` 实例（8001 + 8011），网关请求轮询到两个实例；`service-order` 的 Feign 调用也轮询
5. **Sentinel 连通**：`service-product` 启动后，`http://192.168.0.27:30858/` 机器列表能看到该实例
6. **本地兜底**：停 Nacos 后服务仍能启动（`optional:` 前缀生效）
7. **Docker 部署**：`docker compose -f docker-compose.full.yml up -d --build` 全量启动成功
