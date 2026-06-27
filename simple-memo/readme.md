# Simple Memo

多端提醒备忘项目，支持 **Android 11+** 和 **Windows 11** 客户端，后端使用 **Spring Boot 3 + MySQL + Sa-Token**。

## 项目结构

```
simple-memo/
├── server-backend/       # Spring Boot 后端服务
│   ├── src/main/java/    # Java 源码（DDD 分层）
│   ├── src/main/resources/  # 配置与 SQL 脚本
│   └── pom.xml
├── android-client/       # Android 客户端（Jetpack Compose）
└── windows-client/       # Windows 客户端（Electron + Vue 3）
```

## 技术栈

- **后端**: Java 17, Spring Boot 3.2, MyBatis-Plus, Sa-Token, Redis, MySQL
- **Android**: Kotlin, Jetpack Compose, Retrofit
- **Windows**: Electron 31, Vue 3, TypeScript, Element Plus, Pinia

## 快速开始

1. 初始化数据库：执行 `server-backend/src/main/resources/sql/init-database.sql`
2. 启动后端：`cd server-backend && mvn spring-boot:run`
3. 启动 Windows 客户端：`cd windows-client && npm run electron:dev`
4. Android 客户端：用 Android Studio 打开 `android-client` 目录

更多详情请查看各模块的 README.md。

---

> 初次修改：2026-06-22 - 项目初始搭建
