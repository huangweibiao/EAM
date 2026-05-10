<template>
  <div class="report-page">
    <el-card>
      <template #header>
        <h2>库存分析报表</h2>
      </template>
      
      <!-- 统计汇总 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card" style="background: linear-gradient(45deg, #faad14, #fadb14)">
            <div class="stat-title">备件种类数</div>
            <div class="stat-value">{{ summary.totalTypes?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card" style="background: linear-gradient(45deg, #52c41a, #95de64)">
            <div class="stat-title">总库存量</div>
            <div class="stat-value">{{ summary.totalQuantity?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card" style="background: linear-gradient(45deg, #1890ff, #69c0ff)">
            <div class="stat-title">库存总值</div>
            <div class="stat-value">¥{{ summary.totalValue?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card" style="background: linear-gradient(45deg, #ff4d4f, #ff7875)">
            <div class="stat-title">库存预警数</div>
            <div class="stat-value">{{ summary.lowStockCount?.toLocaleString() || 0 }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 查询条件 -->
      <el-form :model="queryParams" inline class="query-form">
        <el-form-item label="分类筛选：">
          <el-select v-model="queryParams.categoryId" placeholder="全部分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态：">
          <el-select v-model="queryParams.stockStatus" placeholder="全部状态" clearable>
            <el-option label="正常库存" value="normal"></el-option>
            <el-option label="库存预警" value="warning"></el-option>
            <el-option label="缺货" value="outOfStock"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="exportData">导出Excel</el-button>
        </el-form-item>
      </el-form>

      <!-- 库存分布图表 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>库存价值分布</span>
            </template>
            <div ref="valueChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>库存数量分布</span>
            </template>
            <div ref="quantityChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>库存状态分布</span>
            </template>
            <div ref="statusChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 出入库趋势图 -->
      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <span>出入库趋势</span>
            </template>
            <div ref="trendChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 详细库存数据 -->
      <el-card style="margin-top: 20px">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>详细库存数据</span>
            <el-button type="primary" @click="showLowStockWarning">查看库存预警</el-button>
          </div>
        </template>
        
        <el-table :data="tableData" border stripe>
          <el-table-column prop="partCode" label="备件编码" width="120"></el-table-column>
          <el-table-column prop="partName" label="备件名称"></el-table-column>
          <el-table-column prop="categoryName" label="分类" width="120"></el-table-column>
          <el-table-column prop="model" label="规格型号" width="150"></el-table-column>
          <el-table-column prop="unit" label="单位" width="80" align="center"></el-table-column>
          <el-table-column prop="quantity" label="当前库存" width="100" align="center">
            <template #default="{ row }">
              <span :class="{ 'warning-text': row.quantity <= row.minQuantity }">
                {{ row.quantity?.toLocaleString() || 0 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="minQuantity" label="最低库存" width="100" align="center"></el-table-column>
          <el-table-column prop="maxQuantity" label="最高库存" width="100" align="center"></el-table-column>
          <el-table-column prop="unitPrice" label="参考单价" width="120" align="right">
            <template #default="{ row }">¥{{ row.unitPrice?.toLocaleString() || 0 }}</template>
          </el-table-column>
          <el-table-column prop="totalValue" label="总价值" width="120" align="right">
            <template #default="{ row }">¥{{ row.totalValue?.toLocaleString() || 0 }}</template>
          </el-table-column>
          <el-table-column prop="location" label="存放位置" width="150"></el-table-column>
          <el-table-column prop="supplierName" label="供应商"></el-table-column>
          <el-table-column label="库存状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getStockStatusType(row)"
                size="small"
              >
                {{ getStockStatusText(row) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页控件 -->
        <el-pagination
          style="margin-top: 20px; justify-content: flex-end;"
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { reportApi, spareApi } from '@/api'
import * as echarts from 'echarts'
import { ExcelExporter } from '@/utils/excelExport'

const valueChart = ref()
const quantityChart = ref()
const statusChart = ref()
const trendChart = ref()

const queryParams = reactive({
  categoryId: null,
  stockStatus: '',
  pageNum: 1,
  pageSize: 10
})

const categories = ref<any[]>([])
const summary = reactive({
  totalTypes: 0,
  totalQuantity: 0,
  totalValue: 0,
  lowStockCount: 0
})

const tableData = ref<any[]>([])
const total = ref(0)

let valueChartInstance: echarts.ECharts | null = null
let quantityChartInstance: echarts.ECharts | null = null
let statusChartInstance: echarts.ECharts | null = null
let trendChartInstance: echarts.ECharts | null = null

// 获取库存状态类型
const getStockStatusType = (row: any) => {
  if (row.quantity === 0) return 'danger'
  if (row.quantity <= row.minQuantity) return 'warning'
  return 'success'
}

// 获取库存状态文本
const getStockStatusText = (row: any) => {
  if (row.quantity === 0) return '缺货'
  if (row.quantity <= row.minQuantity) return '库存预警'
  return '正常'
}

// 加载备件分类
const loadCategories = async () => {
  try {
    const response = await spareApi.list()
    categories.value = response.data || []
  } catch (error) {
    console.error('加载分类数据失败:', error)
  }
}

const initCharts = () => {
  nextTick(() => {
    if (!valueChart.value || !quantityChart.value || !statusChart.value || !trendChart.value) return
    
    valueChartInstance = echarts.init(valueChart.value)
    quantityChartInstance = echarts.init(quantityChart.value)
    statusChartInstance = echarts.init(statusChart.value)
    trendChartInstance = echarts.init(trendChart.value)
    
    // 库存价值分布图配置
    const pieOption = {
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
          data: []
        }
      ]
    }
    
    // 趋势图配置
    const trendOption = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['入库数量', '出库数量', '净入库量']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '入库数量',
          type: 'bar',
          data: []
        },
        {
          name: '出库数量',
          type: 'bar',
          data: []
        },
        {
          name: '净入库量',
          type: 'line',
          data: []
        }
      ]
    }
    
    valueChartInstance.setOption(pieOption)
    quantityChartInstance.setOption(pieOption)
    statusChartInstance.setOption(pieOption)
    trendChartInstance.setOption(trendOption)
  })
}

const loadData = async () => {
  try {
    const response = await reportApi.inventorySummary()
    const data = response.data || {}
    
    // 计算汇总信息
    summary.totalTypes = data.totalTypes || 0
    summary.totalQuantity = data.totalQuantity || 0
    summary.totalValue = data.totalValue || 0
    summary.lowStockCount = data.lowStockCount || 0
    
    // 这里应该调用分页查询接口获取表格数据
    // const tableResponse = await reportApi.inventoryDetail(queryParams)
    // tableData.value = tableResponse.data?.list || []
    // total.value = tableResponse.data?.total || 0
    
    // 暂时使用模拟数据
    tableData.value = [
      {
        partCode: 'P001',
        partName: '轴承',
        categoryName: '机械',
        model: '6204-2RS',
        unit: '个',
        quantity: 150,
        minQuantity: 50,
        maxQuantity: 1000,
        unitPrice: 35.5,
        totalValue: 5325,
        location: 'A区-01',
        supplierName: 'SKF'
      },
      {
        partCode: 'P002',
        partName: '密封圈',
        categoryName: '密封',
        model: '25x40x7',
        unit: '个',
        quantity: 8,
        minQuantity: 20,
        maxQuantity: 500,
        unitPrice: 2.8,
        totalValue: 22.4,
        location: 'B区-05',
        supplierName: 'Parker'
      }
    ]
    
    // 更新图表
    updateCharts()
  } catch (error) {
    console.error('加载库存数据失败:', error)
  }
}

const updateCharts = () => {
  if (!valueChartInstance || !quantityChartInstance || !statusChartInstance || !trendChartInstance) return
  
  // 库存价值分布数据
  const valueData = [
    { value: 45, name: '机械设备备件' },
    { value: 20, name: '电气设备备件' },
    { value: 15, name: '办公设备备件' },
    { value: 12, name: '仪器仪表件' },
    { value: 8, name: '其他备件' }
  ]
  
  // 库存数量分布数据
  const quantityData = [
    { value: 500, name: '机械设备备件' },
    { value: 300, name: '电气设备备件' },
    { value: 150, name: '办公设备备件' },
    { value: 80, name: '仪器仪表件' },
    { value: 50, name: '其他备件' }
  ]
  
  // 库存状态分布数据
  const statusData = [
    { value: 80, name: '正常' },
    { value: 15, name: '预警' },
    { value: 5, name: '缺货' }
  ]
  
  // 出入库趋势数据
  const periods = ['01月', '02月', '03月', '04月', '05月', '06月']
  const inboundData = [120, 132, 101, 134, 90, 160]
  const outboundData = [80, 92, 110, 100, 105, 140]
  const netData = [40, 40, -9, 34, -15, 20]
  
  // 价值分布图
  const valueOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c}万 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '价值分布',
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
  
  // 数量分布图
  const quantityOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}件 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '数量分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        data: quantityData,
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
  
  // 状态分布图
  const statusOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}种 ({d}%)'
    },
    series: [
      {
        name: '状态分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '50%'],
        data: statusData,
        itemStyle: {
          color: (params: any) => {
            const colors = ['#52c41a', '#faad14', '#ff4d4f']
            return colors[params.dataIndex] || '#ccc'
          }
        },
        label: {
          formatter: '{b}: {c} ({d}%)'
        }
      }
    ]
  }
  
  // 趋势图
  const trendOption = {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        let result = params[0].axisValue + '<br/>'
        params.forEach((item: any) => {
          result += `${item.marker} ${item.seriesName}: ${item.value}件<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['入库数量', '出库数量', '净入库量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: periods
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '入库数量',
        type: 'bar',
        data: inboundData,
        itemStyle: { color: '#52c41a' }
      },
      {
        name: '出库数量',
        type: 'bar',
        data: outboundData,
        itemStyle: { color: '#faad14' }
      },
      {
        name: '净入库量',
        type: 'line',
        data: netData,
        itemStyle: { color: '#1890ff' },
        lineStyle: { width: 3 },
        symbol: 'circle',
        symbolSize: 8
      }
    ]
  }
  
  valueChartInstance.setOption(valueOption, true)
  quantityChartInstance.setOption(quantityOption, true)
  statusChartInstance.setOption(statusOption, true)
  trendChartInstance.setOption(trendOption, true)
  
  // 添加交互事件
  valueChartInstance.off('click')
  valueChartInstance.on('click', (params) => {
    console.log('点击价值分布:', params)
    ElMessage.info(`查看 ${params.name} 的详细价值信息`)
  })
  
  quantityChartInstance.off('click')
  quantityChartInstance.on('click', (params) => {
    console.log('点击数量分布:', params)
    ElMessage.info(`查看 ${params.name} 的库存详情`)
  })
  
  statusChartInstance.off('click')
  statusChartInstance.on('click', (params) => {
    console.log('点击状态分布:', params)
    ElMessage.info(`查看 ${params.name} 状态的备件列表`)
  })
  
  trendChartInstance.off('click')
  trendChartInstance.on('click', (params) => {
    console.log('点击趋势图:', params)
    ElMessage.info(`查看 ${params.name} 的出入库详情`)
  })
}

const exportData = () => {
  if (!tableData.value || tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  try {
    ExcelExporter.exportInventoryAnalysis(tableData.value)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  }
}

const showLowStockWarning = () => {
  // 过滤显示库存预警数据
  const warningItems = tableData.value.filter(item => item.quantity <= item.minQuantity)
  console.log('库存预警物品:', warningItems)
  // 这里可以实现弹出对话框显示详细预警信息
}

const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  loadData()
}

onMounted(() => {
  loadCategories()
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
  color: white;
}

.stat-title {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}

.warning-text {
  color: #ff4d4f;
  font-weight: bold;
}
</style>