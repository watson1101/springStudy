import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Users from './views/Users.vue'
import Orders from './views/Orders.vue'
import Products from './views/Products.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/users', component: Users, meta: { title: '用户服务(PG)' } },
    { path: '/orders', component: Orders, meta: { title: '订单服务(MySQL)' } },
    { path: '/products', component: Products, meta: { title: '商品服务(PG+Sentinel)' } }
  ]
})

export default router
