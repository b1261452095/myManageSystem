import request from '@/utils/request'
import type { LoginRequest } from '@/types'

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}

/**
 * 刷新 Token
 */
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post',
  })
}

