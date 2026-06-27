<template>
  <div class="editor-page">
    <header class="editor-header">
      <el-button text @click="router.back()"><el-icon><ArrowLeft /></el-icon> 返回</el-button>
      <h2>{{ isEdit ? '编辑备忘' : '新建备忘' }}</h2>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </header>

    <div class="editor-form">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="title">
          <el-input v-model="form.title" placeholder="备忘标题" size="large" class="title-input" />
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="form.memoType" class="type-selector">
            <el-radio-button :value="1"><el-icon><Document /></el-icon> 简单备忘</el-radio-button>
            <el-radio-button :value="2"><el-icon><Timer /></el-icon> 单次定时</el-radio-button>
            <el-radio-button :value="3"><el-icon><RefreshRight /></el-icon> 循环定时</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.memoType === 2" prop="remindTime">
          <el-date-picker v-model="form.remindTime" type="datetime" placeholder="选择提醒时间"
            value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item v-if="form.memoType === 3" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="Cron 表达式，如 0 0 8 * * ? (每天早上8点)" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="备忘内容..." class="content-input" />
        </el-form-item>
        <el-form-item label="背景图片">
          <div class="image-upload">
            <el-button @click="selectImage"><el-icon><Picture /></el-icon> 选择图片</el-button>
            <el-button v-if="form.backgroundImage" type="danger" text @click="form.backgroundImage = ''">清除</el-button>
          </div>
          <div v-if="form.backgroundImage" class="image-preview">
            <img :src="form.backgroundImage" alt="背景预览" />
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createMemo, updateMemo, getMemo } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const saving = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '', content: '', memoType: 1,
  remindTime: '', cronExpression: '', backgroundImage: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res: any = await getMemo(Number(route.params.id))
      const data = res.data
      form.title = data.title
      form.content = data.content
      form.memoType = data.memoType
      form.remindTime = data.remindTime || ''
      form.cronExpression = data.cronExpression || ''
      form.backgroundImage = data.backgroundImage || ''
    } catch { ElMessage.error('加载失败') }
  }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateMemo({ id: Number(route.params.id), ...form })
      ElMessage.success('更新成功')
    } else {
      await createMemo(form)
      ElMessage.success('创建成功')
    }
    router.push('/memos')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function selectImage() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e: any) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (ev) => { form.backgroundImage = ev.target?.result as string }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}
</script>

<style scoped>
.editor-page { max-width: 700px; margin: 0 auto; padding: 24px; height: 100vh; overflow-y: auto; }
.editor-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.editor-header h2 { font-size: 20px; font-weight: 600; }
.editor-form { }
.title-input :deep(.el-input__inner) { font-size: 20px; font-weight: 600; border: none; padding-left: 0; }
.type-selector { width: 100%; }
.type-selector :deep(.el-radio-button__inner) { padding: 8px 16px; }
.content-input :deep(.el-textarea__inner) { font-size: 15px; line-height: 1.6; border: none; resize: vertical; }
.image-upload { display: flex; gap: 8px; }
.image-preview { margin-top: 8px; }
.image-preview img { max-width: 100%; max-height: 200px; border-radius: var(--radius-sm); object-fit: cover; }
</style>
