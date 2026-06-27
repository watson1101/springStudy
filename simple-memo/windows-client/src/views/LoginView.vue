<template>
  <div class="login-page">
    <div class="login-card">
      <div class="logo-area">
        <div class="icon-circle"><el-icon :size="36"><Notebook /></el-icon></div>
        <h1 class="title">Simple Memo</h1>
        <p class="subtitle">多端提醒备忘</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin" class="login-form">
        <el-form-item prop="serverUrl">
          <el-input v-model="form.serverUrl" placeholder="服务器地址" size="large" clearable>
            <template #prefix><el-icon><Link /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
          登 录
        </el-button>
        <div class="register-link">
          <el-button text type="primary" @click="showRegister = true">注册账号</el-button>
        </div>
      </el-form>
    </div>

    <!-- 注册对话框 -->
    <el-dialog v-model="showRegister" title="注册账号" width="380px" top="20vh">
      <el-form :model="regForm" :rules="regRules" ref="regFormRef">
        <el-form-item prop="username"><el-input v-model="regForm.username" placeholder="用户名" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="regForm.password" type="password" placeholder="密码" show-password /></el-form-item>
        <el-form-item prop="nickname"><el-input v-model="regForm.nickname" placeholder="昵称（可选）" /></el-form-item>
        <el-form-item prop="email"><el-input v-model="regForm.email" placeholder="邮箱（可选）" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" :loading="regLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { login, register } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const appStore = useAppStore()
const formRef = ref()
const loading = ref(false)
const showRegister = ref(false)
const regFormRef = ref()
const regLoading = ref(false)

const form = reactive({
  serverUrl: appStore.serverUrl || 'http://localhost:8080',
  username: '',
  password: ''
})

const rules = {
  serverUrl: [{ required: true, message: '请输入服务器地址', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const regForm = reactive({ username: '', password: '', nickname: '', email: '' })
const regRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    appStore.setServerUrl(form.serverUrl)
    const res: any = await login(form.username, form.password)
    appStore.setLoginInfo(res.data.token, res.data.username)
    ElMessage.success('登录成功')
    router.push('/memos')
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  const valid = await regFormRef.value.validate().catch(() => false)
  if (!valid) return
  regLoading.value = true
  try {
    await register(regForm.username, regForm.password, regForm.nickname, regForm.email)
    ElMessage.success('注册成功，请登录')
    showRegister.value = false
  } catch (e: any) {
    ElMessage.error(e?.message || '注册失败')
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, var(--bg-primary) 0%, #e8e8ed 100%);
}
[data-theme="dark"] .login-page { background: linear-gradient(135deg, #1c1c1e 0%, #2c2c2e 100%); }
.login-card {
  width: 380px; padding: 40px; border-radius: 20px;
  background: var(--bg-secondary); box-shadow: var(--shadow-md);
  backdrop-filter: blur(20px);
}
.logo-area { text-align: center; margin-bottom: 32px; }
.icon-circle {
  width: 64px; height: 64px; border-radius: 18px;
  background: var(--accent-color); display: inline-flex;
  align-items: center; justify-content: center; color: white; margin-bottom: 16px;
}
.title { font-size: 24px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.subtitle { font-size: 14px; color: var(--text-secondary); }
.login-form { margin-top: 8px; }
.login-btn { width: 100%; height: 44px; margin-top: 8px; border-radius: 10px !important; font-size: 16px; }
.register-link { text-align: center; margin-top: 12px; }
</style>
