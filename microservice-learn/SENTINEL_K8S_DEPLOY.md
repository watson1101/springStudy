# Sentinel Dashboard on K8s（k3s）部署与运维说明

> **部署方式**：方案 1 — Helm Chart（royalwang/sentinel-dashboard 社区 Chart）
> **部署日期**：2026-09-03
> **目标集群**：测试服务器 k3s v1.36.4（单节点 192.168.0.27，用户 hong）
> **文档生成时间**：2026-09-03

---

## 一、部署结果总览

| 项目 | 值 |
|------|-----|
| Helm Release Name | `sentinel-dashboard` |
| Helm Chart | `sentinel-dashboard/sentinel-dashboard`（royalwang 社区源，版本 0.1.0 / App 1.16.0） |
| 命名空间 | `infra` |
| 工作负载类型 | **StatefulSet**（单副本，名称 `sentinel-dashboard`） |
| Pod 名 | `sentinel-dashboard-0` |
| 镜像 | `royalwang/sentinel-dashboard:1.8.4` |
| Dashboard 启动用户 | `sentinel / sentinel` |
| Dashboard JVM | `-server -Xms512M -Xmx1G ...`，容器内 8858 端口 |

### 对外访问地址（推荐局域网直接用 NodePort）
```
Sentinel Dashboard Web UI:   http://192.168.0.27:30858/
Sentinel Dashboard 用户名:   sentinel
Sentinel Dashboard 密码:     sentinel
```

### K8s 内部服务访问（给其他 Pod / SDK 使用）
| 监听器 | K8s 内部地址 |
|--------|-------------|
| Dashboard HTTP（8858） | `sentinel-dashboard.infra.svc.cluster.local:8858` |
| Client API / 心跳上报（8719） | `sentinel-dashboard.infra.svc.cluster.local:8719` |

> 💡 注意：当前 k3s 集群只有一个节点 `hong-ms-7970`，所以 NodePort `30858` / `30719` 会落在该节点的 `192.168.0.27` 上，局域网全机可达。

### 端口映射一览
| 用途 | 容器内 | K8s Service (ClusterIP) | 对外 NodePort (192.168.0.27) |
|------|--------|------------------------|------------------------------|
| Sentinel Dashboard Web UI | 8858 | 8858 | **30858** |
| Sentinel Client API (SDK 心跳上报/规则推回) | 8719 | 8719 | **30719** |

---

## 二、前置环境（本次部署时的真实情况）

| 软件 | 版本 | 备注 |
|------|------|------|
| 操作系统 | Ubuntu 26.04 LTS | 单机 |
| K8s 发行版 | k3s v1.36.4+k3s1 | 单 control-plane 节点（同时是 worker） |
| 容器运行时 | containerd 2.3.4-k3s1.36 | k3s 内置 |
| kubectl | v1.36.4+k3s1 | `/usr/local/bin/kubectl` |
| Helm | v3.16.3 | **本次手动安装**到 `~/.local/bin/helm` |
| Docker Engine | 29.1.3 | 仍在运行，用于本地 pull / save tar（k3s 运行时不走 Docker daemon） |
| 存储类 | `local-path` | k3s 自带（本 Chart 本次未实际启用 PVC，可忽略） |
| Helm Repo | `sentinel-dashboard` → `https://royalwang.github.io/sentinel-dashboard-for-k8s` | 已 `helm repo add` 并 update |
| KUBECONFIG | `~/.kube/config`（从 `/etc/rancher/k3s/k3s.yaml` 复制，权限 600） | 避免 helm 走 Jenkins 8080 / 匿名 403 |

> ⚠️ 注意：本次踩坑——服务器环境变量曾残留 Jenkins 相关代理，导致 Helm/helm 初次访问 K8s API 时被跳转到 Jenkins HTML 登录页；因此必须显式在当前会话声明 `KUBECONFIG=$HOME/.kube/config`，并在 `.bashrc` 中追加 `export PATH="$HOME/.local/bin:$PATH"` 与 `export KUBECONFIG="$HOME/.kube/config"`。下面命令均假设已在 `.bashrc` 中设置好。

---

## 三、重现部署步骤（从头到尾一条线）

### 3.1 安装 Helm 3（仅首次）
```bash
mkdir -p ~/.local/bin ~/tmp && cd ~/tmp
curl -fsSL -o helm.tar.gz https://get.helm.sh/helm-v3.16.3-linux-amd64.tar.gz
tar xzf helm.tar.gz && mv linux-amd64/helm ~/.local/bin/helm && chmod +x ~/.local/bin/helm
grep -q 'export PATH="$HOME/.local/bin:$PATH"' ~/.bashrc || echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
helm version
```

### 3.2 保证 kubectl 可用（避免 Helm 被误跳 Jenkins）
```bash
mkdir -p ~/.kube
cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
chmod 600 ~/.kube/config
echo 'export KUBECONFIG="$HOME/.kube/config"' >> ~/.bashrc
source ~/.bashrc
kubectl cluster-info
kubectl get nodes -o wide
```

### 3.3 添加 Sentinel Helm Repo
```bash
helm repo add sentinel-dashboard https://royalwang.github.io/sentinel-dashboard-for-k8s
helm repo update sentinel-dashboard
helm search repo sentinel-dashboard --versions | head
```

### 3.4 创建命名空间并自定义 values
```bash
kubectl create namespace infra --dry-run=client -o yaml | kubectl apply -f -

cat > ~/sentinel-values.yaml << 'EOF'
replicaCount: 1

image:
  repository: royalwang/sentinel-dashboard
  tag: 1.8.4
  pullPolicy: IfNotPresent

sentinel:
  dashboard:
    auth:
      username: sentinel
      password: sentinel

resources:
  requests:
    cpu: 200m
    memory: 512Mi
  limits:
    cpu: "2"
    memory: 2Gi

service:
  type: NodePort
  port: 8858
  api:
    port: 8719

ingress:
  enabled: false
EOF
```

### 3.5 执行 Helm Install
```bash
helm upgrade --install sentinel-dashboard sentinel-dashboard/sentinel-dashboard \
  --namespace infra --create-namespace \
  -f ~/sentinel-values.yaml --wait --timeout 300s
```

> ⚠️ **网络较慢时的坑**：首次安装时，如果 containerd 直接从 Docker Hub 拉 `royalwang/sentinel-dashboard:1.8.4` 很慢（> 10 分钟）会导致 Helm `--wait` 超时，Release 状态显示 `failed`，但 K8s 实际仍会继续拉镜像 + 启动 Pod。解决方案有两种：
>
> 1. **等一等**：`kubectl -n infra describe pod sentinel-dashboard-0` 里看到 `Pulling` 还在进行，最终会 `Pulled` → `Started`，Pod Ready 后功能正常。本次就是这种情况，`failed` 只是 Helm 安装阶段等待超时，不影响运行态。
> 2. **提前在 Docker 侧拉取后导入 k3s containerd**（见下面的 "高级：镜像预拉 + 离线导入"）。

### 3.6 （可选）把随机 NodePort 固定为 30858 / 30719
Helm chart 默认会随机分配 NodePort。为了让地址稳定，可以 patch Service：
```bash
kubectl -n infra patch svc sentinel-dashboard --type=json -p '[
  {"op":"replace","path":"/spec/ports/0/nodePort","value":30858},
  {"op":"replace","path":"/spec/ports/1/nodePort","value":30719}
]'
```

### 3.7 验证部署
```bash
# 1. 看工作负载
kubectl -n infra get statefulset,pod,svc,endpoints -o wide

# 2. 看 Pod 事件（确认 Pulled/Created/Started）
kubectl -n infra describe pod sentinel-dashboard-0 | tail -30

# 3. HTTP 验证登录页
curl -sI http://192.168.0.27:30858/ | head -10
# 期待看到 HTTP/1.1 200 + Content-Type: text/html + <title>Sentinel Dashboard</title>

# 4. 浏览器打开
# http://192.168.0.27:30858 → 输入 sentinel / sentinel 登录
```

---

## 四、高级：镜像预拉 + 离线导入（避免 Helm 超时，推荐后续升级复用）

本次安装过程中镜像拉取总耗时约 **15m59s**（~331 MB，单节点小水管场景）。若要避免再次等待：

```bash
# 1) Docker 侧拉（复用 Docker Hub 缓存 / 代理）
docker pull royalwang/sentinel-dashboard:1.8.4

# 2) 存为 tar
docker save royalwang/sentinel-dashboard:1.8.4 -o /tmp/sentinel.tar
ls -lh /tmp/sentinel.tar

# 3) 导入 k3s 自带 containerd（需要 sudo）
sudo k3s ctr images import /tmp/sentinel.tar

# 4) 确认镜像已存在（下次 Pod 创建时将直接命中 IfNotPresent，秒级启动）
sudo k3s ctr images ls -q | grep sentinel

# 5) 如后续升级到 1.8.5 等，重复步骤 1~4 再 helm upgrade 即可
```

---

## 五、业务服务接入 Sentinel（Spring Cloud Alibaba）

以仓库内 `service-product`（已接入 Sentinel）为例，把 `spring.cloud.sentinel.transport.dashboard` 从原来的 `127.0.0.1:8858` 改成 **K8s Service DNS**：

```yaml
spring:
  cloud:
    sentinel:
      transport:
        # ← 如果业务服务也跑在同一个 K8s 集群里：推荐走 Service DNS
        dashboard: sentinel-dashboard.infra.svc.cluster.local:8858
        # ← 如果业务服务还在 K8s 外（Docker Compose / 本地 IDE）：走 NodePort
        # dashboard: 192.168.0.27:30858
        port: 8719                # SDK 本机监听端口，Pod 内不用改，会自动避让冲突
        client-ip: ${POD_IP:}     # 推荐：Downward API 注入真实 Pod IP，便于 Dashboard 回连推规则
      eager: true                # 启动立即初始化，避免首次调用才上报造成第一次请求不拦截
      # 可选：持久化规则数据源到 Nacos
      datasource:
        ds1:
          nacos:
            server-addr: 192.168.0.27:8848
            dataId: ${spring.application.name}-sentinel-rules.json
            groupId: SENTINEL_GROUP
            rule-type: flow
```

> 🎯 **关键：Dashboard 与 SDK 的连通性**
>
> SDK（在每个业务 Pod 内）会在启动时**主动连接** `transport.dashboard` 上报心跳和机器列表；之后 Dashboard 要**反向回连** `transport.client-ip:8719` 把 UI 上配置的规则推给 SDK。因此：
>
> - 两个方向的网络必须双向可达（同 K8s 集群里默认是通的）
> - 如果开了 NetworkPolicy：放行 `infra/sentinel-dashboard` → 所有业务 Pod 的 `8719/tcp`，以及业务 Pod → `infra/sentinel-dashboard` 的 `8858/tcp`

---

## 六、常见运维命令速查

```bash
# ===== 通用 =====
source ~/.bashrc              # 确保 PATH + KUBECONFIG
helm env | head                # 确认 helm 环境
helm list -n infra             # Release 列表 (等同于 ls --namespace infra)
helm status sentinel-dashboard -n infra
helm get values sentinel-dashboard -n infra    # 查看当前生效的 values

# ===== 查看当前资源 =====
kubectl -n infra get all -o wide
kubectl -n infra get cm sentinel-dashboard -o yaml | head -50   # 看注入的 env / ConfigMap
kubectl -n infra get events --sort-by=.metadata.creationTimestamp | tail -20

# ===== 日志 =====
kubectl -n infra logs -f statefulset/sentinel-dashboard        # 实时
kubectl -n infra logs --tail=200 pod/sentinel-dashboard-0       # 最近 200 行
kubectl -n infra logs -p pod/sentinel-dashboard-0              # 上一次崩溃的日志（如有重启）

# ===== 进入容器 =====
kubectl -n infra exec -it pod/sentinel-dashboard-0 -- bash      # 或 sh
# 容器内 Sentinel jar 路径：/opt/sentinel-dashboard.jar
# 容器内日志目录：/opt/logs

# ===== 升级 / 改 values =====
# 1) 修改 ~/sentinel-values.yaml（例如 replicaCount -> 2；或改 password）
vi ~/sentinel-values.yaml
# 2) 升级 Release（不用 wait 也可以）
helm upgrade sentinel-dashboard sentinel-dashboard/sentinel-dashboard \
  -n infra -f ~/sentinel-values.yaml --wait --timeout 300s
# 3) 如需回滚到上一个 revision
helm rollback sentinel-dashboard -n infra
helm history sentinel-dashboard -n infra

# ===== 卸载 =====
# ⚠️ 注意：Sentinel Dashboard 规则默认是内存态，卸载后会丢失。已持久化到 Nacos 的规则不受影响。
helm uninstall sentinel-dashboard -n infra
# 可选：清理命名空间 / 自定义 values
kubectl delete ns infra
rm -f ~/sentinel-values.yaml
helm repo remove sentinel-dashboard   # （仅当确认不再用这个 chart）
```

---

## 七、常见问题

### Q1. `helm status` 显示 Release `STATUS: failed`，但 Pod 已经 Running / HTTP 200
这是典型的 **Helm `--wait` 超时**（默认 5 分钟；镜像首拉超过这个时间就记为失败）。
- **判断是否真正成功**：以 `kubectl -n infra get pod` + `curl http://192.168.0.27:30858/` 返回 200 为准。
- **如何消除 failed 状态（可选）**：
  ```bash
  helm upgrade sentinel-dashboard sentinel-dashboard/sentinel-dashboard \
    -n infra -f ~/sentinel-values.yaml
  ```
  不做实际改动的 helm upgrade 通常会把 Release 状态纠正为 `deployed`。

### Q2. SDK 在 Dashboard 上"看不到机器列表"或"规则下发不生效"
排查顺序：
1. 看应用启动日志：是否出现 `Sentinel 客户端启动完成` + `Register to dashboard ...`？
2. 在 Dashboard UI 上打开「机器列表」，确认 IP 是业务 Pod 的真实 Pod IP（不是 127.0.0.1）。
3. 从 Dashboard Pod 内反向 curl SDK Pod:8719 是否通：
   ```bash
   kubectl -n infra exec -i sentinel-dashboard-0 -- curl -sI --max-time 5 http://<业务PodIP>:8719/
   ```
   如果不通 → 检查 NetworkPolicy / 安全组 / 多集群 CNI。

### Q3. Dashboard 重启后，我之前配的限流规则全没了
Sentinel Dashboard **默认是内存存储**，容器重启/漂移即丢失。推荐两种持久化方案（已适配你们的现有技术栈）：
- **最简方案（强烈推荐）**：**规则存到 Nacos**——每个服务的 `spring.cloud.sentinel.datasource.*.nacos` 指向 Nacos 配置（前面第五节给了示例），Dashboard 和 SDK 都读写同一份 dataId。
- **扩展方案**：部署 AHAS（阿里云容器服务版有 ack-ahas-pilot Helm 包）或社区 Sentinel Dashboard 扩展 MySQL/Redis 规则存储。

### Q4. 需要多副本 / 高可用怎么办
Sentinel Dashboard 自身是无状态控制台，多副本时**规则必须走外部持久化（Nacos / DB）**，否则每台内存各自一份，UI 体验不一致。
```bash
# 在 ~/sentinel-values.yaml 里：
replicaCount: 2
# 然后 helm upgrade 即可；StatefulSet 会变成 2 个 Pod：sentinel-dashboard-0 / sentinel-dashboard-1
```
然后务必启用 Nacos 规则持久化，否则每个副本各自记录规则，看起来会"丢规则"。

### Q5. 想换为 Ingress（不用 NodePort）
如果后面 k3s 里装了 nginx-ingress / traefik，可以开启：
```yaml
ingress:
  enabled: true
  className: "nginx"
  hosts:
    - host: sentinel.example.com
      paths:
        - path: /
          pathType: Prefix
  tls: []
```

---

## 八、文档 & 文件位置索引

| 文档 / 文件 | 位置 |
|------------|------|
| 本部署说明（本 MD） | 服务器：`/home/hong/Documents/SENTINEL_K8S_DEPLOY.md` |
| 用户自定义 values.yaml | 服务器：`~/sentinel-values.yaml`（保留，便于后续 helm upgrade） |
| K8s kubeconfig | 服务器：`~/.kube/config`（权限 600） |
| Sentinel Helm Release | `sentinel-dashboard`，命名空间 `infra` |
| Sentinel Docker Compose 旧版（仍可用） | 仓库根：`docker-compose.yml`（ms-sentinel，端口 8858 宿主机映射） |

> 💡 注意：当前服务器上有**两套 Sentinel**同时存在：
> - **Docker Compose 版本**：`ms-sentinel` 容器，占用宿主机 `8858`
> - **K8s Helm 版本（新，本部署）**：`sentinel-dashboard-0` Pod，通过 NodePort 占用宿主机 `30858/30719`
>
> 建议：待确认业务 SDK 全部迁到 `192.168.0.27:30858`（或 K8s 内 Service）后，再下线 Docker Compose 版本以避免混淆。

---

## 九、一分钟验证清单（交付自检）

- [ ] `kubectl -n infra get pod` → `sentinel-dashboard-0 1/1 Running`
- [ ] `kubectl -n infra get svc sentinel-dashboard` → 端口 `8858:30858/TCP, 8719:30719/TCP`
- [ ] 浏览器打开 `http://192.168.0.27:30858/` → 能看到 Sentinel Dashboard 登录页
- [ ] 输入 `sentinel / sentinel` → 能登录并看到空控制台
- [ ] 在本地 IDE 启动 service-product 并将 `sentinel.transport.dashboard` 改为 `192.168.0.27:30858` → 刷新 Dashboard「机器列表」可看到该实例

---

*本文档由 hong 用户名下 k3s 集群 Sentinel Helm 部署实战整理生成，可直接复用于升级、迁移与排障。*
