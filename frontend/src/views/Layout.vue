<template>
  <div class="layout">
    <!-- 导航栏 -->
    <el-header class="navbar">
      <div class="navbar-container">
        <!-- 左侧Logo -->
        <div class="navbar-left">
          <div class="navbar-logo" @click="$router.push('/')">
            <svg width="32" height="32" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
              <rect width="32" height="32" fill="#fff" rx="4"/>
              <text x="50%" y="50%" font-size="18" fill="#333" text-anchor="middle" dy=".35em" font-weight="bold">牛</text>
            </svg>
          </div>
          <span class="navbar-brand">牛客</span>
        </div>

        <!-- 中间菜单 -->
        <div class="navbar-menu">
          <a href="/" :class="{ active: $route.path === '/' }">首页</a>
          <a href="#" class="message-link" :class="{ active: $route.path === '/messages' }">
            消息
            <el-badge v-if="unreadCount > 0" :value="unreadCount" class="message-badge" />
          </a>

          <!-- 登录后：在消息右边显示头像 + 下拉三角 -->
          <template v-if="isLogin">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-avatar-wrapper">
                <el-avatar :size="32" :src="userAvatar" />
                <span class="caret"></span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人主页</el-dropdown-item>
                  <el-dropdown-item command="settings">账号设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <!-- 未登录：显示注册/登录（仍在消息右边） -->
          <template v-else>
            <a href="/register" :class="{ active: $route.path === '/register' }">注册</a>
            <a href="/login" :class="{ active: $route.path === '/login' }">登录</a>
          </template>
        </div>

        <!-- 右侧区域 -->
        <div class="navbar-right">
          <el-input
            v-model="searchText"
            placeholder="搜索帖子、用户..."
            class="navbar-search"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button class="search-btn" @click="handleSearch">搜索</el-button>
        </div>
      </div>
    </el-header>

    <!-- 主体内容 -->
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { logout } from '@/api/user'

const router = useRouter()
const route = useRoute()

const searchText = ref('')
const isLogin = ref(!!localStorage.getItem('ticket'))
const userAvatar = ref(getUserAvatar())
const unreadCount = ref(12) // 未读消息数

function getUserAvatar() {
  // 如果有存储的头像URL，使用存储的，否则使用默认SVG
  const storedAvatar = localStorage.getItem('userAvatar')
  if (storedAvatar) {
    return storedAvatar
  }
  return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzYiIGhlaWdodD0iMzYiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMTgiIGN5PSIxOCIgcj0iMTgiIGZpbGw9IiM2N0M5M0EiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1zaXplPSIxNiIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPueUqDwvdGV4dD48L3N2Zz4='
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      const ticket = localStorage.getItem('ticket')
      if (ticket) {
        await logout(ticket)
      }
      
      // 清除所有登录信息
      localStorage.removeItem('ticket')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('userAvatar')
      isLogin.value = false
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('退出失败')
      }
    }
  } else if (command === 'profile') {
    // 跳转到当前登录用户的主页
    const currentUserId = localStorage.getItem('userId')
    if (currentUserId) {
      router.push(`/user/${currentUserId}`)
    } else {
      ElMessage.warning('请先登录')
      router.push('/login')
    }
  } else if (command === 'settings') {
    router.push('/settings')
  }
}

const handleSearch = () => {
  if (!searchText.value || !searchText.value.trim()) {
    ElMessage.warning('请输入搜索内容')
    return
  }
  // TODO: 跳转到搜索结果页
  ElMessage.info(`搜索：${searchText.value.trim()}`)
}

// Logo已改为SVG，无需错误处理
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background-color: #2c2c2c;
  border-bottom: 1px solid #3c3c3c;
  padding: 0;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  gap: 40px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.navbar-logo svg {
  display: block;
}

.navbar-brand {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  user-select: none;
}

.navbar-brand:hover {
  opacity: 0.8;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 35px;
}

.navbar-menu a {
  color: #fff;
  font-size: 16px;
  text-decoration: none;
  position: relative;
  padding: 5px 0;
  transition: opacity 0.3s;
}

.navbar-menu a:hover {
  opacity: 0.8;
}

.navbar-menu a.active {
  color: #fff;
}

.message-link {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.message-badge {
  position: absolute;
  top: -8px;
  right: -20px;
}

.message-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
  border: none;
  font-size: 12px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.navbar-search {
  width: 200px;
}

.search-btn {
  height: 32px;
  line-height: 32px;
  padding: 0 16px;
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.6);
  color: #fff;
}

.search-btn:hover {
  background-color: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.85);
}

.navbar-search :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
  box-shadow: none;
}

.navbar-search :deep(.el-input__inner) {
  color: #fff;
}

.navbar-search :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.navbar-search :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.5);
}

.navbar-btn {
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  padding: 8px 20px;
}

.navbar-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.user-avatar-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-avatar-wrapper:hover {
  opacity: 0.8;
}

.caret {
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 6px solid rgba(255, 255, 255, 0.8);
  margin-left: 6px;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #e8e8e8;
}

@media (max-width: 992px) {
  .navbar-search {
    display: none;
  }
  
  .navbar-menu {
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }
}
</style>

