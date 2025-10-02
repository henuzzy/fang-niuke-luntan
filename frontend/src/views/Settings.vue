<template>
  <div class="settings-page">
    <el-card class="block-card">
      <!-- 上传头像 -->
      <h3 class="block-title">上传头像</h3>
      <div class="upload-row">
        <span class="upload-label">选择头像：</span>
        <el-input v-model="uploadFileName" placeholder="选择一张图片" class="upload-input" disabled />
        <input type="file" accept="image/*" @change="onFileChange" class="hidden-file-input" ref="fileInputRef" />
        <el-button @click="triggerChooseFile">文件</el-button>
      </div>
      <div class="upload-action">
        <el-button type="primary" @click="handleUploadAvatar" :loading="uploading">立即上传</el-button>
      </div>

      <el-divider />

      <!-- 修改密码 -->
      <h3 class="block-title">修改密码</h3>
      <el-form 
        ref="passwordFormRef"
        :model="passwordForm" 
        :rules="passwordRules"
        label-width="70px" 
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
            立即保存
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { updatePassword, uploadAvatar } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const passwordFormRef = ref()
const fileInputRef = ref()
const uploadFileName = ref('')
const uploading = ref(false)

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

const triggerChooseFile = () => {
  fileInputRef.value && fileInputRef.value.click()
}

const onFileChange = (e) => {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  uploadFileName.value = file.name
  const reader = new FileReader()
  reader.onload = () => {
    profileForm.headerUrl = reader.result
  }
  reader.readAsDataURL(file)
}

const handleUploadAvatar = async () => {
  if (!profileForm.headerUrl) {
    ElMessage.warning('请先选择图片')
    return
  }
  try {
    uploading.value = true
    const userId = localStorage.getItem('userId')
    const res = await uploadAvatar(userId, profileForm.headerUrl)
    if (res && res.data && res.data.headerUrl) {
      localStorage.setItem('userAvatar', res.data.headerUrl)
      ElMessage.success('头像上传成功')
    }
  } finally {
    uploading.value = false
  }
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

.block-card { margin: 0; }
.block-title { font-size: 18px; margin-bottom: 16px; }
.upload-row { display: flex; align-items: center; gap: 10px; }
.upload-label { width: 70px; color: #666; text-align: right; }
.upload-input { flex: 1; }
.hidden-file-input { display: none; }
.upload-action { margin-top: 14px; }

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

.settings-form { max-width: 500px; margin-top: 10px; }

.avatar-uploader {
  cursor: pointer;
}

.avatar-uploader:hover {
  opacity: 0.8;
}
</style>
