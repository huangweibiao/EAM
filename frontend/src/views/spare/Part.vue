<template>
  <div class="spare-part">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>备件台账</span>
          <el-button type="primary" @click="handleAdd">新增备件</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="备件编码/名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="partCode" label="备件编码" width="120" />
        <el-table-column prop="partName" label="备件名称" width="180" />
        <el-table-column prop="model" label="规格型号" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="quantity" label="库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-warning': row.quantity < row.minQuantity }">{{ row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minQuantity" label="最低库存" width="100" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">
            {{ row.unitPrice ? `¥${row.unitPrice}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="备件编码" prop="partCode">
              <el-input v-model="formData.partCode" :disabled="!!formData.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备件名称" prop="partName">
              <el-input v-model="formData.partName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="formData.model" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="formData.unit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="库存">
              <el-input-number v-model="formData.quantity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最低库存">
              <el-input-number v-model="formData.minQuantity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最高库存">
              <el-input-number v-model="formData.maxQuantity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价">
              <el-input-number v-model="formData.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置">
              <el-input v-model="formData.location" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" active-value="ACTIVE" inactive-value="INACTIVE" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { spareApi } from '@/api'

const searchForm = reactive({ keyword: '', status: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const formData = reactive({
  id: null as number | null,
  partCode: '', partName: '', model: '', unit: '',
  quantity: 0, minQuantity: 0, maxQuantity: 0,
  unitPrice: 0, location: '', status: 'ACTIVE', remark: ''
})

const formRules: FormRules = {
  partCode: [{ required: true, message: '请输入备件编码', trigger: 'blur' }],
  partName: [{ required: true, message: '请输入备件名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await spareApi.page({
      pageNum: pagination.pageNum, pageSize: pagination.pageSize,
      keyword: searchForm.keyword || undefined, status: searchForm.status || undefined
    })
    if (res.data) { tableData.value = res.data.records || []; pagination.total = res.data.total || 0 }
  } catch (error) { console.error('加载失败:', error) }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { searchForm.keyword = ''; searchForm.status = ''; pagination.pageNum = 1; loadData() }

const handleAdd = () => {
  dialogTitle.value = '新增备件'
  Object.assign(formData, { id: null, partCode: '', partName: '', model: '', unit: '', quantity: 0, minQuantity: 0, maxQuantity: 0, unitPrice: 0, location: '', status: 'ACTIVE', remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑备件'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try {
    if (formData.id) { await spareApi.update(formData); ElMessage.success('修改成功') }
    else { await spareApi.add(formData); ElMessage.success('新增成功') }
    dialogVisible.value = false; loadData()
  } catch (error: any) { ElMessage.error(error.message || '操作失败') }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该备件吗？', '提示', { type: 'warning' }).then(async () => {
    try { await spareApi.delete(row.id); ElMessage.success('删除成功'); loadData() }
    catch (error: any) { ElMessage.error(error.message || '删除失败') }
  })
}

onMounted(() => { loadData() })
</script>

<style scoped>
.spare-part { padding: 20px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.search-form { margin-bottom: 20px }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end }
.text-warning { color: #e6a23c; font-weight: bold }
</style>