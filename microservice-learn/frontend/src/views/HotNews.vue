<template>
  <div>
    <el-card>
      <template #header>🔥 热点资讯 <small>（service-hotnews 采集端 8008 / 消费端 8009 → RocketMQ → MySQL）</small></template>

      <el-form :inline="true">
        <el-form-item>
          <el-button type="primary" @click="loadLatest" :loading="loadingLatest">最新热榜</el-button>
          <el-button type="danger" @click="triggerCollect" :loading="collecting">触发采集</el-button>
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="batchId" placeholder="TT2026..." style="width:220px" />
        </el-form-item>
        <el-form-item>
          <el-button @click="loadBatch" :loading="loadingBatch">按批次查询</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="collectResult"
        :title="'采集结果: ' + collectResult"
        type="success"
        :closable="false"
        style="margin-bottom:12px"
      />

      <el-table :data="newsList" border v-loading="loadingLatest || loadingBatch">
        <el-table-column prop="rankNo" label="排名" width="80" />
        <el-table-column prop="title" label="标题">
          <template #default="{ row }">
            <a :href="row.url" target="_blank" rel="noopener">{{ row.title }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="hotValue" label="热度" width="120" />
        <el-table-column prop="batchId" label="批次" width="200" />
        <el-table-column prop="collectTime" label="采集时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const newsList = ref([])
const batchId = ref('')
const collectResult = ref('')
const loadingLatest = ref(false)
const loadingBatch = ref(false)
const collecting = ref(false)

async function loadLatest() {
  loadingLatest.value = true
  try {
    const res = await http.get('/hotnews/latest')
    newsList.value = res.data || []
  } catch (e) {
    ElMessage.error('加载失败: ' + (e.response?.data?.message || e.message))
    newsList.value = []
  } finally {
    loadingLatest.value = false
  }
}

async function loadBatch() {
  if (!batchId.value) return ElMessage.warning('请输入批次号')
  loadingBatch.value = true
  try {
    const res = await http.get(`/hotnews/batch/${batchId.value}`)
    newsList.value = res.data || []
  } catch (e) {
    ElMessage.error('查询失败: ' + (e.response?.data?.message || e.message))
    newsList.value = []
  } finally {
    loadingBatch.value = false
  }
}

async function triggerCollect() {
  collecting.value = true
  try {
    const res = await http.post('/hotnews/collect')
    collectResult.value = JSON.stringify(res.data ?? res)
    ElMessage.success('采集已触发')
    loadLatest()
  } catch (e) {
    ElMessage.error('触发失败: ' + (e.response?.data?.message || e.message))
  } finally {
    collecting.value = false
  }
}
</script>
