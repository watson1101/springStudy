import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginAPI, registerAPI, getUserInfoAPI } from '../api/auth.js'

export const useAuthStore = defineStore('auth', () => {
  // 从 localStorage 恢复状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  // 登录
  async function login(credentials) {
    const res = await loginAPI(credentials)
    if (res.code === 200 && res.data) {
      token.value = res.data.token || ''
      userInfo.value = res.data.userInfo || null
      localStorage.setItem('token', token.value)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    return res
  }

  // 注册
  async function register(data) {
    const res = await registerAPI(data)
    return res
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return
    const res = await getUserInfoAPI()
    if (res.code === 200) {
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    return res
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, login, register, fetchUserInfo, logout }
})
