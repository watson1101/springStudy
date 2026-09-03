# 需求说明：microservice-learn 全部微服务迁移到测试服务器 K8s

> 生成时间：2026-09-04 | 项目版本：com.ms.learn:microservice-learn:1.0.0

## 1. 背景与问题

当前 8 个 Spring Boot 微服务 + 1 个前端 Vue 项目，均以独立 JAR / `npm run dev` 或 Docker 容器形式运行。缺少：
- 统一调度与编排（手动 docker run 很繁琐）
- 多副本高可用
- 健康检查与自动恢复
- 滚动更新

用户希望把这些服务部署到测试服务器（192.168.0.27）上已安装的 Kubernetes + Flannel 集群里，每个服务跑 2 个 Pod。

## 2. 范围

### 2.1 必须部署（每服务 replicas=2）

| 服务名 | 模块 | 端口 | 说明 |
|---|---|---:|---|
| ms-gateway | `gateway/` | 8000 | Spring Cloud Gateway，集群唯一对外入口 |
| service-user | `service-user/` | 8001 | 用户 + SSO 认证 |
| service-order | `service-order/` | 8002 | 订单 |
| service-product | `service-product/` | 8003 | 商品 |
| service-transaction | `service-transaction/` | 8004 | 交易/支付模拟 |
| service-points | `service-points/` | 8005 | 积分 |
| ms-ds-system | `ms-ds-system/` | 8090 | 系统配置 + A股资讯同步 |
| flowable-service | `flowable-service/` | 8007 | Flowable 工作流 |
| ms-frontend | `frontend/` | 80 (nginx) | Vue3 + Element Plus 前端静态页 |

### 2.2 明确排除

- `flink-demo/`、`multi-thread/`：纯演示工具，无 Web 端口、无注册逻辑。
- 中间件（MySQL / Nacos / Redis / MongoDB / RocketMQ / Jenkins / Registry）：目前运行在 Docker，暂时保留不动，K8s 内服务通过 `192.168.0.27` 访问。如后续也需迁到 K8s 单独立项。

## 3. 部署环境（约束）

- **测试服务器**：192.168.0.27，Ubuntu 26.04，Kubernetes（Flannel，cni0=10.42.0.1）。
- **namespace**：新建 `ms-learn`，所有资源放入该命名空间。
- **镜像仓库**：服务器已存在私有 Docker Registry `192.168.0.27:5000`，所有构建镜像推送到该 Registry。
- **构建地点**：服务器上构建（代码 rsync/sftp 上传后 `mvn package` → `docker build` → `docker push localhost:5000/...`）。要求服务器具备 JDK 17、Maven 3.9+、Docker。
- **网络暴露**：
  - 微服务 Pod/Service 均为 `ClusterIP`，仅集群内部可达。
  - 网关 `ms-gateway` 使用 `NodePort`（端口 30080），外部访问：`http://192.168.0.27:30080`。
  - 前端 `ms-frontend` 使用 `NodePort`（端口 30081），外部访问：`http://192.168.0.27:30081`。
- **Nacos 地址**：`192.168.0.27:8848`（K8s Pod 可路由）。**注意**：此前要求把 Nacos 的 8080→88080 端口重映射尚未落实，若服务器上仍为 8080 且被 Jenkins 占用，需先修复 Nacos 控制台端口映射，否则只开 8848 不影响服务注册。
- **数据库**：全部通过宿主机 IP + 宿主机端口访问 MySQL：
  - service-user → `192.168.0.27:3306/ms_ds_user`
  - service-order → `192.168.0.27:3306/ms_ds_order`
  - service-product → `192.168.0.27:3306/ms_ds_product`
  - service-transaction → `192.168.0.27:3306/ms_ds_transaction`
  - service-points → `192.168.0.27:3306/ms_ds_points`
  - ms-ds-system → `192.168.0.27:3306/ms_ds_sys_config`（另外依赖 OPENCLAW_A_STOCK 两库）
  - flowable-service → `192.168.0.27:3306/ms_ds_flowable`
- **资源限制（HARDENING）**：
  - 每个 Pod 必须显式声明 `requests.cpu/memory` 与 `limits.cpu/memory`，避免被 evicted 或挤压 K8s 系统 Pod。
  - 每个 Deployment 必须配置 `livenessProbe` 与 `readinessProbe`（Spring Boot 走 `/actuator/health` 或自建健康端点；前端 nginx 走 `/index.html` 或 `/`）。
  - 数据库密码 / Nacos 密码不硬编码在镜像里，统一通过 K8s `Secret` 以环境变量注入；非敏感配置用 `ConfigMap` 挂挂载文件或额外 env。

## 4. 功能 / 非功能需求

### 4.1 功能需求
1. **K8s 部署清单**：仓库内新增 `k8s/` 目录，存放 namespace、configmap、secret（模板）、deployment、service、ingress（暂不启用，留占位）等 YAML，服务 1:1 对应，统一 `kubectl apply -f k8s/` 即可一键部署。
2. **镜像构建脚本**：服务器侧一键构建脚本 `build-push.sh`，按依赖顺序（common → 各 JAR 服务 / frontend）完成打包、build、tag、push 到 `localhost:5000/ms-learn/<service>:1.0.0`。
3. **Deployment 副本数**：所有服务 `replicas: 2`（可在单个 values / env 变量中集中调整）。
4. **Service 配置**：为每个 Deployment 创建同名 `Service`，selector 匹配 `app` label，targetPort 正确映射。网关 + 前端额外 `type: NodePort`。
5. **Nacos 服务注册**：K8s 内每个 Pod 启动后自动注册到 Nacos 192.168.0.27:8848，网关路由仍走 `lb://service-xxx`。
6. **前端部署准备**：
   - `frontend/` 新增 Dockerfile，多阶段构建（node build → nginx static）。
   - nginx.conf 中把 `/api/` 反代到 K8s 集群内部的 `ms-gateway:8000`，解决前端浏览器直接请求网关跨域问题（这比原来的 vite dev proxy 更合理）。
7. **镜像拉取策略**：生产场景 `IfNotPresent`，迭代版本号才重新拉；本次部署用 `1.0.0` tag，Policy 可先 `Always` 以便反复验证。
8. **部署结果验证脚本**：服务器上 `verify-deploy.sh`，输出所有 Pod Ready、副本数、Service/NodePort、Nacos 注册数、通过网关 NodePort 调用 service-user `GET /api/user/list` 返回 200。

### 4.2 非功能需求
1. **可复现**：所有 K8s YAML 入库，脚本入库，README.md 补充 `# Kubernetes 部署` 章节记录部署步骤。
2. **可回滚**：Deployment 使用默认 `RollingUpdate` 策略（`maxSurge=1, maxUnavailable=0`），保证升级期间不中断。
3. **稳定性**：
   - Gateway readinessProbe 失败时不接收流量；livenessProbe 失败时 Kubelet 自动重启容器。
   - JVM 内存与 cgroup 对齐（`-XX:MaxRAMPercentage=75.0`，JDK 17 默认行为）。
4. **可观测**：
   - 每个 Deployment 打上标准 label：`app`、`tier=backend/frontend`、`part-of=ms-learn`；
   - `kubectl logs` 可直接看每个 Pod 的 stdout；
   - 镜像内不写本地日志文件，全部 stdout（符合 Spring Boot 默认）。
5. **安全性**：
   - 数据库 / Nacos 密码仅在 Secret 中存在，K8s YAML 不提交真实值，仅提供 `*-secret.example.yaml`（占位符 `<REPLACE_ME>`），`.gitignore` 忽略真实 `*-secret.yaml`。
   - 容器以非 root 用户运行（JDK 镜像自带 `appuser` 或在 Dockerfile 显式创建），提升安全基线。
6. **容量估算（单节点）**：单机 K8s 上跑 18 个 Pod（9 服务 × 2），需要至少 4G 内存余量（每 Pod 512M requests + system）。服务器实际可用内存低于该阈值时给出 WARN 但不阻塞。

## 5. 假设与已知限制

1. **单节点 K8s 集群**：假设测试服务器是单节点 control-plane + worker，所有 Pod 会落在同一台机器上，副本只能防进程崩溃不能防主机故障。
2. **MySQL 非集群内**：数据库仍在宿主机 Docker，跨 Pod 共享，数据库宕机=全部数据服务不可用（已在非需求中说明）。
3. **Registry 需要 insecure**：私有 Registry 192.168.0.27:5000 未启用 HTTPS，需要服务器 docker daemon 配置 `insecure-registries: ["192.168.0.27:5000","localhost:5000"]` 才能 push/pull。
4. **K8s DNS 正常**：`kube-dns` / `CoreDNS` 正常解析 `service.namespace.svc.cluster.local`；Flannel 使 Pod→Pod、Pod→宿主机 全可达。
5. **RBAC 权限充足**：当前执行 `kubectl apply` 的用户拥有 `ms-learn` namespace 的 admin 级权限。
6. **ms-ds-system 的 Binlog 同步**：该模块运行 `BinlogSyncRunner` 去消费远程 192.168.0.40 binlog，`replicas=2` 会导致两个 Pod 同时消费同一 binlog，可能造成重复写入或重复告警。需要通过 leader election 或直接把该功能 `replicas=1` 专 Deployment 剥离。决定默认：ms-ds-system **Deployment 仍 replicas=2**，但在 ConfigMap 中新增开关 `sync.a-stock.enabled=false`，默认关闭，防止重复消费；用户需要开启时可先缩到单副本。

## 6. 非目标（本轮不做）

- 不在 K8s 内部署 MySQL / Nacos / Redis / MongoDB / RocketMQ（当前 Docker 化）。
- 不上 Helm / Kustomize 编排，直接用原生 YAML（后续可迁移）。
- 不接入 HPA、Istio、Prometheus Operator 等进阶组件（可后续迭代）。
- 不做前端/网关/各服务端的 TLS 终止与 HTTPS 证书（全部 HTTP）。
- 不做完整单元/集成测试补全（项目已有部分测试，但部署工作不强制）。

## 7. 验收标准 Acceptance Criteria

### RULE 类（必须 100% 通过）
1. **AC-R1**：`kubectl get ns ms-learn` 返回 NAME="ms-learn" STATUS="Active"。
2. **AC-R2**：9 个 Deployment 全部存在，每个 `replicas=2, readyReplicas=2`（`kubectl -n ms-learn get deploy -o wide` 可见）。
3. **AC-R3**：每个 Deployment 至少有 1 个 Pod 通过 `livenessProbe` + `readinessProbe`（`kubectl -n ms-learn describe pod` 中无 Recent Unhealthy 事件）。
4. **AC-R4**：服务 Service 资源 9 个存在且 `selector` 与 Deployment label 匹配；网关 Service `type=NodePort` nodePort=30080，前端 Service `type=NodePort` nodePort=30081，其他 ClusterIP。
5. **AC-R5**：私有 Registry 仓库内 9 个镜像 tag=1.0.0 可 `curl http://localhost:5000/v2/_catalog` 枚举。
6. **AC-R6**：外部 `curl http://192.168.0.27:30080/api/user/list` 最终返回 HTTP 200（或 401/权限类响应，前提是 service-user 已鉴权，网关已成功路由到 service-user）。
7. **AC-R7**：外部 `curl http://192.168.0.27:30081/` 返回前端 index.html 响应头 `content-type: text/html`。
8. **AC-R8**：仓库中 9 份 Deployment YAML 中都显式声明 `resources.requests.memory`, `resources.limits.memory`, `livenessProbe`, `readinessProbe`。
9. **AC-R9**：仓库新增 `k8s/app-secret.example.yaml` 含所有密码占位符；真实 `app-secret.yaml` 在 `.gitignore` 中；提交前 `grep -R 'REPLACE_ME' k8s/` 仅出现在 example 文件里。
10. **AC-R10**：验证脚本 `verify-deploy.sh` 运行结束 exit code=0 并打印 "K8s Deploy: OK"。

### RUBRIC 类
1. **AC-Q1 部署文档完整度（0-2，阈值=1）**：README.md 新增「Kubernetes 部署」章节，覆盖环境前置、构建、部署、回滚、常见问题 5 小节；每节有具体命令示例。
   - 2 = 5 节都有具体命令 + 输出示例
   - 1 = 5 节齐全，部分小节无输出示例
   - 0 = 缺 ≥1 节或整体指令不可复现
2. **AC-Q2 资源参数合理性（0-2，阈值=1）**：各 Deployment requests/limits 值符合服务特征（网关高带宽低内存、flowable/ms-ds-system 偏内存密集），不会 OOMKilled 也不会 30 秒内 readiness 就超时。
   - 2 = 每个 Deployment 的 requests 和 limits 都标注了选值理由（注释），且启动 10 分钟内无 OOMKilled
   - 1 = 数值基本合理，仅有 1 个服务需要手动调整
   - 0 = 数值明显过大（浪费）或过小（频繁 CrashLoopBackOff）
3. **AC-Q3 YAML 规范性（0-2，阈值=1）**：统一命名规范、使用 label app=xxx、注释齐全、无硬编码空密码、无重复资源名。
   - 2 = 完全规范，`kubeval --strict k8s/*.yaml` 0 error
   - 1 = 基本规范，1~2 项不影响 apply 的小瑕疵
   - 0 = 存在无法 apply 的语法错误，或同一 name 跨文件冲突
