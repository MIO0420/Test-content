<template>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

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
    loadMyBorrows()
  } catch (err) {
    msg.value = err.response?.data?.message || '歸還失敗'
  }
}

const formatTime = (utcTime) => {
  if (!utcTime) return ''
  const d = new Date(utcTime + 'Z')
  return d.toLocaleString('zh-TW')
}

onMounted(() => {
  loadMyBorrows()
})
</script>

<style scoped>
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