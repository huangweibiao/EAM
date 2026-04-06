<template>
  <div class="report-dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>资产总数</template>
          <div class="stat-value">{{ stats.totalAssets }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>资产总值</template>
          <div class="stat-value">¥{{ stats.totalValue }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>备件种类</template>
          <div class="stat-value">{{ stats.partTypes }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>维护总成本</template>
          <div class="stat-value">¥{{ stats.maintenanceCost }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>资产分类统计</template>
          <el-table :data="categoryData" border>
            <el-table-column prop="categoryId" label="分类ID" />
            <el-table-column prop="totalCount" label="资产数量" />
            <el-table-column prop="totalValue" label="资产总值">
              <template #default="{ row }">¥{{ row.totalValue || 0 }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>维护成本趋势</template>
          <el-table :data="costData" border>
            <el-table-column prop="month" label="月份" />
            <el-table-column prop="count" label="维护次数" />
            <el-table-column prop="totalCost" label="维护成本">
              <template #default="{ row }">¥{{ row.totalCost || 0 }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>库存汇总</template>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="备件种类数">{{ inventoryData.totalPartTypes || 0 }}</el-descriptions-item>
            <el-descriptions-item label="总库存量">{{ inventoryData.totalQuantity || 0 }}</el-descriptions-item>
            <el-descriptions-item label="库存总值">¥{{ inventoryData.totalValue || 0 }}</el-descriptions-item>
            <el-descriptions-item label="库存预警数">{{ inventoryData.lowStockCount || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { reportApi, assetApi, maintenanceApi, spareApi } from '@/api'

const stats = reactive({ totalAssets: 0, totalValue: 0, partTypes: 0, maintenanceCost: 0 })
const categoryData = ref<any[]>([])
const costData = ref<any[]>([])
const inventoryData = ref<any>({})

const loadData = async () => {
  try {
    const [assetRes, costRes, inventoryRes, costTotalRes] = await Promise.all([
      assetApi.list(),
      reportApi.maintenanceCost(),
      reportApi.inventorySummary(),
      maintenanceApi.totalCost()
    ])

    stats.totalAssets = assetRes.data?.length || 0
    stats.totalValue = assetRes.data?.reduce((sum: number, a: any) => sum + (a.purchasePrice || 0), 0) || 0
    stats.partTypes = (await spareApi.list()).data?.length || 0
    stats.maintenanceCost = costTotalRes.data || 0

    categoryData.value = (await reportApi.assetSummaryByCategory()).data || []
    costData.value = costRes.data || []
    inventoryData.value = inventoryRes.data || {}
  } catch (error) {
    console.error('加载报表数据失败:', error)
  }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.report-dashboard { padding: 20px }
.stat-value { font-size: 24px; font-weight: bold; text-align: center; color: #409eff }
</style>