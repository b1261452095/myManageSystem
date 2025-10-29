import axios, { AxiosInstance, AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores/user'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    // 如果存在 token，添加到请求头
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 用于防止重复弹出登录过期提示
let isRefreshing = false

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse): any => {
    const res = response.data

    // 如果响应码不是 200，说明请求出错
    if (res.code !== 200 && res.code !== 0) {
      // 401: 未授权，跳转到登录页（避免重复提示）
      if (res.code === 401) {
        if (!isRefreshing) {
          isRefreshing = true
          message.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          // 3秒后重置标志
          setTimeout(() => {
            isRefreshing = false
          }, 3000)
        }
      } else {
        // 其他业务错误，显示错误信息
        message.error(res.message || '请求失败')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    // 直接返回 data，简化业务代码
    return res.data
  },
  (error) => {
    console.error('响应错误:', error)
    
    // HTTP 错误处理
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      
      switch (status) {
        case 401:
          // HTTP 401 - 未授权
          if (!isRefreshing) {
            isRefreshing = true
            message.error('未授权，请重新登录')
            const userStore = useUserStore()
            userStore.logout()
            setTimeout(() => {
              isRefreshing = false
            }, 3000)
          }
          break
        case 403:
          message.error('拒绝访问，权限不足')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error(data?.message || '服务器内部错误')
          break
        case 502:
          message.error('网关错误，请稍后重试')
          break
        case 503:
          message.error('服务暂时不可用，请稍后重试')
          break
        default:
          message.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      console.error('请求超时或网络错误:', error.request)
      message.error('网络连接失败，请检查网络设置')
    } else {
      // 请求配置出错
      console.error('请求配置错误:', error.message)
      message.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

// 导出时强制类型为 any，避免业务代码中的类型错误
export default service as any

