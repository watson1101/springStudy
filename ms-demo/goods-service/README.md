# goods-service 商品服务

## 模块简介

商品服务是微服务架构中的核心服务之一，负责商品的管理和维护，为订单服务等其他服务提供商品查询和库存操作接口。

## 功能特性

- 商品基础管理（增删改查）
- 商品分类管理
- 库存管理（冻结、扣减、释放）
- 批量查询支持
- 分页查询支持
- 多条件筛选

## 技术栈

- Spring Boot 3.3.11
- Spring Cloud 2023.0.1
- MyBatis Plus 3.5.12
- PostgreSQL
- Nacos（服务发现、配置中心）
- OpenFeign（服务间调用）

## 数据库表

### goods_product（商品信息表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(64) | 商品ID（主键） |
| product_name | VARCHAR(200) | 商品名称 |
| product_code | VARCHAR(100) | 商品编码（SKU，唯一） |
| category_id | VARCHAR(64) | 商品分类ID |
| category_name | VARCHAR(100) | 商品分类名称 |
| brand | VARCHAR(100) | 商品品牌 |
| price | DECIMAL(10,2) | 商品价格 |
| original_price | DECIMAL(10,2) | 商品原价 |
| cost_price | DECIMAL(10,2) | 成本价格 |
| stock_quantity | INT | 库存数量 |
| frozen_stock | INT | 冻结库存（下单未支付） |
| status | SMALLINT | 商品状态（0-下架 1-上架 2-售罄） |
| is_deleted | SMALLINT | 是否删除（0-否 1-是） |
| description | TEXT | 商品描述 |
| specification | VARCHAR(500) | 商品规格 |
| images | TEXT | 商品图片（JSON格式） |
| sales_count | INT | 销量 |
| comment_count | INT | 评论数 |
| rating | DECIMAL(3,2) | 商品评分（1.00-5.00） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |
| create_by | VARCHAR(64) | 创建人 |
| update_by | VARCHAR(64) | 更新人 |

## API接口清单

### 商品管理接口

#### 1. 创建商品

```http
POST /product/create
Content-Type: application/json

{
  "productName": "Apple iPhone 15 Pro Max 256GB",
  "productCode": "IP15PM256",
  "categoryId": "CAT001",
  "categoryName": "手机通讯",
  "brand": "Apple",
  "price": 9999.00,
  "originalPrice": 10999.00,
  "costPrice": 8500.00,
  "stockQuantity": 50,
  "status": 1,
  "description": "搭载A17 Pro芯片，钛金属边框",
  "specification": "颜色:原色钛金属;存储:256GB;屏幕:6.7英寸",
  "images": "[\"http://example.com/image1.jpg\"]"
}
```

**响应**
```json
{
  "code": 200,
  "message": "商品创建成功",
  "data": "GP001"
}
```

#### 2. 更新商品

```http
PUT /product/{productId}
Content-Type: application/json

{
  "productName": "Apple iPhone 15 Pro Max 512GB",
  "price": 10999.00,
  "stockQuantity": 30
}
```

**响应**
```json
{
  "code": 200,
  "message": "商品更新成功",
  "data": true
}
```

#### 3. 删除商品（逻辑删除）

```http
DELETE /product/{productId}
```

**响应**
```json
{
  "code": 200,
  "message": "商品删除成功",
  "data": true
}
```

#### 4. 批量删除商品

```http
DELETE /product/batch
Content-Type: application/json

{
  "productIds": ["GP001", "GP002", "GP003"]
}
```

**响应**
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": true
}
```

#### 5. 查询商品详情

```http
GET /product/{productId}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "GP001",
    "productName": "Apple iPhone 15 Pro Max 256GB",
    "productCode": "IP15PM256",
    "categoryId": "CAT001",
    "categoryName": "手机通讯",
    "brand": "Apple",
    "price": 9999.00,
    "originalPrice": 10999.00,
    "discount": 9.1,
    "costPrice": 8500.00,
    "stockQuantity": 50,
    "frozenStock": 0,
    "availableStock": 50,
    "status": 1,
    "statusDesc": "上架",
    "description": "搭载A17 Pro芯片",
    "specification": "颜色:原色钛金属;存储:256GB",
    "images": "[\"http://example.com/image1.jpg\"]",
    "salesCount": 1523,
    "commentCount": 89,
    "rating": 4.8,
    "createTime": "2024-01-01 00:00:00",
    "updateTime": "2024-01-01 00:00:00"
  }
}
```

#### 6. 根据商品编码查询

```http
GET /product/code/{productCode}
```

**响应**（同查询商品详情）

#### 7. 分页查询商品列表

```http
POST /product/page
Content-Type: application/json

{
  "productName": "iPhone",
  "categoryId": "CAT001",
  "brand": "Apple",
  "status": 1,
  "minPrice": 1000,
  "maxPrice": 10000,
  "current": 1,
  "size": 10
}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "GP001",
        "productName": "Apple iPhone 15 Pro Max 256GB",
        "productCode": "IP15PM256",
        ...
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### 8. 批量查询商品（供订单服务调用）

```http
POST /product/list
Content-Type: application/json

["GP001", "GP002", "GP003"]
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "GP001",
      "productName": "Apple iPhone 15 Pro Max 256GB",
      ...
    }
  ]
}
```

### 库存管理接口

#### 9. 冻结库存（下单时调用）

```http
POST /product/stock/freeze?productId=GP001&quantity=2
```

**响应**
```json
{
  "code": 200,
  "message": "库存冻结成功",
  "data": true
}
```

#### 10. 扣减库存（支付成功后调用）

```http
POST /product/stock/deduct?productId=GP001&quantity=2
```

**响应**
```json
{
  "code": 200,
  "message": "库存扣减成功",
  "data": true
}
```

#### 11. 释放冻结库存（订单取消/超时调用）

```http
POST /product/stock/release?productId=GP001&quantity=2
```

**响应**
```json
{
  "code": 200,
  "message": "冻结库存释放成功",
  "data": true
}
```

## Feign客户端接口

商品服务提供Feign客户端供其他服务调用：

```java
@FeignClient(name = "goods-service")
public interface ProductFeignClient {
    
    // 批量查询商品
    List<ProductVO> getProductsByIds(List<String> productIds);
    
    // 查询单个商品
    ProductVO getProductById(String productId);
    
    // 冻结库存
    Boolean freezeStock(String productId, Integer quantity);
    
    // 扣减库存
    Boolean deductStock(String productId, Integer quantity);
    
    // 释放冻结库存
    Boolean releaseFrozenStock(String productId, Integer quantity);
}
```

## 商品状态说明

| 状态值 | 说明 | 描述 |
|--------|------|------|
| 0 | 下架 | 商品已下架，不可购买 |
| 1 | 上架 | 商品已上架，可正常购买 |
| 2 | 售罄 | 商品库存不足，不可购买 |

## 库存机制说明

- **stock_quantity**: 当前可用库存
- **frozen_stock**: 冻结库存（已下单但未支付）
- **available_stock**: 实际可用库存 = stock_quantity - frozen_stock

### 库存操作流程

1. **下单时**: 调用冻结库存接口，将库存从stock_quantity转移到frozen_stock
2. **支付成功**: 调用扣减库存接口，直接扣减frozen_stock
3. **订单取消/超时**: 调用释放冻结库存接口，将库存从frozen_stock恢复到stock_quantity

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 20001 | 商品不存在 |
| 20002 | 商品库存不足 |
| 20003 | 商品价格变动 |
| 20004 | 商品已下架 |
| 20005 | 商品已删除 |
| 20006 | 商品创建失败 |
| 20007 | 商品更新失败 |
| 20008 | 商品删除失败 |
| 20009 | 商品编码已存在 |

## 配置说明

### 端口配置
- 服务端口: 8003
- 上下文路径: /goods-service

### 数据库配置
- 数据库: PostgreSQL
- 数据库名: ms-demo
- 表前缀: goods_

### Nacos配置
- 命名空间: ms-demo
- 分组: DEFAULT_GROUP

## 初始化数据

项目包含100条商品初始化数据，涵盖以下分类：

1. **电子产品类** (CAT001-CAT004): 手机通讯、电脑办公、平板电脑、数码配件
2. **服装鞋帽类** (CAT005-CAT006): 运动鞋服、休闲服装
3. **美妆护肤类** (CAT007): 护肤品、彩妆
4. **食品饮料类** (CAT008): 酒水、饮料、零食
5. **家居用品类** (CAT009): 家电、收纳、家具
6. **母婴用品类** (CAT010): 奶粉、纸尿裤、推车
7. **图书文具类** (CAT011): 书籍、文具、玩具
8. **运动户外类** (CAT012): 运动装备、户外用品

## 使用示例

### 1. 服务启动

```bash
# 启动商品服务
java -jar goods-service-1.0-SNAPSHOT.jar

# 或使用Maven启动
mvn spring-boot:run
```

### 2. 创建商品示例

```bash
curl -X POST http://localhost:8003/goods-service/product/create \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "测试商品",
    "productCode": "TEST001",
    "categoryId": "CAT001",
    "categoryName": "手机通讯",
    "brand": "测试品牌",
    "price": 99.00,
    "stockQuantity": 100,
    "status": 1
  }'
```

### 3. 查询商品示例

```bash
curl http://localhost:8003/goods-service/product/GP001
```

### 4. 分页查询示例

```bash
curl -X POST http://localhost:8003/goods-service/product/page \
  -H "Content-Type: application/json" \
  -d '{
    "current": 1,
    "size": 10,
    "status": 1
  }'
```

## 开发规范

### 包结构
```
msdemo.hong.com.goods
├── controller      // 控制器层
├── service         // 服务接口层
├── service.impl    // 服务实现层
├── mapper          // 数据访问层
├── model           // 模型层
│   ├── entity      // 实体类
│   ├── dto         // 数据传输对象
│   └── vo          // 视图对象
├── feign           // Feign客户端
└── GoodsServiceApplication.java  // 启动类
```

### 代码注释规范
- 所有类添加功能描述注释
- 所有方法添加参数和返回值说明
- 复杂逻辑添加详细注释
- 注释使用中文，便于新手学习

## 更新日志

### v1.0.0 (2024-01-06)
- 初始版本发布
- 实现商品基础CRUD功能
- 实现库存管理功能
- 提供100条商品初始化数据
- 提供Feign客户端接口

## Swagger 接口文档

本服务使用 SpringDoc OpenAPI 作为接口文档框架。

### 访问地址

```bash
# Swagger UI
http://localhost:8003/goods-service/swagger-ui/index.html

# OpenAPI 规范（JSON）
http://localhost:8003/goods-service/v3/api-docs
```

### 接口分类

| 标签 | 说明 |
|------|------|
| 商品管理 | 商品CRUD、库存管理等核心接口 |