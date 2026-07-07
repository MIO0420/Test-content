<template>
  <div class="page">
    <header class="topbar">
      <h1>📚 圖書借閱系統</h1>
      <div class="user-area">
        <router-link to="/books" class="nav-link">書籍列表</router-link>
        <router-link to="/my-borrows" class="nav-link">我的借閱</router-link>
        <span>{{ authStore.userName }} 您好</span>
        <button class="logout-btn" @click="handleLogout">登出</button>
      </div>
    </header>

    <main class="content">
      <h2>個人資料</h2>
      <p v-if="msg" :class="msgType">{{ msg }}</p>

      <div class="card" v-if="loaded">
        <div class="field">
          <label>手機號碼</label>
          <input :value="profile.phone_number" disabled />
          <small>手機號碼為帳號，不可修改</small>
        </div>

        <div class="field">
          <label>使用者名稱</label>
          <input :value="profile.user_name" disabled />
        </div>

        <div class="field">
          <label>生日</label>
          <input :value="profile.birthday || '未設定'" disabled />
          <small>生日不可修改</small>
        </div>

        <div class="field">
          <label>電子郵件</label>
          <input v-model="email" type="email" placeholder="尚未設定" />
        </div>

        <div class="field">
          <label>地址</label>
          <input v-model="address" type="text" placeholder="尚未設定" />
        </div>

        <div class="field">
          <label>預設取書館</label>
          <select v-model="defaultBranch">
            <option value="">未設定</option>
            <option v-for="b in branches" :key="b.branch_id" :value="b.branch_id">
              {{ b.city }} - {{ b.branch_name }}
            </option>
          </select>
        </div>

        <button class="save-btn" @click="handleSave" :disabled="saving">
          {{ saving ? '儲存中...' : '儲存修改' }}
        </button>
      </div>
      <p v-else class="loading">載入中...</p>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import request from '@/api/request'

const router = useRouter()
const authStore = useAuthStore()

const profile = ref({})
const email = ref('')
const address = ref('')
const defaultBranch = ref('')
const branches = ref([])
const loaded = ref(false)
const saving = ref(false)
const msg = ref('')
const msgType = ref('success-msg')

const loadData = async () => {
  try {
    const [p, bs] = await Promise.all([
      request.get('/api/profile'),
      request.get('/api/branches')
    ])
    profile.value = p
    email.value = p.email || ''
    address.value = p.address || ''
    defaultBranch.value = p.default_branch || ''
    branches.value = bs
  } catch (err) {
    msg.value = '載入資料失敗'
    msgType.value = 'error-msg'
  } finally {
    loaded.value = true
  }
}

const handleSave = async () => {
  msg.value = ''
  if (email.value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    msg.value = '電子郵件格式不正確'
    msgType.value = 'error-msg'
    return
  }
  saving.value = true
  try {
    await request.put('/api/profile', {
      email: email.value,
      address: address.value,
      defaultBranch: defaultBranch.value || null
    })
    msg.value = '資料更新成功！'
    msgType.value = 'success-msg'
  } catch (err) {
    msg.value = err.response?.data?.message || '更新失敗'
    msgType.value = 'error-msg'
  } finally {
    saving.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(loadData)
</script>

<style scoped>
.page { min-height: 100vh; background: #f0f2f5; }
.topbar {
  background: #1a6c3f;
  color: white;
  padding: 16px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.topbar h1 { font-size: 20px; }
.user-area { display: flex; align-items: center; gap: 16px; font-size: 14px; }
.nav-link { color: white; text-decoration: none; padding: 6px 10px; }
.nav-link:hover { text-decoration: underline; }
.logout-btn {
  background: rgba(255,255,255,0.2);
  color: white;
  border: 1px solid white;
  padding: 6px 14px;
  border-radius: 6px;
  cursor: pointer;
}
.content { padding: 32px; max-width: 560px; margin: 0 auto; }
.content h2 { margin-bottom: 16px; color: #333; }
.card {
  background: white;
  padding: 28px;
  border-radius: 10px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.06);
}
.field { margin-bottom: 18px; }
.field label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.field input, .field select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 14px;
}
.field input:disabled {
  background: #f5f5f5;
  color: #888;
}
.field small { color: #999; font-size: 12px; display: block; margin-top: 4px; }
.save-btn {
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
.save-btn:disabled { background: #aaa; cursor: not-allowed; }
.success-msg { color: #1a6c3f; margin-bottom: 12px; font-weight: bold; }
.error-msg { color: #d33; margin-bottom: 12px; font-weight: bold; }
.loading { color: #999; }
</style>