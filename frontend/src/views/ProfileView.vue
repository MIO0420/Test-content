<template>
  <div class="page">
    <header class="topbar">
      <div class="topbar-inner">
        <h1>📚 圖書借閱系統</h1>
        <nav class="user-area">
          <router-link to="/books" class="nav-link">書籍列表</router-link>
          <router-link to="/my-borrows" class="nav-link">我的借閱</router-link>
          <router-link to="/profile" class="nav-link active">個人資料</router-link>
          <span class="username">{{ authStore.userName }} 您好</span>
          <button class="logout-btn" @click="handleLogout">登出</button>
        </nav>
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
.page { min-height: 100vh; background: var(--bg); }
.topbar { background: var(--esun-green); color: white; box-shadow: var(--shadow); }
.topbar-inner {
  max-width: 1080px; margin: 0 auto; padding: 16px 32px;
  display: flex; justify-content: space-between; align-items: center;
}
.topbar h1 { font-size: 19px; font-weight: 600; }
.user-area { display: flex; align-items: center; gap: 8px; font-size: 14px; }
.nav-link {
  color: rgba(255,255,255,0.85); text-decoration: none;
  padding: 7px 12px; border-radius: 8px; transition: all 0.2s;
}
.nav-link:hover { background: rgba(255,255,255,0.12); color: white; }
.nav-link.active { background: rgba(255,255,255,0.18); color: white; font-weight: 600; }
.username { margin: 0 8px 0 12px; opacity: 0.95; }
.logout-btn {
  background: rgba(255,255,255,0.15); color: white;
  border: 1px solid rgba(255,255,255,0.4);
  padding: 7px 14px; border-radius: 8px; cursor: pointer; transition: all 0.2s;
}
.logout-btn:hover { background: rgba(255,255,255,0.25); }
.content { padding: 32px; max-width: 560px; margin: 0 auto; }
.content h2 { font-size: 22px; margin-bottom: 20px; color: var(--text); }
.card {
  background: var(--card-bg); padding: 32px;
  border-radius: var(--radius); box-shadow: var(--shadow);
}
.field { margin-bottom: 20px; }
.field label {
  display: block; margin-bottom: 7px; font-size: 14px;
  color: var(--text); font-weight: 600;
}
.field input, .field select {
  width: 100%; padding: 11px 14px;
  border: 1.5px solid var(--border); border-radius: 10px;
  box-sizing: border-box; font-size: 15px; transition: all 0.2s; background: white;
}
.field input:focus, .field select:focus {
  outline: none; border-color: var(--esun-green);
  box-shadow: 0 0 0 3px var(--esun-green-light);
}
.field input:disabled { background: #f4f6f8; color: var(--text-light); }
.field small { color: var(--text-light); font-size: 12px; display: block; margin-top: 5px; }
.save-btn {
  width: 100%; padding: 13px; background: var(--esun-green); color: white;
  border: none; border-radius: 10px; font-size: 15px; font-weight: 600;
  cursor: pointer; margin-top: 8px; transition: all 0.2s;
}
.save-btn:hover:not(:disabled) { background: var(--esun-green-dark); transform: translateY(-1px); }
.save-btn:disabled { background: #b0b8c0; cursor: not-allowed; }
.success-msg {
  color: var(--esun-green); margin-bottom: 16px; font-weight: 600;
  padding: 12px 16px; background: var(--esun-green-light); border-radius: 8px;
}
.error-msg {
  color: var(--danger); margin-bottom: 16px; font-weight: 600;
  padding: 12px 16px; background: #fdecec; border-radius: 8px;
}
.loading { color: var(--text-light); }
</style>