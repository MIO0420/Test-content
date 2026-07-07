<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>圖書借閱系統</h1>
      <h2>註冊</h2>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>手機號碼</label>
          <input v-model="phoneNumber" type="text" placeholder="09xxxxxxxx" />
        </div>
        <div class="form-group">
          <label>使用者名稱</label>
          <input v-model="userName" type="text" placeholder="請輸入名稱" />
        </div>
        <div class="form-group">
          <label>密碼</label>
          <input v-model="password" type="password" placeholder="至少 6 碼" />
        </div>
        <div class="form-group">
          <label>電子郵件（選填）</label>
          <input v-model="email" type="email" placeholder="example@mail.com" />
        </div>

        <div class="form-group">
          <label>地址（選填）</label>
          <input v-model="address" type="text" placeholder="請輸入地址" />
        </div>

        <div class="form-group">
          <label>生日（選填）</label>
          <input v-model="birthday" type="date" />
        </div>

        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success">{{ successMsg }}</p>

        <button type="submit" :disabled="loading">
          {{ loading ? '註冊中...' : '註冊' }}
        </button>
      </form>

      <p class="switch">
        已經有帳號？<router-link to="/login">前往登入</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const phoneNumber = ref('')
const userName = ref('')
const password = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)
const email = ref('')
const address = ref('')
const birthday = ref('')

const handleRegister = async () => {
  errorMsg.value = ''
  successMsg.value = ''
  loading.value = true
  try {
    await authStore.register({
      phoneNumber: phoneNumber.value,
      password: password.value,
      userName: userName.value,
      email: email.value,
      address: address.value,
      birthday: birthday.value
    })
    successMsg.value = '註冊成功！即將前往登入...'
    setTimeout(() => router.push('/login'), 1500)
  } catch (err) {
    errorMsg.value = err.response?.data?.message || '註冊失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}
.auth-card {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  width: 360px;
}
.auth-card h1 {
  font-size: 20px;
  text-align: center;
  color: #1a6c3f;
  margin-bottom: 8px;
}
.auth-card h2 {
  font-size: 16px;
  text-align: center;
  color: #666;
  margin-bottom: 24px;
  font-weight: normal;
}
.form-group { margin-bottom: 16px; }
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}
.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 14px;
}
button {
  width: 100%;
  padding: 12px;
  background: #1a6c3f;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  margin-top: 8px;
}
button:disabled { background: #aaa; cursor: not-allowed; }
.error { color: #d33; font-size: 14px; margin: 8px 0; }
.success { color: #1a6c3f; font-size: 14px; margin: 8px 0; }
.switch {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}
.switch a { color: #1a6c3f; text-decoration: none; }
</style>