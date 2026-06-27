<template>
  <div class="memo-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2><el-icon><Notebook /></el-icon> Simple Memo</h2>
      </div>
      <el-menu :default-active="activeFilter" @select="handleFilter" class="sidebar-menu">
        <el-menu-item index="all"><el-icon><List /></el-icon>全部备忘</el-menu-item>
        <el-menu-item index="type:1"><el-icon><Document /></el-icon>简单备忘</el-menu-item>
        <el-menu-item index="type:2"><el-icon><Timer /></el-icon>单次定时</el-menu-item>
        <el-menu-item index="type:3"><el-icon><RefreshRight /></el-icon>循环定时</el-menu-item>
        <el-menu-item index="status:1"><el-icon><CircleCheck /></el-icon>待办</el-menu-item>
        <el-menu-item index="status:2"><el-icon><Select /></el-icon>已完成</el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <el-button text @click="router.push('/settings')">
          <el-icon><Setting /></el-icon> 设置
        </el-button>
        <el-button text @click="handleLogout">
          <el-icon><SwitchButton /></el-icon> 退出
        </el-button>
      </div>
    </aside>

    <!-- 主内容 -->
    <main class="main-content">
      <header class="content-header">
        <h2>{{ headerTitle }}</h2>
        <el-button type="primary" @click="router.push('/memos/create')">
          <el-icon><Plus /></el-icon> 新建备忘
        </el-button>
      </header>

      <div class="memo-grid">
        <div v-for="memo in memos" :key="memo.id" class="memo-card"
             :style="memo.backgroundImage ? { backgroundImage: `url(${memo.backgroundImage})`, backgroundSize: 'cover' } : {}"
             @click="openEditor(memo)">
          <div class="card-header">
            <el-tag :type="typeTagMap[memo.memoType]" size="small">{{ memo.memoTypeName }}</el-tag>
            <el-tag :type="statusTagMap[memo.status]" size="small" effect="plain">{{ memo.statusName }}</el-tag>
          </div>
          <h3 class="card-title">{{ memo.title }}</h3>
          <p class="card-content">{{ memo.content }}</p>
          <div v-if="memo.remindTime" class="card-time">
            <el-icon><Clock /></el-icon> {{ formatTime(memo.remindTime) }}
          </div>
          <div class="card-actions" @click.stop>
            <el-button circle size="small" :type="memo.status===2?'success':'default'"
                       @click="toggleComplete(memo)">
              <el-icon><Select /></el-icon>
            </el-button>
            <el-button circle size="small" type="danger" @click="handleDelete(memo.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
        <div v-if="memos.length === 0" class="empty-state">
          <el-icon :size="48"><Notebook /></el-icon>
          <p>暂无备忘，点击右上角新建</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { listMemos, completeMemo, deleteMemo, listMemosByType, listMemosByStatus } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const appStore = useAppStore()
const memos = ref<any[]>([])
const activeFilter = ref('all')

const typeTagMap: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
const statusTagMap: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'info' }

const headerTitle = computed(() => {
  const map: Record<string, string> = {
    'all': '全部备忘', 'type:1': '简单备忘', 'type:2': '单次定时',
    'type:3': '循环定时', 'status:1': '待办', 'status:2': '已完成'
  }
  return map[activeFilter.value] || '全部备忘'
})

onMounted(() => loadMemos())

async function loadMemos() {
  try {
    const res: any = await listMemos()
    memos.value = res.data || []
  } catch { memos.value = [] }
}

async function handleFilter(index: string) {
  activeFilter.value = index
  try {
    if (index.startsWith('type:')) {
      const res: any = await listMemosByType(parseInt(index.split(':')[1]))
      memos.value = res.data || []
    } else if (index.startsWith('status:')) {
      const res: any = await listMemosByStatus(parseInt(index.split(':')[1]))
      memos.value = res.data || []
    } else {
      const res: any = await listMemos()
      memos.value = res.data || []
    }
  } catch { memos.value = [] }
}

function openEditor(memo: any) {
  router.push(`/memos/${memo.id}/edit`)
}

async function toggleComplete(memo: any) {
  try {
    if (memo.status === 2) return
    await completeMemo(memo.id)
    ElMessage.success('已标记完成')
    await loadMemos()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该备忘？', '提示')
    await deleteMemo(id)
    ElMessage.success('已删除')
    await loadMemos()
  } catch {}
}

function handleLogout() {
  appStore.logout()
  router.push('/login')
}

function formatTime(t: string) { return t?.replace('T', ' ').substring(0, 16) }
</script>

<style scoped>
.memo-layout { display: flex; height: 100vh; }
.sidebar {
  width: 200px; background: var(--bg-secondary); border-right: 1px solid var(--border-color);
  display: flex; flex-direction: column;
}
.sidebar-header { padding: 20px 16px; border-bottom: 1px solid var(--border-color); }
.sidebar-header h2 { font-size: 18px; display: flex; align-items: center; gap: 8px; }
.sidebar-menu { flex: 1; border: none; background: transparent; }
.sidebar-footer { padding: 12px; border-top: 1px solid var(--border-color); display: flex; gap: 8px; }
.main-content { flex: 1; padding: 24px; overflow-y: auto; }
.content-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.content-header h2 { font-size: 22px; font-weight: 600; }
.memo-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.memo-card {
  background: var(--bg-card); border-radius: var(--radius-md); padding: 20px;
  box-shadow: var(--shadow-sm); cursor: pointer; transition: all 0.2s;
  position: relative; min-height: 140px;
}
.memo-card:hover { box-shadow: var(--shadow-md); transform: translateY(-2px); }
.card-header { display: flex; gap: 6px; margin-bottom: 12px; }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.card-content { font-size: 13px; color: var(--text-secondary); line-height: 1.5;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.card-time { font-size: 12px; color: var(--text-tertiary); margin-top: 8px; display: flex; align-items: center; gap: 4px; }
.card-actions { position: absolute; bottom: 12px; right: 12px; display: flex; gap: 6px; opacity: 0; transition: opacity 0.2s; }
.memo-card:hover .card-actions { opacity: 1; }
.empty-state { grid-column: 1 / -1; text-align: center; padding: 60px; color: var(--text-tertiary); }
.empty-state p { margin-top: 12px; }
</style>
