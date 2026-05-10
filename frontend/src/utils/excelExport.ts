import { saveAs } from 'file-saver'
import * as XLSX from 'xlsx'

/**
 * Excel导出工具类
 */
export class ExcelExporter {
  /**
   * 导出Excel文件
   * @param data 表格数据
   * @param filename 文件名（不含扩展名）
   * @param columns 列配置
   */
  static exportExcel(data: any[], filename: string, columns: { label: string; prop: string; formatter?: (value: any, row: any) => any }[] = []) {
    if (!data || data.length === 0) {
      console.warn('导出数据为空')
      return
    }

    // 创建工作簿
    const wb = XLSX.utils.book_new()
    
    // 准备数据
    const exportData = []
    
    // 添加表头
    const headers = columns.map(col => col.label)
    exportData.push(headers)
    
    // 添加数据行
    data.forEach((item) => {
      const row: any[] = []
      columns.forEach(col => {
        let value = item[col.prop]
        if (col.formatter) {
          value = col.formatter(value, item)
        }
        row.push(value)
      })
      exportData.push(row)
    })
    
    // 创建工作表
    const ws = XLSX.utils.aoa_to_sheet(exportData)
    
    // 添加到工作簿
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
    
    // 写入文件
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([wbout], { type: 'application/octet-stream' })
    
    // 保存文件
    saveAs(blob, `${filename}_${new Date().toISOString().slice(0, 10)}.xlsx`)
  }
  
  /**
   * 导出资产汇总统计报表
   */
  static exportAssetSummary(data: any[]) {
    const columns = [
      { label: '部门/分类', prop: 'key' },
      { label: '资产数量', prop: 'assetCount' },
      { label: '资产总值', prop: 'totalValue', formatter: (value) => `¥${value?.toLocaleString()}` }
    ]
    
    this.exportExcel(data, '资产汇总统计', columns)
  }
  
  /**
   * 导出维护成本统计报表
   */
  static exportMaintenanceCost(data: any[]) {
    const columns = [
      { label: '期间', prop: 'period' },
      { label: '预防性维护成本', prop: 'preventiveCost', formatter: (value) => `¥${value?.toLocaleString()}` },
      { label: '纠正性维护成本', prop: 'correctiveCost', formatter: (value) => `¥${value?.toLocaleString()}` },
      { label: '总成本', prop: 'totalCost', formatter: (value) => `¥${value?.toLocaleString()}` },
      { label: '维护次数', prop: 'maintenanceCount' }
    ]
    
    this.exportExcel(data, '维护成本统计', columns)
  }
  
  /**
   * 导出库存分析报表
   */
  static exportInventoryAnalysis(data: any[]) {
    const columns = [
      { label: '备件编码', prop: 'partCode' },
      { label: '备件名称', prop: 'partName' },
      { label: '分类', prop: 'categoryName' },
      { label: '规格型号', prop: 'model' },
      { label: '单位', prop: 'unit' },
      { label: '当前库存', prop: 'quantity' },
      { label: '最低库存', prop: 'minQuantity' },
      { label: '最高库存', prop: 'maxQuantity' },
      { label: '参考单价', prop: 'unitPrice', formatter: (value) => `¥${value?.toLocaleString()}` },
      { label: '总价值', prop: 'totalValue', formatter: (value) => `¥${value?.toLocaleString()}` },
      { label: '存放位置', prop: 'location' },
      { label: '供应商', prop: 'supplierName' }
    ]
    
    this.exportExcel(data, '库存分析报表', columns)
  }
  
  /**
   * 导出工单数据
   */
  static exportWorkOrder(data: any[]) {
    const columns = [
      { label: '工单编号', prop: 'orderNo' },
      { label: '工单标题', prop: 'title' },
      { label: '类型', prop: 'orderType', formatter: (value) => {
        const map: Record<string, string> = { 'REPAIR': '维修', 'MAINTENANCE': '保养', 'INSPECTION': '巡检' }
        return map[value] || value
      }},
      { label: '优先级', prop: 'priority', formatter: (value) => {
        const map: Record<string, string> = { 'HIGH': '高', 'MEDIUM': '中', 'LOW': '低' }
        return map[value] || value
      }},
      { label: '状态', prop: 'status', formatter: (value) => {
        const map: Record<string, string> = {
          'PENDING': '待处理',
          'ASSIGNED': '已指派', 
          'PROCESSING': '处理中',
          'COMPLETED': '已完成',
          'CLOSED': '已关闭'
        }
        return map[value] || value
      }},
      { label: '报修人', prop: 'reporter' },
      { label: '指派给', prop: 'assignedTo' },
      { label: '报修时间', prop: 'reportTime' },
      { label: '创建时间', prop: 'createTime' },
      { label: '关联资产ID', prop: 'assetId' }
    ]
    
    this.exportExcel(data, '工单列表', columns)
  }

  /**
   * 导出简单表格数据
   */
  static exportSimpleTable(data: any[], filename: string, headers?: string[]) {
    if (!headers) {
      if (data.length > 0) {
        headers = Object.keys(data[0])
      } else {
        headers = []
      }
    }
    
    const columns = headers.map(header => ({
      label: header,
      prop: header
    }))
    
    this.exportExcel(data, filename, columns)
  }
  
  /**
   * 导出多个工作表
   */
  static exportMultipleSheets(sheets: { name: string; data: any[]; columns: any[] }[], filename: string) {
    if (!sheets || sheets.length === 0) return
    
    const wb = XLSX.utils.book_new()
    
    sheets.forEach((sheet, index) => {
      if (!sheet.data || sheet.data.length === 0) return
      
      // 准备数据
      const exportData = []
      
      // 添加表头
      const headers = sheet.columns.map(col => col.label)
      exportData.push(headers)
      
      // 添加数据行
      sheet.data.forEach((item) => {
        const row: any[] = []
        sheet.columns.forEach(col => {
          let value = item[col.prop]
          if (col.formatter) {
            value = col.formatter(value, item)
          }
          row.push(value)
        })
        exportData.push(row)
      })
      
      // 创建工作表
      const ws = XLSX.utils.aoa_to_sheet(exportData)
      
      // 设置列宽（自动调整宽度）
      const colWidths = sheet.columns.map(() => ({ width: 15 }))
      ws['!cols'] = colWidths
      
      // 添加到工作簿
      const sheetName = sheet.name || `Sheet${index + 1}`
      XLSX.utils.book_append_sheet(wb, ws, sheetName)
    })
    
    // 写入文件
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([wbout], { type: 'application/octet-stream' })
    
    // 保存文件
    saveAs(blob, `${filename}_${new Date().toISOString().slice(0, 10)}.xlsx`)
  }
}

/**
 * CSV导出工具函数（备用）
 */
export const exportCSV = (data: any[], filename: string) => {
  if (!data || data.length === 0) {
    console.warn('导出数据为空')
    return
  }
  
  const headers = Object.keys(data[0])
  const csvContent = [
    headers.join(','),
    ...data.map(row => headers.map(fieldName => JSON.stringify(row[fieldName] || '')).join(','))
  ].join('\n')
  
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  saveAs(blob, `${filename}.csv`)
}