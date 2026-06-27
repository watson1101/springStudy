# Simple Memo - 服务端

Spring Boot 3.2 后端服务，使用 DDD 领域驱动设计分层。

## 技术栈

| 组件 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.2.5 | 框架 |
| MyBatis-Plus | 3.5.7 | ORM |
| Sa-Token | 1.38 | 认证鉴权 |
| MySQL | 8.0 | 数据库 |
| Redis | - | Token 缓存 |
| Hutool | 5.8.28 | 工具类 |

## DDD 分层架构

```
user/memo
 ├── domain/           # 领域层 - 实体、值对象、仓储接口
 │   ├── entity/       # 领域实体
 │   ├── repository/   # 仓储接口
 │   └── types/        # 值对象/枚举
 ├── application/      # 应用层 - 用例编排（薄层）
 ├── infrastructure/   # 基础设施层 - 持久化、转换
 │   ├── persistence/  # PO + Mapper
 │   ├── converter/    # 领域 ↔ PO 转换器
 │   └── impl/         # 仓储实现
 └── interfaces/       # 接口层 - REST 控制器
     └── dto/          # 请求/响应 DTO
```

## 数据库

数据库名：`simple-memo`，SQL 脚本位于 `src/main/resources/sql/`。

| 表名 | 前缀 | 说明 |
|------|------|------|
| user_service_user_info | user_ | 用户表 |
| memo_service_memo | memo_ | 备忘表 |

初始用户（由 DatabaseInitializer 自动插入）：
- admin / admin（管理员）
- hong / 123456（普通用户）

## API 接口

### 用户模块 `/api/user/`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| POST | /api/user/login | 登录 | 否 |
| POST | /api/user/register | 注册 | 否 |
| POST | /api/user/logout | 登出 | 是 |
| GET | /api/user/me | 当前用户 | 是 |
| PUT | /api/user/password | 修改密码 | 是 |
| PUT | /api/user/reset-password | 找回密码 | 否 |

### 备忘模块 `/api/memo/`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/memo | 创建备忘 |
| PUT | /api/memo | 更新备忘 |
| DELETE | /api/memo/{id} | 删除 |
| PUT | /api/memo/{id}/complete | 标记完成 |
| PUT | /api/memo/{id}/cancel | 取消 |
| GET | /api/memo/list | 全部列表 |
| GET | /api/memo/list/type/{type} | 按类型 |
| GET | /api/memo/list/status/{status} | 按状态 |
| GET | /api/memo/{id} | 详情 |
| GET | /api/memo/count | 统计 |

## 构建运行

```bash
# 1. 初始化数据库
mysql -u root -p < src/main/resources/sql/init-database.sql
mysql -u root -p < src/main/resources/sql/init-tables.sql

# 2. 启动服务
mvn spring-boot:run

# 3. 打包
mvn clean package -DskipTests
# 生成 target/simple-memo-server.jar
```

---

> 初次修改：2026-06-22 - 服务端初始搭建
