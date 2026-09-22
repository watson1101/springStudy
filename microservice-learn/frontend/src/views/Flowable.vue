<template>
  <div>
    <el-card>
      <template #header>
        🔄 工作流服务
        <small>（flowable-service → Flowable BPMN，请假/报销流程；⚠️ 当前模块无业务，仅配置路由）</small>
      </template>

      <el-alert
        title="该模块当前无实际业务，仅保留网关路由与页面框架，后端接口需在当前服务器部署后验证。"
        type="warning"
        :closable="false"
        style="margin-bottom:12px"
      />

      <el-form :inline="true">
        <el-form-item>
          <el-button type="primary" @click="loadDefinitions" :loading="loadingDef">流程定义</el-button>
          <el-button @click="loadInstances" :loading="loadingIns">流程实例</el-button>
        </el-form-item>
        <el-form-item label="办理人">
          <el-input v-model="assignee" placeholder="如 zhangsan" style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button @click="loadTasks" :loading="loadingTask">待办任务</el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">流程定义</el-divider>
      <el-table :data="definitions" border v-loading="loadingDef">
        <el-table-column prop="id" label="定义ID" width="120" />
        <el-table-column prop="key" label="Key" width="160" />
        <el-table-column prop="name" label="流程名" />
        <el-table-column prop="version" label="版本" width="80" />
      </el-table>

      <el-divider content-position="left">流程实例</el-divider>
      <el-table :data="instances" border v-loading="loadingIns">
        <el-table-column prop="id" label="实例ID" width="140" />
        <el-table-column prop="processDefinitionKey" label="流程Key" width="160" />
        <el-table-column prop="businessKey" label="业务Key" />
        <el-table-column prop="suspended" label="挂起" width="80" />
      </el-table>

      <el-divider content-position="left">待办任务</el-divider>
      <el-table :data="tasks" border v-loading="loadingTask">
        <el-table-column prop="id" label="任务ID" width="140" />
        <el-table-column prop="name" label="任务名" />
        <el-table-column prop="assignee" label="办理人" width="140" />
        <el-table-column prop="processInstanceId" label="流程实例" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const definitions = ref([])
const instances = ref([])
const tasks = ref([])
const assignee = ref('')
const loadingDef = ref(false)
const loadingIns = ref(false)
const loadingTask = ref(false)

async function loadDefinitions() {
  loadingDef.value = true
  try {
    const res = await http.get('/flowable/process-definitions')
    definitions.value = res.data || []
  } catch (e) {
    ElMessage.error('加载流程定义失败: ' + (e.response?.data?.message || e.message))
    definitions.value = []
  } finally {
    loadingDef.value = false
  }
}

async function loadInstances() {
  loadingIns.value = true
  try {
    const res = await http.get('/flowable/process-instances')
    instances.value = res.data || []
  } catch (e) {
    ElMessage.error('加载流程实例失败: ' + (e.response?.data?.message || e.message))
    instances.value = []
  } finally {
    loadingIns.value = false
  }
}

async function loadTasks() {
  if (!assignee.value) return ElMessage.warning('请输入办理人')
  loadingTask.value = true
  try {
    const res = await http.get(`/flowable/tasks/assignee/${assignee.value}`)
    tasks.value = res.data || []
  } catch (e) {
    ElMessage.error('加载待办任务失败: ' + (e.response?.data?.message || e.message))
    tasks.value = []
  } finally {
    loadingTask.value = false
  }
}
</script>
