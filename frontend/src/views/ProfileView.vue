<template>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyProfile, updateMyProfile, getBranches } from '@/api/api'

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
        getMyProfile(),
        getBranches()
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
    await updateMyProfile({
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

onMounted(loadData)
</script>

<style scoped>
.card {
  background: var(--card-bg); padding: 32px;
  border-radius: var(--radius); box-shadow: var(--shadow);
  max-width: 560px;
  margin: 0 auto;
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