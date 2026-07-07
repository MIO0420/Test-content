<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="brand">
        <div class="brand-icon">📚</div>
        <h1>圖書借閱系統</h1>
        <p class="subtitle">歡迎回來，請登入您的帳號</p>
      </div>

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
    router.push('/books')
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
  padding: 20px;
  background: linear-gradient(135deg, #1a6c3f 0%, #145230 100%);
}
.auth-card {
  background: var(--card-bg);
  padding: 44px 40px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 400px;
}
.brand {
  text-align: center;
  margin-bottom: 32px;
}
.brand-icon {
  font-size: 44px;
  margin-bottom: 12px;
}
.brand h1 {
  font-size: 22px;
  color: var(--esun-green);
  margin-bottom: 6px;
}
.subtitle {
  font-size: 14px;
  color: var(--text-light);
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
}
.form-group input {
  width: 100%;
  padding: 12px 14px;
  border: 1.5px solid var(--border);
  border-radius: 10px;
  box-sizing: border-box;
  font-size: 15px;
  transition: all 0.2s;
}
.form-group input:focus {
    
  outline: none;
  border-color: var(--esun-green);
  box-shadow: 0 0 0 3px var(--esun-green-light);
}
button {
  width: 100%;
  padding: 13px;
  background: var(--esun-green);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: all 0.2s;
}
button:hover:not(:disabled) {
  background: var(--esun-green-dark);
  transform: translateY(-1px);
}
button:disabled {
  background: #b0b8c0;
  cursor: not-allowed;
}
.error {
  color: var(--danger);
  font-size: 14px;
  margin: 4px 0 12px;
  padding: 10px 12px;
  background: #fdecec;
  border-radius: 8px;
}
.switch {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-light);
}
.switch a {
  color: var(--esun-green);
  text-decoration: none;
  font-weight: 600;
}
.switch a:hover {
  text-decoration: underline;
}
</style>