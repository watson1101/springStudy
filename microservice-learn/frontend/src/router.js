import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Users from './views/Users.vue'
import Orders from './views/Orders.vue'
import Products from './views/Products.vue'
import Goods from './views/Goods.vue'
import Points from './views/Points.vue'
import Payments from './views/Payments.vue'
import HotNews from './views/HotNews.vue'
import System from './views/System.vue'
import Flowable from './views/Flowable.vue'
import Login from './views/Login.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login, meta: { title: '登录', public: true } },
    { path: '/', component: Home, meta: { title: '架构总览', requiresAuth: true } },
    { path: '/users', component: Users, meta: { title: '用户服务(PG)', requiresAuth: true } },
    { path: '/orders', component: Orders, meta: { title: '订单服务(MySQL)', requiresAuth: true } },
    { path: '/products', component: Products, meta: { title: '商品服务(PG+Sentinel)', requiresAuth: true } },
    { path: '/goods', component: Goods, meta: { title: '商品管理(MySQL+Sa-Token)', requiresAuth: true } },
    { path: '/points', component: Points, meta: { title: '积分服务', requiresAuth: true } },
    { path: '/payments', component: Payments, meta: { title: '支付服务', requiresAuth: true } },
    { path: '/hotnews', component: HotNews, meta: { title: '热点资讯(采集+消费)', requiresAuth: true } },
    { path: '/system', component: System, meta: { title: '系统配置(字典+CDC)', requiresAuth: true } },
    { path: '/flowable', component: Flowable, meta: { title: '工作流服务(Flowable)', requiresAuth: true } }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('ms_token')
  if (to.meta.public) {
    if (token && to.path === '/login') {
      const redirect = typeof to.query.redirect === 'string' && to.query.redirect.startsWith('/')
        ? to.query.redirect
        : '/'
      return { path: redirect }
    }
    return true
  }
  if (!token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }
  return true
})

export default router
