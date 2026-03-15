<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary">新增菜单</el-button>
        </div>
      </template>
      <el-empty description="菜单管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: Implement menu management
</script>

<style scoped>
.menu-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
