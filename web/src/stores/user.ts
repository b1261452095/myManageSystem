import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { getCurrentUser } from '@/api/user'
import { getUserMenus } from '@/api/menu'
import type { LoginRequest, UserInfo, PermissionInfo } from '@/types'
import router from '@/router'
import { message } from 'ant-design-vue'
import { transformMenuToRoutes } from '@/utils/route'

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

  // 获取用户菜单并生成动态路由
  const fetchUserMenusAndRoutes = async () => {
    // 如果正在加载或已经加载，直接返回
    if (routesLoading.value || routesLoaded.value) {
      return
    }
    
    try {
      routesLoading.value = true
      const data = await getUserMenus()
      
      console.log('📋 接口返回的菜单数据:', data)
      
      if (data) {
        userMenus.value = data
        console.log('✅ 保存到 userMenus:', userMenus.value)
        
        // 将菜单转换为路由
        const dynamicRoutes = transformMenuToRoutes(data)
        console.log('🛣️ 转换后的路由:', dynamicRoutes)
        
        // 添加动态路由到 Layout 下
        dynamicRoutes.forEach(route => {
          router.addRoute('Layout', route)
        })
        
        console.log('✅ 动态路由加载成功，总共添加了', dynamicRoutes.length, '个路由')
        console.log('📍 当前所有路由:', router.getRoutes().map(r => ({ name: r.name, path: r.path })))
      } else {
        console.warn('⚠️ 未获取到菜单数据，data 为空')
      }
      
      // 无论是否有数据，都标记为已加载，避免重复请求
      routesLoaded.value = true
    } catch (error) {
      console.error('获取用户菜单失败:', error)
      // 即使失败也标记为已加载，避免无限重试
      routesLoaded.value = true
      throw error
    } finally {
      routesLoading.value = false
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
    fetchUserMenusAndRoutes,
  }
})

