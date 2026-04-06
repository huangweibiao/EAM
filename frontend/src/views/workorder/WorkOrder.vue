<template>
  <div class="work-order">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>工单管理</span>
          <el-button type="primary" @click="handleAdd">创建工单</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="工单编号/标题" clearable />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="searchForm.orderType" placeholder="请选择" clearable>
            <el-option label="维修" value="REPAIR" />
            <el-option label="保养" value="MAINTENANCE" />
            <el-option label="巡检" value="INSPECTION" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="已指派" value="ASSIGNED" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="工单编号" width="150" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="orderType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getTypeText(row.orderType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reporter" label="报修人" width="100" />
        <el-table-column prop="assignedTo" label="指派给" width="100" />
        <el-table-column prop="reportTime" label="报修时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING'" type="success" link @click="handleAssign(row)">指派</el-button>
            <el-button v-if="row.status === 'ASSIGNED'" type="warning" link @click="handleProcess(row)">处理</el-button>
            <el-button v-if="row.status === 'PROCESSING'" type="success" link @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 'COMPLETED'" type="info" link @click="handleClose(row)">关闭</el-button>
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

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单类型" prop="orderType">
              <el-select v-model="formData.orderType" placeholder="请选择">
                <el-option label="维修" value="REPAIR" />
                <el-option label="保养" value="MAINTENANCE" />
                <el-option label="巡检" value="INSPECTION" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="formData.priority" placeholder="请选择">
                <el-option label="高" value="HIGH" />
                <el-option label="中" value="MEDIUM" />
                <el-option label="低" value="LOW" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入工单标题" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产ID" prop="assetId">
              <el-input-number v-model="formData.assetId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报修人" prop="reporter">
              <el-input v-model="formData.reporter" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 指派对话框 -->
    <el-dialog v-model="assignDialogVisible" title="指派工单" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="指派给">
          <el-input v-model="assignForm.assignedTo" placeholder="请输入处理人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成对话框 -->
    <el-dialog v-model="completeDialogVisible" title="完成工单" width="500px">
      <el-form :model="completeForm" label-width="80px">
        <el-form-item label="解决方案">
          <el-input v-model="completeForm.solution" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog v-model="rateDialogVisible" title="评价工单" width="400px">
      <el-form :model="rateForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="rateForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { workOrderApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  orderType: '',
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
  assetId: null as number | null,
  orderType: 'REPAIR',
  priority: 'MEDIUM',
  title: '',
  description: '',
  reporter: '',
  remark: ''
})

// 指派对话框
const assignDialogVisible = ref(false)
const currentOrderId = ref<number | null>(null)
const assignForm = reactive({ assignedTo: '' })

// 完成对话框
const completeDialogVisible = ref(false)
const completeForm = reactive({ solution: '' })

// 评价对话框
const rateDialogVisible = ref(false)
const rateForm = reactive({ rating: 5 })

// 表单验证
const formRules: FormRules = {
  orderType: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  reporter: [{ required: true, message: '请输入报修人', trigger: 'blur' }],
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }]
}

// 工单类型文本
const getTypeText = (type: string) => {
  const map: Record<string, string> = { 'REPAIR': '维修', 'MAINTENANCE': '保养', 'INSPECTION': '巡检' }
  return map[type] || type
}

// 优先级类型
const getPriorityType = (priority: string) => {
  const map: Record<string, string> = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
  return map[priority] || 'info'
}

// 状态类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': 'info',
    'ASSIGNED': 'primary',
    'PROCESSING': 'warning',
    'COMPLETED': 'success',
    'CLOSED': ''
  }
  return map[status] || 'info'
}

// 状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': '待处理',
    'ASSIGNED': '已指派',
    'PROCESSING': '处理中',
    'COMPLETED': '已完成',
    'CLOSED': '已关闭'
  }
  return map[status] || status
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await workOrderApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword || undefined,
      orderType: searchForm.orderType || undefined,
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
  searchForm.keyword = ''
  searchForm.orderType = ''
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '创建工单'
  formData.id = null
  formData.assetId = null
  formData.orderType = 'REPAIR'
  formData.priority = 'MEDIUM'
  formData.title = ''
  formData.description = ''
  formData.reporter = ''
  formData.remark = ''
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    await workOrderApi.create(formData)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 查看
const handleView = (row: any) => {
  ElMessage.info(`工单详情: ${row.title}`)
}

// 指派
const handleAssign = (row: any) => {
  currentOrderId.value = row.id
  assignForm.assignedTo = ''
  assignDialogVisible.value = true
}

// 提交指派
const handleAssignSubmit = async () => {
  if (!currentOrderId.value || !assignForm.assignedTo) {
    ElMessage.warning('请输入处理人')
    return
  }
  try {
    await workOrderApi.assign(currentOrderId.value, assignForm.assignedTo)
    ElMessage.success('指派成功')
    assignDialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 处理
const handleProcess = async (row: any) => {
  try {
    await workOrderApi.process(row.id)
    ElMessage.success('已开始处理')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 完成
const handleComplete = (row: any) => {
  currentOrderId.value = row.id
  completeForm.solution = ''
  completeDialogVisible.value = true
}

// 提交完成
const handleCompleteSubmit = async () => {
  if (!currentOrderId.value) return
  try {
    await workOrderApi.complete(currentOrderId.value, completeForm.solution)
    ElMessage.success('工单已完成')
    completeDialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 关闭
const handleClose = async (row: any) => {
  try {
    await workOrderApi.close(row.id)
    ElMessage.success('工单已关闭')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.work-order {
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