<template>
  <div class="work-order-detail">
    <el-page-header @back="goBack">
      <template #content>
        <div class="page-header-content">
          <span class="title">工单详情</span>
          <el-tag :type="getStatusType(currentOrder.status)" size="large">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </div>
      </template>
    </el-page-header>

    <el-divider />

    <el-row :gutter="20">
      <!-- 基本信息 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button-group>
                <el-button 
                  v-if="currentOrder.status === 'PENDING'" 
                  type="success" 
                  @click="handleAssign"
                >
                  指派
                </el-button>
                <el-button 
                  v-if="currentOrder.status === 'ASSIGNED'" 
                  type="warning" 
                  @click="handleProcess"
                >
                  处理
                </el-button>
                <el-button 
                  v-if="currentOrder.status === 'PROCESSING'" 
                  type="success" 
                  @click="handleComplete"
                >
                  完成
                </el-button>
                <el-button 
                  v-if="currentOrder.status === 'COMPLETED'" 
                  type="info" 
                  @click="handleClose"
                >
                  关闭
                </el-button>
              </el-button-group>
            </div>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="工单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="工单类型">{{ getTypeText(currentOrder.orderType) }}</el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="getPriorityType(currentOrder.priority)">{{ currentOrder.priority }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="报修人">{{ currentOrder.reporter }}</el-descriptions-item>
            <el-descriptions-item label="指派给">{{ currentOrder.assignedTo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="报修时间">{{ currentOrder.reportTime }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ currentOrder.updateTime }}</el-descriptions-item>
            <el-descriptions-item label="关联资产ID">{{ currentOrder.assetId || '--' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 问题描述 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>问题描述</span>
          </template>
          <div class="content-box">
            {{ currentOrder.description || '无详细描述' }}
          </div>
        </el-card>

        <!-- 解决方案 -->
        <el-card style="margin-top: 20px;" v-if="currentOrder.solution">
          <template #header>
            <span>解决方案</span>
          </template>
          <div class="content-box">
            {{ currentOrder.solution }}
          </div>
        </el-card>

        <!-- 备注信息 -->
        <el-card style="margin-top: 20px;" v-if="currentOrder.remark">
          <template #header>
            <span>备注信息</span>
          </template>
          <div class="content-box">
            {{ currentOrder.remark }}
          </div>
        </el-card>
      </el-col>

      <!-- 操作日志 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>操作日志</span>
          </template>
          <div class="timeline-container">
            <el-timeline v-if="operateLogs.length > 0">
              <el-timeline-item
                v-for="(log, index) in operateLogs"
                :key="index"
                :timestamp="log.createTime"
                placement="top"
              >
                <el-card shadow="hover">
                  <h4>{{ log.action }}</h4>
                  <p>{{ log.details }}</p>
                  <p class="operator">操作人: {{ log.operator }}</p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无操作记录" :image-size="100" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作对话框 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { workOrderApi } from '@/api'

const route = useRoute()
const router = useRouter()

const currentOrder = ref<any>({})
const operateLogs = ref<any[]>([])

// 对话框
const assignDialogVisible = ref(false)
const completeDialogVisible = ref(false)

const assignForm = reactive({ assignedTo: '' })
const completeForm = reactive({ solution: '' })

// 类型转换函数
const getTypeText = (type: string) => {
  const map: Record<string, string> = { 'REPAIR': '维修', 'MAINTENANCE': '保养', 'INSPECTION': '巡检' }
  return map[type] || type
}

const getPriorityType = (priority: string) => {
  const map: Record<string, string> = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
  return map[priority] || 'info'
}

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
  const orderId = route.params.id
  if (!orderId) return

  try {
    const res = await workOrderApi.getById(Number(orderId))
    if (res.data) {
      currentOrder.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取工单详情失败')
  }

  // 模拟操作日志数据
  operateLogs.value = [
    {
      action: '创建工单',
      details: `创建了工单 ${currentOrder.value.orderNo}`,
      operator: currentOrder.value.reporter,
      createTime: currentOrder.value.createTime
    }
  ]
}

// 操作处理函数
const goBack = () => {
  router.back()
}

const handleAssign = () => {
  assignForm.assignedTo = currentOrder.value.assignedTo || ''
  assignDialogVisible.value = true
}

const handleProcess = async () => {
  try {
    await workOrderApi.process(currentOrder.value.id)
    ElMessage.success('已开始处理')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleComplete = () => {
  completeForm.solution = currentOrder.value.solution || ''
  completeDialogVisible.value = true
}

const handleClose = async () => {
  try {
    await workOrderApi.close(currentOrder.value.id)
    ElMessage.success('工单已关闭')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleAssignSubmit = async () => {
  if (!assignForm.assignedTo) {
    ElMessage.warning('请输入处理人')
    return
  }
  try {
    await workOrderApi.assign(currentOrder.value.id, assignForm.assignedTo)
    ElMessage.success('指派成功')
    assignDialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleCompleteSubmit = async () => {
  if (!completeForm.solution) {
    ElMessage.warning('请输入解决方案')
    return
  }
  try {
    await workOrderApi.complete(currentOrder.value.id, completeForm.solution)
    ElMessage.success('工单已完成')
    completeDialogVisible.value = false
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
.work-order-detail {
  padding: 20px;
}

.page-header-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.title {
  font-size: 20px;
  font-weight: bold;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-box {
  line-height: 1.6;
  word-wrap: break-word;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.timeline-container {
  max-height: 500px;
  overflow-y: auto;
}

.operator {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

@media (max-width: 768px) {
  .work-order-detail {
    padding: 10px;
  }
  
  .el-col {
    margin-bottom: 20px;
  }
}
</style>