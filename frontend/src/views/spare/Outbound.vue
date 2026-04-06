<template>
  <div class="spare-outbound">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>出库管理</span>
          <el-button type="primary" @click="handleAdd">新增出库</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="备件ID">
          <el-input-number v-model="searchForm.partId" :min="1" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="outboundNo" label="出库单号" width="150" />
        <el-table-column prop="partId" label="备件ID" width="100" />
        <el-table-column prop="quantity" label="出库数量" width="100" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">{{ row.unitPrice ? `¥${row.unitPrice}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">{{ row.totalAmount ? `¥${row.totalAmount}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="receiver" label="领用人" width="100" />
        <el-table-column prop="purpose" label="用途" width="120" />
        <el-table-column prop="outboundDate" label="出库日期" width="180" />
        <el-table-column prop="createBy" label="经办人" width="100" />
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增出库" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="备件ID" prop="partId">
          <el-input-number v-model="formData.partId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出库数量" prop="quantity">
              <el-input-number v-model="formData.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库单价">
              <el-input-number v-model="formData.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="领用人">
              <el-input v-model="formData.receiver" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用途">
              <el-select v-model="formData.purpose" style="width: 100%">
                <el-option label="维修" value="维修" />
                <el-option label="保养" value="保养" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="经办人">
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
import { spareApi } from '@/api'

const searchForm = reactive({ partId: null as number | null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({ partId: null as number | null, quantity: 1, unitPrice: 0, receiver: '', purpose: '', createBy: '', remark: '' })
const formRules: FormRules = { partId: [{ required: true, message: '请输入备件ID', trigger: 'blur' }], quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await spareApi.outboundPage({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, partId: searchForm.partId ?? undefined })
    if (res.data) { tableData.value = res.data.records || []; pagination.total = res.data.total || 0 }
  } catch (error) { console.error('加载失败:', error) }
  finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { searchForm.partId = null; pagination.pageNum = 1; loadData() }
const handleAdd = () => { Object.assign(formData, { partId: null, quantity: 1, unitPrice: 0, receiver: '', purpose: '', createBy: '', remark: '' }); dialogVisible.value = true }
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try { await spareApi.outbound(formData); ElMessage.success('出库成功'); dialogVisible.value = false; loadData() }
  catch (error: any) { ElMessage.error(error.message || '操作失败') }
}
onMounted(() => { loadData() })
</script>

<style scoped>
.spare-outbound { padding: 20px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.search-form { margin-bottom: 20px }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end }
</style>