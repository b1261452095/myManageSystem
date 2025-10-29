import request from '@/utils/request'
import type { PermissionInfo } from '@/types'

/**
 * 获取权限树
 */
export function getPermissionTree() {
  return request({
    url: '/permission/tree',
    method: 'get',
  })
}

/**
 * 获取权限列表
 */
export function getPermissionList(params?: any) {
  return request({
    url: '/permission/list',
    method: 'get',
    params,
  })
}

/**
 * 根据ID获取权限
 */
export function getPermissionById(id: number) {
  return request({
    url: `/permission/${id}`,
    method: 'get',
  })
}

/**
 * 新增权限
 */
export function createPermission(data: Partial<PermissionInfo>) {
  return request({
    url: '/permission',
    method: 'post',
    data,
  })
}

/**
 * 更新权限
 */
export function updatePermission(id: number, data: Partial<PermissionInfo>) {
  return request({
    url: `/permission/${id}`,
    method: 'put',
    data,
  })
}

/**
 * 删除权限
 */
export function deletePermission(id: number) {
  return request({
    url: `/permission/${id}`,
    method: 'delete',
  })
}

/**
 * 获取当前用户的菜单权限
 */
export function getUserMenus() {
  return request({
    url: '/menu/user-menus',
    method: 'get',
  })
}

