<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单权限管理</span>
          <el-button type="primary" @click="handleAdd">新增菜单</el-button>
        </div>
      </template>

      <el-table :data="menuList" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="permName" label="菜单名称" width="180" />
        <el-table-column prop="permCode" label="权限编码" width="120" />
        <el-table-column prop="permType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.permType === 'D'">目录</el-tag>
            <el-tag v-else-if="row.permType === 'M'" type="success">菜单</el-tag>
            <el-tag v-else-if="row.permType === 'B'" type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="150" />
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAddChild(row)">新增子级</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Dialog for add/edit -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeData"
            :props="{ value: 'id', label: 'permName', children: 'children' }"
            placeholder="请选择上级菜单"
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权限编码" prop="permCode">
          <el-input v-model="form.permCode" placeholder="如: system:user:list" />
        </el-form-item>
        <el-form-item label="菜单名称" prop="permName">
          <el-input v-model="form.permName" placeholder="菜单名称" />
        </el-form-item>
        <el-form-item label="类型" prop="permType">
          <el-radio-group v-model="form.permType">
            <el-radio value="D">目录</el-radio>
            <el-radio value="M">菜单</el-radio>
            <el-radio value="B">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="路由路径，如: /system/user" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="图标名称，如: User" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="2" />
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { permissionApi } from '@/api'

const menuList = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = ref({
  id: null as number | null,
  parentId: 0,
  permCode: '',
  permName: '',
  permType: 'M',
  path: '',
  icon: '',
  sortOrder: 0,
  status: 1,
  remark: ''
})

const rules: FormRules = {
  permCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  permType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 菜单树选择器数据（添加"无"选项）
const menuTreeData = computed(() => {
  return [{ id: 0, permName: '顶级菜单', children: [] }, ...menuList.value]
})

const loadMenus = async () => {
  try {
    const res = await permissionApi.tree()
    menuList.value = res.data || []
  } catch (error) {
    console.error('加载菜单失败', error)
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增菜单'
  form.value = {
    id: null,
    parentId: 0,
    permCode: '',
    permName: '',
    permType: 'M',
    path: '',
    icon: '',
    sortOrder: 0,
    status: 1,
    remark: ''
  }
  dialogVisible.value = true
}

const handleAddChild = (row: any) => {
  isEdit.value = false
  dialogTitle.value = '新增子级菜单'
  form.value = {
    id: null,
    parentId: row.id,
    permCode: '',
    permName: '',
    permType: 'M',
    path: '',
    icon: '',
    sortOrder: 0,
    status: 1,
    remark: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除菜单"${row.permName}"吗？`, '提示', {
      type: 'warning'
    })
    await permissionApi.delete(row.id)
    ElMessage.success('删除成功')
    loadMenus()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  try {
    if (isEdit.value) {
      await permissionApi.update(form.value)
      ElMessage.success('更新成功')
    } else {
      await permissionApi.add(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadMenus()
  } catch (error) {
    console.error('操作失败', error)
  }
}

onMounted(() => {
  loadMenus()
})
</script>

<style scoped>
.menu-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>