<template>
  <div>
    <el-card>
      <template #header>🛒 商品列表 <small>（service-product → PostgreSQL ms_ds_product，带 Sentinel 限流演示）</small></template>
      <el-button type="primary" size="small" @click="load" :loading="loading">刷新</el-button>
      <el-button type="warning" size="small" @click="loadWithDelay" :loading="loading">演示限流(连点)</el-button>
      <el-table :data="products" border style="margin-top:12px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="stock" label="库存" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const products = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await http.get('/product/list')
    products.value = res.data || []
  } catch (e) {
    ElMessage.error('被限流或服务异常: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

function loadWithDelay() {
  // 连点触发 Sentinel 限流，观察熔断降级返回
  for (let i = 0; i < 10; i++) {
    http.get('/product/list').catch(() => {})
  }
}

onMounted(load)
</script>
