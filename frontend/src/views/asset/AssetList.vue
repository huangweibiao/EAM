<template>
  <div class="asset-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产台账</span>
          <el-button type="primary" @click="handleAdd">新增资产</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="资产编码/名称" clearable />
        </el-form-item>
        <el-form-item label="资产分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择" clearable>
            <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="使用部门">
          <el-select v-model="searchForm.deptId" placeholder="请选择" clearable>
            <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="新购" value="NEW" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维修中" value="MAINTENANCE" />
            <el-option label="报废" value="SCRAP" />
            <el-option label="遗失" value="LOST" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="assetCode" label="资产编码" width="120" />
        <el-table-column prop="assetName" label="资产名称" width="180" />
        <el-table-column prop="categoryId" label="分类" width="100">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="model" label="规格型号" width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="deptId" label="使用部门" width="120">
          <template #default="{ row }">
            {{ getDeptName(row.deptId) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120" />
        <el-table-column prop="purchasePrice" label="购买价格" width="100">
          <template #default="{ row }">
            {{ row.purchasePrice ? `¥${row.purchasePrice}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleChange(row)">变动</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="formData.assetCode" :disabled="!!formData.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="formData.assetName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择">
                <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="formData.model" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input v-model="formData.brand" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="序列号">
              <el-input v-model="formData.serialNumber" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用部门">
              <el-select v-model="formData.deptId" placeholder="请选择">
                <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用人">
              <el-input v-model.number="formData.userId" type="number" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="存放位置">
              <el-input v-model="formData.location" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="formData.status" placeholder="请选择">
                <el-option label="新购" value="NEW" />
                <el-option label="使用中" value="IN_USE" />
                <el-option label="维修中" value="MAINTENANCE" />
                <el-option label="报废" value="SCRAP" />
                <el-option label="遗失" value="LOST" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="购买日期">
              <el-date-picker v-model="formData.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购买价格">
              <el-input-number v-model="formData.purchasePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保修截止">
              <el-date-picker v-model="formData.warrantyEndDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护周期(天)">
              <el-input-number v-model="formData.maintenanceCycle" :min="0" style="width: 100%" />
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

    <!-- 资产变动对话框 -->
    <el-dialog v-model="changeDialogVisible" title="资产变动" width="500px">
      <el-form :model="changeForm" label-width="100px">
        <el-form-item label="变动类型">
          <el-select v-model="changeForm.changeType" placeholder="请选择">
            <el-option label="部门变更" value="DEPT" />
            <el-option label="使用人变更" value="USER" />
            <el-option label="状态变更" value="STATUS" />
            <el-option label="位置变更" value="LOCATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="新值">
          <el-input v-model="changeForm.newValue" />
        </el-form-item>
        <el-form-item label="变动原因">
          <el-input v-model="changeForm.reason" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangeSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { assetApi, categoryApi, deptApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: null as number | null,
  deptId: null as number | null,
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
const categoryList = ref<any[]>([])
const deptList = ref<any[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const formData = reactive({
  id: null as number | null,
  assetCode: '',
  assetName: '',
  categoryId: null as number | null,
  model: '',
  brand: '',
  serialNumber: '',
  deptId: null as number | null,
  userId: null as number | null,
  location: '',
  status: 'NEW',
  purchaseDate: '',
  purchasePrice: 0,
  warrantyEndDate: '',
  maintenanceCycle: 0,
  remark: ''
})

// 变动对话框
const changeDialogVisible = ref(false)
const currentAssetId = ref<number | null>(null)
const changeForm = reactive({
  changeType: 'DEPT',
  newValue: '',
  reason: ''
})

// 表单验证
const formRules: FormRules = {
  assetCode: [{ required: true, message: '请输入资产编码', trigger: 'blur' }],
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择资产分类', trigger: 'change' }]
}

// 获取分类名称
const getCategoryName = (categoryId: number) => {
  const cat = categoryList.value.find(c => c.id === categoryId)
  return cat ? cat.categoryName : '-'
}

// 获取部门名称
const getDeptName = (deptId: number) => {
  const dept = deptList.value.find(d => d.id === deptId)
  return dept ? dept.deptName : '-'
}

// 状态类型
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    'NEW': 'info',
    'IN_USE': 'success',
    'MAINTENANCE': 'warning',
    'SCRAP': 'danger',
    'LOST': 'danger'
  }
  return map[status] || 'info'
}

// 状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'NEW': '新购',
    'IN_USE': '使用中',
    'MAINTENANCE': '维修中',
    'SCRAP': '报废',
    'LOST': '遗失'
  }
  return map[status] || status
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await assetApi.page({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword || undefined,
      categoryId: searchForm.categoryId ?? undefined,
      deptId: searchForm.deptId ?? undefined,
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

// 加载分类和部门
const loadOptions = async () => {
  try {
    const [catRes, deptRes] = await Promise.all([
      categoryApi.list(),
      deptApi.list()
    ])
    categoryList.value = catRes.data || []
    deptList.value = deptRes.data || []
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
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.deptId = null
  searchForm.status = ''
  pagination.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增资产'
  formData.id = null
  formData.assetCode = ''
  formData.assetName = ''
  formData.categoryId = null
  formData.model = ''
  formData.brand = ''
  formData.serialNumber = ''
  formData.deptId = null
  formData.userId = null
  formData.location = ''
  formData.status = 'NEW'
  formData.purchaseDate = ''
  formData.purchasePrice = 0
  formData.warrantyEndDate = ''
  formData.maintenanceCycle = 0
  formData.remark = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑资产'
  formData.id = row.id
  formData.assetCode = row.assetCode
  formData.assetName = row.assetName
  formData.categoryId = row.categoryId
  formData.model = row.model || ''
  formData.brand = row.brand || ''
  formData.serialNumber = row.serialNumber || ''
  formData.deptId = row.deptId
  formData.userId = row.userId
  formData.location = row.location || ''
  formData.status = row.status
  formData.purchaseDate = row.purchaseDate || ''
  formData.purchasePrice = row.purchasePrice || 0
  formData.warrantyEndDate = row.warrantyEndDate || ''
  formData.maintenanceCycle = row.maintenanceCycle || 0
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    if (formData.id) {
      await assetApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await assetApi.add(formData)
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
  ElMessageBox.confirm('确认删除该资产吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await assetApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

// 变动
const handleChange = (row: any) => {
  currentAssetId.value = row.id
  changeForm.changeType = 'DEPT'
  changeForm.newValue = ''
  changeForm.reason = ''
  changeDialogVisible.value = true
}

// 提交变动
const handleChangeSubmit = async () => {
  if (!currentAssetId.value) return
  if (!changeForm.newValue) {
    ElMessage.warning('请输入新值')
    return
  }

  try {
    await assetApi.change({
      assetId: currentAssetId.value,
      changeType: changeForm.changeType,
      newValue: changeForm.newValue,
      reason: changeForm.reason
    })
    ElMessage.success('变动成功')
    changeDialogVisible.value = false
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
.asset-list {
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