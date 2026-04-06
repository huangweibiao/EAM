<template>
  <div class="purchase-request">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购申请</span>
          <el-button type="primary" @click="handleAdd">新增申请</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审批" value="PENDING" />
            <el-option label="已审批" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="requestNo" label="申请单号" width="150" />
        <el-table-column prop="partId" label="备件ID" width="100" />
        <el-table-column prop="requestQuantity" label="申请数量" width="100" />
        <el-table-column prop="estimatedPrice" label="预估单价" width="100">
          <template #default="{ row }">{{ row.estimatedPrice ? `¥${row.estimatedPrice}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalEstimatedAmount" label="预估金额" width="120">
          <template #default="{ row }">{{ row.totalEstimatedAmount ? `¥${row.totalEstimatedAmount}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="urgency" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="row.urgency === 'URGENT' ? 'danger' : 'info'">{{ getUrgencyText(row.urgency) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDate" label="期望日期" width="120" />
        <el-table-column prop="requester" label="申请人" width="100" />
        <el-table-column prop="approveStatus" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getApproveType(row.approveStatus)">{{ getApproveText(row.approveStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.approveStatus === 'PENDING'" type="success" link @click="handleApprove(row, true)">通过</el-button>
            <el-button v-if="row.approveStatus === 'PENDING'" type="danger" link @click="handleApprove(row, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增采购申请" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="备件ID" prop="partId">
              <el-input-number v-model="formData.partId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请数量" prop="requestQuantity">
              <el-input-number v-model="formData.requestQuantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预估单价">
              <el-input-number v-model="formData.estimatedPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度">
              <el-select v-model="formData.urgency" style="width: 100%">
                <el-option label="紧急" value="URGENT" />
                <el-option label="普通" value="NORMAL" />
                <el-option label="低" value="LOW" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="期望日期" prop="expectedDate">
          <el-date-picker v-model="formData.expectedDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input v-model="formData.reason" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="申请人">
          <el-input v-model="formData.requester" />
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
import { purchaseApi } from '@/api'

const searchForm = reactive({ status: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({ partId: null as number | null, requestQuantity: 1, estimatedPrice: 0, urgency: 'NORMAL', expectedDate: '', reason: '', requester: '' })
const formRules: FormRules = { partId: [{ required: true, message: '请输入备件ID', trigger: 'blur' }], requestQuantity: [{ required: true, message: '请输入数量', trigger: 'blur' }], expectedDate: [{ required: true, message: '请选择期望日期', trigger: 'change' }], reason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }] }

const getUrgencyText = (v: string) => ({ URGENT: '紧急', NORMAL: '普通', LOW: '低' }[v] || v)
const getApproveType = (v: string) => ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }[v] || 'info')
const getApproveText = (v: string) => ({ PENDING: '待审批', APPROVED: '已通过', REJECTED: '已拒绝' }[v] || v)

const loadData = async () => {
  loading.value = true
  try {
    const res = await purchaseApi.requestPage({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, status: searchForm.status || undefined })
    if (res.data) { tableData.value = res.data.records || []; pagination.total = res.data.total || 0 }
  } catch (error) { console.error('加载失败:', error) }
  finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { searchForm.status = ''; pagination.pageNum = 1; loadData() }
const handleAdd = () => { Object.assign(formData, { partId: null, requestQuantity: 1, estimatedPrice: 0, urgency: 'NORMAL', expectedDate: '', reason: '', requester: '' }); dialogVisible.value = true }
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try { await purchaseApi.requestAdd(formData); ElMessage.success('申请成功'); dialogVisible.value = false; loadData() }
  catch (error: any) { ElMessage.error(error.message || '操作失败') }
}
const handleApprove = (row: any, approved: boolean) => {
  ElMessageBox.confirm(`确认${approved ? '通过' : '拒绝'}该申请吗？`, '提示', { type: 'warning' }).then(async () => {
    try { await purchaseApi.requestApprove(row.id, 'admin', approved, ''); ElMessage.success('审批成功'); loadData() }
    catch (error: any) { ElMessage.error(error.message || '操作失败') }
  })
}
onMounted(() => { loadData() })
</script>

<style scoped>
.purchase-request { padding: 20px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.search-form { margin-bottom: 20px }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end }
</style>