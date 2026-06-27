<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h2>创建账号</h2>
        <p class="auth-subtitle">加入 ms-demo 微服务平台</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-row">
          <div class="form-group">
            <label for="username">用户名 <span class="required">*</span></label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              placeholder="登录账号"
              required
              autocomplete="username"
            />
          </div>
          <div class="form-group">
            <label for="nickname">昵称 <span class="required">*</span></label>
            <input
              id="nickname"
              v-model="form.nickname"
              type="text"
              placeholder="显示名称"
              required
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="password">密码 <span class="required">*</span></label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="至少6位"
              required
              autocomplete="new-password"
            />
          </div>
          <div class="form-group">
            <label for="confirmPwd">确认密码 <span class="required">*</span></label>
            <input
              id="confirmPwd"
              v-model="confirmPwd"
              type="password"
              placeholder="再次输入密码"
              required
              autocomplete="new-password"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="phone">手机号</label>
            <input
              id="phone"
              v-model="form.phone"
              type="tel"
              placeholder="选填"
            />
          </div>
          <div class="form-group">
            <label for="email">邮箱</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="选填"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="realName">真实姓名</label>
            <input
              id="realName"
              v-model="form.realName"
              type="text"
              placeholder="选填"
            />
          </div>
          <div class="form-group">
            <label>性别</label>
            <select v-model="form.gender">
              <option value="0">保密</option>
              <option value="1">男</option>
              <option value="2">女</option>
            </select>
          </div>
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '注册中...' : '注 册' }}
        </button>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      </form>

      <div class="auth-footer">
        已有账号？
        <router-link to="/login">立即登录</router-link>
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
  nickname: '',
  password: '',
  phone: '',
  email: '',
  realName: '',
  gender: '0'
})
const confirmPwd = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function handleRegister() {
  errorMsg.value = ''

  // 基本校验
  if (form.password.length < 6) {
    errorMsg.value = '密码长度不能少于6位'
    return
  }
  if (form.password !== confirmPwd.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    const payload = { ...form, gender: Number(form.gender) }
    const res = await authStore.register(payload)
    if (res.code === 200) {
      // 注册成功，跳转到登录页
      router.push('/login')
    } else {
      errorMsg.value = res.message || '注册失败'
    }
  } catch (e) {
    errorMsg.value = e.message || '网络异常，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>
