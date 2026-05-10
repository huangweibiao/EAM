<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#409eff"><Box /></el-icon>
            <div class="stat-content">
              <div class="stat-value">128</div>
              <div class="stat-label">资产总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#67c23a"><CircleCheck /></el-icon>
            <div class="stat-content">
              <div class="stat-value">98</div>
              <div class="stat-label">正常使用</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#e6a23c"><Tools /></el-icon>
            <div class="stat-content">
              <div class="stat-value">12</div>
              <div class="stat-label">维修中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" color="#f56c6c"><CircleClose /></el-icon>
            <div class="stat-content">
              <div class="stat-value">5</div>
              <div class="stat-label">报废资产</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>欢迎回来</span>
            </div>
          </template>
          <div class="welcome-content">
            <el-icon :size="60" color="#409eff"><User /></el-icon>
            <h3>{{ userStore.userInfo?.nickname || '用户' }}</h3>
            <p>欢迎来到企业资产管理系统</p>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="用户名">{{ userStore.userInfo?.username }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ userStore.userInfo?.email }}</el-descriptions-item>
              <el-descriptions-item label="状态">{{ userStore.userInfo?.status === 1 ? '正常' : '禁用' }}</el-descriptions-item>
              <el-descriptions-item label="登录时间">{{ new Date().toLocaleString() }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/asset/list')">
              <el-icon><List /></el-icon>
              资产台账
            </el-button>
            <el-button type="success" @click="$router.push('/asset/category')">
              <el-icon><FolderOpened /></el-icon>
              资产分类
            </el-button>
            <el-button type="warning" @click="$router.push('/system/user')">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button type="info" @click="$router.push('/system/dept')">
              <el-icon><OfficeBuilding /></el-icon>
              部门管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  // TODO: Load dashboard data
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  box-sizing: border-box;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  font-size: 40px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.card-header {
  font-size: 16px;
  font-weight: 500;
}

.welcome-content {
  text-align: center;
  padding: 15px;
}

.welcome-content h3 {
  margin: 15px 0 8px 0;
  color: #303133;
  font-size: 18px;
}

.welcome-content p {
  color: #909399;
  margin-bottom: 15px;
  font-size: 14px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-actions .el-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  gap: 8px;
  font-size: 13px;
}

:deep(.el-descriptions) {
  font-size: 13px;
}

:deep(.el-descriptions .el-descriptions__label) {
  width: 80px;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .dashboard {
    padding: 15px 10px;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }
  
  .stat-icon {
    font-size: 32px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .welcome-content {
    padding: 10px;
  }
  
  .welcome-content h3 {
    font-size: 16px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .quick-actions .el-button {
    padding: 12px;
    font-size: 12px;
  }
  
  :deep(.el-descriptions) {
    font-size: 12px;
  }
  
  :deep(.el-descriptions .el-descriptions__label) {
    width: 60px;
  }
}
</style>
