<template>
  <div class="goods-list-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增商品
          </el-button>
        </div>
      </template>
      <div class="page-content">
        <p>商品管理页面</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.goods-list-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #1f2937;
}

.page-content {
  padding: 40px 0;
  text-align: center;
  color: #6b7280;
}
</style>
