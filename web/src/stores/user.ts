import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { getCurrentUser } from '@/api/user'
import type { LoginRequest, UserInfo, PermissionInfo } from '@/types'
import router from '@/router'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const userMenus = ref<PermissionInfo[]>([])
  const routesLoaded = ref<boolean>(false) // 路由是否已加载
  const routesLoading = ref<boolean>(false) // 路由是否正在加载中

  // 设置 token
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除 token
  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
  }

  // 设置用户信息
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = null
    userMenus.value = []
    routesLoaded.value = false
    routesLoading.value = false
  }

  // 登录
  const login = async (loginForm: LoginRequest) => {
    try {
      debugger
      const res = await loginApi(loginForm)
      if (res?.token) {
        setToken(res.token)
        message.success('登录成功')
        // 获取用户信息
        await fetchUserInfo()
        // 获取用户菜单并生成动态路由
        await fetchUserMenusAndRoutes()
        // 跳转到首页
        router.push('/')
      }
    } catch (error: any) {
      message.error(error.message || '登录失败')
      throw error
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const res = await getCurrentUser()
      if (res?.username) {
        setUserInfo(res)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }


  // 登出
  const logout = async () => {
    try {
      // 只有在有 token 时才调用后端接口
      if (token.value) {
        await logoutApi()
      }
    } catch (error) {
      console.error('登出失败:', error)
      // 即使登出接口失败，也要清除本地数据
    } finally {
      clearToken()
      clearUserInfo()
      router.push('/login')
    }
  }

  return {
    token,
    userInfo,
    userMenus,
    routesLoaded,
    routesLoading,
    setToken,
    clearToken,
    setUserInfo,
    clearUserInfo,
    login,
    logout,
    fetchUserInfo,
  
  }
})

