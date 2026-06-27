import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      // 将 /api 开头的请求代理到 gateway-service (端口 8000)
      '/api': {
        target: 'http://localhost:8000',
        changeOrigin: true
      }
    }
  }
})
