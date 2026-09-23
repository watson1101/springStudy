<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-brand">
        <div class="login-mark">MS</div>
        <div>
          <h1>微服务学习平台</h1>
          <p>Spring Cloud Alibaba · 统一登录</p>
        </div>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @submit.prevent="submit"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            autocomplete="current-password"
            placeholder="请输入密码"
            show-password
            type="password"
            @keyup.enter="submit"
          />
        </el-form-item>
        <el-button
          class="login-submit"
          type="primary"
          native-type="submit"
          :loading="loading"
        >
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const result = await http.post('/user/auth/login', form)
    localStorage.setItem('ms_token', result.data.tokenValue)
    ElMessage.success('登录成功')
    const redirect = typeof route.query.redirect === 'string' && route.query.redirect.startsWith('/')
      ? route.query.redirect
      : '/'
    router.replace(redirect)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: #f5f7fa;
  padding: 24px;
}

.login-panel {
  width: min(420px, 100%);
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
}

.login-mark {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #001529;
  color: #fff;
  display: grid;
  place-items: center;
  font-weight: 700;
  letter-spacing: 0;
}

.login-brand h1 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}

.login-brand p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.login-submit {
  width: 100%;
  margin-top: 8px;
}
</style>
