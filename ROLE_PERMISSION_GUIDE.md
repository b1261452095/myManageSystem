# 角色权限管理功能使用指南

## 📚 功能概述

已完成的角色权限管理功能（RBAC - 基于角色的访问控制）：

- ✅ 角色管理（增删改查）
- ✅ 权限管理（增删改查，支持树形结构）
- ✅ 角色权限分配
- ✅ 菜单级权限控制
- ✅ 完整的前后端实现

## 🗂️ 已创建的文件

### 后端文件

#### Controller 层
- `server/src/main/java/com/admin/controller/RoleController.java` - 角色管理接口
- `server/src/main/java/com/admin/controller/PermissionController.java` - 权限管理接口
- `server/src/main/java/com/admin/controller/MenuController.java` - 用户菜单接口

#### Service 层
- `server/src/main/java/com/admin/service/RoleService.java` - 角色服务接口
- `server/src/main/java/com/admin/service/impl/RoleServiceImpl.java` - 角色服务实现
- `server/src/main/java/com/admin/service/PermissionService.java` - 权限服务接口
- `server/src/main/java/com/admin/service/impl/PermissionServiceImpl.java` - 权限服务实现

#### Mapper 层
- `server/src/main/java/com/admin/mapper/RoleMapper.java` - 角色数据访问
- `server/src/main/java/com/admin/mapper/PermissionMapper.java` - 权限数据访问

### 前端文件

#### API 接口
- `web/src/api/role.ts` - 角色管理 API
- `web/src/api/permission.ts` - 权限管理 API（含用户菜单）

#### 页面组件
- `web/src/views/system/role/index.vue` - 角色管理页面
- `web/src/views/system/permission/index.vue` - 权限管理页面

#### 类型定义
- `web/src/types/index.ts` - 添加了 RoleInfo 和 PermissionInfo 类型

## 🎯 核心功能说明

### 1. 角色管理

#### 功能列表
- 分页查询角色列表
- 新增角色
- 编辑角色
- 删除角色（支持批量）
- 为角色分配权限

#### API 接口

```typescript
// 获取角色列表（分页）
getRoleList(params?: any)

// 获取所有角色（不分页）
getAllRoles()

// 根据ID获取角色
getRoleById(id: number)

// 新增角色
createRole(data: Partial<RoleInfo>)

// 更新角色
updateRole(id: number, data: Partial<RoleInfo>)

// 删除角色
deleteRole(id: number)

// 批量删除角色
batchDeleteRole(ids: number[])

// 为角色分配权限
assignPermissions(roleId: number, permissionIds: number[])

// 获取角色的权限列表
getRolePermissions(roleId: number)
```

#### 角色数据结构

```typescript
interface RoleInfo {
  id?: number
  name: string          // 角色名称，如：系统管理员
  code: string          // 角色编码，如：SUPER_ADMIN
  description?: string  // 角色描述
  createdAt?: string
  updatedAt?: string
}
```

### 2. 权限管理

#### 权限类型
- **menu** - 菜单权限（用于控制左侧菜单显示）
- **button** - 按钮权限（用于控制页面按钮显示）
- **api** - 接口权限（用于控制 API 访问）

#### 功能列表
- 查看权限树
- 新增权限（支持父子关系）
- 编辑权限
- 删除权限

#### API 接口

```typescript
// 获取权限树
getPermissionTree()

// 获取权限列表
getPermissionList(params?: any)

// 根据ID获取权限
getPermissionById(id: number)

// 新增权限
createPermission(data: Partial<PermissionInfo>)

// 更新权限
updatePermission(id: number, data: Partial<PermissionInfo>)

// 删除权限
deletePermission(id: number)

// 获取当前用户的菜单权限
getUserMenus()
```

#### 权限数据结构

```typescript
interface PermissionInfo {
  id?: number
  name: string          // 权限名称，如：用户管理
  code: string          // 权限编码，如：system:user:list
  type: string          // 权限类型：menu/button/api
  path?: string         // 路由路径，如：/system/user
  component?: string    // 组件路径，如：system/user/index
  icon?: string         // 图标名称，如：UserOutlined
  sort?: number         // 排序值
  parentId?: number     // 父权限ID
  createdAt?: string
  updatedAt?: string
  children?: PermissionInfo[]  // 子权限列表
}
```

### 3. 权限分配流程

1. **创建角色**
   - 进入"系统管理" → "角色管理"
   - 点击"新建角色"
   - 填写角色名称和编码
   - 保存

2. **分配权限**
   - 在角色列表中点击"分配权限"
   - 在权限树中勾选需要的权限
   - 保存

3. **为用户分配角色**
   - 进入"用户管理"
   - 编辑用户时选择角色
   - 用户将拥有该角色的所有权限

## 📊 数据库表结构

### sys_role (角色表)
- id - 主键
- name - 角色名称
- code - 角色编码
- description - 描述
- created_at - 创建时间
- updated_at - 更新时间
- deleted - 逻辑删除标记

### sys_permission (权限表)
- id - 主键
- name - 权限名称
- code - 权限编码
- type - 权限类型 (menu/button/api)
- path - 路由路径
- component - 组件路径
- icon - 图标
- sort - 排序
- parent_id - 父权限ID
- created_at - 创建时间
- updated_at - 更新时间
- deleted - 逻辑删除标记

### sys_role_permission (角色权限关联表)
- id - 主键
- role_id - 角色ID
- permission_id - 权限ID
- created_at - 创建时间

### sys_user_role (用户角色关联表)
- id - 主键
- user_id - 用户ID
- role_id - 角色ID
- created_at - 创建时间

## 🚀 使用示例

### 示例 1：创建一个普通用户角色

1. **创建角色**
```typescript
{
  name: "普通用户",
  code: "USER",
  description: "系统普通用户"
}
```

2. **分配权限**
只分配以下菜单权限：
- 工作台
- 个人中心

用户登录后只能看到这两个菜单。

### 示例 2：创建管理员角色

1. **创建角色**
```typescript
{
  name: "管理员",
  code: "ADMIN",
  description: "系统管理员"
}
```

2. **分配权限**
分配所有菜单权限：
- 工作台
- 用户管理
- 系统管理
  - 角色管理
  - 权限管理

## 🔐 权限控制说明

### 后端权限控制

使用 Spring Security 的 `@PreAuthorize` 注解：

```java
@PreAuthorize("hasRole('SUPER_ADMIN')")
public Result<List<Role>> getRoleList() {
    // 只有超级管理员才能访问
}
```

### 前端菜单控制

通过用户权限动态生成菜单：

```typescript
// 获取用户菜单权限
const userMenus = await getUserMenus()

// 根据权限生成菜单
const menuItems = generateMenuFromPermissions(userMenus)
```

## ⚠️ 注意事项

1. **超级管理员角色**
   - 角色编码：`SUPER_ADMIN`
   - 不允许删除
   - 拥有所有权限

2. **权限编码规范**
   - 使用冒号分隔：`模块:功能:操作`
   - 示例：`system:user:add`（系统模块-用户功能-新增操作）

3. **角色编码规范**
   - 使用大写字母和下划线
   - 示例：`SUPER_ADMIN`, `ADMIN`, `USER`

4. **权限删除限制**
   - 有子权限的权限不能删除
   - 需要先删除子权限

5. **角色删除限制**
   - 超级管理员角色不能删除
   - 已分配给用户的角色不能删除（建议添加此限制）

## 🔧 后续扩展建议

1. **按钮级权限控制**
   - 创建自定义指令 `v-permission`
   - 根据权限显示/隐藏按钮

2. **API 级权限控制**
   - 后端：使用拦截器验证 API 权限
   - 前端：请求前检查权限

3. **数据权限控制**
   - 根据用户角色控制数据范围
   - 如：只能查看自己部门的数据

4. **权限缓存**
   - 使用 Redis 缓存用户权限
   - 提高权限验证性能

5. **操作日志**
   - 记录角色权限分配操作
   - 记录权限变更日志

## 📝 测试流程

### 1. 测试角色管理
```bash
# 1. 启动后端
cd server && ./mvnw spring-boot:run

# 2. 启动前端
cd web && npm run dev

# 3. 登录系统 (admin/123456)

# 4. 进入"系统管理" → "角色管理"

# 5. 测试新增/编辑/删除角色功能
```

### 2. 测试权限管理
```bash
# 1. 进入"系统管理" → "权限管理"

# 2. 查看权限树结构

# 3. 测试新增子权限

# 4. 测试编辑/删除权限
```

### 3. 测试权限分配
```bash
# 1. 进入"角色管理"

# 2. 点击某个角色的"分配权限"

# 3. 勾选权限并保存

# 4. 查看权限是否正确保存
```

## 🎉 完成情况

✅ 所有功能已实现并可以正常使用！

需要的话可以根据实际业务需求继续扩展和优化。

