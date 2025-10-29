import request from '@/utils/request'
import type { PermissionInfo } from '@/types'

/**
 * 获取当前用户的菜单树（根据权限）
 */
export const getUserMenus = () => {
  return request({
    url: '/menu/user-menus',
    method: 'get'
  })
}

/**
 * 获取菜单树（所有菜单，用于管理）
 */
export const getMenuTree = () => {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

/**
 * 获取菜单详情
 */
export const getMenuById = (id: number) => {
  return request({
    url: `/menu/${id}`,
    method: 'get'
  })
}

/**
 * 创建菜单
 */
export const createMenu = (data: Partial<PermissionInfo>) => {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

/**
 * 更新菜单
 */
export const updateMenu = (id: number, data: Partial<PermissionInfo>) => {
  return request({
    url: `/menu/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除菜单
 */
export const deleteMenu = (id: number) => {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除菜单
 */
export const batchDeleteMenu = (ids: number[]) => {
  return request({
    url: '/menu/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取父菜单选项
 */
export const getParentMenuOptions = () => {
  return request({
    url: '/menu/parent-options',
    method: 'get'
  })
}

