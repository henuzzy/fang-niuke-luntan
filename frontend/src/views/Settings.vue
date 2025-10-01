<template>
  <div class="settings-page">
    <el-card class="settings-card">
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="settings-tabs">
        <el-tab-pane label="个人资料" name="profile">
          <el-form :model="profileForm" label-width="100px" class="settings-form">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :auto-upload="false"
              >
                <el-avatar :size="100" :src="profileForm.headerUrl || getDefaultAvatar()" />
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form 
            ref="passwordFormRef"
            :model="passwordForm" 
            :rules="passwordRules"
            label-width="100px" 
            class="settings-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="loading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { updatePassword } from '@/api/user'

const router = useRouter()
const activeTab = ref('profile')
const loading = ref(false)
const passwordFormRef = ref()

const profileForm = reactive({
  username: '',
  email: '',
  headerUrl: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const getDefaultAvatar = () => {
  return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iIzY3QzkzQSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjQ1IiBmaWxsPSIjZmZmIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+55SoPC90ZXh0Pjwvc3ZnPg=='
}

const handleUpdateProfile = () => {
  ElMessage.info('个人资料修改功能开发中...')
}

const handleUpdatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    loading.value = true
    const userId = localStorage.getItem('userId')
    
    const res = await updatePassword(userId, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPwd: passwordForm.confirmPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清除登录信息
    localStorage.clear()
    
    // 跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 1500)
    
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 加载用户信息
  profileForm.username = localStorage.getItem('username') || ''
  profileForm.email = 'user@example.com' // TODO: 从后端获取
  profileForm.headerUrl = localStorage.getItem('userAvatar') || ''
})
</script>

<style scoped>
.settings-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.settings-card {
  border-radius: 4px;
}

.settings-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.settings-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e8e8e8;
}

.settings-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
}

.settings-tabs :deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 500;
}

.settings-tabs :deep(.el-tabs__active-bar) {
  height: 2px;
  background-color: #409eff;
}

.settings-form {
  max-width: 500px;
  margin-top: 30px;
}

.avatar-uploader {
  cursor: pointer;
}

.avatar-uploader:hover {
  opacity: 0.8;
}
</style>
