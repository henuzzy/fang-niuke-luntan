<template>
  <div class="post-detail">
    <el-card v-loading="loading">
      <h2>帖子详情页</h2>
      <p>功能开发中...</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const loading = ref(false)
</script>

<style scoped>
.post-detail {
  max-width: 1000px;
  margin: 0 auto;
}
</style>

