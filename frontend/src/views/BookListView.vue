<template>
  <div class="page">
    <header class="topbar">
      <h1>📚 圖書借閱系統</h1>
      <div class="user-area">
        <span>{{ authStore.userName }} 您好</span>
        <button class="logout-btn" @click="handleLogout">登出</button>
      </div>
    </header>

    <main class="content">
      <h2>書籍列表</h2>
      <p v-if="msg" class="msg">{{ msg }}</p>

      <table v-if="books.length" class="book-table">
        <thead>
          <tr>
            <th>書名</th>
            <th>作者</th>
            <th>分類</th>
            <th>語言</th>
            <th>可借 / 總數</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="book in books" :key="book.isbn">
            <td>{{ book.name }}</td>
            <td>{{ book.author }}</td>
            <td>{{ book.category }}</td>
            <td>{{ book.language }}</td>
            <td>{{ book.available_count }} / {{ book.total_count }}</td>
            <td>
              <button
                class="borrow-btn"
                :disabled="book.available_count < 1"
                @click="handleBorrow(book)">
                {{ book.available_count < 1 ? '已借完' : '借閱' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">載入中或暫無書籍...</p>
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

const books = ref([])
const msg = ref('')

const loadBooks = async () => {
  try {
    books.value = await request.get('/api/books')
  } catch (err) {
    msg.value = '載入書籍失敗'
  }
}

const handleBorrow = async (book) => {
  msg.value = ''
  if (!book.available_inventory_id) {
    msg.value = `《${book.name}》目前無可借庫存`
    return
  }
  try {
    await request.post('/api/borrow', { inventoryId: book.available_inventory_id })
    msg.value = `《${book.name}》借閱成功！`
    loadBooks()  // 重新載入，更新可借數量
  } catch (err) {
    msg.value = err.response?.data?.message || '借閱失敗'
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadBooks()
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
.borrow-btn {
  background: #1a6c3f;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 6px;
  cursor: pointer;
}
.borrow-btn:disabled { background: #bbb; cursor: not-allowed; }
.empty { color: #999; margin-top: 20px; }
</style>