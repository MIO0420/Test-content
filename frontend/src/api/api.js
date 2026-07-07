import request from './request'

// ===== 書籍 =====
export async function getBooks() {
  const res = await request.get('/api/books')
  return res.data
}

// ===== 借還書 =====
export async function borrowBook(inventoryId) {
  const res = await request.post('/api/borrow', { inventoryId })
  return res.data
}

export async function returnBook(inventoryId) {
  const res = await request.post('/api/borrow/return', { inventoryId })
  return res.data
}

export async function getMyBorrows() {
  const res = await request.get('/api/borrow/my')
  return res.data
}

// ===== 個人資料 / 分館 =====
export async function getMyProfile() {
  const res = await request.get('/api/profile')
  return res.data
}

export async function updateMyProfile(data) {
  const res = await request.put('/api/profile', data)
  return res.data
}

export async function getBranches() {
  const res = await request.get('/api/branches')
  return res.data
}