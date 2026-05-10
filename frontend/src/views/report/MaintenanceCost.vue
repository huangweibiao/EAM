<template>
  <div class="report-page">
    <el-card>
      <template #header>
        <h2>维护成本统计报表</h2>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="queryParams" inline class="query-form">
        <el-form-item label="时间范围：">
          <el-date-picker
            v-model="dateRange"
            type="monthrange"
            range-separator="至"
            start-placeholder="开始月份"
            end-placeholder="结束月份"
            value-format="YYYY-MM"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="统计粒度：">
          <el-select v-model="queryParams.granularity" placeholder="请选择统计粒度">
            <el-option label="按月" value="month"></el-option>
            <el-option label="按季" value="quarter"></el-option>
            <el-option label="按年" value="year"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="exportData">导出Excel</el-button>
        </el-form-item>
      </el-form>

      <!-- 成本概览卡片 -->
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">维护总成本</div>
            <div class="stat-value">¥{{ summary.totalCost?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">平均维护成本</div>
            <div class="stat-value">¥{{ summary.avgCost?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">最大单次维护</div>
            <div class="stat-value">¥{{ summary.maxCost?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">维护次数</div>
            <div class="stat-value">{{ summary.maintenanceCount || 0 }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 成本趋势图 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span>维护成本趋势</span>
            </template>
            <div ref="trendChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 按资产分类统计 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :xs="24" :sm="12">
          <el-card shadow="hover">
            <template #header>
              <span>按资产分类统计</span>
            </template>
            <div ref="categoryChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-card shadow="hover">
            <template #header>
              <span>按维护类型统计</span>
            </template>
            <div ref="typeChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 详细数据表格 -->
      <el-card style="margin-top: 20px">
        <template #header>
          <span>详细数据</span>
        </template>
        <el-table :data="tableData" border stripe>
          <el-table-column prop="period" label="时间段"></el-table-column>
          <el-table-column prop="preventiveCount" label="预防性维护(次)"></el-table-column>
          <el-table-column prop="preventiveCost" label="预防性维护成本">
            <template #default="{ row }">¥{{ row.preventiveCost?.toLocaleString() || 0 }}</template>
          </el-table-column>
          <el-table-column prop="correctiveCount" label="纠正性维护(次)"></el-table-column>
          <el-table-column prop="correctiveCost" label="纠正性维护成本">
            <template #default="{ row }">¥{{ row.correctiveCost?.toLocaleString() || 0 }}</template>
          </el-table-column>
          <el-table-column prop="totalCount" label="维护总数"></el-table-column>
          <el-table-column prop="totalCost" label="总成本">
            <template #default="{ row }">¥{{ row.totalCost?.toLocaleString() || 0 }}</template>
          </el-table-column>
        </el-table>
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

const trendChart = ref()
const categoryChart = ref()
const typeChart = ref()

const dateRange = ref(['2025-01', '2025-12'])
const queryParams = reactive({
  startDate: '',
  endDate: '',
  granularity: 'month'
})

const summary = reactive({
  totalCost: 0,
  avgCost: 0,
  totalCount: 0,
  avgAssetCost: 0
})

const tableData = ref<any[]>([])

let trendChartInstance: echarts.ECharts | null = null
let categoryChartInstance: echarts.ECharts | null = null
let typeChartInstance: echarts.ECharts | null = null

const handleDateChange = (value: string[]) => {
  if (value && value.length === 2) {
    queryParams.startDate = value[0]
    queryParams.endDate = value[1]
  }
}

const initCharts = () => {
  nextTick(() => {
    if (!trendChart.value || !categoryChart.value || !typeChart.value) return
    
    trendChartInstance = echarts.init(trendChart.value)
    categoryChartInstance = echarts.init(categoryChart.value)
    typeChartInstance = echarts.init(typeChart.value)
    
    // 趋势图配置
    const trendOption = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['预防性维护成本', '纠正性维护成本', '总成本']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '预防性维护成本',
          type: 'line',
          smooth: true,
          data: []
        },
        {
          name: '纠正性维护成本',
          type: 'line',
          smooth: true,
          data: []
        },
        {
          name: '总成本',
          type: 'line',
          smooth: true,
          data: []
        }
      ]
    }
    
    // 分类图配置
    const pieOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: ¥{c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center'
      },
      series: [
        {
          name: '成本分布',
          type: 'pie',
          radius: ['40%', '70%'],
          data: []
        }
      ]
    }
    
    trendChartInstance.setOption(trendOption)
    categoryChartInstance.setOption(pieOption)
    typeChartInstance.setOption(pieOption)
  })
}

const loadData = async () => {
  try {
    // 设置默认时间范围
    if (!queryParams.startDate && dateRange.value?.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    }
    
    const response = await reportApi.maintenanceCost()
    tableData.value = response.data || []
    
    // 计算汇总信息
    calculateSummary()
    
    // 更新图表
    updateCharts()
  } catch (error) {
    console.error('加载维护成本数据失败:', error)
  }
}

const calculateSummary = () => {
  const totalCost = tableData.value.reduce((sum, item) => sum + (item.totalCost || 0), 0)
  const totalCount = tableData.value.reduce((sum, item) => sum + (item.totalCount || 0), 0)
  
  summary.totalCost = totalCost
  summary.totalCount = totalCount
  summary.avgCost = totalCount > 0 ? totalCost / totalCount : 0
  summary.avgAssetCost = totalCost // 简化计算，实际可能需要资产总数
}

const updateCharts = () => {
  if (!trendChartInstance || !categoryChartInstance || !typeChartInstance) return
  
  // 趋势图数据
  const periods = tableData.value.map(item => item.period || '未知')
  const preventiveCosts = tableData.value.map(item => item.preventiveCost || 0)
  const correctiveCosts = tableData.value.map(item => item.correctiveCost || 0)
  const totalCosts = tableData.value.map(item => item.totalCost || 0)
  
  // 类别分布数据
  const categoryData = [
    { value: 45, name: '机械设备' },
    { value: 25, name: '电气设备' },
    { value: 15, name: '办公设备' },
    { value: 10, name: '运输设备' },
    { value: 5, name: '其他' }
  ]
  
  // 类型分布数据
  const typeData = [
    { value: 60, name: '例行维护' },
    { value: 25, name: '故障维修' },
    { value: 10, name: '预防性维护' },
    { value: 5, name: '紧急维修' }
  ]
  
  const trendOption = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params: any) {
        let result = params[0].axisValue + '<br/>'
        params.forEach((item: any) => {
          result += `${item.marker} ${item.seriesName}: ¥${item.value?.toLocaleString()}<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['预防性维护成本', '纠错性维护成本', '总维护成本']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: periods
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '预防性维护成本',
        type: 'line',
        smooth: true,
        data: preventiveCosts,
        itemStyle: { color: '#ffa940' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(255, 169, 64, 0.5)' },
              { offset: 1, color: 'rgba(255, 169, 64, 0.1)' }
            ]
          }
        }
      },
      {
        name: '纠错性维护成本',
        type: 'line',
        smooth: true,
        data: correctiveCosts,
        itemStyle: { color: '#ff4d4f' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(255, 77, 79, 0.5)' },
              { offset: 1, color: 'rgba(255, 77, 79, 0.1)' }
            ]
          }
        }
      },
      {
        name: '总维护成本',
        type: 'line',
        symbol: 'circle',
        symbolSize: 8,
        data: totalCosts,
        itemStyle: { color: '#1890ff' },
        lineStyle: { width: 3 }
      }
    ]
  }
  
  const pieOptions = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c} ({d}%)'
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
        center: ['35%', '50%'],
        data: [],
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
  
  trendChartInstance.setOption(trendOption, true)
  categoryChartInstance.setOption({
    ...pieOptions,
    series: [{ ...pieOptions.series[0], data: categoryData, name: '资产类别分布' }]
  }, true)
  typeChartInstance.setOption({
    ...pieOptions,
    series: [{ ...pieOptions.series[0], data: typeData, name: '维护类型分布' }]
  }, true)
  
  // 添加图表事件
  trendChartInstance.off('click')
  trendChartInstance.on('click', (params) => {
    ElMessage.info(`查看 ${params.name} 的详细成本数据`)
  })

  categoryChartInstance.off('click')
  categoryChartInstance.on('click', (params) => {
    ElMessage.info(`查看 ${params.name} 类别的成本分析`)
  })

  typeChartInstance.off('click')
  typeChartInstance.on('click', (params) => {
    ElMessage.info(`查看 ${params.name} 类型的成本详情`)
  })
}

const exportData = () => {
  if (!tableData.value || tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  try {
    ExcelExporter.exportMaintenanceCost(tableData.value)
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
}

.query-form {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
</style>