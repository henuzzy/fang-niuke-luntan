<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="login-header">
        <div class="logo">
          <svg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg">
            <rect width="60" height="60" fill="#409eff" rx="10"/>
            <text x="50%" y="50%" font-size="32" fill="#fff" text-anchor="middle" dy=".35em" font-weight="bold">牛</text>
          </svg>
        </div>
        <h1>欢迎回来</h1>
        <p>登录到牛客论坛</p>
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="0"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="captcha">
          <el-input
            v-model="formData.captcha"
            placeholder="请输入验证码"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><PictureFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="formData.rememberMe">记住我（30天有效）</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, PictureFilled } from '@element-plus/icons-vue'
import { login } from '@/api/user'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const formData = reactive({
  username: '',
  password: '',
  captcha: 'TEST',
  rememberMe: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await formRef.value.validate()
  
  loading.value = true
  try {
    const res = await login(formData)
    
    if (res.data && res.data.ticket) {
      // 保存登录信息到 localStorage
      localStorage.setItem('ticket', res.data.ticket)
      localStorage.setItem('userId', res.data.userId)
      localStorage.setItem('username', res.data.username)
      if (res.data.headerUrl) {
        localStorage.setItem('userAvatar', res.data.headerUrl)
      }
      
      ElMessage.success('登录成功')
      router.push('/')
    }
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

// Logo已改为SVG，无需错误处理
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.logo svg {
  display: block;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-footer {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 20px;
}

.login-footer a {
  color: #409eff;
  text-decoration: none;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>

