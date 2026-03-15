<template>
  <div class="asset-category">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产分类</span>
          <el-button type="primary">新增分类</el-button>
        </div>
      </template>
      <el-empty description="资产分类功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// TODO: Implement asset category
</script>

<style scoped>
.asset-category {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
