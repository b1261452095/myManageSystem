import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      // 一级菜单：工作台
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'DashboardOutlined' },
      },
      
      // 多级菜单：用户管理（父级）
      // {
      //   path: '/user',
      //   name: 'User',
      //   meta: { title: '用户管理', icon: 'UserOutlined' },
      //   children: [
      //     {
      //       path: '/user/list',
      //       name: 'UserList',
      //       component: () => import('@/views/user/list.vue'),
      //       meta: { title: '用户列表', icon: 'UnorderedListOutlined' },
      //     },
      //   ],
      // },

      // 多级菜单：系统管理（父级）
      {
        path: '/system',
        name: 'System',
        meta: { title: '系统管理', icon: 'SettingOutlined' },
        children: [
          {
            path: '/user/list',
            name: 'UserList',
            component: () => import('@/views/user/list.vue'),
            meta: { title: '用户列表', icon: 'UnorderedListOutlined' },
          },
          {
            path: '/system/role',
            name: 'SystemRole',
            component: () => import('@/views/system/role/index.vue'),
            meta: { title: '角色管理', icon: 'TeamOutlined' },
          },
          {
            path: '/system/permission',
            name: 'SystemPermission',
            component: () => import('@/views/system/permission/index.vue'),
            meta: { title: '权限管理', icon: 'SafetyOutlined' },
          },
          {
            path: '/system/menu',
            name: 'SystemMenu',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '菜单管理', icon: 'MenuOutlined' },
          },
          {
            path: '/system/dict',
            name: 'SystemDict',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '数据字典', icon: 'DictionaryOutlined' },
          },
          {
            path: '/system/dict-data',
            name: 'SystemDictData',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '数据配置', icon: 'ConfigurationOutlined' },
          },
          {
            path: '/system/log',
            name: 'SystemLog',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '系统日志', icon: 'LogOutlined' },
          },
          {
            path: '/system/file',
            name: 'SystemFile',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '文件管理', icon: 'FileOutlined' },
          },
          {
            path: '/system/notice',
            name: 'SystemNotice',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '通知公告', icon: 'NotificationOutlined' },
          },
          {
            path: '/system/message',
            name: 'SystemMessage',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '消息中心', icon: 'MessageOutlined' },
          },
          {
            path: '/system/setting',
            name: 'SystemSetting',
            component: () => import('@/views/dashboard/index.vue'), // 暂时使用 dashboard 占位
            meta: { title: '系统设置', icon: 'SettingOutlined' },
          },
          {
            path: '/system/profile',
            name: 'SystemProfile',
            component: () => import('@/views/profile/index.vue'),
            meta: { title: '个人中心', icon: 'UserOutlined' },
          },
          
        ],
      },

      // 隐藏菜单：个人中心（不在左侧菜单显示）
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'UserOutlined', hideInMenu: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 企业级后台管理系统`
  }

  // 如果访问登录页，且已登录，则跳转到首页
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 如果需要认证且未登录，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  next()
})

export default router

