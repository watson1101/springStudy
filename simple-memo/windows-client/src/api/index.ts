import axios from 'axios'
import { useAppStore } from '@/stores/app'
import router from '@/router'

const apiClient = axios.create({
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器：自动附加 Token
apiClient.interceptors.request.use(config => {
  const store = useAppStore()
  if (store.serverUrl) {
    config.baseURL = store.serverUrl
  }
  if (store.token) {
    config.headers['satoken'] = store.token
  }
  return config
})

// 响应拦截器：统一处理错误
apiClient.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      const store = useAppStore()
      store.logout()
      router.push('/login')
    }
    return Promise.reject(error.response?.data || { message: '网络错误' })
  }
)

export default apiClient

// ========== API 方法 ==========

/** 登录 */
export function login(username: string, password: string) {
  return apiClient.post('/api/user/login', { username, password })
}

/** 注册 */
export function register(username: string, password: string, nickname?: string, email?: string) {
  return apiClient.post('/api/user/register', { username, password, nickname, email })
}

/** 获取当前用户 */
export function getCurrentUser() {
  return apiClient.get('/api/user/me')
}

/** 修改密码 */
export function changePassword(oldPassword: string, newPassword: string) {
  return apiClient.put('/api/user/password', { oldPassword, newPassword })
}

/** 查询备忘列表 */
export function listMemos() {
  return apiClient.get('/api/memo/list')
}

/** 按类型查询 */
export function listMemosByType(memoType: number) {
  return apiClient.get(`/api/memo/list/type/${memoType}`)
}

/** 按状态查询 */
export function listMemosByStatus(status: number) {
  return apiClient.get(`/api/memo/list/status/${status}`)
}

/** 查询单个备忘 */
export function getMemo(id: number) {
  return apiClient.get(`/api/memo/${id}`)
}

/** 创建备忘 */
export function createMemo(data: any) {
  return apiClient.post('/api/memo', data)
}

/** 更新备忘 */
export function updateMemo(data: any) {
  return apiClient.put('/api/memo', data)
}

/** 标记完成 */
export function completeMemo(id: number) {
  return apiClient.put(`/api/memo/${id}/complete`)
}

/** 取消备忘 */
export function cancelMemo(id: number) {
  return apiClient.put(`/api/memo/${id}/cancel`)
}

/** 重新激活 */
export function reactivateMemo(id: number) {
  return apiClient.put(`/api/memo/${id}/reactivate`)
}

/** 删除备忘 */
export function deleteMemo(id: number) {
  return apiClient.delete(`/api/memo/${id}`)
}
