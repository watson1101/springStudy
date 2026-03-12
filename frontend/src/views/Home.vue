<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-icon">
          <el-icon :size="80" color="#1890ff"><OfficeBuilding /></el-icon>
        </div>
        <h1>欢迎使用用户管理系统</h1>
        <p class="description">
          专业的用户管理解决方案，为您提供高效、安全的用户服务
        </p>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToLogin" v-if="!isLoggedIn">
            <el-icon><User /></el-icon>
            登录系统
          </el-button>
          <el-button type="success" size="large" @click="goToRegister" v-if="!isLoggedIn">
            <el-icon><EditPen /></el-icon>
            注册账号
          </el-button>
          <el-button type="primary" size="large" @click="goToUserManage" v-if="isLoggedIn">
            <el-icon><UserFilled /></el-icon>
            用户管理
          </el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="24" class="feature-section">
      <el-col :span="8">
        <el-card class="feature-card">
          <div class="feature-icon" style="background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%)">
            <el-icon :size="40" color="white"><Lock /></el-icon>
          </div>
          <h3>安全可靠</h3>
          <p>采用 Sa-Token 身份认证，保障系统安全</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="feature-card">
          <div class="feature-icon" style="background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%)">
            <el-icon :size="40" color="white"><Setting /></el-icon>
          </div>
          <h3>功能完善</h3>
          <p>支持用户注册、登录、管理等完整功能</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="feature-card">
          <div class="feature-icon" style="background: linear-gradient(135deg, #fa8c16 0%, #d46b08 100%)">
            <el-icon :size="40" color="white"><TrendCharts /></el-icon>
          </div>
          <h3>高效便捷</h3>
          <p>基于 Vue 3 + Element Plus，操作流畅</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { OfficeBuilding, User, EditPen, UserFilled, Lock, Setting, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const isLoggedIn = ref(false)

onMounted(() => {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
})

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const goToUserManage = () => {
  router.push('/user')
}
</script>

<style scoped>
.home-container {
  padding: 40px 0;
}

.welcome-card {
  text-align: center;
  padding: 60px 40px;
  margin-bottom: 40px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.welcome-content {
  max-width: 800px;
  margin: 0 auto;
}

.welcome-icon {
  margin-bottom: 32px;
}

.welcome-content h1 {
  font-size: 36px;
  color: #1f2937;
  margin-bottom: 16px;
  font-weight: 600;
}

.description {
  font-size: 18px;
  color: #6b7280;
  margin-bottom: 40px;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.feature-section {
  margin-top: 40px;
}

.feature-card {
  text-align: center;
  padding: 40px 24px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.feature-card h3 {
  font-size: 20px;
  color: #1f2937;
  margin-bottom: 12px;
  font-weight: 600;
}

.feature-card p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}
</style>
