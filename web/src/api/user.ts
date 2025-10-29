import request from '@/utils/request'
import type { UserInfo } from '@/types'

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/user/info',
    method: 'get',
  })
}

/**
 * 获取用户列表（分页）
 */
export function getUserList(params?: any) {
  return request({
    url: '/user/list',
    method: 'get',
    params,
  })
}

/**
 * 根据ID获取用户
 */
export function getUserById(id: number) {
  return request({
    url: `/user/${id}`,
    method: 'get',
  })
}

/**
 * 新增用户
 */
export function createUser(data: Partial<UserInfo>) {
  return request({
    url: '/user',
    method: 'post',
    data,
  })
}

/**
 * 更新用户
 */
export function updateUser(id: number, data: Partial<UserInfo>) {
  return request({
    url: `/user/${id}`,
    method: 'put',
    data,
  })
}

/**
 * 删除用户
 */
export function deleteUser(id: number) {
  return request({
    url: `/user/${id}`,
    method: 'delete',
  })
}

/**
 * 批量删除用户
 */
export function batchDeleteUser(ids: number[]) {
  return request({
    url: '/user/batch',
    method: 'delete',
    data: ids,
  })
}

