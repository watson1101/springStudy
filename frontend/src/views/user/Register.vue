<template>
  <div class="register-container">
    <el-card class="register-card">
      <div class="register-header">
        <div class="logo-section">
          <el-icon :size="48" color="#1890ff"><EditPen /></el-icon>
          <h2>用户管理系统</h2>
        </div>
        <p class="subtitle">创建您的账号</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="login-link">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EditPen, User, Lock } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length > 32) {
    callback(new Error('用户名长度不能超过32位'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8位'))
  } else if (value.length > 32) {
    callback(new Error('密码长度不能超过32位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register({
          username: registerForm.username,
          password: registerForm.password
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册失败', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.register-card {
  width: 100%;
  max-width: 420px;
  padding: 48px 40px;
  border: none;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
  border-radius: 8px;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.logo-section h2 {
  font-size: 24px;
  color: #1f2937;
  margin: 0;
  font-weight: 600;
}

.subtitle {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.register-form {
  margin-top: 32px;
}

.register-button {
  width: 100%;
  font-weight: 500;
}

.register-footer {
  text-align: center;
  margin-top: 24px;
  color: #6b7280;
  font-size: 14px;
}

.login-link {
  color: #1890ff;
  text-decoration: none;
  margin-left: 4px;
  font-weight: 500;
}

.login-link:hover {
  text-decoration: underline;
}
</style>
