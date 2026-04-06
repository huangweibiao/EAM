<template>
  <div class="spare-warning">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存预警</span>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="partCode" label="备件编码" width="120" />
        <el-table-column prop="partName" label="备件名称" width="180" />
        <el-table-column prop="model" label="规格型号" width="120" />
        <el-table-column prop="quantity" label="当前库存" width="100">
          <template #default="{ row }">
            <span class="text-danger">{{ row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minQuantity" label="最低库存" width="100" />
        <el-table-column label="缺少数量" width="100">
          <template #default="{ row }">
            <span class="text-danger">{{ (row.minQuantity - row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">{{ row.unitPrice ? `¥${row.unitPrice}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link>采购申请</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { spareApi } from '@/api'

const tableData = ref<any[]>([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await spareApi.warn()
    tableData.value = res.data || []
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.spare-warning { padding: 20px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.text-danger { color: #f56c6c; font-weight: bold }
</style>