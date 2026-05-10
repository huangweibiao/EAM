<template>
  <div class="log-page">
    <el-card>
      <template #header>
        <div class="header">
          <h2>操作日志管理</h2>
          <el-button type="danger" @click="clearLogs" :disabled="tableData.length === 0">
            <el-icon><Delete /></el-icon>
            清空日志
          </el-button>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="queryParams" inline class="query-form">
        <el-form-item label="操作人：">
          <el-input
            v-model="queryParams.operator"
            placeholder="请输入操作人姓名"
            clearable
            style="width: 140px"
          />
        </el-form-item>
        <el-form-item label="操作类型：">
          <el-select v-model="queryParams.operationType" placeholder="全部类型" clearable>
            <el-option label="新增" value="CREATE"></el-option>
            <el-option label="修改" value="UPDATE"></el-option>
            <el-option label="删除" value="DELETE"></el-option>
            <el-option label="查询" value="QUERY"></el-option>
            <el-option label="登录" value="LOGIN"></el-option>
            <el-option label="登出" value="LOGOUT"></el-option>
            <el-option label="导出" value="EXPORT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="操作模块：">
          <el-select v-model="queryParams.module" placeholder="全部模块" clearable>
            <el-option label="用户管理" value="USER"></el-option>
            <el-option label="角色管理" value="ROLE"></el-option>
            <el-option label="资产管理" value="ASSET"></el-option>
            <el-option label="维护管理" value="MAINTENANCE"></el-option>
            <el-option label="工单管理" value="WORKORDER"></el-option>
            <el-option label="库存管理" value="INVENTORY"></el-option>
            <el-option label="系统设置" value="SYSTEM"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间：">
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleTimeChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button @click="exportLogs" :disabled="tableData.length === 0">
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <el-button-group>
          <el-button :type="timeFilter === 'today' ? 'primary' : ''" @click="filterToday">
            今日
          </el-button>
          <el-button :type="timeFilter === 'week' ? 'primary' : ''" @click="filterThisWeek">
            本周
          </el-button>
          <el-button :type="timeFilter === 'month' ? 'primary' : ''" @click="filterThisMonth">
            本月
          </el-button>
        </el-button-group>
        <div class="statistics">
          <el-tag type="info">总数：{{ total }}</el-tag>
          <el-tag type="success">新增：{{ statistics.CREATE || 0 }}</el-tag>
          <el-tag type="warning">修改：{{ statistics.UPDATE || 0 }}</el-tag>
          <el-tag type="danger">删除：{{ statistics.DELETE || 0 }}</el-tag>
        </div>
      </div>

      <!-- 日志列表 -->
      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
        @sort-change="handleSortChange"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column 
          prop="operationTime" 
          label="操作时间" 
          width="160" 
          sortable="custom"
        >
          <template #default="{ row }">
            {{ row.operationTime }}
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)" size="small">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="200" />
        <el-table-column prop="ipAddress" label="IP地址" width="130" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="operationSystem" label="操作系统" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'" size="small">
              {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button link type="danger" @click="deleteLog(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailVisible" title="操作日志详情" width="600px">
      <el-descriptions title="基本信息" :column="2" border>
        <el-descriptions-item label="操作时间" :span="2">
          {{ currentLog.operationTime }}
        </el-descriptions-item>
        <el-descriptions-item label="操作人">
          {{ currentLog.operator }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(currentLog.operationType)">
            {{ getOperationTypeText(currentLog.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作模块">
          {{ currentLog.module }}
        </el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="currentLog.status === 'SUCCESS' ? 'success' : 'danger'">
            {{ currentLog.status === 'SUCCESS' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">
          {{ currentLog.ipAddress }}
        </el-descriptions-item>
        <el-descriptions-item label="浏览器">
          {{ currentLog.browser }}
        </el-descriptions-item>
        <el-descriptions-item label="操作系统">
          {{ currentLog.operationSystem }}
        </el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="操作详情" :column="1" border style="margin-top: 20px">
        <el-descriptions-item label="操作描述">
          {{ currentLog.description }}
        </el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre style="background: #f5f5f5; padding: 10px; border-radius: 4px; max-height: 200px; overflow: auto;">{{ currentLog.requestParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果">
          <pre style="background: #f5f5f5; padding: 10px; border-radius: 4px; max-height: 200px; overflow: auto;">{{ currentLog.responseResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" v-if="currentLog.status === 'FAILED'">
          <pre style="background: #fff2f0; padding: 10px; border-radius: 4px; max-height: 200px; overflow: auto; color: #ff4d4f;">{{ currentLog.errorMessage }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="执行耗时">
          {{ currentLog.executionTime }} ms
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download, Delete, View } from '@element-plus/icons-vue'
import { logApi } from '@/api'
import { ExcelExporter } from '@/utils/excelExport'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentLog = ref({} as any)
const timeRange = ref([])
const timeFilter = ref('')

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operator: '',
  operationType: '',
  module: '',
  startTime: '',
  endTime: '',
  sortField: 'operationTime',
  sortOrder: 'desc'
})

const statistics = computed(() => {
  const stats: any = {}
  tableData.value.forEach(item => {
    stats[item.operationType] = (stats[item.operationType] || 0) + 1
  })
  return stats
})

// 获取操作类型标签样式
const getOperationTypeTag = (type: string) => {
  const typeMap: any = {
    'CREATE': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'QUERY': 'info',
    'LOGIN': 'primary',
    'LOGOUT': '',
    'EXPORT': 'info'
  }
  return typeMap[type] || 'info'
}

// 获取操作类型文本
const getOperationTypeText = (type: string) => {
  const typeMap: any = {
    'CREATE': '新增',
    'UPDATE': '修改',
    'DELETE': '删除',
    'QUERY': '查询',
    'LOGIN': '登录',
    'LOGOUT': '登出',
    'EXPORT': '导出'
  }
  return typeMap[type] || type
}

// 处理时间范围变化
const handleTimeChange = (value: any) => {
  if (value && value.length === 2) {
    queryParams.startTime = value[0]
    queryParams.endTime = value[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
  timeFilter.value = ''
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 模拟数据，实际项目中调用API接口
    // const response = await logApi.getLogList(queryParams)
    // tableData.value = response.data.list
    // total.value = response.data.total
    
    // 模拟数据
    tableData.value = [
      {
        id: 1,
        operationTime: '2024-01-15 10:30:25',
        operator: 'admin',
        module: 'USER',
        operationType: 'CREATE',
        description: '新增用户 test001',
        ipAddress: '192.168.1.100',
        browser: 'Chrome',
        operationSystem: 'Windows 10',
        status: 'SUCCESS',
        requestParams: JSON.stringify({ username: 'test001', realName: '测试用户' }, null, 2),
        responseResult: JSON.stringify({ code: 200, message: '操作成功', data: { id: 1001 } }, null, 2),
        executionTime: 150
      },
      {
        id: 2,
        operationTime: '2024-01-15 09:15:30',
        operator: 'user1',
        module: 'ASSET',
        operationType: 'UPDATE',
        description: '更新设备信息：CNC001',
        ipAddress: '192.168.1.101',
        browser: 'Firefox',
        operationSystem: 'Mac OS',
        status: 'SUCCESS',
        requestParams: JSON.stringify({ id: 2001, name: 'CNC机床001' }, null, 2),
        responseResult: JSON.stringify({ code: 200, message: '更新成功' }, null, 2),
        executionTime: 89
      }
    ]
    total.value = 2
  } catch (error) {
    console.error('加载日志数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 重置查询条件
const resetQuery = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    operator: '',
    operationType: '',
    module: '',
    startTime: '',
    endTime: ''
  })
  timeRange.value = []
  timeFilter.value = ''
  loadData()
}

// 快捷时间筛选
const filterToday = () => {
  const today = new Date().toISOString().split('T')[0]
  queryParams.startTime = today
  queryParams.endTime = today
  timeRange.value = [today, today]
  timeFilter.value = 'today'
  loadData()
}

const filterThisWeek = () => {
  const today = new Date()
  const startOfWeek = new Date(today.setDate(today.getDate() - today.getDay()))
  const endOfWeek = new Date(today.setDate(today.getDate() + (6 - today.getDay())))
  
  queryParams.startTime = startOfWeek.toISOString().split('T')[0]
  queryParams.endTime = endOfWeek.toISOString().split('T')[0]
  timeRange.value = [queryParams.startTime, queryParams.endTime]
  timeFilter.value = 'week'
  loadData()
}

const filterThisMonth = () => {
  const today = new Date()
  const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1)
  const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0)
  
  queryParams.startTime = startOfMonth.toISOString().split('T')[0]
  queryParams.endTime = endOfMonth.toISOString().split('T')[0]
  timeRange.value = [queryParams.startTime, queryParams.endTime]
  timeFilter.value = 'month'
  loadData()
}

// 查看详情
const viewDetail = (row: any) => {
  currentLog.value = { ...row }
  detailVisible.value = true
}

// 删除日志
const deleteLog = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除 ${row.operator} 在 ${row.operationTime} 的操作日志吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用删除接口
    // await logApi.deleteLog(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 取消删除
  }
}

// 清空日志
const clearLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有操作日志吗？此操作不可恢复！',
      '确认清空',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用清空接口
    // await logApi.clearLogs()
    ElMessage.success('清空成功')
    loadData()
  } catch (error) {
    // 取消清空
  }
}

// 导出日志
const exportLogs = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  try {
    const columns = [
      { label: '操作时间', prop: 'operationTime' },
      { label: '操作人', prop: 'operator' },
      { label: '操作模块', prop: 'module' },
      { label: '操作类型', prop: 'operationType', formatter: (value: any) => getOperationTypeText(value) },
      { label: '操作描述', prop: 'description' },
      { label: 'IP地址', prop: 'ipAddress' },
      { label: '浏览器', prop: 'browser' },
      { label: '操作系统', prop: 'operationSystem' },
      { label: '状态', prop: 'status', formatter: (value: any) => value === 'SUCCESS' ? '成功' : '失败' },
      { label: '执行耗时(ms)', prop: 'executionTime' }
    ]
    
    ExcelExporter.exportMultipleSheets([
      {
        name: '操作日志',
        data: tableData.value,
        columns: columns
      },
      {
        name: '统计信息',
        data: [statistics.value],
        columns: [
          { label: '新增', prop: 'CREATE' },
          { label: '修改', prop: 'UPDATE' },
          { label: '删除', prop: 'DELETE' },
          { label: '查询', prop: 'QUERY' },
          { label: '登录', prop: 'LOGIN' },
          { label: '登出', prop: 'LOGOUT' },
          { label: '导出', prop: 'EXPORT' }
        ]
      }
    ], '操作日志报表')
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 排序处理
const handleSortChange = (sort: any) => {
  if (sort.prop) {
    queryParams.sortField = sort.prop
    queryParams.sortOrder = sort.order === 'ascending' ? 'asc' : 'desc'
    loadData()
  }
}

// 分页处理
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
  loadData()
})
</script>

<style scoped>
.log-page {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 20px;
}

.quick-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.statistics {
  display: flex;
  gap: 10px;
}

.statistics .el-tag {
  font-size: 12px;
}

pre {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
}
</style>