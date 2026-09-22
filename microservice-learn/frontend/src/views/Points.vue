<template>
  <div>
    <el-card>
      <template #header>🎁 积分服务 <small>（service-points → 积分账户 / 流水 / 消费加分）</small></template>

      <el-form :inline="true">
        <el-form-item label="用户ID">
          <el-input v-model="userId" placeholder="如 1" style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAccount" :loading="loadingAccount">查账户</el-button>
          <el-button @click="loadRecords" :loading="loadingRecords">查流水</el-button>
          <el-button type="warning" @click="earnByConsume" :loading="earning">消费加分</el-button>
        </el-form-item>
      </el-form>

      <el-descriptions v-if="account" :column="3" border style="margin-bottom:16px">
        <el-descriptions-item label="用户ID">{{ account.userId }}</el-descriptions-item>
        <el-descriptions-item label="可用积分">{{ account.balance }}</el-descriptions-item>
        <el-descriptions-item label="累计获得">{{ account.totalEarned }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="records" border v-loading="loadingRecords">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="changeValue" label="变动" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.changeValue >= 0 ? '#67c23a' : '#f56c6c' }">
              {{ row.changeValue >= 0 ? '+' : '' }}{{ row.changeValue }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const userId = ref('1')
const account = ref(null)
const records = ref([])
const loadingAccount = ref(false)
const loadingRecords = ref(false)
const earning = ref(false)

async function loadAccount() {
  loadingAccount.value = true
  try {
    const res = await http.get(`/points/account/${userId.value}`)
    account.value = res.data
  } catch (e) {
    ElMessage.error('查询账户失败: ' + (e.response?.data?.message || e.message))
    account.value = null
  } finally {
    loadingAccount.value = false
  }
}

async function loadRecords() {
  loadingRecords.value = true
  try {
    const res = await http.get(`/points/records/${userId.value}`)
    records.value = res.data || []
  } catch (e) {
    ElMessage.error('查询流水失败: ' + (e.response?.data?.message || e.message))
    records.value = []
  } finally {
    loadingRecords.value = false
  }
}

async function earnByConsume() {
  earning.value = true
  try {
    await http.post('/points/earn/consume', { userId: Number(userId.value), amount: 100 })
    ElMessage.success('消费加分成功')
    loadAccount()
    loadRecords()
  } catch (e) {
    ElMessage.error('操作失败: ' + (e.response?.data?.message || e.message))
  } finally {
    earning.value = false
  }
}
</script>
