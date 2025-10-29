<template>
  <a-layout class="layout-container">
    <!-- 左侧菜单 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      class="layout-sider"
    >
      <div class="logo">
        <span v-if="!collapsed">管理系统</span>
        <span v-else>MS</span>
      </div>
      
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        theme="dark"
        :items="menuItems"
        @click="handleMenuClick"
      />
    </a-layout-sider>

    <!-- 右侧内容区 -->
    <a-layout>
      <!-- 顶部栏 -->
      <a-layout-header class="layout-header">
        <div class="header-left">
          <MenuUnfoldOutlined
            v-if="collapsed"
            class="trigger"
            @click="() => (collapsed = !collapsed)"
          />
          <MenuFoldOutlined
            v-else
            class="trigger"
            @click="() => (collapsed = !collapsed)"
          />
        </div>

        <div class="header-right">
          <a-dropdown>
            <div class="user-info">
              <a-avatar :size="32" style="background-color: #87d068">
                <template #icon>
                  <UserOutlined />
                </template>
              </a-avatar>
              <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile" @click="goToProfile">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区 -->
      <a-layout-content class="layout-content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </a-layout-content>

      <!-- 底部 -->
      <a-layout-footer class="layout-footer">
        企业级后台管理系统 © 2025
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  UserOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue'
import type { MenuProps } from 'ant-design-vue'
import { generateMenuFromRoutes, getSelectedMenuKeys, getOpenMenuKeys } from '@/utils/menu'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const selectedKeys = ref<string[]>(['/dashboard'])
const openKeys = ref<string[]>([])

// 从路由配置生成菜单项
const menuItems = computed<MenuProps['items']>(() => {
  // 获取 Layout 路由的子路由
  const layoutRoute = router.options.routes.find(r => r.path === '/')
  if (layoutRoute?.children) {
    return generateMenuFromRoutes(layoutRoute.children)
  }
  return []
})

// 监听路由变化，更新选中的菜单
const updateSelectedKeys = () => {
  selectedKeys.value = getSelectedMenuKeys(route.path)
  openKeys.value = getOpenMenuKeys(route.path)
}

// 监听路由变化
watch(() => route.path, updateSelectedKeys, { immediate: true })

onMounted(() => {
  updateSelectedKeys()
  // 获取用户信息
  if (userStore.token && !userStore.userInfo) {
    userStore.fetchUserInfo()
  }
})

// 菜单点击事件
const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-sider {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.layout-header {
  position: fixed;
  top: 0;
  right: 0;
  left: 200px;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: left 0.2s;
}

.ant-layout-sider-collapsed + .ant-layout .layout-header {
  left: 80px;
}

.header-left {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 18px;
  padding: 0 20px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.username {
  font-size: 14px;
  color: #333;
}

.layout-content {
  margin-left: 200px;
  margin-top: 64px;
  padding: 20px;
  min-height: calc(100vh - 64px - 70px);
  transition: margin-left 0.2s;
}

.ant-layout-sider-collapsed ~ .ant-layout .layout-content {
  margin-left: 80px;
}

.content-wrapper {
  background: white;
  border-radius: 4px;
  padding: 20px 20px 0 20px;
  min-height: 100%;
}

.layout-footer {
  margin-left: 200px;
  text-align: center;
  background: #f0f2f5;
  transition: margin-left 0.2s;
}

.ant-layout-sider-collapsed ~ .ant-layout .layout-footer {
  margin-left: 80px;
}
</style>

