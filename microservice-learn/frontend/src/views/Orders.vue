<template>
  <div>
    <el-card>
      <template #header>📦 订单列表 <small>（service-order → MySQL ms_ds_order，下单时会经 OpenFeign 调用户服务校验）</small></template>
      <el-button type="primary" size="small" @click="load" :loading="loading">刷新</el-button>
      <el-table :data="orders" border style="margin-top:12px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="userId" label="用户ID" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
      <div style="margin-top:16px">
        <el-input-number v-model="userId" :min="1" placeholder="用户ID" style="margin-right:8px" />
        <el-input v-model="amount" placeholder="金额" style="width:120px;margin-right:8px" />
        <el-button type="success" @click="create">创建订单(校验用户)</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const orders = ref([])
const loading = ref(false)
const userId = ref(1)
const amount = ref(100)

async function load() {
  loading.value = true
  try {
    const res = await http.get('/order/list')
    orders.value = res.data || []
  } catch (e) {
    ElMessage.error('加载失败（网关或 order 服务异常）')
  } finally {
    loading.value = false
  }
}

async function create() {
  await http.post(`/order/${userId.value}`, { amount: Number(amount.value) })
  ElMessage.success('已创建订单')
  load()
}

onMounted(load)
</script>
