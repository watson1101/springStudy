<template>
  <div>
    <el-alert type="success" :closable="false" title="Spring Cloud Alibaba 微服务学习项目" />
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="6" v-for="s in services" :key="s.name">
        <el-card>
          <template #header>{{ s.name }}</template>
          <div>{{ s.desc }}</div>
          <el-tag size="small" style="margin-top:8px">{{ s.port }}</el-tag>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:16px">
      <template #header>🏗️ 架构图</template>
      <pre style="line-height:1.8;font-size:14px">{{ arch }}</pre>
    </el-card>
  </div>
</template>

<script setup>
const arch = `
[Vue3 前端]  http://localhost:3000
      │  (vite proxy /api)
      ▼
[Gateway]  Spring Cloud Gateway :8000   ← 统一入口/路由/鉴权
      │  (lb:// 负载均衡)
      ▼
  ┌─────────┬───────────┬────────────┐
  ▼         ▼           ▼
[user ]   [order ]   [product ]
 :8001     :8002      :8003
 PG:ms_ds_user  MySQL:ms_ds_order  PG:ms_ds_product
 (Nacos注册)  (OpenFeign调用user)  (Sentinel熔断)
`
const services = [
  { name: 'service-user', port: '8001 / PG ms_ds_user', desc: '用户服务：PostgreSQL，Nacos 注册发现' },
  { name: 'service-order', port: '8002 / MySQL ms_ds_order', desc: '订单服务：MySQL，OpenFeign 调用用户服务' },
  { name: 'service-product', port: '8003 / PG ms_ds_product', desc: '商品服务：PostgreSQL，Sentinel 限流降级' },
  { name: 'ms-gateway', port: '8000（网关）', desc: 'Spring Cloud Gateway 统一路由入口' }
]
</script>
