import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/books'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
{
      path: '/',
      component: () => import('@/components/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'books',
          name: 'books',
          component: () => import('@/views/BookListView.vue')
        },
        {
          path: 'my-borrows',
          name: 'my-borrows',
          component: () => import('@/views/MyBorrowsView.vue')
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue')
        },
      ]
    },
  ]
})

// 路由守衛：需要登入的頁面，未登入就導向登入頁
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router