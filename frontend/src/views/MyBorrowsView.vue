<template>
  <div class="page">
    <header class="topbar">
      <div class="topbar-inner">
        <h1>📚 圖書借閱系統</h1>
        <nav class="user-area">
          <router-link to="/books" class="nav-link">書籍列表</router-link>
          <router-link to="/my-borrows" class="nav-link active">我的借閱</router-link>
          <router-link to="/profile" class="nav-link">個人資料</router-link>
          <span class="username">{{ authStore.userName }} 您好</span>
          <button class="logout-btn" @click="handleLogout">登出</button>
        </nav>
      </div>
    </header>

    <main class="content">
      <h2>我的借閱</h2>
      <p v-if="msg" class="msg">{{ msg }}</p>

      <div class="table-wrap" v-if="borrows.length">
        <table class="book-table">
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
              <td class="title">{{ item.book_name }}</td>
              <td>{{ item.author }}</td>
              <td>{{ item.barcode || '（電子書）' }}</td>
              <td>{{ formatTime(item.borrowing_time) }}</td>
              <td>
                <button class="return-btn" @click="handleReturn(item)">歸還</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else class="empty-card">
        {{ loaded ? '目前沒有借閱中的書籍' : '載入中...' }}
      </div>
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
.content { padding: 32px; max-width: 1080px; margin: 0 auto; }
.content h2 { font-size: 22px; margin-bottom: 20px; color: var(--text); }
.msg {
  color: var(--esun-green); margin-bottom: 16px; font-weight: 600;
  padding: 12px 16px; background: var(--esun-green-light); border-radius: 8px;
}
.table-wrap {
  background: var(--card-bg); border-radius: var(--radius);
  box-shadow: var(--shadow); overflow: hidden;
}
.book-table { width: 100%; border-collapse: collapse; }
.book-table th {
  background: #f8fafb; padding: 14px 18px; text-align: left;
  font-size: 13px; color: var(--text-light); font-weight: 600;
  border-bottom: 2px solid var(--border);
}
.book-table td { padding: 15px 18px; border-bottom: 1px solid var(--border); font-size: 14px; }
.book-table tbody tr:hover { background: #fafbfc; }
.title { font-weight: 600; color: var(--text); }
.return-btn {
  background: #c8811a; color: white; border: none;
  padding: 7px 18px; border-radius: 8px; cursor: pointer;
  font-size: 14px; font-weight: 500; transition: all 0.2s;
}
.return-btn:hover { background: #a86913; transform: translateY(-1px); }
.empty-card {
  background: var(--card-bg); border-radius: var(--radius); box-shadow: var(--shadow);
  padding: 60px; text-align: center; color: var(--text-light);
}
</style>