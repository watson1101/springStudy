<template>
  <div>
    <el-card>
      <template #header>💳 支付服务 <small>（service-transaction → 发起支付 / 支付单查询）</small></template>

      <el-form :inline="true">
        <el-form-item label="用户ID">
          <el-input v-model="userId" placeholder="如 1" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList" :loading="loading">查支付单</el-button>
        </el-form-item>
        <el-form-item label="支付单号">
          <el-input v-model="payNo" placeholder="payNo" style="width:200px" />
        </el-form-item>
        <el-form-item>
          <el-button @click="loadDetail" :loading="loadingDetail">查详情</el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">发起支付</el-divider>
      <el-form :inline="true">
        <el-form-item label="用户ID">
          <el-input-number v-model="payForm.userId" :min="1" />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="payForm.amount" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="doPay" :loading="paying">发起支付</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border v-loading="loading" style="margin-top:12px">
        <el-table-column prop="payNo" label="支付单号" width="220" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <el-descriptions v-if="detail" :column="2" border style="margin-top:16px" title="支付单详情">
        <el-descriptions-item label="支付单号">{{ detail.payNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ detail.amount }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const userId = ref('1')
const payNo = ref('')
const list = ref([])
const detail = ref(null)
const loading = ref(false)
const loadingDetail = ref(false)
const paying = ref(false)
const payForm = reactive({ userId: 1, amount: 99.9 })

async function loadList() {
  loading.value = true
  try {
    const res = await http.get(`/payment/list/${userId.value}`)
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('查询失败: ' + (e.response?.data?.message || e.message))
    list.value = []
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!payNo.value) return ElMessage.warning('请输入支付单号')
  loadingDetail.value = true
  try {
    const res = await http.get(`/payment/${payNo.value}`)
    detail.value = res.data
  } catch (e) {
    ElMessage.error('查询详情失败: ' + (e.response?.data?.message || e.message))
    detail.value = null
  } finally {
    loadingDetail.value = false
  }
}

async function doPay() {
  paying.value = true
  try {
    const res = await http.post('/payment/pay', { ...payForm })
    ElMessage.success('支付已发起')
    if (res.data?.payNo) {
      userId.value = String(payForm.userId)
      payNo.value = res.data.payNo
      loadList()
    }
  } catch (e) {
    ElMessage.error('支付失败: ' + (e.response?.data?.message || e.message))
  } finally {
    paying.value = false
  }
}
</script>
