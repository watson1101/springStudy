<template>
  <div>
    <el-card>
      <template #header>
        📦 商品管理 <small>（service-goods → MySQL，Sa-Token 鉴权 + OpenFeign 调字典）</small>
      </template>

      <el-form :inline="true" style="margin-bottom:8px">
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable style="width:160px" @change="load">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名" clearable style="width:180px" @keyup.enter="load" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load" :loading="loading">查询</el-button>
          <el-button @click="openCreate">新增商品</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="page.records" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名" />
        <el-table-column prop="categoryId" label="分类ID" width="90" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="stock" label="库存" width="90" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在售' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 1" size="small" type="success" @click="changeStatus(row, 'on-shelf')">上架</el-button>
            <el-button v-else size="small" type="warning" @click="changeStatus(row, 'off-shelf')">下架</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top:12px"
        layout="total, prev, pager, next"
        :total="page.total || 0"
        :page-size="query.size"
        :current-page="query.page"
        @current-change="onPageChange"
      />
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑商品' : '新增商品'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类ID"><el-input-number v-model="form.categoryId" :min="1" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const loading = ref(false)
const submitting = ref(false)
const categories = ref([])
const page = ref({ records: [], total: 0 })
const query = reactive({ page: 1, size: 10, categoryId: null, keyword: '' })

const dialog = reactive({ visible: false, isEdit: false })
const form = reactive({ id: null, name: '', categoryId: 1, price: 0, stock: 0 })

async function load() {
  loading.value = true
  try {
    const res = await http.get('/goods/page', { params: query })
    const data = res.data || {}
    page.value = { records: data.records || [], total: data.total || 0 }
  } catch (e) {
    ElMessage.error('加载失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const res = await http.get('/goods/categories')
    categories.value = res.data || []
  } catch (e) {
    /* 字典依赖 ms-ds-system，失败不阻塞主流程 */
  }
}

function onPageChange(p) {
  query.page = p
  load()
}

function openCreate() {
  dialog.isEdit = false
  Object.assign(form, { id: null, name: '', categoryId: 1, price: 0, stock: 0 })
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  Object.assign(form, row)
  dialog.visible = true
}

async function submit() {
  submitting.value = true
  try {
    if (dialog.isEdit) {
      await http.put('/goods', form)
    } else {
      await http.post('/goods', form)
    }
    ElMessage.success('保存成功')
    dialog.visible = false
    load()
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.response?.data?.message || e.message))
  } finally {
    submitting.value = false
  }
}

async function changeStatus(row, action) {
  try {
    await http.put(`/goods/${row.id}/${action}`)
    ElMessage.success(action === 'on-shelf' ? '已上架' : '已下架')
    load()
  } catch (e) {
    ElMessage.error('操作失败: ' + (e.response?.data?.message || e.message))
  }
}

onMounted(() => {
  load()
  loadCategories()
})
</script>
