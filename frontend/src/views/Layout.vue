<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <h3>EAM System</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/system/role">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/system/menu">
            <el-icon><Menu /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/system/dept">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="asset">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>资产管理</span>
          </template>
          <el-menu-item index="/asset/list">
            <el-icon><List /></el-icon>
            <span>资产台账</span>
          </el-menu-item>
          <el-menu-item index="/asset/category">
            <el-icon><FolderOpened /></el-icon>
            <span>资产分类</span>
          </el-menu-item>
          <el-menu-item index="/asset/transfer">
            <el-icon><Switch /></el-icon>
            <span>资产调拨</span>
          </el-menu-item>
          <el-menu-item index="/asset/inventory">
            <el-icon><Search /></el-icon>
            <span>资产盘点</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="maintenance">
          <template #title>
            <el-icon><Tools /></el-icon>
            <span>设备维护</span>
          </template>
          <el-menu-item index="/maintenance/plan">
            <el-icon><Calendar /></el-icon>
            <span>维护计划</span>
          </el-menu-item>
          <el-menu-item index="/maintenance/record">
            <el-icon><Document /></el-icon>
            <span>维护记录</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="workorder">
          <template #title>
            <el-icon><Tickets /></el-icon>
            <span>工单管理</span>
          </template>
          <el-menu-item index="/workorder/list">
            <el-icon><List /></el-icon>
            <span>工单列表</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="spare">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>备件库存</span>
          </template>
          <el-menu-item index="/spare/part">
            <el-icon><Goods /></el-icon>
            <span>备件列表</span>
          </el-menu-item>
          <el-menu-item index="/spare/inbound">
            <el-icon><BottomLeft /></el-icon>
            <span>入库记录</span>
          </el-menu-item>
          <el-menu-item index="/spare/outbound">
            <el-icon><BottomRight /></el-icon>
            <span>出库记录</span>
          </el-menu-item>
          <el-menu-item index="/spare/warning">
            <el-icon><Warning /></el-icon>
            <span>库存预警</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase/request">
            <el-icon><DocumentAdd /></el-icon>
            <span>采购申请</span>
          </el-menu-item>
          <el-menu-item index="/purchase/order">
            <el-icon><Tickets /></el-icon>
            <span>采购订单</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="report">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>报表分析</span>
          </template>
          <el-menu-item index="/report/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>数据看板</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="header-left">
            <span class="page-title">{{ currentPageTitle }}</span>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) }}
                </el-avatar>
                <span>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Odometer, Setting, User, UserFilled, Menu, OfficeBuilding,
  Box, List, FolderOpened, Switch, Search, Tools, Calendar,
  Document, Tickets, Goods, BottomLeft, BottomRight, Warning,
  ShoppingCart, DocumentAdd, DataAnalysis, DataLine
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentPageTitle = computed(() => {
  return route.meta.title as string || '仪表盘'
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    window.location.href = '/eam/logout'
  } else if (command === 'profile') {
    // TODO: Implement profile page
    console.log('Profile page')
  }
}

// Initialize user info on mount
userStore.initUserInfo()
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #545c64;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #434a50;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.user-info span {
  font-size: 14px;
  color: #606266;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
