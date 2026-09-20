# 异常日志捕获处理 —— 使用说明

> 归属：microservice-learn 微服务学习项目
> 创建日期：2026-09-21
> 方案：统一能力放 `common` 模块，各服务依赖后自动生效（方案 1 —— 日志落各自业务库）

---

## 一、功能概述

在 `common` 模块提供**全局异常捕获 + 异常日志记录**能力：

```
业务服务抛异常
      ↓
common/GlobalExceptionHandler      统一捕获（@RestControllerAdvice）
      ↓
common/ExceptionLogRecorder        统一记录（双写）
      ├── 写文件 → 宿主机目录（volume 挂载，禁止只留容器内）
      └── 写数据库 → 各服务自己的库（表名可配置）
      ↓
内存无侵入，异常不影响主业务返回
```

**告警**：预留扩展开关，暂不实现具体处理。

---

## 二、模块结构（common）

```
common/src/main/java/com/ms/learn/common/exception/
├── ExceptionLog.java                异常日志实体
├── ExceptionLogProperties.java      配置项（开关）
├── ExceptionLogRecorder.java        记录器（双写调度）
├── ExceptionLogDbWriter.java        落库写入器（表名可配置）
├── GlobalExceptionHandler.java      全局异常处理器
└── ExceptionLogAutoConfiguration.java 自动配置（零改动生效）

common/src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 三、配置开关

本地 `application.yml` 提供默认值，**Nacos 配置中心可远程覆盖**（`service-<名>.yaml`），
Nacos 优先且支持动态刷新。

```yaml
exception-log:
  enabled: true                # 总开关，默认开启
  file:
    enabled: true              # 写文件，默认开启
    path: /app/logs/exception  # 容器内路径（须挂载宿主机）
    file-name: exception.log
    max-history: 30
    max-file-size-mb: 100      # 单文件大小上限(MB)，超过即滚动生成新文件
  db:
    enabled: true              # 落库，默认开启（表名固定 exception_log）
  alert:
    enabled: false             # 告警开关（预留扩展，暂不处理）
```

---

## 四、建表

通用脚本：`sql/exception_log.sql`

**方案 1**：各服务在自己的业务库中建同名表 `exception_log`。

```bash
# 示例：在 ms_ds_hotnews 库建表
mysql -uroot -p*** ms_ds_hotnews < sql/exception_log.sql
```

关键字段：`id`（雪花）、`service_name`、`exception_type`、`message`、`stack_trace`、
`request_uri`、`request_method`、`request_params`、`user_id`、`ip`、`occur_time`、
`deleted`（软删除）、`create_time`、`update_time`。

---

## 五、启用方式

**无需改代码** —— 服务已依赖 `common`，Spring Boot 会自动加载 `ExceptionLogAutoConfiguration`。

`common/pom.xml` 新增依赖（`optional=true`，不强制下游）：

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

**注意**：若服务自己已有 `GlobalExceptionHandler`（如 `service-user`、`service-goods`），
本模块的兜底处理器优先级最低（`Ordered.LOWEST_PRECEDENCE`），由服务自身处理器优先响应。

---

## 六、日志文件落宿主机（k8s）

按规范**禁止只把日志留在容器内**，须用 volume 挂载：

```yaml
# Deployment 片段
spec:
  template:
    spec:
      containers:
        - name: service-hotnews-consumer
          volumeMounts:
            - name: exception-log
              mountPath: /app/logs/exception
      volumes:
        - name: exception-log
          hostPath:
            path: /home/hong/logs/hotnews-consumer/exception
            type: DirectoryOrCreate
```

---

## 七、验证结果

<!-- 待部署验证后补充 -->

---

## 八、待办 / 注意

1. 告警扩展点（`exception-log.alert.enabled`）仅定义开关，未实现处理逻辑。
2. 其他 10 个服务若要启用，只需在自己的库建 `exception_log` 表 + 加配置开关即可。
3. `BizException`（可预期业务异常）不落异常日志，避免噪声；仅落未预期异常。
