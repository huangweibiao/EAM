<template>
  <div class="report-page">
    <el-card>
      <template #header>
        <h2>资产汇总统计报表</h2>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="queryParams" class="query-form">
        <div class="form-row">
          <el-form-item label="统计维度：">
            <el-select v-model="queryParams.dimension" placeholder="请选择统计维度" @change="loadData">
              <el-option label="按部门" value="dept"></el-option>
              <el-option label="按分类" value="category"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="exportData">导出Excel</el-button>
          </el-form-item>
        </div>
      </el-form>

      <!-- 统计图表 -->
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12">
          <el-card shadow="hover">
            <template #header>
              <span>资产数量分布</span>
            </template>
            <div ref="countChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-card shadow="hover">
            <template #header>
              <span>资产价值分布</span>
            </template>
            <div ref="valueChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 详细数据表格 -->
      <el-card style="margin-top: 20px">
        <template #header>
          <span>详细数据</span>
        </template>
        <el-table :data="tableData" border stripe>
          <el-table-column prop="key" :label="queryParams.dimension === 'dept' ? '部门名称' : '分类名称'"></el-table-column>
          <el-table-column prop="assetCount" label="资产数量"></el-table-column>
          <el-table-column prop="totalValue" label="资产总值">
            <template #default="{ row }">¥{{ row.totalValue?.toLocaleString() || 0 }}</template>
          </el-table-column>
          <el-table-column prop="avgValue" label="平均价值">
            <template #default="{ row }">¥{{ row.avgValue?.toLocaleString() || 0 }}</template>
          </el-table-column>
          <el-table-column prop="percentage" label="占比">
            <template #default="{ row }">{{ row.percentage?.toFixed(2) }}%</template>
          </el-table-column>
        </el-table>
        
        <!-- 汇总信息 -->
        <el-descriptions :column="4" border style="margin-top: 20px">
          <el-descriptions-item label="总资产数">{{ totalStats.assetCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="总资产价值">¥{{ totalStats.totalValue?.toLocaleString() || 0 }}</el-descriptions-item>
          <el-descriptions-item label="资产种类数">{{ tableData.length }}</el-descriptions-item>
          <el-descriptions-item label="平均价值">¥{{ totalStats.avgValue?.toLocaleString() || 0 }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { reportApi } from '@/api'
import * as echarts from 'echarts'
import { ExcelExporter } from '@/utils/excelExport'

const countChart = ref()
const valueChart = ref()

const queryParams = reactive({
  dimension: 'dept'
})

const tableData = ref<any[]>([])
const totalStats = reactive({
  assetCount: 0,
  totalValue: 0,
  avgValue: 0
})

let countChartInstance: echarts.ECharts | null = null
let valueChartInstance: echarts.ECharts | null = null

const initCharts = () => {
  nextTick(() => {
    if (!countChart.value || !valueChart.value) return
    
    countChartInstance = echarts.init(countChart.value)
    valueChartInstance = echarts.init(valueChart.value)
    
    // 初始化图表配置
    const commonOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center'
      },
      series: [
        {
          name: '分布情况',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 18,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: []
        }
      ]
    }
    
    countChartInstance.setOption({ ...commonOption })
    valueChartInstance.setOption({ ...commonOption })
  })
}

const loadData = async () => {
  try {
    let response
    if (queryParams.dimension === 'dept') {
      response = await reportApi.assetSummaryByDept()
    } else {
      response = await reportApi.assetSummaryByCategory()
    }
    
    tableData.value = response.data || []
    
    // 计算汇总信息
    totalStats.assetCount = tableData.value.reduce((sum, item) => sum + (item.assetCount || 0), 0)
    totalStats.totalValue = tableData.value.reduce((sum, item) => sum + (item.totalValue || 0), 0)
    totalStats.avgValue = totalStats.assetCount > 0 ? totalStats.totalValue / totalStats.assetCount : 0
    
    // 更新图表数据
    updateCharts()
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 更新图表数据
const updateCharts = () => {
  if (!countChartInstance || !valueChartInstance) return
  
  // 资产数量分布图数据
  const countData = tableData.value.map(item => ({
    value: item.assetCount || 0,
    name: item.key || '未知'
  }))
  
  // 资产价值分布图数据
  const valueData = tableData.value.map(item => ({
    value: item.totalValue || 0,
    name: item.key || '未知'
  }))
  
  const countOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{a}: {c} 件 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '资产数量',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        data: countData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  const valueOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{a}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '资产价值',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        data: valueData,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  countChartInstance.setOption(countOption, true)
  valueChartInstance.setOption(valueOption, true)
  
  // 添加图表点击事件
  countChartInstance.off('click')
  countChartInstance.on('click', (params) => {
    ElMessage.info(`查看 ${params.name} 的资产详情`)
  })
  
  valueChartInstance.off('click')
  valueChartInstance.on('click', (params) => {
    ElMessage.info(`查看 ${params.name} 价值分布详情`)
  })
}

const exportData = () => {
  if (!tableData.value || tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  try {
    ExcelExporter.exportAssetSummary(tableData.value)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  }
}

onMounted(() => {
  initCharts()
  loadData()
})
</script>

<style scoped>
.report-page {
  padding: 20px;
  box-sizing: border-box;
}

.query-form {
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .report-page {
    padding: 10px;
  }
  
  .query-form {
    margin-bottom: 15px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 10px;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
  
  :deep(.el-card__body) {
    padding: 15px;
  }
  
  :deep(.el-form-item__label) {
    width: auto !important;
  }
  
  :deep(.chart-container),
  :deep(.el-card) {
    margin-bottom: 15px;
  }
}
</style>