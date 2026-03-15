<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary">新增用户</el-button>
        </div>
      </template>
      <el-empty description="用户管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: Implement user management
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
