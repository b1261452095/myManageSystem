# 快速启动指南

## 第一次启动

### 1. 确保后端已启动

在启动前端之前，请确保后端服务已经正常运行在 `http://localhost:8080`

```bash
# 在项目根目录下启动后端
cd ../
./start-backend.sh
```

### 2. 安装依赖

```bash
cd web
npm install
```

### 3. 启动开发服务器

```bash
npm run dev
```

开发服务器将在 `http://localhost:3000` 启动

### 4. 登录系统

打开浏览器访问 `http://localhost:3000`，使用以下账号登录：

- 用户名：`admin`
- 密码：`123456`

## 项目结构说明

```
src/
├── api/              # API 接口定义
│   ├── auth.ts       # 认证相关接口（登录、登出、刷新token）
│   └── user.ts       # 用户相关接口
├── layout/           # 布局组件
│   └── index.vue     # 主布局（左侧菜单 + 顶部栏 + 内容区）
├── router/           # 路由配置
│   └── index.ts      # 路由定义和守卫
├── stores/           # Pinia 状态管理
│   └── user.ts       # 用户状态（登录信息、用户信息等）
├── styles/           # 全局样式
│   └── index.css     # 基础样式
├── types/            # TypeScript 类型定义
│   └── index.ts      # 通用类型（请求/响应类型等）
├── utils/            # 工具函数
│   └── request.ts    # Axios 封装（请求/响应拦截器）
├── views/            # 页面组件
│   ├── dashboard/    # 工作台
│   ├── login/        # 登录页
│   ├── profile/      # 个人中心
│   └── user/         # 用户管理
├── App.vue           # 根组件
└── main.ts           # 应用入口
```

## 开发指南

### 添加新页面

1. 在 `src/views` 下创建新的页面组件
2. 在 `src/router/index.ts` 中添加路由配置
3. 如果需要在菜单中显示，在 `src/layout/index.vue` 的 `menuItems` 中添加菜单项

### 调用后端 API

1. 在 `src/types/index.ts` 中定义请求和响应的 TypeScript 类型
2. 在 `src/api` 目录下创建对应的 API 文件
3. 使用封装好的 `request` 工具发起请求

示例：

```typescript
// src/types/index.ts
export interface User {
  id: number
  username: string
  // ...
}

// src/api/user.ts
import request from '@/utils/request'
import type { Result, User } from '@/types'

export function getUserList() {
  return request<Result<User[]>>({
    url: '/user/list',
    method: 'get',
  })
}

// 在组件中使用
import { getUserList } from '@/api/user'

const fetchUsers = async () => {
  const res = await getUserList()
  console.log(res.data)
}
```

### 使用 Pinia Store

```typescript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 访问状态
console.log(userStore.token)
console.log(userStore.userInfo)

// 调用方法
await userStore.login({ username: 'admin', password: '123456' })
await userStore.logout()
```

### Ant Design Vue 组件按需加载

本项目已配置自动按需导入，直接使用组件即可，无需手动导入：

```vue
<template>
  <a-button type="primary">按钮</a-button>
  <a-input v-model:value="value" />
  <a-table :columns="columns" :data-source="data" />
</template>
```

## 常见问题

### 1. 启动后页面空白

检查浏览器控制台是否有错误信息，通常是因为：
- 后端服务未启动
- API 代理配置错误
- 依赖安装不完整

### 2. 登录后接口返回 401

- 检查 token 是否正确保存到 localStorage
- 检查请求头是否正确携带 Authorization
- 检查后端 JWT 配置是否正确

### 3. 路由跳转不生效

- 检查路由配置是否正确
- 检查路由守卫逻辑
- 检查是否有重复的路由定义

### 4. 组件样式不生效

- 检查是否正确导入了 Ant Design Vue 的样式
- 检查 CSS 作用域是否正确
- 清除浏览器缓存后重试

## 构建生产版本

```bash
# 构建
npm run build

# 预览构建结果
npm run preview
```

构建产物将在 `dist` 目录下。

## 下一步

- 完善用户管理的增删改查功能
- 添加角色权限管理模块
- 优化界面交互和用户体验
- 添加更多的业务功能模块

