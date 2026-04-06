<template>
  <div class="asset-inventory">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产盘点</span>
          <el-button type="primary" @click="handleAdd">创建盘点</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="inventoryNo" label="盘点单号" width="150" />
        <el-table-column prop="inventoryName" label="盘点名称" width="180" />
        <el-table-column prop="deptId" label="盘点部门" width="120">
          <template #default="{ row }">
            {{ getDeptName(row.deptId) }}
          </template>
        </el-table-column>
        <el-table-column prop="inventoryScope" label="盘点范围" width="100" />
        <el-table-column prop="totalAssetCount" label="应盘点数" width="100" />
        <el-table-column prop="actualCount" label="实际盘点" width="100" />
        <el-table-column prop="mismatchCount" label="差异数" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.mismatchCount > 0 }">{{ row.mismatchCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetails(row)">盘点明细</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" type="success" link @click="handleComplete(row)">完成盘点</el-button>
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
    <el-dialog v-model="dialogVisible" title="创建盘点" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="盘点名称" prop="inventoryName">
          <el-input v-model="formData.inventoryName" placeholder="请输入盘点名称" />
        </el-form-item>
        <el-form-item label="盘点部门">
          <el-select v-model="formData.deptId" placeholder="请选择(不选则为全公司)" clearable>
            <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点范围">
          <el-radio-group v-model="formData.inventoryScope">
            <el-radio value="ALL">全部资产</el-radio>
            <el-radio value="PARTIAL">部分资产</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="创建人">
          <el-input v-model="formData.createBy" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 盘点明细对话框 -->
    <el-dialog v-model="detailDialogVisible" title="盘点明细" width="800px">
      <el-table :data="detailData" border stripe>
        <el-table-column prop="assetId" label="资产ID" width="100" />
        <el-table-column prop="systemLocation" label="系统位置" width="150" />
        <el-table-column prop="actualLocation" label="实际位置" width="150" />
        <el-table-column prop="systemStatus" label="系统状态" width="100" />
        <el-table-column prop="actualStatus" label="实际状态" width="100" />
        <el-table-column prop="isMatch" label="是否一致" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isMatch === 1 ? 'success' : 'danger'">
              {{ row.isMatch === 1 ? '一致' : '差异' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inventoryBy" label="盘点人" width="100" />
        <el-table-column prop="inventoryTime" label="盘点时间" width="180" />
        <el-table-column prop="remark" label="差异说明" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { inventoryApi, deptApi, assetApi } from '@/api'

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
const assetList = ref<any[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const formRef = ref<FormInstance>()
const detailData = ref<any[]>([])
const currentInventoryId = ref<number | null>(null)

const formData = reactive({
  inventoryName: '',
  deptId: null as number | null,
  inventoryScope: 'ALL',
  createBy: ''
})

// 表单验证
const formRules: FormRules = {
  inventoryName: [{ required: true, message: '请输入盘点名称', trigger: 'blur' }]
}

// 状态类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success',
    'CANCELED': 'info'
  }
  return map[status] || 'info'
}

// 状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELED': '已取消'
  }
  return map[status] || status
}

// 获取部门名称
const getDeptName = (deptId: number) => {
  if (!deptId) return '全公司'
  const dept = deptList.value.find(d => d.id === deptId)
  return dept ? dept.deptName : '-'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await inventoryApi.page({
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

// 加载选项
const loadOptions = async () => {
  try {
    const [deptRes, assetRes] = await Promise.all([
      deptApi.list(),
      assetApi.list()
    ])
    deptList.value = deptRes.data || []
    assetList.value = assetRes.data || []
  } catch (error) {
    console.error('加载选项失败:', error)
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
  formData.inventoryName = ''
  formData.deptId = null
  formData.inventoryScope = 'ALL'
  formData.createBy = ''
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    await inventoryApi.create(formData)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 查看明细
const handleViewDetails = async (row: any) => {
  currentInventoryId.value = row.id
  try {
    const res = await inventoryApi.details(row.id)
    detailData.value = res.data || []
    detailDialogVisible.value = true
  } catch (error: any) {
    ElMessage.error('加载明细失败')
  }
}

// 完成盘点
const handleComplete = async (row: any) => {
  try {
    await inventoryApi.complete(row.id)
    ElMessage.success('盘点已完成')
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

onMounted(() => {
  loadData()
  loadOptions()
})
</script>

<style scoped>
.asset-inventory {
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

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>