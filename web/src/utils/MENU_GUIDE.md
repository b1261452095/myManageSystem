# 菜单配置指南

## 概述

左侧菜单已经实现了从路由配置自动生成，无需手动维护菜单数据。菜单会根据路由配置的 `meta` 信息自动生成。

## 如何添加新菜单

只需要在 `router/index.ts` 中添加路由配置即可，菜单会自动生成。

### 基本配置

```typescript
{
  path: '/example',
  name: 'Example',
  component: () => import('@/views/example/index.vue'),
  meta: {
    title: '示例页面',           // 必填：菜单显示的文字
    icon: 'AppstoreOutlined',   // 可选：图标名称（来自 @ant-design/icons-vue）
    hideInMenu: false,          // 可选：是否在菜单中隐藏，默认 false
  },
}
```

### 一级菜单示例

```typescript
{
  path: '/dashboard',
  name: 'Dashboard',
  component: () => import('@/views/dashboard/index.vue'),
  meta: {
    title: '工作台',
    icon: 'DashboardOutlined',
  },
}
```

### 多级菜单示例

```typescript
{
  path: '/system',
  name: 'System',
  meta: {
    title: '系统管理',
    icon: 'SettingOutlined',
  },
  children: [
    {
      path: '/system/user',
      name: 'SystemUser',
      component: () => import('@/views/system/user/index.vue'),
      meta: {
        title: '用户管理',
        icon: 'UserOutlined',
      },
    },
    {
      path: '/system/role',
      name: 'SystemRole',
      component: () => import('@/views/system/role/index.vue'),
      meta: {
        title: '角色管理',
        icon: 'TeamOutlined',
      },
    },
  ],
}
```

### 隐藏菜单示例

某些页面（如个人中心、详情页）需要路由但不需要在菜单中显示：

```typescript
{
  path: '/profile',
  name: 'Profile',
  component: () => import('@/views/profile/index.vue'),
  meta: {
    title: '个人中心',
    icon: 'UserOutlined',
    hideInMenu: true,  // 不在左侧菜单中显示
  },
}
```

## Meta 配置说明

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 菜单显示的文字和页面标题 |
| icon | string | 否 | 图标名称，来自 @ant-design/icons-vue |
| hideInMenu | boolean | 否 | 是否在菜单中隐藏，默认 false |
| requiresAuth | boolean | 否 | 是否需要登录认证 |

## 可用图标

所有图标来自 `@ant-design/icons-vue`，常用图标包括：

- `DashboardOutlined` - 仪表盘
- `UserOutlined` - 用户
- `TeamOutlined` - 团队
- `SettingOutlined` - 设置
- `AppstoreOutlined` - 应用
- `UnorderedListOutlined` - 列表
- `FileOutlined` - 文件
- `BankOutlined` - 银行/组织
- `ShoppingOutlined` - 购物
- `BarChartOutlined` - 图表

更多图标请参考：https://antdv.com/components/icon-cn

## 工作原理

1. `utils/menu.ts` 提供了 `generateMenuFromRoutes` 函数，用于将路由配置转换为菜单数据
2. `layout/index.vue` 中通过 `computed` 自动从路由配置生成菜单
3. 当路由变化时，自动更新菜单选中状态和展开状态

## 优势

- ✅ **单一数据源**：路由和菜单使用同一份配置，避免数据不一致
- ✅ **自动同步**：添加/修改路由后，菜单自动更新
- ✅ **类型安全**：基于 TypeScript，提供完整的类型提示
- ✅ **易于维护**：只需要维护路由配置，无需额外维护菜单

