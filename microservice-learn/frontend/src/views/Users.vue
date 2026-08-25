<template>
  <div>
    <el-card>
      <template #header>👤 用户列表 <small>（经网关调 service-user → PostgreSQL ms_ds_user）</small></template>
      <el-button type="primary" size="small" @click="load" :loading="loading">刷新</el-button>
      <el-table :data="users" border style="margin-top:12px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
      <div style="margin-top:16px">
        <el-input v-model="form.username" placeholder="用户名" style="width:180px;margin-right:8px" />
        <el-input v-model="form.nickname" placeholder="昵称" style="width:180px;margin-right:8px" />
        <el-button type="success" @click="create">新增用户</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const users = ref([])
const loading = ref(false)
const form = ref({})

async function load() {
  loading.value = true
  try {
    const res = await http.get('/user/list')
    users.value = res.data || []
  } catch (e) {
    ElMessage.error('加载失败（网关或 user 服务异常）')
  } finally {
    loading.value = false
  }
}

async function create() {
  if (!form.value.username) return ElMessage.warning('请输入用户名')
  await http.post('/user', form.value)
  ElMessage.success('已新增')
  form.value = {}
  load()
}

onMounted(load)
</script>
