import axios from 'axios'

// 统一通过 GateWay (经由 vite proxy -> 8000) 访问微服务
const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('ms_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('ms_token')
      const currentPath = `${window.location.pathname}${window.location.search}`
      if (!currentPath.startsWith('/login')) {
        window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
      }
    }
    console.error('请求失败:', err)
    return Promise.reject(err)
  }
)

export default http
