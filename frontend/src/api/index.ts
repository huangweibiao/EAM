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

export default service
