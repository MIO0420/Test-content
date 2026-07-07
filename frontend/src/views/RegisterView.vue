<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="brand">
        <div class="brand-icon">📚</div>
        <h1>圖書借閱系統</h1>
        <p class="subtitle">建立您的專屬帳號</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>手機號碼 <span class="req">*</span></label>
          <input v-model="phoneNumber" type="text" placeholder="09xxxxxxxx" />
        </div>
        <div class="form-group">
          <label>使用者名稱 <span class="req">*</span></label>
          <input v-model="userName" type="text" placeholder="請輸入名稱" />
        </div>
        <div class="form-group">
          <label>密碼 <span class="req">*</span></label>
          <input v-model="password" type="password" placeholder="至少 6 碼" />
        </div>
        <div class="form-group">
          <label>電子郵件</label>
          <input v-model="email" type="email" placeholder="example@mail.com" />
        </div>
        <div class="form-group">
          <label>地址</label>
          <input v-model="address" type="text" placeholder="請輸入地址" />
        </div>
        <div class="form-group">
          <label>生日</label>
          <input v-model="birthday" type="date" />
        </div>
        <div class="form-group">
          <label>預設取書館</label>
          <select v-model="defaultBranch">
            <option value="">請選擇分館</option>
            <option v-for="b in branches" :key="b.branch_id" :value="b.branch_id">
              {{ b.city }} - {{ b.branch_name }}
            </option>
          </select>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import request from '@/api/request'

const router = useRouter()
const authStore = useAuthStore()

const phoneNumber = ref('')
const userName = ref('')
const password = ref('')
const email = ref('')
const address = ref('')
const birthday = ref('')
const defaultBranch = ref('')
const branches = ref([])
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

onMounted(async () => {
  try {
    branches.value = await request.get('/api/branches')
  } catch (err) {
    console.error('載入分館失敗', err)
  }
})

const handleRegister = async () => {
  errorMsg.value = ''
  successMsg.value = ''

  if (!/^09\d{8}$/.test(phoneNumber.value)) {
    errorMsg.value = '手機號碼格式錯誤（需為 09 開頭共 10 碼）'
    return
  }
  if (!userName.value.trim()) {
    errorMsg.value = '請輸入使用者名稱'
    return
  }
  if (password.value.length < 6) {
    errorMsg.value = '密碼至少需 6 碼'
    return
  }
  if (email.value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errorMsg.value = '電子郵件格式不正確'
    return
  }

  loading.value = true
  try {
    await authStore.register({
      phoneNumber: phoneNumber.value,
      password: password.value,
      userName: userName.value,
      email: email.value,
      address: address.value,
      birthday: birthday.value,
      defaultBranch: defaultBranch.value || null
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
  padding: 40px 20px;
  background: linear-gradient(135deg, #1a6c3f 0%, #145230 100%);
}
.auth-card {
  background: var(--card-bg);
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 420px;
}
.brand {
  text-align: center;
  margin-bottom: 28px;
}
.brand-icon { font-size: 44px; margin-bottom: 12px; }
.brand h1 { font-size: 22px; color: var(--esun-green); margin-bottom: 6px; }
.subtitle { font-size: 14px; color: var(--text-light); }
.form-group { margin-bottom: 16px; }
.form-group label {
  display: block;
  margin-bottom: 7px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
}
.req { color: var(--danger); }
.form-group input,
.form-group select {
  width: 100%;
  padding: 11px 14px;
  border: 1.5px solid var(--border);
  border-radius: 10px;
  box-sizing: border-box;
  font-size: 15px;
  transition: all 0.2s;
  background: white;
}
.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--esun-green);
  box-shadow: 0 0 0 3px var(--esun-green-light);
}
.form-group input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0 100px white inset;
  -webkit-text-fill-color: var(--text);
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
button:disabled { background: #b0b8c0; cursor: not-allowed; }
.error {
  color: var(--danger);
  font-size: 14px;
  margin: 4px 0 12px;
  padding: 10px 12px;
  background: #fdecec;
  border-radius: 8px;
}
.success {
  color: var(--esun-green);
  font-size: 14px;
  margin: 4px 0 12px;
  padding: 10px 12px;
  background: var(--esun-green-light);
  border-radius: 8px;
}
.switch {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--text-light);
}
.switch a { color: var(--esun-green); text-decoration: none; font-weight: 600; }
.switch a:hover { text-decoration: underline; }
</style>