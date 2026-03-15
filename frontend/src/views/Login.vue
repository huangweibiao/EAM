<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>EAM 企业资产管理系统</h2>
          <p>Enterprise Asset Management System</p>
        </div>
      </template>
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
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const handleOAuth2Login = () => {
  // Redirect to OAuth2 login endpoint
  window.location.href = '/eam/oauth2/authorization/oauth2-server'
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
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  max-width: 90%;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.card-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
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
