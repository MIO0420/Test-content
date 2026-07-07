<template>
  <div class="page">
    <header class="topbar">
      <h1>📚 圖書借閱系統</h1>
        <div class="user-area">
        <router-link to="/my-borrows" class="nav-link">我的借閱</router-link>
        <router-link to="/profile" class="nav-link">個人資料</router-link>
        <span>{{ authStore.userName }} 您好</span>
        <button class="logout-btn" @click="handleLogout">登出</button>
      </div>
    </header>

    <main class="content">
      <h2>書籍列表</h2>

      <div class="filter-bar">
        <label>分類篩選：</label>
        <select v-model="selectedCategory">
          <option value="">全部分類</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
        <span class="count">共 {{ filteredBooks.length }} 本</span>
      </div>
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
          <template v-for="book in filteredBooks" :key="book.isbn">
            <tr>
              <td>
                <a href="#" class="book-name" @click.prevent="toggleDetail(book.isbn)">
                  {{ book.name }}
                </a>
              </td>
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
            <tr v-if="expandedIsbn === book.isbn" class="detail-row">
              <td colspan="6">
                <div class="detail">
                  <p v-if="book.translator"><strong>譯者：</strong>{{ book.translator }}</p>
                  <p v-if="book.original_author"><strong>原文作者：</strong>{{ book.original_author }}</p>
                  <p v-if="book.publisher"><strong>出版社：</strong>{{ book.publisher }}</p>
                  <p v-if="book.published_date"><strong>出版日期：</strong>{{ book.published_date }}</p>
                  <p><strong>ISBN：</strong>{{ book.isbn }}</p>
                  <p v-if="book.introduction"><strong>簡介：</strong>{{ book.introduction }}</p>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
      <p v-else class="empty">載入中或暫無書籍...</p>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import request from '@/api/request'

const router = useRouter()
const authStore = useAuthStore()

const books = ref([])
const selectedCategory = ref('')

// 從書單自動抽出所有不重複的分類
const categories = computed(() => {
  const set = new Set(books.value.map(b => b.category).filter(Boolean))
  return Array.from(set)
})

// 依選擇的分類過濾書單
const filteredBooks = computed(() => {
  if (!selectedCategory.value) return books.value
  return books.value.filter(b => b.category === selectedCategory.value)
})
const msg = ref('')
const expandedIsbn = ref('')

const toggleDetail = (isbn) => {
  expandedIsbn.value = expandedIsbn.value === isbn ? '' : isbn
}

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
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.filter-bar label { font-size: 14px; color: #333; }
.filter-bar select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}
.filter-bar .count { font-size: 14px; color: #888; margin-left: auto; }
.book-name {
  color: #1a6c3f;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
}
.book-name:hover { text-decoration: underline; }
.detail-row td { background: #f9faf9; }
.detail { padding: 8px 4px; font-size: 14px; color: #555; line-height: 1.8; }
.detail strong { color: #333; }
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