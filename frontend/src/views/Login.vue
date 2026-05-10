<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>EAM 企业资产管理系统</h2>
          <p>Enterprise Asset Management System</p>
        </div>
        <div class="login-type-tabs">
          <el-radio-group v-model="loginType" @change="handleLoginTypeChange">
            <el-radio-button label="oauth2">OAuth2登录</el-radio-button>
            <el-radio-button label="local">本地登录</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <!-- OAuth2登录表单 -->
      <div v-if="loginType === 'oauth2'">
        <el-form :model="form" label-position="top">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleOAuth2Login" style="width: 100%">
              OAuth2 单点登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <p>点击上方按钮通过 OAuth2 进行单点登录</p>
        </div>
      </div>

      <!-- 本地账号密码登录表单 -->
      <div v-if="loginType === 'local'">
        <el-form :model="localForm" :rules="localRules" ref="localFormRef" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="localForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="localForm.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLocalLogin" :loading="loading" style="width: 100%">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <p>使用本地账号密码进行登录</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { authApi } from '@/api'

const router = useRouter()
const localFormRef = ref<FormInstance>()

// 登录类型切换
const loginType = ref<'oauth2' | 'local'>('oauth2')

// OAuth2表单数据
const form = reactive({
  username: '',
  password: ''
})

// 本地登录表单数据
const localForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const localRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const loading = ref(false)

// 登录类型切换处理
const handleLoginTypeChange = () => {
  if (localFormRef.value) {
    localFormRef.value.clearValidate()
  }
}

// OAuth2登录
const handleOAuth2Login = () => {
  // Redirect to OAuth2 login endpoint
  window.location.href = '/eam/oauth2/authorization/oauth2-server'
}

// 本地账号密码登录
const handleLocalLogin = async () => {
  if (!localFormRef.value) return
  
  try {
    await localFormRef.value.validate()
    loading.value = true
    
    const response = await authApi.login({
      username: localForm.username,
      password: localForm.password
    })
    
    if (response.code === 200) {
      // 保存token到localStorage
      localStorage.setItem('access_token', response.data.accessToken)
      
      ElMessage.success('登录成功')
      
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } finally {
    loading.value = false
  }
}

// Check if there's an error parameter in URL
const urlParams = new URLSearchParams(window.location.search)
const error = urlParams.get('error')
if (error) {
  console.error('Login error:', error)
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  width: 450px;
  max-width: 90vw;
  box-sizing: border-box;
}

.card-header {
  text-align: center;
  margin-bottom: 15px;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 22px;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-type-tabs {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.el-radio-group) {
  width: 100%;
}

:deep(.el-radio-button) {
  flex: 1;
}

:deep(.el-radio-button__inner) {
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .login-card {
    width: 100%;
    max-width: 100%;
    margin: 0;
  }
  
  .card-header h2 {
    font-size: 20px;
    margin-bottom: 8px;
  }
  
  .card-header p {
    font-size: 13px;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 16px;
  }
  
  :deep(.el-button) {
    width: 100%;
  }
  
  .login-type-tabs {
    margin-top: 15px;
  }
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.login-footer p {
  color: #999;
  font-size: 12px;
}
</style>
