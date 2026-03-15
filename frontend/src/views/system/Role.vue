<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary">新增角色</el-button>
        </div>
      </template>
      <el-empty description="角色管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: Implement role management
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
