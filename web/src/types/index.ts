// 通用响应类型
export interface Result<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应类型
export interface PageResult<T = any> {
  data: T[]
  total: number
  current: number
  pageSize: number
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  tokenType: string
  expiresIn: number
}

// 用户信息
export interface UserInfo {
  id?: number
  username: string
  password?: string
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  status?: number
  roleIds?: number[]
  roles?: RoleInfo[]
  createTime?: string
  updateTime?: string
}

// 菜单项
export interface MenuItem {
  key: string
  label: string
  icon?: string
  path?: string
  children?: MenuItem[]
}

// 角色信息
export interface RoleInfo {
  id?: number
  name: string
  code: string
  description?: string
  createdAt?: string
  updatedAt?: string
}

// 权限信息
export interface PermissionInfo {
  id?: number
  name: string
  code: string
  type: string // menu-菜单，button-按钮，api-接口
  path?: string
  component?: string
  icon?: string
  sort?: number
  parentId?: number
  visible?: number // 是否显示：0-隐藏，1-显示
  status?: number // 状态：0-禁用，1-启用
  redirect?: string // 重定向路径
  alwaysShow?: number // 是否总是显示：0-否，1-是
  createdAt?: string
  updatedAt?: string
  children?: PermissionInfo[]
}

