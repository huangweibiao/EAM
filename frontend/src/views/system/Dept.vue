<template>
  <div class="dept-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary">新增部门</el-button>
        </div>
      </template>
      <el-empty description="部门管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: Implement department management
</script>

<style scoped>
.dept-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
