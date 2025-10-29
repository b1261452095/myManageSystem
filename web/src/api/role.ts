import request from '@/utils/request'
import type { RoleInfo } from '@/types'

/**
 * 获取角色列表（分页）
 */
export function getRoleList(params?: any) {
  return request({
    url: '/role/list',
    method: 'get',
    params,
  })
}

/**
 * 获取所有角色（不分页）
 */
export function getAllRoles() {
  return request({
    url: '/role/all',
    method: 'get',
  })
}

/**
 * 根据ID获取角色
 */
export function getRoleById(id: number) {
  return request({
    url: `/role/${id}`,
    method: 'get',
  })
}

/**
 * 新增角色
 */
export function createRole(data: Partial<RoleInfo>) {
  return request({
    url: '/role',
    method: 'post',
    data,
  })
}

/**
 * 更新角色
 */
export function updateRole(id: number, data: Partial<RoleInfo>) {
  return request({
    url: `/role/${id}`,
    method: 'put',
    data,
  })
}

/**
 * 删除角色
 */
export function deleteRole(id: number) {
  return request({
    url: `/role/${id}`,
    method: 'delete',
  })
}

/**
 * 批量删除角色
 */
export function batchDeleteRole(ids: number[]) {
  return request({
    url: '/role/batch',
    method: 'delete',
    data: ids,
  })
}

/**
 * 为角色分配权限
 */
export function assignPermissions(roleId: number, permissionIds: number[]) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'post',
    data: permissionIds,
  })
}

/**
 * 获取角色的权限列表
 */
export function getRolePermissions(roleId: number) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'get',
  })
}

