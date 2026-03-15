<template>
  <div class="asset-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产台账</span>
          <el-button type="primary">新增资产</el-button>
        </div>
      </template>
      <el-empty description="资产台账功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: Implement asset list
</script>

<style scoped>
.asset-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
