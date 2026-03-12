<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="header-content">
          <div class="logo">
            <el-icon><User /></el-icon>
            <span>用户管理系统</span>
          </div>
          <div class="header-actions">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/user" class="nav-item">用户管理</router-link>
          </div>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
      <el-footer class="app-footer">
        <p>© 2026 用户管理系统 - 商务风格</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.app-header {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  padding: 0;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
  font-size: 22px;
  font-weight: 600;
}

.logo .el-icon {
  font-size: 32px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.nav-item {
  color: white;
  text-decoration: none;
  padding: 10px 24px;
  border-radius: 4px;
  transition: all 0.3s;
  font-weight: 500;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.nav-item.router-link-active {
  background-color: rgba(255, 255, 255, 0.3);
}

.app-main {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
}

.app-footer {
  text-align: center;
  padding: 24px;
  background-color: #fff;
  color: #606266;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
}
</style>
