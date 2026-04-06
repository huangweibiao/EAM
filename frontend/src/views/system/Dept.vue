<template>
  <div class="dept-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd">新增部门</el-button>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        :data="tableData"
        border
        stripe
        row-key="id"
        default-expand-all
        v-loading="loading"
      >
        <el-table-column prop="deptCode" label="部门编码" width="150" />
        <el-table-column prop="deptName" label="部门名称" width="180" />
        <el-table-column prop="manager" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleAddChild(row)">添加子部门</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeOptions"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择上级部门"
            clearable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="formData.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="formData.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-input v-model="formData.manager" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
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
import { deptApi } from '@/api'

// 表格数据
const tableData = ref<any[]>([])
const treeOptions = ref<any[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const formData = reactive({
  id: null as number | null,
  parentId: 0,
  deptCode: '',
  deptName: '',
  manager: '',
  phone: '',
  sortOrder: 0,
  status: 1,
  remark: ''
})

// 表单验证
const formRules: FormRules = {
  deptCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await deptApi.tree()
    if (res.data) {
      tableData.value = res.data || []
      treeOptions.value = [{ id: 0, deptName: '顶级部门', children: res.data || [] }]
    }
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  formData.id = null
  formData.parentId = 0
  formData.deptCode = ''
  formData.deptName = ''
  formData.manager = ''
  formData.phone = ''
  formData.sortOrder = 0
  formData.status = 1
  formData.remark = ''
  dialogVisible.value = true
}

// 添加子部门
const handleAddChild = (row: any) => {
  dialogTitle.value = '新增子部门'
  formData.id = null
  formData.parentId = row.id
  formData.deptCode = ''
  formData.deptName = ''
  formData.manager = ''
  formData.phone = ''
  formData.sortOrder = 0
  formData.status = 1
  formData.remark = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑部门'
  formData.id = row.id
  formData.parentId = row.parentId
  formData.deptCode = row.deptCode
  formData.deptName = row.deptName
  formData.manager = row.manager || ''
  formData.phone = row.phone || ''
  formData.sortOrder = row.sortOrder || 0
  formData.status = row.status
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    if (formData.id) {
      await deptApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await deptApi.add(formData)
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
  ElMessageBox.confirm('确认删除该部门吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deptApi.delete(row.id)
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
.dept-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>