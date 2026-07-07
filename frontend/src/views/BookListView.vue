<template>
  <div class="page">
    <header class="topbar">
      <div class="topbar-inner">
        <h1>📚 圖書借閱系統</h1>
        <nav class="user-area">
          <router-link to="/books" class="nav-link active">書籍列表</router-link>
          <router-link to="/my-borrows" class="nav-link">我的借閱</router-link>
          <router-link to="/profile" class="nav-link">個人資料</router-link>
          <span class="username">{{ authStore.userName }} 您好</span>
          <button class="logout-btn" @click="handleLogout">登出</button>
        </nav>
      </div>
    </header>

    <main class="content">
      <div class="page-head">
        <h2>書籍列表</h2>
        <div class="filter-bar">
          <label>分類篩選</label>
          <select v-model="selectedCategory">
            <option value="">全部分類</option>
            <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
          </select>
          <span class="count">共 {{ filteredBooks.length }} 本</span>
        </div>
      </div>

      <p v-if="msg" class="msg">{{ msg }}</p>

      <div class="table-wrap">
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
                <td><span class="tag">{{ book.category }}</span></td>
                <td>{{ book.language }}</td>
                <td>
                  <span :class="book.available_count > 0 ? 'stock-ok' : 'stock-none'">
                    {{ book.available_count }} / {{ book.total_count }}
                  </span>
                </td>
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
      </div>
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
.page { min-height: 100vh; background: var(--bg); }
.topbar {
  background: var(--esun-green);
  color: white;
  box-shadow: var(--shadow);
}
.topbar-inner {
  max-width: 1080px;
  margin: 0 auto;
  padding: 16px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.topbar h1 { font-size: 19px; font-weight: 600; }
.user-area { display: flex; align-items: center; gap: 8px; font-size: 14px; }
.nav-link {
  color: rgba(255,255,255,0.85);
  text-decoration: none;
  padding: 7px 12px;
  border-radius: 8px;
  transition: all 0.2s;
}
.nav-link:hover { background: rgba(255,255,255,0.12); color: white; }
.nav-link.active { background: rgba(255,255,255,0.18); color: white; font-weight: 600; }
.username { margin: 0 8px 0 12px; opacity: 0.95; }
.logout-btn {
  background: rgba(255,255,255,0.15);
  color: white;
  border: 1px solid rgba(255,255,255,0.4);
  padding: 7px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.logout-btn:hover { background: rgba(255,255,255,0.25); }
.content { padding: 32px; max-width: 1080px; margin: 0 auto; }
.page-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}
.page-head h2 { font-size: 22px; color: var(--text); }
.filter-bar { display: flex; align-items: center; gap: 10px; }
.filter-bar label { font-size: 14px; color: var(--text-light); }
.filter-bar select {
  padding: 8px 12px;
  border: 1.5px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}
.filter-bar select:focus {
  outline: none;
  border-color: var(--esun-green);
  box-shadow: 0 0 0 3px var(--esun-green-light);
}
.filter-bar .count {
  font-size: 13px;
  color: var(--text-light);
  background: white;
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid var(--border);
}
.msg {
  color: var(--esun-green);
  margin-bottom: 16px;
  font-weight: 600;
  padding: 12px 16px;
  background: var(--esun-green-light);
  border-radius: 8px;
}
.table-wrap {
  background: var(--card-bg);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  overflow: hidden;
}
.book-table { width: 100%; border-collapse: collapse; }
.book-table th {
  background: #f8fafb;
  padding: 14px 18px;
  text-align: left;
  font-size: 13px;
  color: var(--text-light);
  font-weight: 600;
  border-bottom: 2px solid var(--border);
}
.book-table td {
  padding: 15px 18px;
  border-bottom: 1px solid var(--border);
  font-size: 14px;
}
.book-table tbody tr:hover { background: #fafbfc; }
.book-name {
  color: var(--esun-green);
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}
.book-name:hover { text-decoration: underline; }
.tag {
  display: inline-block;
  padding: 3px 10px;
  background: var(--esun-green-light);
  color: var(--esun-green);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.stock-ok { color: var(--esun-green); font-weight: 600; }
.stock-none { color: var(--text-light); }
.borrow-btn {
  background: var(--esun-green);
  color: white;
  border: none;
  padding: 7px 18px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}
.borrow-btn:hover:not(:disabled) {
  background: var(--esun-green-dark);
  transform: translateY(-1px);
}
.borrow-btn:disabled { background: #cbd5db; cursor: not-allowed; }
.detail-row td { background: #f8fafb; padding: 0; }
.detail {
  padding: 16px 24px;
  font-size: 14px;
  color: var(--text);
  line-height: 1.9;
}
.detail strong { color: var(--esun-green); }
.empty { color: var(--text-light); padding: 40px; text-align: center; }
</style>