import { defineStore } from 'pinia'
import request from '@/api/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: localStorage.getItem('userId') || '',
    userName: localStorage.getItem('userName') || ''
  }),

  getters: {
    // 是否已登入
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    // 登入：呼叫後端 API，成功則儲存 token 與使用者資訊
    async login(phoneNumber, password) {
      const data = await request.post('/api/auth/login', {
        phoneNumber,
        password
      })
      this.token = data.token
      this.userId = data.userId
      this.userName = data.userName
      localStorage.setItem('token', data.token)
      localStorage.setItem('userId', data.userId)
      localStorage.setItem('userName', data.userName)
      return data
    },

    // 註冊：呼叫後端 API
    async register(payload) {
        return await request.post('/api/auth/register', payload)
    },

    // 登出：清除狀態
    logout() {
      this.token = ''
      this.userId = ''
      this.userName = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('userName')
    }
  }
})