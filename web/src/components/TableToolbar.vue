<template>
  <div class="table-toolbar">
    <div class="table-toolbar-left">
      <slot name="left">
        <span class="table-title">{{ title }}</span>
      </slot>
    </div>
    <div class="table-toolbar-right">
      <a-space>
        <slot name="extra"></slot>
        
        <a-tooltip title="导出" v-if="showExport">
          <a-button @click="handleExportClick">
            <template #icon><ExportOutlined /></template>
          </a-button>
        </a-tooltip>
        
        <a-tooltip title="刷新" v-if="showRefresh">
          <a-button @click="handleRefresh">
            <template #icon><ReloadOutlined /></template>
          </a-button>
        </a-tooltip>
        
        <a-tooltip title="密度" v-if="showDensity">
          <a-dropdown :trigger="['click']">
            <a-button>
              <template #icon><ColumnHeightOutlined /></template>
            </a-button>
            <template #overlay>
              <a-menu @click="handleDensityChange">
                <a-menu-item key="default">
                  <CheckOutlined v-if="currentDensity === 'default'" />
                  默认
                </a-menu-item>
                <a-menu-item key="middle">
                  <CheckOutlined v-if="currentDensity === 'middle'" />
                  中等
                </a-menu-item>
                <a-menu-item key="small">
                  <CheckOutlined v-if="currentDensity === 'small'" />
                  紧凑
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-tooltip>
        
        <a-tooltip title="列设置" v-if="showColumnSetting">
          <a-dropdown :trigger="['click']">
            <a-button>
              <template #icon><SettingOutlined /></template>
            </a-button>
                <template #overlay>
                  <div class="column-settings-menu">
                    <div class="column-settings-header">
                      <span>列展示</span>
                      <a-button 
                        type="link" 
                        size="small" 
                        @click="handleResetColumns"
                      >
                        重置
                      </a-button>
                    </div>
                    <div class="column-settings-list">
                      <div
                        v-for="(col, index) in sortedColumns"
                        :key="col.key"
                        class="column-item"
                        :class="{ 'dragging': draggedIndex === index }"
                        draggable="true"
                        @dragstart="handleDragStart(index, $event)"
                        @dragover="handleDragOver(index, $event)"
                        @dragend="handleDragEnd"
                        @drop="handleDrop"
                      >
                        <HolderOutlined class="drag-handle" />
                        <a-checkbox
                          :checked="visibleColumnKeys.includes(col.key)"
                          @change="handleColumnCheckChange(col.key, $event)"
                          @click.stop
                        >
                          {{ col.title }}
                        </a-checkbox>
                        <div class="column-fixed-buttons" @click.stop>
                          <a-tooltip :title="col.fixed === 'left' ? '取消左固定' : '固定在左侧'">
                            <a-button
                              type="text"
                              size="small"
                              :class="{ 
                                'active': col.fixed === 'left',
                                'fixed-left': col.fixed === 'left'
                              }"
                              @click="handleFixedToggle(col.key, 'left')"
                            >
                              <PushpinOutlined :rotate="col.fixed === 'left' ? -45 : 0" />
                            </a-button>
                          </a-tooltip>
                          <a-tooltip :title="col.fixed === 'right' ? '取消右固定' : '固定在右侧'">
                            <a-button
                              type="text"
                              size="small"
                              :class="{ 
                                'active': col.fixed === 'right',
                                'fixed-right': col.fixed === 'right'
                              }"
                              @click="handleFixedToggle(col.key, 'right')"
                            >
                              <PushpinOutlined :rotate="col.fixed === 'right' ? 45 : 0" />
                            </a-button>
                          </a-tooltip>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
          </a-dropdown>
        </a-tooltip>
        
        <a-tooltip :title="isFullscreen ? '退出全屏' : '全屏'" v-if="showFullscreen">
          <a-button @click="toggleFullscreen">
            <template #icon>
              <FullscreenOutlined v-if="!isFullscreen" />
              <FullscreenExitOutlined v-else />
            </template>
          </a-button>
        </a-tooltip>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import {
  ReloadOutlined,
  SettingOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  ColumnHeightOutlined,
  CheckOutlined,
  HolderOutlined,
  PushpinOutlined,
  ExportOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

interface Column {
  key: string
  title: string
  [key: string]: any
}

interface Props {
  title?: string
  columns?: Column[]
  visibleColumnKeys?: string[]
  showRefresh?: boolean
  showDensity?: boolean
  showColumnSetting?: boolean
  showFullscreen?: boolean
  showExport?: boolean
  containerClass?: string
  density?: 'default' | 'middle' | 'small'
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  columns: () => [],
  visibleColumnKeys: () => [],
  showRefresh: true,
  showDensity: true,
  showColumnSetting: true,
  showFullscreen: true,
  showExport: false,
  containerClass: '',
  density: 'default'
})

const emit = defineEmits<{
  refresh: []
  columnChange: [keys: string[]]
  columnOrderChange: [columns: Column[]]
  densityChange: [density: 'default' | 'middle' | 'small']
  exportClick: []
}>()

const isFullscreen = ref(false)
const currentDensity = ref(props.density)

// 列排序状态
const sortedColumns = ref<Column[]>([])
const draggedIndex = ref<number | null>(null)
const originalColumns = ref<Column[]>([])

// 初始化排序后的列
const initSortedColumns = () => {
  if (props.columns.length > 0) {
    sortedColumns.value = [...props.columns]
    originalColumns.value = [...props.columns]
  }
}

// 监听 columns 变化
const updateSortedColumns = () => {
  if (props.columns.length > 0 && sortedColumns.value.length === 0) {
    initSortedColumns()
  }
}

// 初始化
updateSortedColumns()

// 刷新
const handleRefresh = () => {
  emit('refresh')
}

// 导出
const handleExportClick = () => {
  emit('exportClick')
}

// 密度设置
const handleDensityChange = ({ key }: { key: string }) => {
  currentDensity.value = key as 'default' | 'middle' | 'small'
  emit('densityChange', currentDensity.value)
  message.success('密度设置成功')
}

// 列显示/隐藏设置
const handleColumnCheckChange = (key: string, event: any) => {
  const newVisibleKeys = [...props.visibleColumnKeys]
  
  if (event.target.checked) {
    if (!newVisibleKeys.includes(key)) {
      newVisibleKeys.push(key)
    }
  } else {
    // 至少保留一列
    if (newVisibleKeys.length > 1) {
      const index = newVisibleKeys.indexOf(key)
      if (index > -1) {
        newVisibleKeys.splice(index, 1)
      }
    } else {
      message.warning('至少需要显示一列')
      return
    }
  }
  
  emit('columnChange', newVisibleKeys)
}

// 拖拽开始
const handleDragStart = (index: number, event: DragEvent) => {
  draggedIndex.value = index
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/html', String(index))
  }
}

// 拖拽经过
const handleDragOver = (index: number, event: DragEvent) => {
  event.preventDefault()
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
  
  if (draggedIndex.value !== null && draggedIndex.value !== index) {
    const newColumns = [...sortedColumns.value]
    const draggedItem = newColumns[draggedIndex.value]
    
    // 移除拖拽项
    newColumns.splice(draggedIndex.value, 1)
    // 插入到新位置
    newColumns.splice(index, 0, draggedItem)
    
    sortedColumns.value = newColumns
    draggedIndex.value = index
  }
}

// 拖拽放置
const handleDrop = () => {
  // 拖拽完成，触发事件
  emit('columnOrderChange', sortedColumns.value)
}

// 拖拽结束
const handleDragEnd = () => {
  draggedIndex.value = null
}

// 列固定切换（点击切换固定状态）
const handleFixedToggle = (key: string, fixed: 'left' | 'right') => {
  const column = sortedColumns.value.find(col => col.key === key)
  if (column) {
    // 如果当前已经是该固定状态，则取消固定
    if (column.fixed === fixed) {
      delete column.fixed
      message.success('已取消固定')
    } else {
      // 否则设置为该固定状态
      column.fixed = fixed
      const fixedText = fixed === 'left' ? '左侧' : '右侧'
      message.success(`已固定在${fixedText}`)
    }
    // 触发列配置更新
    emit('columnOrderChange', sortedColumns.value)
  }
}

// 重置列顺序和显示
const handleResetColumns = () => {
  sortedColumns.value = [...originalColumns.value]
  const allKeys = originalColumns.value.map(col => col.key)
  emit('columnChange', allKeys)
  emit('columnOrderChange', originalColumns.value)
  message.success('已重置列设置')
}

// 全屏切换
const toggleFullscreen = () => {
  const containerSelector = props.containerClass || '.table-container'
  const element = document.querySelector(containerSelector) as HTMLElement
  
  if (!element) {
    message.warning('未找到容器元素')
    return
  }
  
  if (!isFullscreen.value) {
    // 进入全屏
    if (element.requestFullscreen) {
      element.requestFullscreen()
    }
  } else {
    // 退出全屏
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 监听全屏变化事件
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange)
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 0;
}

.table-toolbar-left {
  display: flex;
  align-items: center;
}

.table-title {
  font-size: 16px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.88);
}

.table-toolbar-right {
  display: flex;
  align-items: center;
}

.column-settings-menu {
  min-width: 200px;
  max-height: 500px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.column-settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 500;
}

.column-settings-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

.column-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: move;
  transition: all 0.2s;
  user-select: none;
}

.column-item:hover {
  background-color: #f5f5f5;
}

.column-item.dragging {
  opacity: 0.5;
  background-color: #e6f7ff;
}

.drag-handle {
  margin-right: 8px;
  color: #999;
  cursor: move;
  font-size: 14px;
}

.column-item :deep(.ant-checkbox-wrapper) {
  flex: 1;
  display: flex;
  align-items: center;
  margin-right: 8px;
}

.column-fixed-buttons {
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0.3;
  transition: opacity 0.2s;
}

.column-item:hover .column-fixed-buttons {
  opacity: 1;
}

.column-fixed-buttons :deep(.ant-btn) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  width: 28px;
  height: 28px;
  border-radius: 4px;
  color: #999;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.column-fixed-buttons :deep(.ant-btn:hover) {
  color: #1890ff;
  background-color: #e6f7ff;
  transform: scale(1.1);
}

.column-fixed-buttons :deep(.ant-btn.active) {
  color: #fff;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 左固定按钮激活状态 - 蓝色渐变 */
.column-fixed-buttons :deep(.ant-btn.fixed-left) {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}

.column-fixed-buttons :deep(.ant-btn.fixed-left:hover) {
  background: linear-gradient(135deg, #40a9ff 0%, #5cdbd3 100%);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}

/* 右固定按钮激活状态 - 紫粉渐变 */
.column-fixed-buttons :deep(.ant-btn.fixed-right) {
  background: linear-gradient(135deg, #722ed1 0%, #eb2f96 100%);
}

.column-fixed-buttons :deep(.ant-btn.fixed-right:hover) {
  background: linear-gradient(135deg, #9254de 0%, #f759ab 100%);
  box-shadow: 0 4px 12px rgba(114, 46, 209, 0.4);
}

/* 图标旋转动画 */
.column-fixed-buttons :deep(.anticon) {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>

