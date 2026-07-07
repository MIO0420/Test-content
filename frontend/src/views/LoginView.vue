<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>圖書借閱系統</h1>
      <h2>登入</h2>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>手機號碼</label>
          <input v-model="phoneNumber" type="text" placeholder="09xxxxxxxx" />
        </div>

        <div class="form-group">
          <label>密碼</label>
          <input v-model="password" type="password" placeholder="請輸入密碼" />
        </div>

        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

        <button type="submit" :disabled="loading">
          {{ loading ? '登入中...' : '登入' }}
        </button>
      </form>

      <p class="switch">
        還沒有帳號？<router-link to="/register">前往註冊</router-link>
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
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

const handleLogin = async () => {
  errorMsg.value = ''
  loading.value = true
  try {
    await authStore.login(phoneNumber.value, password.value)
    router.push('/books')  // 登入成功 → 導向書籍列表
  } catch (err) {
    errorMsg.value = err.response?.data?.message || '登入失敗，請檢查帳號密碼'
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
.form-group {
  margin-bottom: 16px;
}
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
button:disabled {
  background: #aaa;
  cursor: not-allowed;
}
.error {
  color: #d33;
  font-size: 14px;
  margin: 8px 0;
}
.switch {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}
.switch a {
  color: #1a6c3f;
  text-decoration: none;
}
</style>