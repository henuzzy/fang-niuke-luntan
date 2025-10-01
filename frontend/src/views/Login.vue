<template>
  <div class="login-page">
    <div class="login-box">
      <!-- 标题 -->
      <h2 class="login-title">登 录</h2>
      <div class="title-line"></div>

      <!-- 表单 -->
      <div class="login-form">
        <!-- 账号 -->
        <div class="form-row">
          <label class="form-label">账号:</label>
          <div class="form-field">
            <input 
              v-model="formData.username" 
              type="text" 
              placeholder="请输入您的账号！"
              class="input-field"
            />
            <span v-if="errors.username" class="error-text">{{ errors.username }}</span>
          </div>
        </div>

        <!-- 密码 -->
        <div class="form-row">
          <label class="form-label">密码:</label>
          <div class="form-field">
            <input 
              v-model="formData.password" 
              type="password" 
              placeholder="请输入您的密码！"
              class="input-field"
            />
            <span v-if="errors.password" class="error-text">{{ errors.password }}</span>
          </div>
        </div>

        <!-- 验证码 -->
        <div class="form-row">
          <label class="form-label">验证码:</label>
          <div class="form-field">
            <div class="captcha-wrapper">
              <input 
                v-model="formData.captcha" 
                type="text" 
                placeholder="请输入验证码！"
                class="input-field captcha-input"
              />
              <img 
                v-if="captchaUrl" 
                :src="captchaUrl" 
                alt="验证码" 
                class="captcha-img"
                @click="refreshCaptcha"
              />
              <span class="refresh-text" @click="refreshCaptcha">刷新验证码</span>
            </div>
            <span v-if="errors.captcha" class="error-text">{{ errors.captcha }}</span>
          </div>
        </div>

        <!-- 记住我 / 忘记密码 -->
        <div class="form-row checkbox-row">
          <label class="remember-label">
            <input type="checkbox" v-model="formData.rememberMe" />
            记住我
          </label>
          <a href="#" class="forgot-password">忘记密码?</a>
        </div>

        <!-- 登录按钮 -->
        <button class="submit-btn" @click="handleLogin" :disabled="loading">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const captchaUrl = ref('')

const formData = reactive({
  username: '',
  password: '',
  captcha: '',
  rememberMe: false
})

const errors = reactive({
  username: '',
  password: '',
  captcha: ''
})

// 刷新验证码
const refreshCaptcha = () => {
  const timestamp = new Date().getTime()
  captchaUrl.value = `/api/captcha?t=${timestamp}`
}

const handleLogin = async () => {
  // 清除错误
  errors.username = ''
  errors.password = ''
  errors.captcha = ''
  
  // 验证
  if (!formData.username) {
    errors.username = '该账号不存在!'
    return
  }
  if (!formData.password || formData.password.length < 6) {
    errors.password = '密码长度不能少于6位!'
    return
  }
  if (!formData.captcha) {
    errors.captcha = '验证码不正确!'
    return
  }
  
  loading.value = true
  try {
    const res = await login(formData)
    
    if (res.data && res.data.ticket) {
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
    if (error.message && error.message.includes('账号')) {
      errors.username = '该账号不存在!'
    } else if (error.message && error.message.includes('密码')) {
      errors.password = '密码不正确!'
    } else if (error.message && error.message.includes('验证码')) {
      errors.captcha = '验证码不正确!'
      refreshCaptcha()
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #efefef;
}

.login-box {
  width: 500px;
  background: #fff;
  padding: 35px 50px 45px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

.login-title {
  font-size: 22px;
  color: #17a2b8;
  text-align: center;
  font-weight: normal;
  letter-spacing: 8px;
  margin-bottom: 12px;
}

.title-line {
  height: 1px;
  background-color: #ddd;
  margin-bottom: 30px;
}

.login-form {
  width: 100%;
}

.form-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 22px;
}

.form-label {
  width: 70px;
  text-align: right;
  padding-right: 10px;
  line-height: 40px;
  font-size: 14px;
  color: #333;
  flex-shrink: 0;
}

.form-field {
  flex: 1;
}

.input-field {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  background-color: #e8f4f8;
  transition: all 0.3s;
}

.input-field:focus {
  outline: none;
  border-color: #17a2b8;
  background-color: #fff;
}

.input-field::placeholder {
  color: #999;
}

.error-text {
  display: block;
  color: #dc3545;
  font-size: 12px;
  margin-top: 5px;
}

.captcha-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-input {
  flex: 1;
  max-width: 180px;
}

.captcha-img {
  width: 100px;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  object-fit: cover;
}

.captcha-img:hover {
  border-color: #17a2b8;
}

.refresh-text {
  color: #17a2b8;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}

.refresh-text:hover {
  text-decoration: underline;
}

.checkbox-row {
  justify-content: space-between;
  margin-bottom: 25px;
  margin-left: 70px;
}

.remember-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}

.remember-label input[type="checkbox"] {
  margin-right: 5px;
  cursor: pointer;
}

.forgot-password {
  color: #17a2b8;
  font-size: 14px;
  text-decoration: none;
}

.forgot-password:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  height: 42px;
  background-color: #17a2b8;
  border: none;
  border-radius: 4px;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #138496;
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
