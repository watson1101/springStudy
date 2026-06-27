import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import MemoListView from '@/views/MemoListView.vue'
import MemoEditorView from '@/views/MemoEditorView.vue'
import SettingsView from '@/views/SettingsView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/memos', name: 'memos', component: MemoListView, meta: { requiresAuth: true } },
  { path: '/memos/create', name: 'memo-create', component: MemoEditorView, meta: { requiresAuth: true } },
  { path: '/memos/:id/edit', name: 'memo-edit', component: MemoEditorView, meta: { requiresAuth: true } },
  { path: '/settings', name: 'settings', component: SettingsView, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：未登录则重定向到登录页
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
