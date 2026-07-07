import axios from 'axios'

// 建立 axios 實例，統一設定後端網址
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 請求攔截器：每次發送前，自動帶上 token（若有）
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 回應攔截器：統一處理回應與錯誤
request.interceptors.response.use(
  response => response.data,
  error => {
    // 401/403：token 失效或未登入，導回登入頁
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem('token')
      // 若不在登入頁，導回登入
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default request