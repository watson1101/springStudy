import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useAppStore = defineStore('app', () => {
  const darkMode = ref(localStorage.getItem('darkMode') === 'true')
  const serverUrl = ref(localStorage.getItem('serverUrl') || 'http://localhost:8080')
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')

  // 切换主题
  function toggleDarkMode() {
    darkMode.value = !darkMode.value
    applyTheme()
  }

  function applyTheme() {
    document.documentElement.setAttribute('data-theme', darkMode.value ? 'dark' : 'light')
    localStorage.setItem('darkMode', darkMode.value.toString())
  }

  // 保存登录信息
  function setLoginInfo(t: string, u: string) {
    token.value = t
    username.value = u
    localStorage.setItem('token', t)
    localStorage.setItem('username', u)
  }

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  function setServerUrl(url: string) {
    serverUrl.value = url
    localStorage.setItem('serverUrl', url)
  }

  // 初始应用主题
  applyTheme()

  return {
    darkMode, serverUrl, token, username,
    toggleDarkMode, applyTheme, setLoginInfo, logout, setServerUrl
  }
})
