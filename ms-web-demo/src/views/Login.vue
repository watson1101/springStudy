<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h2>欢迎登录</h2>
        <p class="auth-subtitle">ms-demo 微服务平台</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            required
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            required
            autocomplete="current-password"
          />
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      </form>

      <div class="auth-footer">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: ''
})
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    const res = await authStore.login({ ...form })
    if (res.code === 200) {
      // 登录成功，跳转到首页（或 dashboard）
      router.push('/')
    } else {
      errorMsg.value = res.message || '登录失败，请检查用户名和密码'
    }
  } catch (e) {
    errorMsg.value = e.message || '网络异常，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>
