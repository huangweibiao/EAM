<template>
  <div class="asset-transfer">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产调拨</span>
          <el-button type="primary" @click="handleAdd">申请调拨</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已审批" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="transferNo" label="调拨单号" width="150" />
        <el-table-column prop="assetId" label="资产ID" width="100" />
        <el-table-column prop="fromDeptId" label="调出部门" width="120">
          <template #default="{ row }">
            {{ getDeptName(row.fromDeptId) }}
          </template>
        </el-table-column>
        <el-table-column prop="toDeptId" label="调入部门" width="120">
          <template #default="{ row }">
            {{ getDeptName(row.toDeptId) }}
          </template>
        </el-table-column>
        <el-table-column prop="transferReason" label="调拨原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transferTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" link @click="handleApprove(row, true)">审批通过</el-button>
            <el-button v-if="row.status === 'PENDING'" type="danger" link @click="handleApprove(row, false)">审批拒绝</el-button>
            <el-button v-if="row.status === 'APPROVED'" type="primary" link @click="handleComplete(row)">完成调拨</el-button>
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

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="申请调拨" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="资产ID" prop="assetId">
          <el-input-number v-model="formData.assetId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调出部门" prop="fromDeptId">
              <el-select v-model="formData.fromDeptId" placeholder="请选择">
                <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调入部门" prop="toDeptId">
              <el-select v-model="formData.toDeptId" placeholder="请选择">
                <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="调拨原因" prop="transferReason">
          <el-input v-model="formData.transferReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="formData.operator" />
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
import { transferApi, deptApi } from '@/api'

// 搜索表单
const searchForm = reactive({
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
const deptList = ref<any[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  assetId: null as number | null,
  fromDeptId: null as number | null,
  toDeptId: null as number | null,
  transferReason: '',
  operator: ''
})

// 表单验证
const formRules: FormRules = {
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }],
  fromDeptId: [{ required: true, message: '请选择调出部门', trigger: 'change' }],
  toDeptId: [{ required: true, message: '请选择调入部门', trigger: 'change' }],
  transferReason: [{ required: true, message: '请输入调拨原因', trigger: 'blur' }]
}

// 状态类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'COMPLETED': 'info'
  }
  return map[status] || 'info'
}

// 状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': '待审批',
    'APPROVED': '已审批',
    'REJECTED': '已拒绝',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

// 获取部门名称
const getDeptName = (deptId: number) => {
  const dept = deptList.value.find(d => d.id === deptId)
  return dept ? dept.deptName : '-'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await transferApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
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

// 加载部门
const loadDepts = async () => {
  try {
    const res = await deptApi.list()
    deptList.value = res.data || []
  } catch (error) {
    console.error('加载部门失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  formData.assetId = null
  formData.fromDeptId = null
  formData.toDeptId = null
  formData.transferReason = ''
  formData.operator = ''
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    await transferApi.create(formData)
    ElMessage.success('申请成功')
    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 审批
const handleApprove = (row: any, approved: boolean) => {
  const action = approved ? '通过' : '拒绝'
  ElMessageBox.confirm(`确认审批${action}该调拨单吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await transferApi.approve(row.id, 'admin', approved)
      ElMessage.success(`审批${action}成功`)
      loadData()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

// 完成调拨
const handleComplete = async (row: any) => {
  try {
    await transferApi.complete(row.id)
    ElMessage.success('调拨已完成')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadData()
  loadDepts()
})
</script>

<style scoped>
.asset-transfer {
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