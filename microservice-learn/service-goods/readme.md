# service-goods 商品管理服务

## 模块概述

商品管理模块，负责管理商品信息，包括：
- **录入商品**：新增商品（名称、价格、主图、多图、规格、描述、三级分类）
- **上架/下架商品**：切换商品上下架状态
- **修改商品信息**：价格、图片、描述、规格、三级分类

## 技术栈

| 组件 | 说明 |
|------|------|
| Spring Boot 4 | Web 框架 |
| MyBatis-Plus | ORM（含分页插件） |
| MySQL | 数据库 `ms_ds_goods` |
| Nacos | 注册中心 + 配置中心 |
| Sentinel | 熔断/限流 |
| Sa-Token | 鉴权（SSO 客户端，认证中心为 service-user） |
| OpenFeign | 调用 ms-ds-system 数据字典 |

## 服务信息

- **端口**：8006
- **服务名**：`service-goods`
- **数据库**：`ms_ds_goods`
- **表**：`ms_ds_goods`（表名以 `ms_ds_` 开头）

## 数据库

建表脚本：[sql/ms_ds_goods/init.sql](../../sql/ms_ds_goods/init.sql)

```sql
CREATE TABLE ms_ds_goods (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    name         VARCHAR(128)  NOT NULL COMMENT '商品名称',
    category_id  BIGINT        NOT NULL COMMENT '三级分类ID（字典 GOODS_CATEGORY level=3）',
    price        DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '价格（元）',
    main_image   VARCHAR(512)  DEFAULT NULL COMMENT '主图 URL',
    images       TEXT          DEFAULT NULL COMMENT '多图 JSON 数组',
    specs        TEXT          DEFAULT NULL COMMENT '规格 JSON',
    description  TEXT          DEFAULT NULL COMMENT '描述',
    status       TINYINT       NOT NULL DEFAULT 0 COMMENT '0=草稿 1=上架 2=下架',
    stock        INT           NOT NULL DEFAULT 0 COMMENT '库存',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

## 商品分类（数据字典）

商品三级分类由 **ms-ds-system** 的数据字典管理，dict_type = `GOODS_CATEGORY`，通过 `parent_id` + `level` 实现三级层级。

- 字典表：`ms_ds_sys_config.sys_dict` + `sys_dict_item`
- service-goods 通过 **Feign** 调用 `ms-ds-system` 获取分类：
  - `GET /api/system/dict/tree/GOODS_CATEGORY` 获取三级分类树
  - `GET /api/system/dict/item/{id}` 校验分类是否为三级

商品的 `category_id` 存储的是 `sys_dict_item.id`（必须为 level=3 的三级分类）。

## 接口列表

所有接口需登录（Sa-Token），通过网关访问路径 `/api/goods/**`。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/goods` | 录入商品 |
| PUT | `/api/goods` | 修改商品信息 |
| PUT | `/api/goods/{id}/on-shelf` | 上架商品 |
| PUT | `/api/goods/{id}/off-shelf` | 下架商品 |
| GET | `/api/goods/{id}` | 查询商品详情 |
| GET | `/api/goods/page` | 分页查询商品（status/categoryId/name 筛选） |
| GET | `/api/goods/categories` | 获取商品分类三级树 |

## 商品状态枚举

| 值 | 含义 |
|----|------|
| 0 | 草稿 |
| 1 | 上架 |
| 2 | 下架 |

## 依赖关系

```
service-goods
  ├── Feign → ms-ds-system (获取商品分类字典)
  └── Sa-Token SSO → service-user (鉴权)
```

## 本地启动

1. 确保 MySQL（`ms_ds_goods`）、Nacos、ms-ds-system、service-user 已启动
2. 执行建表脚本：`mysql -uroot -p < sql/ms_ds_goods/init.sql`
3. 执行 ms-ds-system 字典脚本初始化分类：`mysql -uroot -p < ms-ds-system/sql/ms_ds_sys_config.sql`
4. 启动服务：`mvn -pl service-goods -am spring-boot:run`
