import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/User.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/goods',
    name: 'Goods',
    component: () => import('@/views/goods/GoodsList.vue')
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/views/order/OrderList.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
