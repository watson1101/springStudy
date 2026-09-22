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

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/users', component: Users, meta: { title: '用户服务(PG)' } },
    { path: '/orders', component: Orders, meta: { title: '订单服务(MySQL)' } },
    { path: '/products', component: Products, meta: { title: '商品服务(PG+Sentinel)' } },
    { path: '/goods', component: Goods, meta: { title: '商品管理(MySQL+Sa-Token)' } },
    { path: '/points', component: Points, meta: { title: '积分服务' } },
    { path: '/payments', component: Payments, meta: { title: '支付服务' } },
    { path: '/hotnews', component: HotNews, meta: { title: '热点资讯(采集+消费)' } },
    { path: '/system', component: System, meta: { title: '系统配置(字典+CDC)' } },
    { path: '/flowable', component: Flowable, meta: { title: '工作流服务(Flowable)' } }
  ]
})

export default router
