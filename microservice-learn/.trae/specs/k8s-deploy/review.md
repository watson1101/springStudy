# K8s 部署评审报告 (Review)

> 评审时间：2026-09-04
> 评审形式：Implementer 自审（单 reviewer 上下文）
> 相关文档：[spec.md](./spec.md) · [tasks.md](./tasks.md)
> 评审对象：仓库代码 `k8s/`、`frontend/`、各模块 Dockerfile/pom.xml、`.gitignore`、README.md 第八章/九章

---

## 一、Review 执行方法

1. **静态自审 PowerShell 脚本**（命令 id：`job-793ef3eb99e6464f9eb96840e271c593`）覆盖：
   - `REPLACE_ME` 占位仅出现在 `02-app-secret.example.yaml`；
   - 9 份 Deployment YAML 中 `replicas=2` 数量；
   - 每份 Deployment 存在 `livenessProbe` / `readinessProbe` / `requests` / `limits` / 至少两个 `memory:` 行；
   - 网关 Service `nodePort=30080`、前端 Service `nodePort=30081`；
   - 8 个 JAR pom.xml 带 `spring-boot-starter-actuator`；
   - 9 个 Dockerfile 带 `USER`；
   - 4 个 shell 脚本带 `set -euo pipefail`；
   - `.gitignore` 含 `k8s/02-app-secret.yaml` 且磁盘上不存在该敏感文件。
2. **手工 spot-check**：对照 AC，逐个文件交叉核对
   - spec AC-R2/R3/R4/R8/R9 vs 交付物；
   - 数据库脚本完整性：核对 7 个 ms_ds_* 子目录；
   - README.md「八、Kubernetes 部署」5 小节齐全性；
   - 前端 nginx.conf `/api/` 反代 + `/health` endpoint 存在；
   - K8s ConfigMap 覆盖 spec 5-6 中 flowable 子引擎开关与 ms-ds-system binlog 开关。

发现的 1 项小问题已在 Review 期间作为 pending issue 进入 Implement：
- ms_ds_order 缺少独立 `sql/ms_ds_order/init.sql`（只存在旧文件 sql/init-mysql.sql 中）→ 已按其余 6 个库的格式补齐，并同步修订 README 8.1-4 数据库准备指令。

---

## 二、RULE AC 核查（AC-R1 ~ AC-R10）

> 说明：AC-R2/R3/R4/R5 涉及「真实 K8s apply 成功」，当前尚未在服务器执行（SSH 密钥受限，需用户在服务器终端执行脚本后二次 verify 补证据）。所以
> 下述分为「静态可验证 ✅」与「部署后二次核查 ⏳」两类。

| AC ID | 通过？ | 证据 |
|---|---|---|
| AC-R1  ns ms-learn Active | ✅ 静态可验证 | `k8s/00-namespace.yaml` 已创建；deploy-apply.sh 首步 `kubectl apply -f 00-namespace.yaml`。**部署后二次核查：** `kubectl get ns ms-learn` 输出 Active 才算最终 pass（verify-deploy.sh 第 [1/8] 项已做）。 |
| AC-R2  9 Deployment replicas=2 + ready=2 | ✅ 静态可验证 / ⏳ 部署后 | 9 份 YAML 均含 `replicas: 2`（自审脚本 count=9/9 通过）；ready=2 需部署后 `kubectl -n ms-learn get deploy` 确认（verify-deploy.sh 第 [2/8] 项已做）。 |
| AC-R3  9 Pod 均 live/ready probe 无 Unhealthy 事件 | ✅ 静态可验证 / ⏳ 部署后 | 9 份 YAML 均声明了 readiness/liveness probe（自审脚本 9/9 全部 OK）；实际 pod describe 中 Unhealthy 计数需部署后核查（verify 第 [3/8] 项）。 |
| AC-R4  9 Service 类型/端口正确 | ✅ 静态可验证 / ⏳ 部署后 | 网关 `nodePort: 30080`、前端 `nodePort: 30081`（自审脚本金牌 OK）；各 ClusterIP 端口 8001/8002/... 与 application.yml port 值一一对齐（spot check 6 份均一致）。deploy 后 `kubectl -n ms-learn get svc` 结果在 verify 第 [4/8] 项。 |
| AC-R5  私有 Registry 9 ms-learn 镜像 tag=1.0.0 | ⏳ 部署后 | build-push.sh 已写入 9 次 docker build/push（8 JAR + frontend），末尾 `curl v2/_catalog` 枚举；verify-deploy.sh 第 [5/8] 项检查 jq/python length ≥9。待服务器执行脚本后贴日志。 |
| AC-R6  网关 `/api/user/list` HTTP 200/401/403 | ⏳ 部署后 | verify-deploy.sh 第 [6/8] 项命中，200/401/403 视为合法。需要注意 service-user 接口默认 `@SaCheckPermission("user:list")` 会返回 401，是预期结果。 |
| AC-R7  前端 `/` 200 + content-type text/html | ⏳ 部署后 | verify-deploy.sh 第 [7/8] 项，已 curl -D header + grep content-type。 |
| AC-R8  9 份 deploy 显式 resources + probes | ✅ 静态可验证 | 自审脚本 9/9 全部 OK（live/ready>=1 且 reqs/limits/memory 条目达标）。 |
| AC-R9  secret 模板 + ignore + 无 REPLACE_ME 泄漏 | ✅ 静态可验证 | example 文件含 5 条 `<REPLACE_ME_base64_of_xxx>` 占位；磁盘不存在真实 `02-app-secret.yaml`；.gitignore 精确匹配（自审脚本 OK / PowerShell `grep REPLACE_ME k8s/*.*` 只命中 example 文件）。 |
| AC-R10 verify 脚本 exit=0 + 打印 OK | ⏳ 部署后 | 脚本末尾 `[ "${FAIL}" -eq 0 ] && echo "K8s Deploy: OK"` + exit 0，结构正确。 |

**临时结论**：所有「本地可静态自审」的 RULE AC 均 ✅ 通过。剩下 6 项（R2 ready=2 / R3 实际事件 / R4 实际 svc 对象 / R5 镜像 / R6 网关路由 / R7 前端响应 / R10 verify 退出码）必须在用户部署脚本跑完后贴出 verify-deploy.sh 日志才能签 pass。

---

## 三、RUBRIC AC 评审打分

### AC-Q1 文档完整度（README.md 第八章/九章）

**打分：2 / 2（阈值=1，通过）**

| 小节 | 状态 | 备注 |
|---|---|---|
| 8.1 环境前置（一次性） | ✅ 有命令 + 示例输出 | JDK17/mvn/docker 版本检查、insecure-registry JSON、K8s 可用性、MySQL prepare 一键 for 循环 |
| 8.2 上传项目 | ✅ 推荐 rsync + 回退 tar/sftp | 提供 Windows sftp 压缩包上传完整流程 |
| 8.3 构建&推送镜像 | ✅ 一键脚本 + 修复表 | build-push.sh 用法 + 三类常见报错处理表格 |
| 8.4 生成Secret+部署 | ✅ 两步到位 + `--overwrite`说明 | gen-secret.sh 默认值和覆盖变量均说明 |
| 8.5 验证 | ✅ 验证脚本 + 8 条验收表 | 含浏览器可访问的外部 URL 最终对照 |
| 8.6 回滚/维护 | ✅ 5 条常用命令 | rollout undo/history、scale、日志、卸载（危险项注释掉） |
| 8.7 常见问题 | ✅ 5 条已写入 | 留缺口 "部署后补齐" 标记 |
| 九、相关文档 | ✅ 6 个链接 | 新增 SENTINEL_K8S_DEPLOY / sql/ms_ds_*/init.sql / spec 文档 |

**证据**：`grep '^### 8\.' README.md` 可命中 7 个子节标题，第九节「相关文档」存在。

### AC-Q2 资源参数合理性

**打分：2 / 2（阈值=1，通过）**

9 份 Deployment 每份都在 resources.requests/limits 前后的注释中声明了「选值理由」（见任务 2 YAML 文件注释）。档位分三档：

- 极轻量（frontend）：50m/64Mi → 200m/256Mi
- 标准后端（gateway/user/order/product/transaction/points）：200m/384~512Mi → 500m/768Mi~1Gi
- 重内存（ms-ds-system / flowable-service）：300m/768Mi → 800m/1.5Gi

同时：
- Dockerfile 内 `MaxRAMPercentage=75.0` 与 limits.memory 严格对齐，JVM 不会越界触发 OOMKilled；
- flowable-service 把 initialDelaySeconds/readiness 给到 40s/120s（避免 ACT_ 建表阶段误杀），与 spec §4.2-3 稳定性要求一致。

**待部署后验证**：`kubectl -n ms-learn top pod` 若出现 limits/usage > 90%，需要调整档级。目前只能给静态 2 分，实跑后再复核。

### AC-Q3 YAML 规范性

**打分：2 / 2（阈值=1，通过）**

- 命名规范：`k8s/NN-<svc>.yaml`（NN=00/01/02/10..17/20）语义清晰，apply 顺序可预测。
- label 统一：`app/part-of/tier` 三项齐全，可被 `kubectl -l tier=backend` 等方式筛选。
- 无硬编码空密码：所有密码都是 `valueFrom.secretKeyRef`（spot check ms-gateway/service-user/flowable/frontend 均成立）。
- 无重名：各 Deployment/Service name 与 9 份 YAML 名称一一对应，无跨文件 name 冲突。
- 结构：每份 Deployment YAML 内 Deployment 和 Service 通过 `---` 分隔，缩进 2 格，Pod spec 子段 4/6/8 格，符合 K8s 惯例。
- Secret/ConfigMap 引用：`envFrom.configMapRef.name=ms-learn-common` / `envFrom.secretRef.name=ms-learn-secret` 完全匹配 01/02 文件 metadata.name。

（本地未安装 kubeval，无法做 strict 语法验证；但缩进/语法已 spot check 3 份未发现错误。建议用户首次 apply 前先 `kubectl apply --dry-run=client -f k8s/00-.../k8s/10-.../...` 过一遍验证。）

---

## 四、任务队列 (tasks.md) 逐项 Status + Evidence

| Task | Status | Completion Evidence |
|---|---|---|
| Task 1  基础脚手架 | completed | `k8s/00-namespace.yaml`, `k8s/01-configmap-common.yaml`, `k8s/02-app-secret.example.yaml`, `.gitignore` 追加 secret 忽略条目。T1-R1/T1-R2/T1-R3 均静态通过。 |
| Task 2  9 份 YAML | completed | 9 份 1:1 对应服务；T2-R1(9/9) T2-R2(9/9) T2-R3(9/9) T2-R4(网关/前端 NP) T2-R5(secret 引用无明文) 静态通过；T2-Q1=2, T2-Q2=2。 |
| Task 3  Dockerfile + actuator + frontend | completed | 8 JAR pom 都带 actuator 依赖（静态 8/8）；9 份 Dockerfile 都含 `USER`（9/9）；frontend/Dockerfile + nginx.conf（/api 反代 + /health endpoint）存在且结构核对通过。T3-R1/R2/R3/R4 全通过。 |
| Task 4  三脚本 | completed | build-push.sh / deploy-apply.sh / verify-deploy.sh + gen-secret.sh 共 4 份 shell，均含 `set -euo pipefail`；T4-R1(顺序正确) T4-R2(secret 缺失拦截) T4-R3(R6/R7/副本检查) 全静态通过。 |
| Task 5/6  服务器上传&部署+验证 | blocked → 待用户执行 | **Blocked By**：沙箱拦截含密码的 SSH，SSH 密钥尚未能连通服务器。**Unblock Condition**：用户在 192.168.0.27 终端依 README 8.2~8.5 四步依次跑完，粘贴 `verify-deploy.sh` 完整日志 / `kubectl -n ms-learn get pods -o wide` / `kubectl -n ms-learn get deploy` 结果 → 即可把 Task 5/6 置 completed，并据此补充 tasks.md 中 T5-R1~R3、T6-R1~R4 的 Evidence 字段。 |
| Task 7  README.md | completed | 新增八章（7 子节 + 九章文档索引）。AC-Q1 评分 2/2。 |
| Review 修正项（ms_ds_order init.sql 缺失） | completed（Review → Implement → 复验） | 已新建 `sql/ms_ds_order/init.sql`，修订 README.md 8.1 数据库准备 for 循环现在能 6 个库一次性跑完。复验：`ls sql/ms_ds_*/init.sql` 命中 6 个。 |

---

## 五、Findings & Action Items（部署后复查）

以下 findings 不是阻塞问题，但需要用户在服务器跑完后把输出贴回，我会据此更新 tasks.md `Blocked → completed`，并在下次独立 Review 中作为证据闭环。

### F1（P0，阻塞最终通过）部署后输出缺失

需要用户贴回：

```bash
cd ~/microservice-learn
bash k8s/scripts/build-push.sh   2>&1 | tee /tmp/build.log
bash k8s/scripts/gen-secret.sh
bash k8s/scripts/deploy-apply.sh  2>&1 | tee /tmp/deploy.log
sleep 90   # 等 flowable-service 拉镜像+首次建表
bash k8s/scripts/verify-deploy.sh 2>&1 | tee /tmp/verify.log
echo "== kubectl get pods =="; kubectl -n ms-learn get pods -o wide
echo "== kubectl get deploy =="; kubectl -n ms-learn get deploy
echo "== top pods =="; kubectl -n ms-learn top pods 2>/dev/null || echo "metrics-server not installed, skip"
echo "== events =="; kubectl -n ms-learn get events --sort-by=.lastTimestamp --no-headers | tail -50
```

将上述完整输出贴回来即可结案。

### F2（P1，非阻塞）建议后续改进

- **F2.1**：Nacos/Nginx Ingress 路由：目前前端 `nginx.conf` 已解决浏览器端 CORS。如果未来要装 ingress-nginx，可把 ms-frontend/ms-gateway 两个 NodePort 改回 ClusterIP + Ingress host 路由。
- **F2.2**：service-user 接口 `@SaCheckPermission("user:list")` 使 verify R6 只能拿 401 当成功。后续可部署后先登录创建 admin 会话再调接口，作为 verify 脚本增强项。
- **F2.3**：缺少 `k8s/svc-*.yaml` 单独拆分文件，所有单模块 Deployment+Service 共在一个文件中，改 Service 时需小心文件结构。如后续多人协作可拆分 `k8s/base/<svc>/deployment.yaml`、`svc.yaml`。

---

## 六、最终裁决（待部署后二次审查）

> 规则：`pass` 仅当所有 Findings 都有证据、且所有 RULE AC 都满足。

| 结果类别 | 当前结论 |
|---|---|
| 静态 REVIEW | **pass**（文件一致性、RULE 可静态项、3 条 RUBRIC 阈值均通过） |
| 运行时 REVIEW | **blocked**（原因：F1 部署执行证据缺失，Unblock=F1 中用户贴回脚本输出） |
| 综合结论 | **blocked** |

待用户贴回 verify 输出后，我会新建一份独立的「Cycle 2 Review」，更新：R2/R3/R4/R5/R6/R7/R10 最终证据、RUBRIC 实际运行复核、修正 F2.1/F2.2/F2.3 可选改造优先级。
