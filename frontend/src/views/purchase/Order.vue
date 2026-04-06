<template>
  <div class="purchase-order">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购订单</span>
          <el-button type="primary" @click="handleAdd">新增订单</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已发货" value="SHIPPED" />
            <el-option label="已收货" value="RECEIVED" />
            <el-option label="已取消" value="CANCELED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="150" />
        <el-table-column prop="requestId" label="申请ID" width="100" />
        <el-table-column prop="supplierId" label="供应商ID" width="100" />
        <el-table-column prop="partId" label="备件ID" width="100" />
        <el-table-column prop="orderQuantity" label="采购数量" width="100" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">{{ row.unitPrice ? `¥${row.unitPrice}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">{{ row.totalAmount ? `¥${row.totalAmount}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="下单日期" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'SHIPPED'" type="success" link @click="handleReceive(row)">确认收货</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增采购订单" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购申请ID">
              <el-input-number v-model="formData.requestId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商ID" prop="supplierId">
              <el-input-number v-model="formData.supplierId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="备件ID" prop="partId">
              <el-input-number v-model="formData.partId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购数量" prop="orderQuantity">
              <el-input-number v-model="formData.orderQuantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同单价" prop="unitPrice">
              <el-input-number v-model="formData.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计交货">
              <el-date-picker v-model="formData.deliveryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="收货人">
          <el-input v-model="formData.receiver" />
        </el-form-item>
        <el-form-item label="创建人">
          <el-input v-model="formData.createBy" />
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
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { purchaseApi } from '@/api'

const searchForm = reactive({ status: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({ requestId: null as number | null, supplierId: null as number | null, partId: null as number | null, orderQuantity: 1, unitPrice: 0, deliveryDate: '', receiver: '', createBy: '', remark: '' })
const formRules: FormRules = { supplierId: [{ required: true, message: '请输入供应商ID', trigger: 'blur' }], partId: [{ required: true, message: '请输入备件ID', trigger: 'blur' }], orderQuantity: [{ required: true, message: '请输入数量', trigger: 'blur' }] }

const getStatusType = (v: string) => ({ PENDING: 'warning', CONFIRMED: 'primary', SHIPPED: 'info', RECEIVED: 'success', CANCELED: 'info' }[v] || 'info')
const getStatusText = (v: string) => ({ PENDING: '待确认', CONFIRMED: '已确认', SHIPPED: '已发货', RECEIVED: '已收货', CANCELED: '已取消' }[v] || v)

const loadData = async () => {
  loading.value = true
  try {
    const res = await purchaseApi.orderPage({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, status: searchForm.status || undefined })
    if (res.data) { tableData.value = res.data.records || []; pagination.total = res.data.total || 0 }
  } catch (error) { console.error('加载失败:', error) }
  finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { searchForm.status = ''; pagination.pageNum = 1; loadData() }
const handleAdd = () => { Object.assign(formData, { requestId: null, supplierId: null, partId: null, orderQuantity: 1, unitPrice: 0, deliveryDate: '', receiver: '', createBy: '', remark: '' }); dialogVisible.value = true }
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try { await purchaseApi.orderAdd(formData); ElMessage.success('创建成功'); dialogVisible.value = false; loadData() }
  catch (error: any) { ElMessage.error(error.message || '操作失败') }
}
const handleReceive = async (row: any) => {
  try { await purchaseApi.orderReceive(row.id); ElMessage.success('已确认收货'); loadData() }
  catch (error: any) { ElMessage.error(error.message || '操作失败') }
}
onMounted(() => { loadData() })
</script>

<style scoped>
.purchase-order { padding: 20px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.search-form { margin-bottom: 20px }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end }
</style>