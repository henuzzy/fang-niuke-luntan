<template>
  <div class="user-profile">
    <el-card class="profile-card" v-loading="loading">
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="个人信息" name="info">
          <!-- 用户信息区域 -->
          <div class="user-info-section">
            <!-- 左侧：头像 -->
            <div class="user-avatar">
              <el-avatar :size="80" :src="userInfo.headerUrl || getDefaultAvatar()" />
            </div>
            
            <!-- 中间：详细信息 -->
            <div class="user-details">
              <h2 class="username">{{ userInfo.username || 'nowcoder' }}</h2>
              <p class="register-time">注册于 {{ formatTime(userInfo.createTime) }}</p>
              <div class="user-stats">
                <span class="stat-item">关注了 <span class="stat-number">{{ userInfo.followeeCount || 5 }}</span> 人</span>
                <span class="stat-item">关注者 <span class="stat-number">{{ userInfo.followerCount || 123 }}</span> 人</span>
                <span class="stat-item">获得了 <span class="stat-number">{{ userInfo.likeCount || 87 }}</span> 个赞</span>
              </div>
            </div>
            
            <!-- 右侧：关注按钮 -->
            <div class="user-actions">
              <el-button 
                type="info" 
                :loading="followLoading"
                @click="handleFollow"
                v-if="!isCurrentUser"
              >
                {{ userInfo.hasFollowed ? '已关注' : '关注TA' }}
              </el-button>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的帖子" name="posts">
          <div class="tab-content">
            <el-empty description="暂无帖子" />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的回复" name="replies">
          <div class="tab-content">
            <el-empty description="暂无回复" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getUserProfile, followUser, unfollowUser } from '@/api/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const followLoading = ref(false)
const userInfo = ref({})
const activeTab = ref('info')

// 判断是否是当前登录用户自己的主页
const isCurrentUser = computed(() => {
  const currentUserId = localStorage.getItem('userId')
  return currentUserId === route.params.id
})

const fetchUserInfo = async () => {
  loading.value = true
  try {
    const userId = route.params.id
    const res = await getUserProfile(userId)
    if (res.data) {
      userInfo.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const handleFollow = async () => {
  followLoading.value = true
  try {
    const userId = route.params.id
    if (userInfo.value.hasFollowed) {
      await unfollowUser(userId)
      ElMessage.success('已取消关注')
      userInfo.value.hasFollowed = false
      userInfo.value.followerCount = (userInfo.value.followerCount || 0) - 1
    } else {
      await followUser(userId)
      ElMessage.success('关注成功')
      userInfo.value.hasFollowed = true
      userInfo.value.followerCount = (userInfo.value.followerCount || 0) + 1
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    followLoading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '2015-06-12 15:20:12'
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const getDefaultAvatar = () => {
  return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iODAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iNDAiIGN5PSI0MCIgcj0iNDAiIGZpbGw9IiM2N0M5M0EiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1zaXplPSIzNSIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPueUqDwvdGV4dD48L3N2Zz4='
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.user-profile {
  max-width: 1000px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 4px;
}

/* 标签页样式 */
.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e8e8e8;
}

.profile-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  color: #333;
  font-weight: 500;
}

.profile-tabs :deep(.el-tabs__active-bar) {
  height: 2px;
  background-color: #f0ad4e;
}

/* 用户信息区域 */
.user-info-section {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 30px 0;
}

.user-avatar {
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 20px;
  font-weight: 500;
  color: #f0ad4e;
  margin: 0 0 12px 0;
}

.register-time {
  font-size: 13px;
  color: #999;
  margin: 0 0 15px 0;
}

.user-stats {
  display: flex;
  gap: 30px;
  font-size: 13px;
  color: #666;
}

.stat-item {
  display: inline-flex;
  align-items: center;
}

.stat-number {
  color: #409eff;
  font-weight: 500;
  margin: 0 2px;
}

.user-actions {
  flex-shrink: 0;
}

.user-actions .el-button {
  background-color: #5bc0de;
  border-color: #5bc0de;
  color: #fff;
  padding: 8px 20px;
}

.user-actions .el-button:hover {
  background-color: #46b8da;
  border-color: #46b8da;
}

/* 标签页内容 */
.tab-content {
  padding: 30px 0;
  min-height: 200px;
}
</style>

