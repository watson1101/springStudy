# 前端工程说明（microservice-learn-frontend）

> **技术栈**：Vue 3 + Vite 5 + Vue Router 4 + Element Plus + Axios
> **定位**：微服务学习项目的统一 Web 控制台，覆盖所有对外提供 HTTP 接口的业务模块
> **最后更新**：2026-09-23

---

## 一、快速开始

```bash
cd frontend
npm install          # 安装依赖
npm run dev          # 开发模式，默认 http://localhost:3000
npm run build        # 生产构建，产物输出到 dist/
npm run preview      # 预览构建产物
```

### 1.1 开发代理（`vite.config.js`）

代理目标由环境变量 `VITE_GATEWAY_TARGET` 控制，**默认指向测试服务器 k3s 网关**：

```js
const gatewayTarget = env.VITE_GATEWAY_TARGET || 'http://192.168.0.27:30080'

proxy: {
  '/api': { target: gatewayTarget, changeOrigin: true },
  '/sso': { target: gatewayTarget, changeOrigin: true }
}
```

`.env.development` 中切换两种联调场景：

```bash
# 场景 1（默认）：联调测试服务器 k3s 上的后端
VITE_GATEWAY_TARGET=http://192.168.0.27:30080

# 场景 2：联调本机（Mac mini）启动的后端
# VITE_GATEWAY_TARGET=http://127.0.0.1:8000
```

> **原理**：Vite dev server 的 `proxy` 是**服务端转发**。浏览器只访问 `localhost:3000`，
> 由 Vite 进程把请求转发到目标网关，响应再原路返回浏览器。
> 因此只要本机与目标主机**网络互通**，即可跨机联调，且天然规避 CORS。

### 1.2 本地启动前端 + 跨机联调（实测可行）

```bash
cd frontend
npm run dev          # http://localhost:3000

# 浏览器或 curl 直接验证（请求经 Vite 转发到 k3s 网关）
curl -i http://localhost:3000/api/hotnews/latest    # → 200，返回真实热榜数据
curl -i http://localhost:3000/api/order/list        # → 200
curl -i http://localhost:3000/api/user/list         # → 401（鉴权拦截，符合预期）
```

---

## 二、页面总览

| 路由 | 页面文件 | 对应模块 | 网关前缀 | 状态 |
|---|---|---|---|---|
| `/` | `Home.vue` | —（架构总览） | — | ✅ |
| `/users` | `Users.vue` | service-user | `/api/user/**` | ✅ |
| `/orders` | `Orders.vue` | service-order | `/api/order/**` | ✅ |
| `/products` | `Products.vue` | service-product | `/api/product/**` | ✅ |
| `/goods` | `Goods.vue` | service-goods | `/api/goods/**` | ✅ |
| `/points` | `Points.vue` | service-points | `/api/points/**` | ✅ |
| `/payments` | `Payments.vue` | service-transaction | `/api/payment/**` | ✅ |
| `/hotnews` | `HotNews.vue` | service-hotnews-collector / -consumer | `/api/hotnews/**` | ✅ |
| `/system` | `System.vue` | ms-ds-system | `/api/system/**` | ✅ |
| `/flowable` | `Flowable.vue` | flowable-service | `/api/flowable/**` | ⚠️ 仅框架，后端未验证 |

---

## 三、各页面功能明细

### 1. `/users` — 用户服务
- **模块**：`service-user`（8001，MySQL `ms_ds_user`）
- **功能**：用户列表、详情、注册/登录/登出、当前登录人
- **主要接口**：`GET /api/user/list`、`GET /api/user/{id}`、`POST /api/user`、
  `POST /api/user/auth/register`、`POST /api/user/auth/login`、`POST /api/user/auth/logout`、
  `GET /api/user/auth/me`
- **说明**：Sa-Token SSO Server，`/sso/**` 亦由该服务提供

### 2. `/orders` — 订单服务
- **模块**：`service-order`（8002，MySQL `ms_ds_order`）
- **功能**：订单列表、下单（OpenFeign 调 user 取用户信息）
- **主要接口**：`GET /api/order/list`、`POST /api/order/{userId}`

### 3. `/products` — 商品服务
- **模块**：`service-product`（8003，MySQL `ms_ds_product`）
- **功能**：商品列表、详情、新增；**Sentinel 限流演示**（连点触发熔断）
- **主要接口**：`GET /api/product/list`、`GET /api/product/{id}`、`POST /api/product`

### 4. `/goods` — 商品管理
- **模块**：`service-goods`（8006，MySQL，Sa-Token + OpenFeign 调字典）
- **功能**：分页查询、条件筛选（分类/关键词）、新增、编辑、**上架/下架**
- **主要接口**：
  | 方法 | 路径 | 说明 |
  |---|---|---|
  | `GET` | `/api/goods/page` | 分页查询（page/size/categoryId/keyword） |
  | `GET` | `/api/goods/{id}` | 商品详情 |
  | `POST` | `/api/goods` | 新增商品 |
  | `PUT` | `/api/goods` | 编辑商品 |
  | `PUT` | `/api/goods/{id}/on-shelf` | 上架 |
  | `PUT` | `/api/goods/{id}/off-shelf` | 下架 |
  | `GET` | `/api/goods/categories` | 分类字典（来自 ms-ds-system） |

### 5. `/points` — 积分服务
- **模块**：`service-points`（8005，MySQL）
- **功能**：查积分账户、查积分流水、模拟消费加分
- **主要接口**：
  | 方法 | 路径 | 说明 |
  |---|---|---|
  | `GET` | `/api/points/account/{userId}` | 查询积分账户 |
  | `GET` | `/api/points/records/{userId}` | 查询积分流水 |
  | `POST` | `/api/points/earn/consume` | 消费加分 |

### 6. `/payments` — 支付服务
- **模块**：`service-transaction`（8004，MySQL）
- **功能**：发起支付、按用户查支付单、按单号查详情
- **主要接口**：
  | 方法 | 路径 | 说明 |
  |---|---|---|
  | `POST` | `/api/payment/pay` | 发起支付 |
  | `GET` | `/api/payment/list/{userId}` | 用户支付单列表 |
  | `GET` | `/api/payment/{payNo}` | 支付单详情 |

### 7. `/hotnews` — 热点资讯
- **模块**：`service-hotnews-collector`（8008）+ `service-hotnews-consumer`（8009）
- **链路**：定时抓头条热榜 → RocketMQ → 消费入库 MySQL → 前端展示
- **功能**：查看最新热榜、按批次查询、**手动触发采集**
- **主要接口**：
  | 方法 | 路径 | 归属 |
  |---|---|---|
  | `GET` | `/api/hotnews/latest` | 消费端：最新热榜（≤50 条） |
  | `GET` | `/api/hotnews/batch/{batchId}` | 消费端：按批次查询 |
  | `POST` | `/api/hotnews/collect` | 采集端：手动触发一次采集 |
  | `GET` | `/api/hotnews/config` | 采集端：查看当前采集配置 |

> ✅ **路由已按服务细分**（2026-09-22 修复）：网关按声明顺序匹配，采集端排在消费端之前：
> `Path=/api/hotnews/collect,/api/hotnews/config` → 采集端；
> `Path=/api/hotnews/**` → 消费端。

### 8. `/system` — 系统配置
- **模块**：`ms-ds-system`（8090，MySQL；含 Binlog CDC 同步器；**双数据源**）
- **功能**（三个 Tab）：
  - **字典管理**：字典类型列表、按类型查字典项
  - **配置组**：配置分组列表
  - **Binlog 同步**：查状态、开启、关闭
- **主要接口**：
  | 方法 | 路径 | 说明 |
  |---|---|---|
  | `GET` | `/api/system/dict/types` | 字典类型列表 |
  | `GET` | `/api/system/dict/type/{dictType}` | 按类型查字典项 |
  | `GET` | `/api/system/group/list` | 配置组列表 |
  | `GET` | `/api/system/sync/status` | Binlog 同步状态 |
  | `POST` | `/api/system/sync/enable` | 开启同步 |
  | `POST` | `/api/system/sync/disable` | 关闭同步 |

### 9. `/flowable` — 工作流服务（仅框架）
- **模块**：`flowable-service`（8007，MySQL `flowable`）
- **功能**：流程定义 / 流程实例 / 待办任务 只读展示
- **主要接口**：`GET /api/flowable/process-definitions`、
  `GET /api/flowable/process-instances`、`GET /api/flowable/tasks/assignee/{assignee}`
- **⚠️ 说明**：按需求该模块**只配置不验证**。网关路由已配置，前端页面仅为框架。

---

## 四、目录结构

```
frontend/
├── index.html
├── package.json / package-lock.json
├── vite.config.js              # 开发端口 3000，/api 与 /sso 代理到 VITE_GATEWAY_TARGET
├── .env.development            # 开发联调目标（默认 k3s 网关 30080）
├── .env.production             # 打包模式（同源路径，nginx 反代）
├── nginx.conf                  # 容器内 nginx 配置（反代网关，变量延迟解析）
├── Dockerfile                  # 多阶段构建：node 构建 → nginx 托管
└── src/
    ├── main.js                 # 应用入口（注册 Element Plus / Router）
    ├── App.vue                 # 布局：左侧菜单 + 顶部标题 + 路由出口
    ├── router.js               # 路由表
    ├── api/
    │   └── http.js             # Axios 实例（baseURL=/api，响应拦截）
    └── views/                  # 页面组件
        ├── Home.vue            # 架构总览
        ├── Users.vue           # 用户服务
        ├── Orders.vue          # 订单服务
        ├── Products.vue        # 商品服务（Sentinel 演示）
        ├── Goods.vue           # 商品管理
        ├── Points.vue          # 积分服务
        ├── Payments.vue        # 支付服务
        ├── HotNews.vue         # 热点资讯
        ├── System.vue          # 系统配置
        └── Flowable.vue        # 工作流（仅框架）
```

---

## 五、网关路由对照

前端所有请求统一走 `/api/**` → 网关（8000）→ `lb://<服务名>`：

| 网关 Path | 目标服务 | 说明 |
|---|---|---|
| `/api/user/**` | `lb://service-user` | 用户服务 |
| `/sso/**` | `lb://service-user` | SSO 入口 |
| `/api/order/**` | `lb://service-order` | 订单服务 |
| `/api/product/**` | `lb://service-product` | 商品服务 |
| `/api/payment/**` | `lb://service-transaction` | 支付服务 |
| `/api/points/**` | `lb://service-points` | 积分服务 |
| `/api/goods/**` | `lb://service-goods` | 商品管理 |
| `/api/system/**` | `lb://ms-ds-system` | 系统配置 |
| `/api/flowable/**` | `lb://flowable-service` | 工作流（仅配置） |
| `/api/hotnews/collect`,`/api/hotnews/config` | `lb://service-hotnews-collector` | 采集端（须排在消费端之前） |
| `/api/hotnews/**` | `lb://service-hotnews-consumer` | 消费端 |

> ⚠️ **网关路由配置位置**：`gateway/src/main/resources/application.yml`，
> 前缀为 **`spring.cloud.gateway.server.webflux.routes`**（Gateway 5.x 新前缀，
> 旧前缀 `spring.cloud.gateway.routes` 会导致路由不加载）。

---

## 六、部署

### 6.1 nginx 配置要点（容器内）

`frontend/nginx.conf` 的两个关键设计：

**(1) 上游使用变量，延迟 DNS 解析** —— 避免后端未部署时 nginx 启动失败：

```nginx
resolver 10.43.0.10 valid=10s ipv6=off;   # k3s CoreDNS

location /api/ {
    set $gateway_upstream "ms-gateway.ms-learn.svc.cluster.local:8000";
    proxy_pass http://$gateway_upstream$request_uri;   # 变量 → 运行时解析
}
```

> **原因**：`proxy_pass` 直接写域名时，nginx **启动阶段**就会解析该域名；
> 只要集群内对应 Service 不存在，nginx 会以 `[emerg] host not found in upstream` 退出，
> 容器反复 CrashLoopBackOff。改用变量后，**只在实际请求时解析**，
> 前端不再依赖后端服务是否就绪。

**(2) `/sso/` 也走网关**（而非直连 service-user）：统一入口，减少硬依赖。

其余：`/` 走 SPA history 回退（`try_files ... /index.html`）、`/health` 供探针使用。

**浏览器只需访问前端 NodePort（30081）**，跨域问题由 nginx 内部反代解决。

### 6.2 构建与推送（服务器 192.168.0.27 上执行）

> ⚠️ 服务器 npm 访问外网不稳定，**推荐「本机构建 dist → 传服务器 → 只做 nginx 阶段」**：

```bash
# ① 本机（Mac）构建
cd frontend && npm run build

# ② 传到服务器
rsync -az --delete frontend/dist/ hong@192.168.0.27:/home/hong/build-new/frontend-dist/
rsync -az frontend/nginx.conf hong@192.168.0.27:/home/hong/build-new/frontend/

# ③ 服务器上打包镜像（仅 nginx 阶段，秒级完成）
cd /home/hong/build-new
docker build -f Dockerfile.frontend-only -t localhost:5000/ms-learn/frontend:<tag> .
docker push localhost:5000/ms-learn/frontend:<tag>
```

### 6.3 k8s 部署

```bash
scp k8s/20-frontend.yaml hong@192.168.0.27:/tmp/
ssh hong@192.168.0.27 'kubectl apply -f /tmp/20-frontend.yaml'
```

**⚠️ 镜像 tag 必须递增**（如 `1.0.0` → `1.0.1`）：k8s 默认
`imagePullPolicy: IfNotPresent`，**tag 不变时 kubelet 会复用节点缓存的旧镜像**，
导致改了代码却看不到效果。

---

## 七、新增页面的约定

新增一个模块页面时，按以下步骤：

1. 在 `src/views/` 新建 `<模块名>.vue`，参考现有页面风格（`el-card` + `el-table`）
2. 在 `src/router.js` 注册路由，并在 `meta.title` 写明服务与数据库
3. 在 `src/App.vue` 的 `el-menu` 中加菜单项（emoji + 中文名）
4. **确认网关有对应路由**（`gateway/src/main/resources/application.yml`），否则 `lb://` 无法转发
5. 在本 README 的**第三节表格**与**第五节路由表**补充条目
6. 运行 `npm run build` 验证

---

## 八、待办 / 注意

1. **flowable-service**：按需求仅配置未验证；需其注册 Nacos 后 `lb://` 才能转发成功。
2. **登录态管理**：已接入 Sa-Token 登录态校验，业务页面未登录会跳转 `/login`，
   登录成功后回跳原页面；Axios 会自动携带 `Authorization: Bearer <token>`。
3. **异常日志能力**：`common` 模块提供全局异常捕获，前端只需处理统一 `Result`
   结构（`code`/`message`/`data`）。
4. **测试环境副本数**：k3s 单节点 CPU 有限，后端服务统一 **1 副本**；
   需要高可用时可扩容，但需同步评估节点 CPU requests 余量。
---

## 九、登录状态校验

- 使用 Vue Router 全局守卫保护业务页面，未登录访问时跳转 `/login`。
- 登录成功后按 `redirect` 参数回跳原请求页面。
- Axios 请求自动携带 `Authorization: Bearer <token>`。
- 收到 401 时清除本地 token 并跳转登录页。
- 退出登录时会调用 `service-user` 的 `/api/user/auth/logout`。
