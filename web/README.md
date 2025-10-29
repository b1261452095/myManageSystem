# 企业级后台管理系统 - 前端

基于 Vue3 + Vite + Pinia + Ant Design Vue + TypeScript 构建的现代化企业级后台管理系统。

## 技术栈

- **框架**: Vue 3.4
- **构建工具**: Vite 5.x
- **状态管理**: Pinia 2.x
- **UI 组件库**: Ant Design Vue 4.x (按需加载)
- **路由**: Vue Router 4.x
- **类型检查**: TypeScript 5.x
- **HTTP 客户端**: Axios

## 项目特性

- ✅ Vue3 Composition API
- ✅ TypeScript 类型安全
- ✅ Ant Design Vue 按需加载
- ✅ Pinia 状态管理
- ✅ Vue Router 路由守卫
- ✅ Axios 请求拦截
- ✅ JWT 身份认证
- ✅ 响应式布局
- ✅ 优雅的登录页面
- ✅ 左侧导航菜单
- ✅ 用户中心

## 目录结构

```
web/
├── src/
│   ├── api/              # API 接口
│   │   ├── auth.ts       # 认证接口
│   │   └── user.ts       # 用户接口
│   ├── layout/           # 布局组件
│   │   └── index.vue     # 主布局
│   ├── router/           # 路由配置
│   │   └── index.ts      # 路由定义
│   ├── stores/           # Pinia 状态管理
│   │   └── user.ts       # 用户状态
│   ├── styles/           # 全局样式
│   │   └── index.css     # 基础样式
│   ├── types/            # TypeScript 类型定义
│   │   └── index.ts      # 通用类型
│   ├── utils/            # 工具函数
│   │   └── request.ts    # Axios 封装
│   ├── views/            # 页面组件
│   │   ├── dashboard/    # 工作台
│   │   ├── login/        # 登录页
│   │   ├── profile/      # 个人中心
│   │   └── user/         # 用户管理
│   ├── App.vue           # 根组件
│   ├── main.ts           # 入口文件
│   └── vite-env.d.ts     # 类型声明
├── index.html            # HTML 模板
├── package.json          # 项目配置
├── tsconfig.json         # TypeScript 配置
└── vite.config.ts        # Vite 配置
```

## 快速开始

### 1. 安装依赖

```bash
cd web
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

开发服务器将在 `http://localhost:3000` 启动

### 3. 构建生产版本

```bash
npm run build
```

## 功能说明

### 登录功能

- 路径: `/login`
- 默认账号: `admin` / `123456`
- 登录成功后会保存 token 到 localStorage
- 自动跳转到工作台

### 主布局

- 左侧可折叠导航菜单
- 顶部用户信息和退出登录
- 响应式内容区域

### 路由守卫

- 未登录用户访问受保护页面会自动跳转到登录页
- 已登录用户访问登录页会自动跳转到首页

### 页面列表

- `/dashboard` - 工作台（数据统计、快捷操作）
- `/user/list` - 用户列表管理
- `/profile` - 个人中心

## API 配置

后端接口代理配置在 `vite.config.ts` 中：

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

所有以 `/api` 开头的请求会被代理到后端服务 `http://localhost:8080`

## 开发规范

### 代码风格

- 使用 TypeScript 编写
- 使用 Composition API
- 使用 `<script setup>` 语法
- 组件使用 PascalCase 命名

### 状态管理

使用 Pinia 进行状态管理，示例：

```typescript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
await userStore.login({ username, password })
```

### API 调用

使用封装的 request 工具，示例：

```typescript
import { login } from '@/api/auth'

const res = await login({ username, password })
```

## 后续开发建议

1. 完善用户管理的 CRUD 功能
2. 添加角色权限管理
3. 添加更多业务模块
4. 优化错误处理和加载状态
5. 添加单元测试
6. 添加 ESLint 和 Prettier 配置

## License

MIT

