import axios from 'axios'
import type { AxiosInstance, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import type { UserInfo } from '@/stores/user'

// Create axios instance
const service: AxiosInstance = axios.create({
  baseURL: '/eam',
  timeout: 10000
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        ElMessage.error('未授权，请重新登录')
        localStorage.removeItem('access_token')
        window.location.href = '/eam/login'
      } else if (status === 403) {
        ElMessage.error('拒绝访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器错误')
      } else {
        ElMessage.error('请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// API interfaces
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface LoginData {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  userInfo: UserInfo
}

// Auth API
export const authApi = {
  getCurrentUser: () => service.get<any, ApiResponse<UserInfo>>('/api/auth/user'),
  checkAuth: () => service.get<any, ApiResponse<boolean>>('/api/auth/check'),
  getOidcUser: () => service.get<any, ApiResponse<any>>('/api/auth/oidc-user')
}

// Hello API
export const helloApi = {
  hello: () => service.get<any, ApiResponse<string>>('/api/hello'),
  whoami: () => service.get<any, ApiResponse<string>>('/api/whoami')
}

// System User API
export const userApi = {
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/system/user/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/system/user/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/system/user/${id}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/system/user/add', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/system/user/update', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/system/user/${id}`),
  resetPassword: (id: number) => service.post<any, ApiResponse<any>>(`/api/system/user/resetPassword/${id}`)
}

// System Role API
export const roleApi = {
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/system/role/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/system/role/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/system/role/${id}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/system/role/add', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/system/role/update', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/system/role/${id}`),
  assignPerms: (roleId: number, permIds: number[]) => service.put<any, ApiResponse<any>>('/api/system/role/perm', null, { params: { roleId } })
}

// System Department API
export const deptApi = {
  tree: () => service.get<any, ApiResponse<any>>('/api/system/dept/tree'),
  list: () => service.get<any, ApiResponse<any[]>>('/api/system/dept/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/system/dept/${id}`),
  children: (parentId: number) => service.get<any, ApiResponse<any[]>>(`/api/system/dept/children/${parentId}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/system/dept/add', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/system/dept/update', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/system/dept/${id}`)
}

// System Permission/Menu API
export const permissionApi = {
  tree: () => service.get<any, ApiResponse<any>>('/api/system/permission/tree'),
  userMenus: (userId: number) => service.get<any, ApiResponse<any>>(`/api/system/permission/user/${userId}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/system/permission', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/system/permission', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/system/permission/${id}`)
}

// Asset API
export const assetApi = {
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/asset/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/asset/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/asset/${id}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/asset/add', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/asset/update', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/asset/${id}`),
  change: (params: any) => service.post<any, ApiResponse<any>>('/api/asset/change', null, { params }),
  transfer: (params: any) => service.post<any, ApiResponse<any>>('/api/asset/transfer', null, { params })
}

// Asset Category API
export const categoryApi = {
  tree: () => service.get<any, ApiResponse<any>>('/api/asset/category/tree'),
  list: () => service.get<any, ApiResponse<any[]>>('/api/asset/category/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/asset/category/${id}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/asset/category/add', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/asset/category/update', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/asset/category/${id}`)
}

// Work Order API
export const workOrderApi = {
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/workorder/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/workorder/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/workorder/${id}`),
  create: (data: any) => service.post<any, ApiResponse<any>>('/api/workorder/create', data),
  assign: (id: number, assignedTo: string) => service.post<any, ApiResponse<any>>('/api/workorder/assign', null, { params: { id, assignedTo } }),
  process: (id: number) => service.post<any, ApiResponse<any>>('/api/workorder/process', null, { params: { id } }),
  complete: (id: number, solution: string) => service.post<any, ApiResponse<any>>('/api/workorder/complete', null, { params: { id, solution } }),
  close: (id: number) => service.post<any, ApiResponse<any>>('/api/workorder/close', null, { params: { id } }),
  rate: (id: number, rating: number) => service.post<any, ApiResponse<any>>('/api/workorder/rate', null, { params: { id, rating } })
}

// Asset Transfer API
export const transferApi = {
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/asset/transfer/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/asset/transfer/list'),
  pending: () => service.get<any, ApiResponse<any[]>>('/api/asset/transfer/pending'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/asset/transfer/${id}`),
  create: (data: any) => service.post<any, ApiResponse<any>>('/api/asset/transfer/create', data),
  approve: (id: number, approver: string, approved: boolean) => service.post<any, ApiResponse<any>>('/api/asset/transfer/approve', null, { params: { id, approver, approved } }),
  complete: (id: number) => service.post<any, ApiResponse<any>>('/api/asset/transfer/complete', null, { params: { id } })
}

// Asset Inventory API
export const inventoryApi = {
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/asset/inventory/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/asset/inventory/list'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/asset/inventory/${id}`),
  details: (id: number) => service.get<any, ApiResponse<any[]>>(`/api/asset/inventory/${id}/details`),
  create: (data: any) => service.post<any, ApiResponse<any>>('/api/asset/inventory/create', data),
  complete: (id: number) => service.post<any, ApiResponse<any>>('/api/asset/inventory/complete', null, { params: { id } }),
  addDetail: (data: any) => service.post<any, ApiResponse<any>>('/api/asset/inventory/detail', data),
  updateDetail: (data: any) => service.put<any, ApiResponse<any>>('/api/asset/inventory/detail', data)
}

// Maintenance API
export const maintenanceApi = {
  // Plan
  planPage: (params: any) => service.get<any, ApiResponse<any>>('/api/maintenance/plan/page', { params }),
  planList: () => service.get<any, ApiResponse<any[]>>('/api/maintenance/plan/list'),
  planGetById: (id: number) => service.get<any, ApiResponse<any>>(`/api/maintenance/plan/${id}`),
  planAdd: (data: any) => service.post<any, ApiResponse<any>>('/api/maintenance/plan/add', data),
  planUpdate: (data: any) => service.put<any, ApiResponse<any>>('/api/maintenance/plan/update', data),
  planDelete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/maintenance/plan/${id}`),
  planPending: () => service.get<any, ApiResponse<any[]>>('/api/maintenance/plan/pending'),
  planRemind: (days: number) => service.get<any, ApiResponse<any>>('/api/maintenance/plan/remind', { params: { days } }),
  // Record
  recordPage: (params: any) => service.get<any, ApiResponse<any>>('/api/maintenance/record/page', { params }),
  recordList: () => service.get<any, ApiResponse<any[]>>('/api/maintenance/record/list'),
  recordGetById: (id: number) => service.get<any, ApiResponse<any>>(`/api/maintenance/record/${id}`),
  recordAdd: (data: any) => service.post<any, ApiResponse<any>>('/api/maintenance/record/add', data),
  recordUpdate: (data: any) => service.put<any, ApiResponse<any>>('/api/maintenance/record/update', data),
  recordDelete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/maintenance/record/${id}`),
  recordByAsset: (assetId: number) => service.get<any, ApiResponse<any>>(`/api/maintenance/record/asset/${assetId}`),
  costStatistics: (startDate: string, endDate: string) => service.get<any, ApiResponse<any>>('/api/maintenance/record/cost', { params: { startDate, endDate } }),
  totalCost: () => service.get<any, ApiResponse<any>>('/api/maintenance/record/totalCost')
}

// Spare Part API
export const spareApi = {
  // Part
  page: (params: any) => service.get<any, ApiResponse<any>>('/api/part/page', { params }),
  list: () => service.get<any, ApiResponse<any[]>>('/api/part/list'),
  warn: () => service.get<any, ApiResponse<any[]>>('/api/part/warn'),
  getById: (id: number) => service.get<any, ApiResponse<any>>(`/api/part/${id}`),
  add: (data: any) => service.post<any, ApiResponse<any>>('/api/part/add', data),
  update: (data: any) => service.put<any, ApiResponse<any>>('/api/part/update', data),
  delete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/part/${id}`),
  // Inbound
  inboundPage: (params: any) => service.get<any, ApiResponse<any>>('/api/part/inbound/page', { params }),
  inbound: (data: any) => service.post<any, ApiResponse<any>>('/api/part/inbound', data),
  // Outbound
  outboundPage: (params: any) => service.get<any, ApiResponse<any>>('/api/part/outbound/page', { params }),
  outbound: (data: any) => service.post<any, ApiResponse<any>>('/api/part/outbound', data)
}

// Purchase API
export const purchaseApi = {
  // Request
  requestPage: (params: any) => service.get<any, ApiResponse<any>>('/api/purchase/request/page', { params }),
  requestList: () => service.get<any, ApiResponse<any[]>>('/api/purchase/request/list'),
  requestAdd: (data: any) => service.post<any, ApiResponse<any>>('/api/purchase/request/add', data),
  requestApprove: (id: number, approver: string, approved: boolean, remark: string) => service.post<any, ApiResponse<any>>('/api/purchase/request/approve', null, { params: { id, approver, approved, remark } }),
  requestDelete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/purchase/request/${id}`),
  // Order
  orderPage: (params: any) => service.get<any, ApiResponse<any>>('/api/purchase/order/page', { params }),
  orderList: () => service.get<any, ApiResponse<any[]>>('/api/purchase/order/list'),
  orderAdd: (data: any) => service.post<any, ApiResponse<any>>('/api/purchase/order/add', data),
  orderReceive: (id: number) => service.post<any, ApiResponse<any>>('/api/purchase/order/receive', null, { params: { id } }),
  orderDelete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/purchase/order/${id}`),
  // Supplier
  supplierList: () => service.get<any, ApiResponse<any[]>>('/api/purchase/supplier/list'),
  supplierAdd: (data: any) => service.post<any, ApiResponse<any>>('/api/purchase/supplier/add', data),
  supplierUpdate: (data: any) => service.put<any, ApiResponse<any>>('/api/purchase/supplier/update', data),
  supplierDelete: (id: number) => service.delete<any, ApiResponse<any>>(`/api/purchase/supplier/${id}`)
}

// Report API
export const reportApi = {
  assetSummaryByDept: () => service.get<any, ApiResponse<any>>('/api/report/asset-summary/dept'),
  assetSummaryByCategory: () => service.get<any, ApiResponse<any>>('/api/report/asset-summary/category'),
  maintenanceCost: () => service.get<any, ApiResponse<any>>('/api/report/maintenance-cost'),
  inventorySummary: () => service.get<any, ApiResponse<any>>('/api/report/inventory-summary'),
  inboundOutbound: () => service.get<any, ApiResponse<any>>('/api/report/inbound-outbound')
}

export default service
