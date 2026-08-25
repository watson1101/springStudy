import axios from 'axios'

// 统一通过 GateWay (经由 vite proxy -> 8000) 访问微服务
const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.response.use(
  (res) => res.data,
  (err) => {
    console.error('请求失败:', err)
    return Promise.reject(err)
  }
)

export default http
