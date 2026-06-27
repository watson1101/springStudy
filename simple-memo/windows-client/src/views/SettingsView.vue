<template>
  <div class="settings-page">
    <header class="settings-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h2>设置</h2>
      <div></div>
    </header>

    <div class="settings-sections">
      <!-- 外观 -->
      <section class="settings-section">
        <h3 class="section-title">外观</h3>
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">深色模式</span>
            <span class="setting-desc">切换深色/浅色主题</span>
          </div>
          <el-switch :model-value="appStore.darkMode" @change="appStore.toggleDarkMode()" />
        </div>
      </section>

      <!-- 服务器 -->
      <section class="settings-section">
        <h3 class="section-title">服务器</h3>
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">服务端地址</span>
            <span class="setting-desc">{{ appStore.serverUrl }}</span>
          </div>
          <el-button text @click="showServerDialog = true">修改</el-button>
        </div>
      </section>

      <!-- 账户 -->
      <section class="settings-section">
        <h3 class="section-title">账户</h3>
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">当前用户</span>
            <span class="setting-desc">{{ appStore.username }}</span>
          </div>
          <el-button text @click="showPasswordDialog = true">修改密码</el-button>
        </div>
      </section>

      <!-- 关于 -->
      <section class="settings-section">
        <h3 class="section-title">关于</h3>
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">版本</span>
            <span class="setting-desc">1.0.0</span>
          </div>
        </div>
      </section>
    </div>

    <!-- 修改服务器地址 -->
    <el-dialog v-model="showServerDialog" title="修改服务器地址" width="380px">
      <el-input v-model="serverUrlInput" placeholder="http://localhost:8080" />
      <template #footer>
        <el-button @click="showServerDialog = false">取消</el-button>
        <el-button type="primary" @click="saveServerUrl">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="380px">
      <el-form :model="passwordForm">
        <el-form-item label="旧密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { changePassword } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const appStore = useAppStore()
const showServerDialog = ref(false)
const showPasswordDialog = ref(false)
const serverUrlInput = ref(appStore.serverUrl)
const pwdLoading = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '' })

function saveServerUrl() {
  appStore.setServerUrl(serverUrlInput.value)
  showServerDialog.value = false
  ElMessage.success('服务器地址已更新')
}

async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写旧密码和新密码')
    return
  }
  pwdLoading.value = true
  try {
    await changePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.value = { oldPassword: '', newPassword: '' }
  } catch (e: any) {
    ElMessage.error(e?.message || '修改失败')
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.settings-page { max-width: 600px; margin: 0 auto; padding: 24px; }
.settings-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.settings-header h2 { font-size: 20px; font-weight: 600; }
.settings-sections { display: flex; flex-direction: column; gap: 16px; }
.settings-section {
  background: var(--bg-card); border-radius: var(--radius-md); padding: 20px;
  box-shadow: var(--shadow-sm);
}
.section-title { font-size: 14px; font-weight: 600; color: var(--text-secondary); margin-bottom: 16px; text-transform: uppercase; letter-spacing: 0.5px; }
.setting-item { display: flex; align-items: center; justify-content: space-between; padding: 8px 0; }
.setting-label { display: block; font-size: 15px; font-weight: 500; }
.setting-desc { font-size: 13px; color: var(--text-secondary); }
</style>
