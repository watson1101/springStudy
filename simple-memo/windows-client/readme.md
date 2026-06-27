# Simple Memo - Windows 客户端

基于 **Electron 31 + Vue 3 + Element Plus** 构建的 Windows 桌面客户端。

## 技术栈

- **框架**: Electron 31, Vue 3, TypeScript
- **UI**: Element Plus, Apple 设计风格（深色/浅色）
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios

## 功能

- 登录 / 注册 / 退出
- 备忘管理（简单备忘、单次定时、循环定时）
- 备忘分类筛选（按类型、按状态）
- 本地图片作为备忘背景
- 深色/浅色主题切换
- 服务端地址配置
- 密码修改

## 项目结构

```
windows-client/
├── electron/           # Electron 主进程
│   ├── main.js         # 主进程入口
│   └── preload.js      # 预加载脚本
├── src/                # Vue 渲染进程
│   ├── App.vue         # 根组件
│   ├── main.ts         # Vue 入口
│   ├── api/            # API 接口层
│   ├── router/         # 路由配置
│   ├── stores/         # Pinia 状态管理
│   ├── styles/         # 全局样式
│   └── views/          # 页面视图
│       ├── LoginView.vue        # 登录页
│       ├── MemoListView.vue     # 备忘列表
│       ├── MemoEditorView.vue   # 备忘编辑
│       └── SettingsView.vue     # 设置页
├── package.json
├── vite.config.ts
└── index.html
```

## 开发运行

```bash
# 安装依赖
npm install

# 开发模式（热更新）
npm run dev

# Electron 开发模式
npm run electron:dev

# 生产构建（生成可执行文件）
npm run electron:build
```

## 打包说明

打包命令 `npm run electron:build` 会在 `release/` 目录生成 NSIS 安装包。

> 注意：首次打包需要下载 Electron 二进制文件，耗时较长。

---

> 初次修改：2026-06-22 - Windows 客户端初始搭建
