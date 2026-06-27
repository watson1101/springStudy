# ms-web-demo — 微服务前端项目

## 项目简介

ms-web-demo 是 [ms-demo](/abs/path/C:/workspace/github/springStudy/ms-demo) 微服务项目的配套前端，基于 **Vue 3 + Vite** 构建，提供用户登录、注册等基础交互界面，通过 API 网关与后端微服务通信。

> 后端项目：[ms-demo](/abs/path/C:/workspace/github/springStudy/ms-demo)

---

## 技术栈

| 类别       | 技术                    | 说明                       |
| ---------- | ----------------------- | -------------------------- |
| 框架       | Vue 3 (Composition API) | 前端 MVP 框架              |
| 构建工具   | Vite                    | 开发服务器 + 生产打包      |
| 路由       | Vue Router 4            | 客户端路由                 |
| 状态管理   | Pinia                   | 响应式状态管理              |
| HTTP 客户端| Axios                   | 请求封装 + 拦截器           |
| 后端代理   | Vite Proxy              | 开发阶段转发 /api -> 网关  |

---

## 项目结构

`
ms-web-demo/
├── index.html                # HTML 入口
├── package.json              # 依赖与脚本
├── vite.config.js            # Vite 配置（代理、端口）
├── README.md                 # 本文件
├── public/                   # 静态资源
└── src/
    ├── main.js               # 应用入口（挂载 Pinia + Router）
    ├── App.vue               # 根组件
    ├── style.css             # 全局样式
    ├── api/                  # API 层
    │   ├── index.js          #   Axios 实例 + 拦截器
    │   └── auth.js           #   登录 / 注册 API
    ├── router/
    │   └── index.js          # 路由配置
    ├── stores/
    │   └── auth.js           # Pinia 认证 Store
    └── views/
        ├── Login.vue         # 登录页
        └── Register.vue      # 注册页
`

---

## 开发环境启动

### 前置条件

- **Node.js** >= 18.x（推荐 20+）
- **npm** >= 9.x（随 Node.js 一起安装）
- 后端 **ms-demo** 微服务集群已启动（详见后端项目 README）

### 快速开始

`ash
# 1. 进入项目目录
cd ms-web-demo

# 2. 安装依赖
npm install

# 3. 启动开发服务器（默认端口 3000）
npm run dev
`

启动后打开终端输出的 URL（通常为 http://localhost:3000）即可访问。

### 开发代理配置

ite.config.js 中的 server.proxy 配置将 /api 开头的请求转发到后端网关：

| 前端请求路径           | 转发目标                      |
| ---------------------- | ----------------------------- |
| /api/user-service/** | http://localhost:8000/user-service/** |

> 确保 **gateway-service** (端口 8000) 和 **user-service** (端口 8001) 已在后端正常启动。

---

## 生产环境部署

### 构建

`ash
npm run build
`

构建产物位于 dist/ 目录，包含 index.html 及经过哈希处理的静态资源。

### 部署方式

#### 方式一：Nginx 部署（推荐）

`
ginx
# /etc/nginx/conf.d/ms-web-demo.conf
server {
    listen       80;
    server_name  your-domain.com;

    # 前端静态资源
    location / {
        root   /path/to/ms-web-demo/dist;
        index  index.html;
        try_files  / /index.html;   # SPA 路由支持
    }

    # API 反向代理到后端网关
    location /api/ {
        proxy_pass http://localhost:8000;    # gateway-service 地址
        proxy_set_header Host System.Management.Automation.Internal.Host.InternalHost;
        proxy_set_header X-Real-IP ;
        proxy_set_header X-Forwarded-For ;
        proxy_set_header X-Forwarded-Proto ;
    }
}

# 重新加载 Nginx 配置
sudo nginx -t && sudo nginx -s reload
`

#### 方式二：Docker 部署

`dockerfile
# Dockerfile（置于项目根目录）
FROM nginx:alpine
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
`

配套 
ginx.conf 内容同上。

#### 方式三：直接预览构建产物

`ash
npm run preview
`

Vite 会启动一个本地静态服务器预览构建产物（默认 http://localhost:4173）。

---

## API 接口说明

前端通过网关调用后端接口，以下为已对接的接口：

### POST /api/user-service/user/login

登录接口。

**请求体：**
`json
{
  "username": "string",
  "password": "string"
}
`

**响应体：**
`json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "jwt-token-string",
    "userInfo": { ... }
  }
}
`

### POST /api/user-service/user/register

注册接口。

**请求体：**
`json
{
  "username": "string",
  "password": "string",
  "nickname": "string",
  "phone": "string (选填)",
  "email": "string (选填)",
  "realName": "string (选填)",
  "gender": 0
}
`

**响应体：**
`json
{
  "code": 200,
  "message": "success",
  "data": null
}
`

### GET /api/user-service/user/info

获取当前登录用户信息（需在请求头携带 Authorization: Bearer <token>）。

---

## 常见问题

### 1. 开发时接口 502 / 连接失败

检查后端 gateway-service (8000) 和 user-service (8001) 是否已启动。

### 2. 跨域问题

开发阶段通过 Vite Proxy 解决，无需后端 CORS；生产环境通过 Nginx 反向代理同域访问。

### 3. 端口冲突

修改 ite.config.js 中的 server.port 即可换用其他端口。

---

## 许可证

MIT License
