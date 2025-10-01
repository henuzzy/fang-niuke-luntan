<template>
  <div class="home">
    <div class="container">
      <!-- 帖子列表卡片 -->
      <el-card class="post-card" v-loading="loading">
        <!-- 顶部操作栏 -->
        <div class="toolbar">
          <el-tabs v-model="orderMode" @tab-change="handleTabChange">
            <el-tab-pane label="最新" name="latest"></el-tab-pane>
            <el-tab-pane label="最热" name="hot"></el-tab-pane>
          </el-tabs>
          <el-button type="primary" @click="handlePublish">我要发布</el-button>
        </div>

        <!-- 帖子列表 -->
        <div class="post-list">
          <div class="post-item" v-for="post in postList" :key="post.id">
            <!-- 用户头像（可点击） -->
            <div class="post-avatar" @click="goToUserProfile(post.userId)">
              <el-avatar :size="48" :src="getAvatar()" />
            </div>
            
            <!-- 帖子内容 -->
            <div class="post-content">
              <!-- 帖子标题 -->
              <div class="post-title-row">
                <span class="post-title" @click="goToDetail(post.id)">
                  {{ post.title }}
                </span>
                <el-tag v-if="post.type === 1" class="badge-top">置顶</el-tag>
                <el-tag v-if="post.status === 1" class="badge-essence">精华</el-tag>
              </div>
              
              <!-- 作者和时间 -->
              <div class="post-meta">
                <span class="post-author" @click="goToUserProfile(post.userId)">{{ post.username || '寒江雪' }}</span>
                <span class="meta-separator">发布于</span>
                <span class="post-time">{{ formatTime(post.createTime) }}</span>
              </div>
            </div>
            
            <!-- 统计信息（右侧） -->
            <div class="post-stats">
              <span class="stat-item">赞 {{ post.likeCount || 11 }}</span>
              <span class="stat-separator">|</span>
              <span class="stat-item">回帖 {{ post.commentCount || 12 }}</span>
            </div>
          </div>

          <!-- 空状态 -->
          <el-empty v-if="!loading && postList.length === 0" description="暂无帖子" />
        </div>
      </el-card>

      <!-- 分页 -->
      <el-card class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostList } from '@/api/post'

const router = useRouter()

const loading = ref(false)
const postList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderMode = ref('latest')

const fetchPostList = async () => {
  loading.value = true
  try {
    const res = await getPostList({
      page: currentPage.value,
      pageSize: pageSize.value,
      order: orderMode.value
    })
    
    if (res.data) {
      postList.value = res.data.rows || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchPostList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchPostList()
}

const goToDetail = (id) => {
  router.push(`/post/${id}`)
}

const handleReply = (id) => {
  router.push(`/post/${id}#reply`)
}

const handleTabChange = () => {
  currentPage.value = 1
  fetchPostList()
}

const handlePublish = () => {
  // TODO: 跳转到发布帖子页面
  ElMessage.info('发布帖子功能开发中...')
}

const goToUserProfile = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const getAvatar = () => {
  return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMjQiIGN5PSIyNCIgcj0iMjQiIGZpbGw9IiM0MDllZmYiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1zaXplPSIyMCIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPueUqDwvdGV4dD48L3N2Zz4='
}

onMounted(() => {
  fetchPostList()
})
</script>

<style scoped>
.home {
  max-width: 1000px;
  margin: 0 auto;
}

.container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 帖子卡片 */
.post-card {
  border-radius: 4px;
}

/* 顶部工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
  padding-bottom: 0;
}

.toolbar :deep(.el-tabs) {
  flex: 1;
}

.toolbar :deep(.el-tabs__header) {
  margin: 0 0 15px 0;
}

.toolbar :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e8e8e8;
}

.toolbar :deep(.el-tabs__active-bar) {
  height: 2px;
  background-color: #409eff;
}

.toolbar :deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
}

.toolbar :deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 500;
}

/* 帖子列表 */
.post-list {
  margin-top: 10px;
}

.post-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid #e8e8e8;
  transition: background-color 0.2s;
}

.post-item:first-child {
  padding-top: 5px;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover {
  background-color: #fafafa;
}

.post-avatar {
  flex-shrink: 0;
  cursor: pointer;
}

.post-avatar:hover {
  opacity: 0.8;
}

.post-content {
  flex: 1;
  min-width: 0;
  margin-right: auto;
}

.post-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.post-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  line-height: 1.5;
}

.post-title:hover {
  color: #409eff;
}

.badge-top {
  background-color: #409eff;
  border: none;
  color: #fff;
  font-size: 12px;
  height: 20px;
  line-height: 20px;
  padding: 0 8px;
  border-radius: 2px;
}

.badge-essence {
  background-color: #f56c6c;
  border: none;
  color: #fff;
  font-size: 12px;
  height: 20px;
  line-height: 20px;
  padding: 0 8px;
  border-radius: 2px;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 8px;
}

.post-author {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.post-author:hover {
  color: #409eff;
}

.meta-separator {
  font-size: 12px;
  color: #ccc;
  margin: 0 2px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  margin-left: 20px;
  padding-top: 33px;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
}

.stat-separator {
  color: #ccc;
  margin: 0 5px;
}

/* 分页 */
.pagination-wrapper {
  border-radius: 4px;
}

.pagination-wrapper :deep(.el-card__body) {
  display: flex;
  justify-content: center;
}
</style>

