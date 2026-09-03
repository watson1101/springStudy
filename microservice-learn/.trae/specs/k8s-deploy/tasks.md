# 实施任务清单：K8s 部署 microservice-learn

> 对应 spec 文档：`spec.md` | 创建时间：2026-09-04

依赖顺序：Task 1（基础 K8s 脚手架）→ Task 2/3/4（可并行：清单/脚本/Dockerfile）→ Task 5（服务器部署）→ Task 6（验证）→ Task 7（文档）

---

## Task 1: 初始化 K8s 基础脚手架

**Scope**: 新建 `k8s/` 目录结构，含 namespace、公共 configmap 与 secret example 模板、统一 labels、`.gitignore` 忽略真实 secret。
**Priority**: high
**Status**: pending

**Depends**: None.

**交付物**：
- `k8s/00-namespace.yaml`：`kind: Namespace, name: ms-learn`，label `app.kubernetes.io/part-of: ms-learn`
- `k8s/01-configmap-common.yaml`：ConfigMap 保存非敏感公共配置（NACOS_SERVER、MYSQL_HOST、MYSQL_PORT、SSO_SECRET 占位）。注意密码不放这里。
- `k8s/02-app-secret.example.yaml`：Secret 模板，值全部为 `<REPLACE_ME>` 占位。包含以下 key：`MYSQL_PASSWORD`、`NACOS_USERNAME`、`NACOS_PASSWORD`、`SSO_SERVER_SECRET`、`SSO_CLIENT_SECRET`。
- 在根 `.gitignore` 追加 `k8s/02-app-secret.yaml`。
- `k8s/kustomization.yaml`（或 `README` 提示 apply 顺序）：按前缀号 00/01/02/10/20/30 依次 `kubectl apply -f`。

**AC Coverage**:
- AC-R1（namespace 存在）由 00-namespace.yaml 直接满足。
- AC-R9（secret example + ignore）由 02-app-secret.example.yaml + .gitignore 满足。

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T1-R1 | rule | `kubectl apply -f k8s/00-namespace.yaml && kubectl get ns ms-learn` 返回 Active |
| T1-R2 | rule | `grep -R 'REPLACE_ME' k8s/` 仅出现在 02-app-secret.example.yaml |
| T1-R3 | rule | `.gitignore` 精确包含 `k8s/02-app-secret.yaml` |

---

## Task 2: 为 9 个服务生成 K8s Deployment + Service YAML

**Scope**: 按 9 个服务（gateway、service-user、service-order、service-product、service-transaction、service-points、ms-ds-system、flowable-service、frontend）各生成一份 Deployment + Service。
**Priority**: high
**Status**: pending

**Depends**: Task 1。

**交付物**（文件总数 18 个 YAML）：
- `k8s/10-gateway.yaml`（含 Deployment + Service 同文件，以 `---` 分隔）
- `k8s/11-service-user.yaml`
- `k8s/12-service-order.yaml`
- `k8s/13-service-product.yaml`
- `k8s/14-service-transaction.yaml`
- `k8s/15-service-points.yaml`
- `k8s/16-ms-ds-system.yaml`
- `k8s/17-flowable-service.yaml`
- `k8s/20-frontend.yaml`

每份必须包含：

- Deployment:
  - `replicas: 2`
  - 容器镜像 `localhost:5000/ms-learn/<service>:1.0.0`，`imagePullPolicy: IfNotPresent`
  - 统一 label：`app: <svc-name>`、`tier: backend|frontend`、`part-of: ms-learn`
  - 容器 env 引用 ConfigMap / Secret（`NACOS_SERVER_ADDR`、`MYSQL_HOST` 等公共的用 ConfigMap，密码全部 Secret envFrom）
  - `resources.requests.memory`、`resources.limits.memory` 显式声明，CPU 也声明
  - Java 服务 `livenessProbe` + `readinessProbe` 走 HTTP GET `/actuator/health`；若当前项目未引入 actuator，则加一个 HTTP GET `/api/health`（可复用已有的 HealthController 逻辑，新增端点即可）
  - frontend nginx liveness/readiness = HTTP GET `/index.html`（200）
  - `strategy: RollingUpdate (maxSurge=1, maxUnavailable=0)`
  - 非 root user：`securityContext.runAsNonRoot=true, runAsUser=1000`（需与 Dockerfile USER 对齐）
  - flowable-service 的 env 显式关闭多余子引擎：`FLOWABLE_IDM_ENABLED=false`、`FLOWABLE_EVENT_REGISTRY_ENABLED=false`
  - ms-ds-system 的 env 显式设置 `SYNC_A_STOCK_ENABLED=false`（默认关闭 binlog 同步双写问题）

- Service:
  - `app` selector 匹配
  - 大部分 type=ClusterIP，port 与 containerPort 一致（8000/8001/...）
  - Gateway Service `type: NodePort, nodePort: 30080, port: 8000`
  - Frontend Service `type: NodePort, nodePort: 30081, port: 80`

**资源参数建议（Task 3 构建镜像时一起验证合理性，AC-Q2 打分基于此）**：

| 服务 | requests CPU/Mem | limits CPU/Mem | 选值理由 |
|---|---:|---:|---|
| gateway | 200m / 384Mi | 500m / 768Mi | 纯路由无状态，内存低 |
| service-user | 200m / 512Mi | 500m / 1Gi | 鉴权+SQL，比 gateway 稍重 |
| service-order | 200m / 512Mi | 500m / 1Gi | SQL+Feign，同 user 档 |
| service-product | 200m / 512Mi | 500m / 1Gi | SQL 为主 |
| service-transaction | 200m / 512Mi | 500m / 1Gi | 2s 延时+积分 Feign |
| service-points | 200m / 512Mi | 500m / 1Gi | 账户+流水 SQL |
| ms-ds-system | 300m / 768Mi | 800m / 1.5Gi | MyBatis 多表+binlog（默认关），重内存 |
| flowable-service | 300m / 768Mi | 800m / 1.5Gi | Flowable 引擎初始化占内存 |
| frontend | 50m / 64Mi | 200m / 256Mi | 纯静态 nginx，极轻 |

**AC Coverage**:
- AC-R2（replicas=2 + ready=2）：每份 Deployment `replicas:2` + probes
- AC-R3（probes 存在且工作）：所有 9 份都声明 liveness/readiness
- AC-R4（Service 类型/端口）：网关 NodePort 30080，前端 30081，其他 ClusterIP
- AC-R8（显式 resources + probes）：逐文件检查
- AC-Q2：资源值与上表一致

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T2-R1 | rule | 9 份 YAML 都包含 `kind: Deployment` 和 `kind: Service` |
| T2-R2 | rule | `grep -c 'replicas: 2' k8s/1* k8s/2*` = 9 |
| T2-R3 | rule | 9 份 Deployment 都含 `readinessProbe:` 和 `livenessProbe:` 字段 |
| T2-R4 | rule | gateway svc 含 `nodePort: 30080`，frontend svc 含 `nodePort: 30081` |
| T2-R5 | rule | 所有 secret 密码引用都走 `valueFrom.secretKeyRef`，env literal 中无明文密码 |
| T2-Q1 | rubric | AC-Q2 映射，0-2，阈值 1 |
| T2-Q2 | rubric | AC-Q3 映射，0-2，阈值 1（可在 Review 环节用 kubeval 校验） |

---

## Task 3: 统一 JAR 服务 Dockerfile，新增 Spring Actuator，新建 frontend Dockerfile + nginx.conf

**Scope**: 改造所有服务的 Dockerfile 使其可在服务器侧单阶段/多阶段构建，并满足非 root、健康端点、JVM 内存参数；新增 actutor/web 依赖；提供 frontend 的多阶段 build + nginx 反代 api。
**Priority**: high
**Status**: pending

**Depends**: None。

**交付物**：
1. `gateway/pom.xml`、各 service-user/pom.xml ... ：全部补 `spring-boot-starter-actuator` 依赖，保证 `/actuator/health` 端点可用。flowable-service 与 ms-ds-system 已含健康端点时，保留 actuator 作为额外标准端点。
2. 统一 Dockerfile 规则（逐个改 8 份 Dockerfile；现有模板大部分已可用，需改 USER + JVM 参数）：
   - 多阶段：`maven:3.9-eclipse-temurin-17 AS build` + `eclipse-temurin:17-jre`
   - 非 root：`useradd -u 1000 appuser` + `USER 1000`
   - 启动命令加 `-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC`，并暴露 `JAVA_OPTS` 环境变量覆盖
3. 新文件 `frontend/Dockerfile`：
   - Stage 1 `node:20-alpine` + `npm ci --registry=https://registry.npmmirror.com` + `npm run build`
   - Stage 2 `nginx:1.27-alpine`，COPY dist 到 `/usr/share/nginx/html`
   - `USER 1000`（nginx alpine 有 nginx uid=101 可选，选 101 或新建 uid=1000 都行，但需与 k8s runAsUser 一致）
4. 新文件 `frontend/nginx.conf`（替换默认）：
   - listen 80；server_name _；
   - `location / { try_files $uri $uri/ /index.html; }` 支持 SPA history 模式
   - `location /api/ { proxy_pass http://ms-gateway.ms-learn.svc.cluster.local:8000/; }`，并设置必要 proxy headers（X-Real-IP/X-Forwarded-For/X-Forwarded-Proto）
   - 暴露健康检查 `location = /health { return 200 'ok'; }`

**AC Coverage**:
- AC-R3：通过 actuator 端点提供 probe
- AC-Q2：`MaxRAMPercentage=75` 保证 JVM 与 cgroup limits 对齐
- AC-SECURITY（非显式 AC，属于 4.2-5）：非 root user

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T3-R1 | rule | 8 份 JAR 服务 Dockerfile 都存在 `USER` 指令 |
| T3-R2 | rule | 9 份 pom.xml（8 微服务 + gateway）都包含 `spring-boot-starter-actuator` 依赖 |
| T3-R3 | rule | frontend/Dockerfile 存在，有 node 构建阶段 + nginx 静态托管阶段 |
| T3-R4 | rule | frontend/nginx.conf 存在，`/api/` 块 `proxy_pass` 指向 `ms-gateway` ClusterIP 地址 |

---

## Task 4: 生成服务器侧构建+推送脚本、部署脚本、验证脚本

**Scope**: 三个 shell 脚本（在服务器上 `bash` 可运行），保证一键完成部署。
**Priority**: high
**Status**: pending

**Depends**: Task 2、3。

**交付物**：
1. `k8s/scripts/build-push.sh`
   - 检查 JDK17、Maven 3.9+、Docker、Registry 可达（`curl -s http://localhost:5000/v2/_catalog`）
   - 顺序：先 common，再 gateway、8 service，最后 frontend。
   - 每步失败立即 `set -euo pipefail` 退出。
   - 镜像 tag 规则：`localhost:5000/ms-learn/<service>:1.0.0`
   - Docker 构建上下文各自对应模块目录（frontend 构建用 frontend/Dockerfile，其他多阶段 jar 用项目根 Dockerfile 或对应模块 Dockerfile）。
2. `k8s/scripts/deploy-apply.sh`
   - 依次 `kubectl apply -f k8s/00-namespace.yaml` `01-configmap-common.yaml` `02-app-secret.yaml` `10-*.yaml` `11-*.yaml` ... `20-frontend.yaml`
   - 中间如果缺少 `02-app-secret.yaml`，打印提示并退出，要求用户从 example 复制并填入实际密码。
   - 应用完成后 `kubectl -n ms-learn rollout status deploy --timeout=5m` 逐个等待就绪。
3. `k8s/scripts/verify-deploy.sh`
   - 逐服务检查 Pod Ready 数量、Deployment 2/2、Pod restart count <3
   - 枚举私有 Registry 镜像数量 = 9
   - 检查 Nacos 注册实例 = 至少 16 个（8 个 java 服务 × 2）
   - `curl -sS http://192.168.0.27:30080/api/user/list` 返回 200 或 401（只要不是 404/503/504 网关空响应就算通过）
   - `curl -sS -o /dev/null -w '%{http_code}' http://192.168.0.27:30081/` 返回 200
   - 全部通过后 `echo "K8s Deploy: OK"; exit 0`，否则 `echo "K8s Deploy: FAIL"; exit 1`

**AC Coverage**:
- AC-R5：build-push 保证镜像存在，verify 中 `curl v2/_catalog | jq '.repositories | length' == 9`
- AC-R6、AC-R7、AC-R10：verify-deploy 直接完成

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T4-R1 | rule | build-push.sh 有 `set -euo pipefail` 并按 common → 各 JAR → frontend 顺序执行 |
| T4-R2 | rule | deploy-apply.sh 缺 02-app-secret.yaml 时 exit code !=0 并打印提示 |
| T4-R3 | rule | verify-deploy.sh 至少包含 AC-R6 / AC-R7 / 副本数检查三段逻辑 |

---

## Task 5: 同步代码到服务器并执行构建、部署

**Scope**: 通过 sftp/rsync（或直接让用户自行上传）把整个项目传到 192.168.0.27，再执行 build-push.sh → deploy-apply.sh。**注：SSH 工具链目前受限（密钥未配置成功），将以「给用户一份精确的步骤命令」方式在服务器终端手工执行或让用户解决 SSH 公钥后我来跑。**
**Priority**: high
**Status**: pending

**Depends**: Task 1/2/3/4 全部完成。

**交付物**：
1. 项目代码（含新生成的 k8s/、frontend Dockerfile、改好的各 pom/Dockerfile）在服务器上有干净副本。
2. 依次运行：`bash k8s/scripts/build-push.sh` → `cp k8s/02-app-secret.example.yaml k8s/02-app-secret.yaml && vi ...` → `bash k8s/scripts/deploy-apply.sh` → `bash k8s/scripts/verify-deploy.sh` 并记录日志。
3. 遇到构建失败（缺 JDK/Maven/docker insecure 等）时记录成 Blocked，给用户修复命令后再重试。

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T5-R1 | rule | `curl -s http://localhost:5000/v2/_catalog | jq '.repositories | length'` = 9 |
| T5-R2 | rule | `kubectl -n ms-learn get deploy` 显示 9 个 deploy 且 READY 列都是 2/2 |
| T5-R3 | rule | `kubectl -n ms-learn get pods` 显示 18+ Pods Running 且 restart 次数全 ≤1 |

---

## Task 6: 运行验证脚本并修复首轮问题

**Scope**: 运行 verify-deploy.sh，对失败项逐个定位并修复。常见问题池：
- Registry insecure 未配置 → 改 `/etc/docker/daemon.json` 重启 docker
- 前端 nginx 与 runAsNonRoot UID 不匹配 → 调 Dockerfile USER
- 某服务启动报数据库密码不对 → 检查 secret 注入
- service-user /api/user/list 403 缺 token → 改 verify 脚本条件为「!=404 也 !=503」即可或先注册 admin 获取 token
- flowable-service ACT_ 表未建 → 检查 `FLOWABLE_DATABASE_SCHEMA_UPDATE=true` env
- ms-ds-system OPENCLAW_A_STOCK 库不存在 → 走默认关 binlog
- Nacos 服务注册不足 16 → 逐个看 Pod log `com.alibaba.nacos.*: [REGISTER-SERVICE]`

**Priority**: high
**Status**: pending

**Depends**: Task 5。

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T6-R1 | rule | verify-deploy.sh 最终 exit code=0 |
| T6-R2 | rule | 每个 Pod restart count <= 1 |
| T6-R3 | rule | 外部 `curl http://192.168.0.27:30080/api/user/list` 最终返回 code 200 或 401（非 404/503/504） |
| T6-R4 | rule | 外部 `curl -sS -o /dev/null -w '%{http_code}' http://192.168.0.27:30081/` = 200 |

---

## Task 7: 更新 README.md 新增「Kubernetes 部署」章节 + 最终文档

**Scope**: 在项目根 README.md 追加章节，补充：
- 5.1 环境前置（Ubuntu、K8s、Docker Registry insecure、MySQL/Nacos IP）
- 5.2 构建 & 推送镜像
- 5.3 生成 Secret 并部署
- 5.4 回滚：`kubectl -n ms-learn rollout undo deploy/xxx`
- 5.5 常见问题（Registry push 失败 x509、前端 /api 跨域、service-user 401、flowable 表未建、Pod OOMKilled）
- 5.6 常用 kubectl 命令速查

**Priority**: medium
**Status**: pending

**Depends**: Task 6（因为 5.5 常见问题里要填充实际遇到的坑）。

**AC Coverage**:
- AC-Q1：README 章节完整度打分

**Test Requirements (TR)**:

| ID | Type | 条件 |
|---|---|---|
| T7-Q1 | rubric | AC-Q1 映射，0-2，阈值 1 |

---

## 取消项（Cancelled）

无。所有 Cancel 必须在该表显式登记并有用户审批记录。
