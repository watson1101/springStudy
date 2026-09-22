<template>
  <div>
    <el-card>
      <template #header>⚙️ 系统配置 <small>（ms-ds-system → 字典管理 / 配置组 / Binlog 同步开关）</small></template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="字典管理" name="dict">
          <el-form :inline="true">
            <el-form-item label="字典类型">
              <el-select v-model="dictType" placeholder="选择类型" style="width:200px" @change="loadDictItems">
                <el-option v-for="t in dictTypes" :key="t.id" :label="t.dictType" :value="t.dictType" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadDictTypes" :loading="loadingTypes">刷新类型</el-button>
              <el-button @click="loadDictItems" :loading="loadingItems">查字典项</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="dictItems" border v-loading="loadingItems">
            <el-table-column prop="itemId" label="ID" width="80" />
            <el-table-column prop="itemValue" label="值" />
            <el-table-column prop="itemLabel" label="标签" />
            <el-table-column prop="sort" label="排序" width="80" />
          </el-table>

          <el-divider content-position="left">字典类型</el-divider>
          <el-table :data="dictTypes" border v-loading="loadingTypes">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="dictType" label="类型编码" />
            <el-table-column prop="dictName" label="类型名称" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="配置组" name="group">
          <el-form :inline="true">
            <el-form-item>
              <el-button type="primary" @click="loadGroups" :loading="loadingGroups">刷新配置组</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="groups" border v-loading="loadingGroups">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="groupName" label="分组名" />
            <el-table-column prop="groupCode" label="分组编码" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="Binlog 同步" name="sync">
          <el-form :inline="true">
            <el-form-item>
              <el-button type="primary" @click="loadSync" :loading="loadingSync">查状态</el-button>
              <el-button type="success" @click="enableSync" :loading="syncing">开启同步</el-button>
              <el-button type="warning" @click="disableSync" :loading="syncing">关闭同步</el-button>
            </el-form-item>
          </el-form>
          <el-descriptions v-if="syncStatus" :column="2" border>
            <el-descriptions-item label="运行状态">{{ syncStatus.running }}</el-descriptions-item>
            <el-descriptions-item label="当前位点">{{ syncStatus.position }}</el-descriptions-item>
            <el-descriptions-item label="监听表">{{ syncStatus.tables }}</el-descriptions-item>
            <el-descriptions-item label="最后事件">{{ syncStatus.lastEvent }}</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="点击「查状态」获取同步信息" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const activeTab = ref('dict')

const dictTypes = ref([])
const dictItems = ref([])
const dictType = ref('')
const loadingTypes = ref(false)
const loadingItems = ref(false)

const groups = ref([])
const loadingGroups = ref(false)

const syncStatus = ref(null)
const loadingSync = ref(false)
const syncing = ref(false)

async function loadDictTypes() {
  loadingTypes.value = true
  try {
    const res = await http.get('/system/dict/types')
    dictTypes.value = res.data || []
  } catch (e) {
    ElMessage.error('加载字典类型失败: ' + (e.response?.data?.message || e.message))
    dictTypes.value = []
  } finally {
    loadingTypes.value = false
  }
}

async function loadDictItems() {
  if (!dictType.value) return
  loadingItems.value = true
  try {
    const res = await http.get(`/system/dict/type/${dictType.value}`)
    dictItems.value = res.data || []
  } catch (e) {
    ElMessage.error('加载字典项失败: ' + (e.response?.data?.message || e.message))
    dictItems.value = []
  } finally {
    loadingItems.value = false
  }
}

async function loadGroups() {
  loadingGroups.value = true
  try {
    const res = await http.get('/system/group/list')
    groups.value = res.data || []
  } catch (e) {
    ElMessage.error('加载配置组失败: ' + (e.response?.data?.message || e.message))
    groups.value = []
  } finally {
    loadingGroups.value = false
  }
}

async function loadSync() {
  loadingSync.value = true
  try {
    const res = await http.get('/system/sync/status')
    syncStatus.value = res.data
  } catch (e) {
    ElMessage.error('查询同步状态失败: ' + (e.response?.data?.message || e.message))
    syncStatus.value = null
  } finally {
    loadingSync.value = false
  }
}

async function enableSync() {
  syncing.value = true
  try {
    await http.post('/system/sync/enable')
    ElMessage.success('同步已开启')
    loadSync()
  } catch (e) {
    ElMessage.error('开启失败: ' + (e.response?.data?.message || e.message))
  } finally {
    syncing.value = false
  }
}

async function disableSync() {
  syncing.value = true
  try {
    await http.post('/system/sync/disable')
    ElMessage.success('同步已关闭')
    loadSync()
  } catch (e) {
    ElMessage.error('关闭失败: ' + (e.response?.data?.message || e.message))
  } finally {
    syncing.value = false
  }
}

loadDictTypes()
</script>
