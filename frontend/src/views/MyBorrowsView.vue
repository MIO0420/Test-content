<template>
  <div class="page">
    <header class="topbar">
      <h1>📚 圖書借閱系統</h1>
      <div class="user-area">
        <router-link to="/books" class="nav-link">書籍列表</router-link>
        <span>{{ authStore.userName }} 您好</span>
        <button class="logout-btn" @click="handleLogout">登出</button>
      </div>
    </header>

    <main class="content">
      <h2>我的借閱</h2>
      <p v-if="msg" class="msg">{{ msg }}</p>

      <table v-if="borrows.length" class="book-table">
        <thead>
          <tr>
            <th>書名</th>
            <th>作者</th>
            <th>條碼</th>
            <th>借閱時間</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in borrows" :key="item.record_id">
            <td>{{ item.book_name }}</td>
            <td>{{ item.author }}</td>
            <td>{{ item.barcode || '（電子書）' }}</td>
            <td>{{ formatTime(item.borrowing_time) }}</td>
            <td>
              <button class="return-btn" @click="handleReturn(item)">歸還</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">{{ loaded ? '目前沒有借閱中的書籍' : '載入中...' }}</p>
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

const borrows = ref([])
const msg = ref('')
const loaded = ref(false)

const loadMyBorrows = async () => {
  try {
    borrows.value = await request.get('/api/borrow/my')
  } catch (err) {
    msg.value = '載入借閱清單失敗'
  } finally {
    loaded.value = true
  }
}

const handleReturn = async (item) => {
  msg.value = ''
  try {
    await request.post('/api/borrow/return', { inventoryId: item.inventory_id })
    msg.value = `《${item.book_name}》歸還成功！`
    loadMyBorrows()  // 重新載入，已還的會消失
  } catch (err) {
    msg.value = err.response?.data?.message || '歸還失敗'
  }
}

const formatTime = (utcTime) => {
  if (!utcTime) return ''
  // 後端存 UTC，這裡轉成本地時間顯示
  const d = new Date(utcTime + 'Z')
  return d.toLocaleString('zh-TW')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadMyBorrows()
})
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
.content { padding: 32px; max-width: 1000px; margin: 0 auto; }
.content h2 { margin-bottom: 16px; color: #333; }
.msg { color: #1a6c3f; margin-bottom: 12px; font-weight: bold; }
.book-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0,0,0,0.06);
}
.book-table th, .book-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #eee;
}
.book-table th { background: #f7f7f7; font-size: 14px; color: #555; }
.return-btn {
  background: #c8811a;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 6px;
  cursor: pointer;
}
.empty { color: #999; margin-top: 20px; }
</style>