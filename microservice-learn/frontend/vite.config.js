import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// 前端开发服务器配置
//
// 代理目标由环境变量 VITE_GATEWAY_TARGET 控制，便于在两种调试场景间切换：
//   1) 联调测试服务器（k3s）后端：VITE_GATEWAY_TARGET=http://192.168.0.27:30080
//   2) 联调本机启动的后端：        VITE_GATEWAY_TARGET=http://127.0.0.1:8000
//
// Vite dev server 的 proxy 属于「服务端转发」：浏览器只访问 localhost:3000，
// 由 Vite 进程把请求转发到目标网关，响应再原路返回浏览器。
// 因此只要 Mac 与目标主机网络互通，即可跨机联调（含跨域规避）。
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const gatewayTarget = env.VITE_GATEWAY_TARGET || 'http://192.168.0.27:30080'

  return {
    plugins: [vue()],
    server: {
      port: 3000,
      proxy: {
        // 业务接口统一走网关
        '/api': {
          target: gatewayTarget,
          changeOrigin: true
        },
        // SSO 入口路径（service-user 中 /sso/*），同样走网关
        '/sso': {
          target: gatewayTarget,
          changeOrigin: true
        }
      }
    }
  }
})
