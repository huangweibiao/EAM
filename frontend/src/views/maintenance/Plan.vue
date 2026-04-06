<template>
  <div class="maintenance-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>维护计划</span>
          <el-button type="primary" @click="handleAdd">新增计划</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="资产ID">
          <el-input-number v-model="searchForm.assetId" :min="1" placeholder="资产ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="启用" value="ACTIVE" />
            <el-option label="暂停" value="PAUSED" />
            <el-option label="完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="planCode" label="计划编码" width="150" />
        <el-table-column prop="planName" label="计划名称" width="180" />
        <el-table-column prop="assetId" label="资产ID" width="100" />
        <el-table-column prop="maintenanceType" label="维护类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getTypeText(row.maintenanceType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cycleType" label="周期类型" width="100">
          <template #default="{ row }">
            {{ row.cycleType }}/{{ row.cycleValue }}
          </template>
        </el-table-column>
        <el-table-column prop="nextExecuteTime" label="下次执行时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="responsiblePerson" label="负责人" width="100" />
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
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="formData.planName" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产ID" prop="assetId">
              <el-input-number v-model="formData.assetId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护类型" prop="maintenanceType">
              <el-select v-model="formData.maintenanceType" style="width: 100%">
                <el-option label="预防性维护" value="PREVENTIVE" />
                <el-option label="纠正性维护" value="CORRECTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="周期类型">
              <el-select v-model="formData.cycleType" style="width: 100%">
                <el-option label="天" value="DAY" />
                <el-option label="月" value="MONTH" />
                <el-option label="年" value="YEAR" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="周期值">
              <el-input-number v-model="formData.cycleValue" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="formData.status" style="width: 100%">
                <el-option label="启用" value="ACTIVE" />
                <el-option label="暂停" value="PAUSED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="负责人">
          <el-input v-model="formData.responsiblePerson" />
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
import { maintenanceApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  assetId: null as number | null,
  status: ''
})

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref<any[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const formData = reactive({
  id: null as number | null,
  planName: '',
  assetId: null as number | null,
  maintenanceType: 'PREVENTIVE',
  cycleType: 'MONTH',
  cycleValue: 1,
  status: 'ACTIVE',
  responsiblePerson: '',
  remark: ''
})

// 表单验证
const formRules: FormRules = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }],
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }]
}

// 类型文本
const getTypeText = (type: string) => {
  const map: Record<string, string> = { 'PREVENTIVE': '预防性', 'CORRECTIVE': '纠正性' }
  return map[type] || type
}

// 状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = { 'ACTIVE': '启用', 'PAUSED': '暂停', 'COMPLETED': '完成' }
  return map[status] || status
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await maintenanceApi.planPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      assetId: searchForm.assetId ?? undefined,
      status: searchForm.status || undefined
    })
    if (res.data) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.assetId = null
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增计划'
  formData.id = null
  formData.planName = ''
  formData.assetId = null
  formData.maintenanceType = 'PREVENTIVE'
  formData.cycleType = 'MONTH'
  formData.cycleValue = 1
  formData.status = 'ACTIVE'
  formData.responsiblePerson = ''
  formData.remark = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑计划'
  formData.id = row.id
  formData.planName = row.planName
  formData.assetId = row.assetId
  formData.maintenanceType = row.maintenanceType
  formData.cycleType = row.cycleType
  formData.cycleValue = row.cycleValue
  formData.status = row.status
  formData.responsiblePerson = row.responsiblePerson || ''
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    if (formData.id) {
      await maintenanceApi.planUpdate(formData)
      ElMessage.success('修改成功')
    } else {
      await maintenanceApi.planAdd(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该计划吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await maintenanceApi.planDelete(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.maintenance-plan {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>