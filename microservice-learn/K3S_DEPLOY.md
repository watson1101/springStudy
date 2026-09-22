# k3s 测试环境部署手册（完整前后端）

> **服务器**：Ubuntu 26.04 LTS，`192.168.0.27`，用户 `hong`，主机名 `hong-ms-7970`
> **本机**：Mac mini，`192.168.0.40`，用户 `hongwanzhen`
> **最后更新**：2026-09-23
> **状态**：✅ 已验证（网关 11 条路由生效、7 个后端服务运行、Mac 可并行注册 Nacos）

---

## 一、目标架构

```
┌────────────────── 测试服务器 192.168.0.27 ──────────────────┐
│                                                             │
│  宿主机 Docker（非 k3s）                                     │
│   ├─ nacos-server   8848（注册中心 + 配置中心，Nacos 3.x）    │
│   ├─ rmqnamesrv     9876（RocketMQ NameServer）              │
│   ├─ MySQL          3306                                     │
│   └─ registry       5000（本地镜像仓库）                      │
│                                                             │
│  k3s 集群（namespace: ms-learn）                             │
│   ├─ ms-frontend             NodePort 30081  ×2              │
│   ├─ ms-gateway              NodePort 30080  ×2（11 条路由）  │
│   ├─ service-user            8001            ×1              │
│   ├─ service-order           8002            ×1              │
│   ├─ service-product         8003            ×1              │
│   ├─ service-transaction     8004            ×1              │
│   ├─ service-points          8005            ×1              │
│   ├─ service-goods           8006            ×1              │
│   ├─ ms-ds-system            8090            ×1（双数据源）   │
│   ├─ service-hotnews-collector 8008          ×1              │
│   └─ service-hotnews-consumer  8009          ×2              │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │ 同一 Nacos 注册
                          │
┌──────────────────── Mac mini 192.168.0.40 ──────────────────┐
│  Vite dev :3000  ──代理──► 30080（k3s 网关）                  │
│  本机后端服务（可选）注册 Nacos，IP=192.168.0.40               │
│   └─ 与 k3s 同名服务并行，网关 lb:// 轮询分发                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、部署顺序（务必遵守依赖）

| 步骤 | 内容 | 依赖 |
|---|---|---|
| 1 | 宿主机 Nacos / MySQL / RocketMQ / registry | — |
| 2 | 命名空间 + ConfigMap + Secret | 1 |
| 3 | **ms-gateway** | 1、2 |
| 4 | **ms-frontend** | 3（nginx 上游需要网关 Service 存在） |
| 5 | 7 个后端业务服务 | 1、2 |

> ⚠️ **前端必须在网关之后部署**。虽然 nginx.conf 已用变量延迟解析避免崩溃，
> 但 `/api` 链路依赖网关 Service 存在。

---

## 三、构建策略（绕开服务器网络问题）

服务器 **npm 与 Maven 访问外网不稳定**，因此统一采用：

> **本机（Mac）构建产物 → rsync 传服务器 → 服务器仅打包镜像（FROM 运行阶段）**

### 3.1 网关

```bash
# ① 本机构建
cd microservice-learn
mvn -q -pl common,gateway -am clean package -DskipTests   # → gateway/target/gateway-1.0.0.jar

# ② 传 jar
rsync -az gateway/target/gateway-1.0.0.jar hong@192.168.0.27:/home/hong/build-new/gateway-jar/
```

服务器 `/home/hong/build-new/Dockerfile.gateway-only`：

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY gateway-jar/gateway-1.0.0.jar /app/app.jar
# 基础镜像已含 UID 1000 用户（ubuntu），useradd 会报 UID 冲突，故直接复用
RUN chown -R 1000:1000 /app
USER 1000
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"
EXPOSE 8000
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
```

### 3.2 后端服务（统一模板）

```bash
# ① 本机构建（可一次构建多个模块）
mvn -q -pl common,service-user,service-order,service-product,service-transaction,\
service-points,service-goods,ms-ds-system -am clean package -DskipTests

# ② 传 jar（注意 ms-ds-system 的 jar 名为 ms-ds-system.jar）
for m in service-user service-order service-product service-transaction service-points service-goods; do
  rsync -az $m/target/$m-1.0.0.jar hong@192.168.0.27:/home/hong/build-new/jars/
done
rsync -az ms-ds-system/target/ms-ds-system.jar hong@192.168.0.27:/home/hong/build-new/jars/
```

服务器 `/home/hong/build-new/Dockerfile.backend-only`：

```dockerfile
FROM eclipse-temurin:17-jre
ARG SERVICE
ARG PORT
WORKDIR /app
COPY jars/ /tmp/jars/
RUN set -eux; \
    if [ "$SERVICE" = "ms-ds-system" ]; then \
      cp /tmp/jars/ms-ds-system.jar /app/app.jar; \
    else \
      cp /tmp/jars/${SERVICE}-1.0.0.jar /app/app.jar; \
    fi; \
    rm -rf /tmp/jars; \
    chown -R 1000:1000 /app
USER 1000
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"
EXPOSE ${PORT}
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]
```

批量构建 + 推送：

```bash
cd /home/hong/build-new
for spec in "service-user 8001" "service-order 8002" "service-product 8003" \
            "service-transaction 8004" "service-points 8005" "service-goods 8006" \
            "ms-ds-system 8090"; do
  set -- $spec
  docker build -f Dockerfile.backend-only --build-arg SERVICE=$1 --build-arg PORT=$2 \
    -t localhost:5000/ms-learn/$1:<tag> .
  docker push localhost:5000/ms-learn/$1:<tag>
done
```

### 3.3 前端

```bash
# ① 本机构建
cd frontend && npm run build

# ② 传 dist 与 nginx.conf
rsync -az --delete frontend/dist/ hong@192.168.0.27:/home/hong/build-new/frontend-dist/
rsync -az frontend/nginx.conf hong@192.168.0.27:/home/hong/build-new/frontend/

# ③ 服务器打包（仅 nginx 阶段，秒级）
cd /home/hong/build-new
docker build -f Dockerfile.frontend-only -t localhost:5000/ms-learn/frontend:<tag> .
docker push localhost:5000/ms-learn/frontend:<tag>
```

---

## 四、k8s 资源应用顺序

```bash
# 1) 命名空间 + 公共配置
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-configmap-common.yaml
kubectl apply -f k8s/02-app-secret.yaml      # 含明文凭据，勿提交 Git

# 2) 网关 → 前端
kubectl apply -f k8s/10-gateway.yaml
kubectl apply -f k8s/20-frontend.yaml

# 3) 后端业务服务
for f in 11-service-user 12-service-order 13-service-product 14-service-transaction \
         15-service-points 18-service-goods 16-ms-ds-system; do
  kubectl apply -f k8s/$f.yaml
done
# flowable-service（17）按需求仅配置、不部署
```

**创建 Secret**（值勿提交 Git）：

```bash
kubectl create secret generic ms-learn-secret -n ms-learn \
  --from-literal=MYSQL_PASSWORD=<mysql密码> \
  --from-literal=NACOS_USERNAME=nacos \
  --from-literal=NACOS_PASSWORD=<nacos密码> \
  --from-literal=SSO_SERVER_SECRET=<sso-server密钥> \
  --from-literal=SSO_CLIENT_SECRET=<sso-client密钥> \
  --dry-run=client -o yaml | kubectl apply -f -
```

---

## 五、访问地址

| 组件 | 地址 | 说明 |
|---|---|---|
| 前端页面 | `http://192.168.0.27:30081` | SPA 控制台 |
| 前端健康检查 | `http://192.168.0.27:30081/health` | nginx 探针 |
| 网关（NodePort） | `http://192.168.0.27:30080` | API 统一入口 |
| Nacos 控制台 | `http://192.168.0.27:8848/nacos` | Nacos 3.x |
| RocketMQ NameServer | `192.168.0.27:9876` | |
| MySQL | `192.168.0.27:3306` | |
| 本地镜像仓库 | `http://192.168.0.27:5000` | |
| Sentinel Dashboard | `192.168.0.27:30858` | |

**冒烟验证**：

```bash
curl -i http://192.168.0.27:30081/                     # 200
curl -i http://192.168.0.27:30081/health               # 200
curl -i http://192.168.0.27:30080/api/hotnews/latest   # 200，真实热榜数据
curl -i http://192.168.0.27:30080/api/order/list       # 200
curl -i http://192.168.0.27:30080/api/user/list        # 401（鉴权拦截，预期）
```

---

## 六、本机（Mac）联调方式

### 6.1 前端：Vite dev 直连 k3s 后端

```bash
cd frontend && npm run dev     # http://localhost:3000
# .env.development 默认 VITE_GATEWAY_TARGET=http://192.168.0.27:30080
# 请求经 Vite 服务端转发到 k3s 网关，响应回浏览器 → 可跨机联调且无 CORS
curl -i http://localhost:3000/api/hotnews/latest   # → 200
```

切换到联调**本机后端**：把 `.env.development` 改为
`VITE_GATEWAY_TARGET=http://127.0.0.1:8000`。

### 6.2 后端：本机服务注册 Nacos，与 k3s Pod 并行

每个服务已提供 `src/main/resources/application-local.yml`：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        ip: 192.168.0.40     # 显式指定，避免自动探测到虚拟网卡导致 k3s 无法回访
        port: 8002
```

启动：

```bash
java -jar service-order/target/service-order-1.0.0.jar --spring.profiles.active=local
# 或：mvn -pl service-order spring-boot:run -Dspring-boot.run.profiles=local
```

**验证注册成功**（Nacos 3.x 用 v3 接口）：

```bash
curl -s "http://192.168.0.27:8848/nacos/v3/client/ns/instance/list?serviceName=service-order&namespaceId=public"
# 应返回 2 个实例：
#   k3s Pod  → 10.42.x.x:8002
#   本机      → 192.168.0.40:8002
```

> ⚠️ **必须让进程持久存活**。若进程被回收，心跳中断，Nacos 会自动剔除该实例。
> 建议用 `nohup ... &` 启动到独立会话，或直接用 IDE / 前台终端运行。

---

## 七、踩坑记录（重要）

### 7.1 镜像 tag 不变 → k8s 复用旧镜像

**现象**：改了代码、重新 push 同名 tag，Pod 行为依旧。

**原因**：k8s 默认 `imagePullPolicy: IfNotPresent`，k3s 用 containerd，
存在**独立于 docker 的镜像缓存**；tag 未变则直接用缓存。

**解决**：**每次构建递增 tag**（`1.0.0` → `1.0.1` → `1.0.2` …）。

### 7.2 网关路由全部失效（`New routes count: 0`）

**现象**：网关正常启动，但所有 `/api/**` 返回 404。

**原因**：**Spring Cloud Gateway 5.x 配置前缀变更**。
`GatewayProperties` 的前缀从 `spring.cloud.gateway`
改为 **`spring.cloud.gateway.server.webflux`**。
旧前缀写得再对也不会被加载。

**验证方法**（反编译确认前缀常量）：

```bash
javap -v org/springframework/cloud/gateway/config/GatewayProperties.class | grep "spring.cloud.gateway"
# 输出：ConstantValue: String spring.cloud.gateway.server.webflux
```

**解决**：配置改为

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes: [...]
          globalcors: {...}
```

### 7.3 异常日志自动配置导致启动失败（两种场景）

`common` 模块的 `ExceptionLogAutoConfiguration` 曾引发两类启动崩溃：

| 场景 | 报错 | 原因 | 修复 |
|---|---|---|---|
| WebFlux 网关（无 spring-jdbc） | `NoClassDefFoundError: JdbcTemplate` | 方法签名引用 JdbcTemplate，Spring 解析返回类型时触发类加载 | 配置类加 `@ConditionalOnClass(JdbcTemplate.class)` |
| ms-ds-system（双数据源） | `required a single bean, but 2 were found` | `sourceJdbcTemplate` 与 `targetJdbcTemplate` 造成注入歧义 | 配置类加 `@ConditionalOnSingleCandidate(JdbcTemplate.class)` |

> **教训**：给 `common` 写自动配置时，必须假设**下游环境可能没有对应依赖**
> 或 **Bean 不唯一**，用条件注解显式约束。

### 7.4 前端 nginx 因后端未部署而崩溃

**现象**：`[emerg] host not found in upstream "service-user.ms-learn.svc.cluster.local"`，
容器 CrashLoopBackOff。

**原因**：`proxy_pass` 用域名时，nginx **启动阶段**即解析；集群内无该 Service 就退出。

**解决**：使用变量延迟解析，并让 `/sso/` 走网关：

```nginx
resolver 10.43.0.10 valid=10s ipv6=off;

location /api/ {
    set $gateway_upstream "ms-gateway.ms-learn.svc.cluster.local:8000";
    proxy_pass http://$gateway_upstream$request_uri;
}
```

### 7.5 节点 CPU requests 耗尽 → Pod Pending

**现象**：`0/1 nodes are available: 1 Insufficient cpu`。

**原因**：单节点 k3s，所有服务默认 2 副本时 **CPU requests 合计达到 100%**。

**解决**：测试环境后端服务统一 **1 副本**（CPU requests 从 4.0 降至 3.3，82%）。

### 7.6 Nacos 3.x 的 HTTP API 变化

- 镜像 `nacos/nacos-server:latest` 已是 **Nacos 3.x**；
  `nacos.core.auth.enabled=false` 时 **v1 鉴权接口返回 500**，
  `v1/v2` 多数端点 **404/501**（`no such api`）。
- **可用的读写接口**（实测）：
  - 读配置：`GET /nacos/v3/client/cs/config?dataId=...&groupName=...&namespaceId=public`
  - 查实例：`GET /nacos/v3/client/ns/instance/list?serviceName=...&namespaceId=public`
- **容器内 ContextPath 为 `/nacos`**，端口映射：`8848→8848`、`7080→8080`。

### 7.7 RocketMQ NameServer 需保持运行

消费端依赖 NameServer（`192.168.0.27:9876`）。若其停止，消费端会启动/运行异常。
已将其重启策略设为 `always`。

---

## 八、常用运维命令

```bash
# 查看全部资源
kubectl get pods,svc,deploy -n ms-learn

# 滚动状态
kubectl rollout status deploy/ms-gateway -n ms-learn --timeout=180s

# 日志
kubectl logs -n ms-learn -l app=ms-gateway --tail=100
kubectl logs <pod> -n ms-learn --previous     # 上次崩溃日志

# 强制重新拉取镜像（临时手段，更推荐换 tag）
kubectl rollout restart deploy/<name> -n ms-learn

# 扩容/缩容
kubectl scale deploy/<name> -n ms-learn --replicas=1

# 节点资源压力（排查 Pending）
kubectl describe node hong-ms-7970 | grep -A6 "Allocated resources"
```

---

## 九、变更记录

| 日期 | 变更 |
|---|---|
| 2026-09-22 | 首次完整部署：网关（11 路由）+ 前端 + 7 个后端服务上线 |
| 2026-09-22 | 修复 RocketMQ NameServer 宕机导致的消费端崩溃 |
| 2026-09-22 | 修复 `ExceptionLogAutoConfiguration` 缺条件注解导致的网关/双数据源服务启动失败 |
| 2026-09-22 | 网关路由前缀适配 Gateway 5.x（`spring.cloud.gateway.server.webflux`） |
| 2026-09-22 | 前端 nginx 改为变量延迟解析 DNS，解除对后端 Service 的启动依赖 |
| 2026-09-23 | 后端服务统一 1 副本，解决单节点 CPU requests 耗尽 |
| 2026-09-23 | 新增 `application-local.yml`，支持 Mac 服务并行注册 Nacos |
| 2026-09-23 | 前端 vite proxy 参数化（`VITE_GATEWAY_TARGET`），支持跨机联调 |
