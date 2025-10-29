import { h } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'
import * as Icons from '@ant-design/icons-vue'

/**
 * 从路由配置生成菜单项
 */
export function generateMenuFromRoutes(routes: RouteRecordRaw[]): MenuProps['items'] {
  const menuItems: MenuProps['items'] = []

  routes.forEach((route) => {
    // 跳过隐藏的菜单项
    if (route.meta?.hideInMenu) {
      return
    }

    // 如果有 children，递归生成子菜单
    if (route.children && route.children.length > 0) {
      const children = generateMenuFromRoutes(route.children)
      
      // 如果只有一个子菜单且父级没有 component，直接使用子菜单
      if (children.length === 1 && !route.component) {
        menuItems.push(children[0])
        return
      }

      // 如果有多个子菜单
      if (children.length > 0) {
        menuItems.push({
          key: route.path,
          icon: route.meta?.icon ? () => h(getIcon(route.meta.icon as string)) : undefined,
          label: route.meta?.title || route.name,
          title: route.meta?.title || route.name,
          children,
        })
      }
    } else if (route.path && route.meta?.title) {
      // 没有子菜单的普通菜单项
      menuItems.push({
        key: route.path,
        icon: route.meta?.icon ? () => h(getIcon(route.meta.icon as string)) : undefined,
        label: route.meta?.title,
        title: route.meta?.title,
      })
    }
  })

  return menuItems
}

/**
 * 根据图标名称获取图标组件
 */
function getIcon(iconName: string) {
  const IconComponent = (Icons as any)[iconName]
  return IconComponent || Icons.AppstoreOutlined
}

/**
 * 根据当前路由路径获取菜单选中的 key
 */
export function getSelectedMenuKeys(path: string): string[] {
  return [path]
}

/**
 * 根据当前路由路径获取需要展开的菜单 key
 */
export function getOpenMenuKeys(path: string): string[] {
  const pathSegments = path.split('/').filter(Boolean)
  const openKeys: string[] = []
  
  // 生成所有父级路径
  for (let i = 1; i < pathSegments.length; i++) {
    openKeys.push('/' + pathSegments.slice(0, i).join('/'))
  }
  
  return openKeys
}

