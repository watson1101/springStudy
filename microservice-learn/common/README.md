# common 模块说明

> **模块定位**：公共依赖库（jar），不独立启动、不注册 Nacos
> **归属**：microservice-learn 微服务学习项目
> **最后更新**：2026-09-21

---

## 一、模块概览

`common` 是**普通工具 jar**（`packaging=jar`，`spring-boot-maven-plugin` 已 `skip`），
被以下模块 `com.ms.learn:common:${project.version}` 依赖：

`gateway`、`service-user`、`service-order`、`service-product`、`service-goods`、
`ms-ds-system`、`service-transaction`、`service-points`、`service-hotnews-collector`、
`service-hotnews-consumer`

**核心价值**：把横切关注点（统一响应、业务异常、异常日志）收敛到一处，避免各服务重复实现。

---

## 二、包结构

```
common/src/main/java/com/ms/learn/common/
├── result/
│   └── Result.java                        统一响应体（code / message / data）
└── exception/
    ├── BizException.java                  业务异常（可预期，自带 code）
    ├── ExceptionLog.java                  🆕 异常日志实体
    ├── ExceptionLogProperties.java        🆕 配置项（开关）
    ├── ExceptionLogRecorder.java          🆕 记录器（文件 + 数据库双写调度）
    ├── ExceptionLogDbWriter.java          🆕 落库写入器（JdbcTemplate）
    ├── GlobalExceptionHandler.java        🆕 全局异常处理器
    └── ExceptionLogAutoConfiguration.java 🆕 自动配置

common/src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports  🆕 自动配置注册
```

---

## 三、统一响应 `Result`

所有服务对外返回统一结构，调用方（前端 / 其他服务）只认这一种格式。

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 200 成功，其余失败 |
| `message` | String | 提示信息 |
| `data` | T | 业务数据 |

常用静态方法：`success()` / `success(data)` / `success(message, data)` /
`fail(message)` / `fail(code, message)`

---

## 四、业务异常 `BizException`

用于抛出**可预期的业务错误**，自带 `code`（默认 500）。

```java
throw new BizException("库存不足");
throw new BizException(4001, "订单状态不允许取消");
```

**注意**：`BizException` 属可预期异常，**不写入异常日志**，避免噪声。

---

## 五、全局异常捕获与异常日志（2026-09-21 新增）

### 5.1 工作流程

```
业务服务抛异常
      ↓
GlobalExceptionHandler    统一捕获（@RestControllerAdvice，优先级最低）
      ↓
ExceptionLogRecorder      统一记录（双写，两路互不影响）
      ├── 写文件   → 宿主机目录（volume 挂载，禁止只留容器内）
      └── 写数据库 → 各服务自己的业务库（表名固定 exception_log）
      ↓
告警扩展开关（预留，暂未实现）
```

### 5.2 捕获的异常类型

| 异常 | 处理 |
|------|------|
| `BizException` | 按自带 code 返回，**不落日志** |
| `IllegalArgumentException` | 400 + 落日志 |
| `Exception`（兜底） | 500「系统繁忙，请稍后重试」+ 落日志 |

异常信息**不直接回传前端**（堆栈仅落日志），避免泄漏内部细节。

### 5.3 配置开关

本地 `application.yml` 提供默认值，**Nacos 配置中心可远程覆盖**（`service-<名>.yaml`），Nacos 优先。

```yaml
exception-log:
  enabled: true                # 总开关，默认开启
  file:
    enabled: true              # 写文件，默认开启
    path: /app/logs/exception  # 容器内路径（须挂载宿主机）
    file-name: exception.log
    max-history: 30
  db:
    enabled: true              # 落库，默认开启（表名固定 exception_log）
  alert:
    enabled: false             # 告警开关（预留扩展，暂不处理）
```

**默认全部打开**（告警除外）。

### 5.4 启用方式

**无需改代码** —— 服务已依赖 `common`，Spring Boot 自动加载 `ExceptionLogAutoConfiguration`。

`common/pom.xml` 新增依赖（`optional=true`，不强制下游引入）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <optional>true</optional>
</dependency>
```

> **与既有处理器共存**：`service-user`、`service-goods` 已有自己的 `GlobalExceptionHandler`。
> common 的兜底处理器标记 `@Order(Ordered.LOWEST_PRECEDENCE)`，由服务自身处理器优先响应。

### 5.5 建表

通用脚本：`sql/exception_log.sql`（仓库根目录 `sql/`）

**方案 1**：各服务在**自己的业务库**中建同名表 `exception_log`。

```bash
mysql -uroot -p*** <库名> < sql/exception_log.sql
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `trace_id` | VARCHAR(64) | 链路追踪 ID |
| `service_name` | VARCHAR(64) | 服务名 |
| `exception_type` | VARCHAR(255) | 异常全类名 |
| `message` | VARCHAR(1000) | 异常消息 |
| `stack_trace` | TEXT | 异常堆栈 |
| `request_uri` / `request_method` / `request_params` | — | 请求上下文 |
| `user_id` / `ip` | VARCHAR | 调用方信息 |
| `occur_time` | DATETIME | 异常发生时间 |
| `deleted` | TINYINT | **软删除标记**（0 未删 / 1 已删） |
| `create_time` / `update_time` | DATETIME | 时间戳 |

索引：`occur_time`、`service_name`、`exception_type`、`trace_id`

### 5.6 日志文件落宿主机（k8s）

按项目规范，**禁止只把日志留在容器内**，须用 volume 挂载：

```yaml
# Deployment 片段
spec:
  template:
    spec:
      containers:
        - name: <服务名>
          volumeMounts:
            - name: exception-log
              mountPath: /app/logs/exception
      volumes:
        - name: exception-log
          hostPath:
            path: /home/hong/logs/<服务名>/exception
            type: DirectoryOrCreate
```

---

## 六、新增模块时的注意事项

1. `common` 是纯工具 jar，**不要**在 `common` 里启动 Web 服务或注册 Nacos。
2. 新增公共能力时，优先考虑**自动配置**（`AutoConfiguration.imports`）+ **开关**，
   保证「依赖即生效、可按服务关闭」。
3. 新增第三方依赖务必加 `optional=true`，避免污染所有下游服务的依赖树。
4. `common` 中不要写死业务表名/库名（异常日志表名 `exception_log` 属规范统一约定，例外）。

---

## 七、待办 / 注意

1. 告警扩展点（`exception-log.alert.enabled`）仅定义开关，**未实现处理逻辑**。
2. 其他服务若要启用异常日志，只需在**自己的库**建 `exception_log` 表 + 加配置开关。
3. 目前仅 `service-hotnews-consumer` 已接入开关配置（其他服务依赖 common 后即有默认行为）。
