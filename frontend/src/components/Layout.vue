<template>
  <div class="page">
    <header class="topbar">
      <div class="topbar-inner">
        <h1>
            <img src="@/assets/logo.png" alt="logo" class="logo" />
            圖書借閱系統
        </h1>
        <nav class="user-area">
          <router-link to="/books" class="nav-link">書籍列表</router-link>
          <router-link to="/my-borrows" class="nav-link">我的借閱</router-link>
          <router-link to="/profile" class="nav-link">個人資料</router-link>
          <span class="username">{{ authStore.userName }} 您好</span>
          <button class="logout-btn" @click="handleLogout">登出</button>
        </nav>
      </div>
    </header>

    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
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
.logo {
  height: 50px;
  width: 50px;
  vertical-align: middle;
  margin-right: 8px;
  object-fit: contain;
}
.nav-link:hover { background: rgba(255,255,255,0.12); color: white; }
.nav-link.router-link-active { background: rgba(255,255,255,0.18); color: white; font-weight: 600; }
.username { margin: 0 8px 0 12px; opacity: 0.95; }
.logout-btn {
  background: rgba(255,255,255,0.15); color: white;
  border: 1px solid rgba(255,255,255,0.4);
  padding: 7px 14px; border-radius: 8px; cursor: pointer; transition: all 0.2s;
}
.logout-btn:hover { background: rgba(255,255,255,0.25); }
.content { padding: 32px; max-width: 1080px; margin: 0 auto; }
</style>