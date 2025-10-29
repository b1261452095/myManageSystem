# TableToolbar 组件功能总览

## 📋 版本历史

### v1.2.0 (2025-10-28) - 列固定功能
- ✅ 新增左固定功能
- ✅ 新增右固定功能
- ✅ 新增不固定选项
- ✅ 固定按钮悬停显示
- ✅ 固定状态高亮提示
- ✅ 重置功能包含固定状态恢复

### v1.1.0 (2025-10-28) - 拖拽排序功能
- ✅ 新增拖拽排序功能
- ✅ 实时位置预览
- ✅ 拖拽视觉反馈
- ✅ 列顺序持久化支持

### v1.0.0 (2025-10-28) - 基础功能
- ✅ 刷新功能
- ✅ 密度调整（默认/中等/紧凑）
- ✅ 列显示/隐藏
- ✅ 全屏模式
- ✅ 自定义插槽

## 🎯 完整功能列表

### 1. 刷新功能 🔄
```
点击刷新按钮 → 触发 refresh 事件 → 父组件刷新数据
```
- 一键刷新表格数据
- 图标：`ReloadOutlined`
- 提示：显示刷新结果

### 2. 密度调整 📏
```
默认 (default) → 标准行高
中等 (middle)  → 适中行高  
紧凑 (small)   → 紧凑行高
```
- 三种密度可选
- 图标：`ColumnHeightOutlined`
- 当前选中项显示 `✓`

### 3. 列设置 ⚙️

#### 3.1 显示/隐藏 👁️
- 复选框控制列的显示和隐藏
- 至少保留一列可见
- 实时更新表格

#### 3.2 拖拽排序 🔄
- 拖拽手柄：`⋮⋮` (HolderOutlined)
- 拖拽中：半透明 + 蓝色背景
- 实时预览位置变化
- 松开鼠标立即应用

#### 3.3 列固定 📌
**左固定按钮** `|◀` (VerticalLeftOutlined)
- 固定列在表格左侧
- 滚动时保持可见
- 适合：ID、名称等关键列

**不固定按钮** `|—|` (ColumnWidthOutlined)
- 取消列的固定状态
- 列可随表格滚动
- 适合：普通数据列

**右固定按钮** `▶|` (VerticalRightOutlined)
- 固定列在表格右侧
- 滚动时保持可见
- 适合：操作列、按钮列

**视觉效果**：
- 默认隐藏（opacity: 0）
- 悬停显示（opacity: 1）
- 激活高亮（蓝色背景）
- 平滑过渡（0.2s）

#### 3.4 重置功能 🔄
- 一键重置所有设置：
  - ✅ 列的显示/隐藏状态
  - ✅ 列的排序顺序
  - ✅ 列的固定状态
- 恢复到初始配置
- 显示成功提示

### 4. 全屏模式 🖥️
```
点击全屏按钮 → 进入全屏
再次点击     → 退出全屏
ESC 键       → 退出全屏
```
- 图标自动切换
- 支持容器自定义
- 全屏样式优化

### 5. 自定义插槽 🎨

#### left 插槽
- 替换默认标题
- 可添加任意内容
- 位于工具栏左侧

#### extra 插槽
- 添加额外按钮
- 位于工具按钮之前
- 常用于：导出、导入等

## 🎨 UI 设计

### 列设置面板布局
```
┌─────────────────────────────────────────────────────────┐
│                     列展示                    重置       │  ← 头部
├─────────────────────────────────────────────────────────┤
│  [⋮⋮] [☑] 列名                    [|◀] [|—|] [▶|]     │  ← 列项
│  [⋮⋮] [☑] 列名                    [|◀] [|—|] [▶|]     │
│  [⋮⋮] [☑] 列名                    [|◀] [|—|] [▶|]     │
│                     ...                                  │
└─────────────────────────────────────────────────────────┘
   ↑    ↑                            ↑    ↑    ↑
 拖拽  显示                         左   取消  右
                                   固定  固定  固定
```

### 颜色方案
- **主色**：`#1890ff` (蓝色)
- **背景**：`#ffffff` (白色)
- **悬停**：`#f5f5f5` (浅灰)
- **激活**：`#e6f7ff` (浅蓝)
- **拖拽**：`#e6f7ff` + `opacity: 0.5`
- **边框**：`#f0f0f0`

### 动画效果
- **过渡时间**：0.2s
- **缓动函数**：ease
- **应用场景**：
  - 按钮悬停
  - 固定按钮显示/隐藏
  - 拖拽状态变化

## 📊 数据流

```
┌─────────────┐
│   父组件    │
└──────┬──────┘
       │
       │ Props
       ├─── columns: Column[]           (所有列配置)
       ├─── visibleColumnKeys: string[] (可见列)
       ├─── density: string             (密度)
       └─── ...其他配置
       │
       │ Events
       ├─── @refresh                    (刷新)
       ├─── @column-change              (显示/隐藏)
       ├─── @column-order-change        (排序/固定)
       └─── @density-change             (密度)
       │
┌──────┴──────┐
│ TableToolbar│
└─────────────┘
```

## 💾 持久化方案

### 保存列配置
```typescript
const saveConfig = () => {
  const config = {
    // 列顺序
    columnOrder: currentColumns.value.map(c => c.key),
    
    // 可见列
    visibleColumns: visibleColumns.value,
    
    // 固定状态
    fixedColumns: currentColumns.value
      .filter(c => c.fixed)
      .map(c => ({ key: c.key, fixed: c.fixed })),
    
    // 密度
    density: tableSize.value
  }
  
  localStorage.setItem('table_config', JSON.stringify(config))
}
```

### 恢复列配置
```typescript
const restoreConfig = () => {
  const saved = localStorage.getItem('table_config')
  if (!saved) return
  
  const config = JSON.parse(saved)
  
  // 恢复顺序
  currentColumns.value = config.columnOrder
    .map(key => allColumns.find(c => c.key === key))
    .filter(Boolean)
  
  // 恢复可见性
  visibleColumns.value = config.visibleColumns
  
  // 恢复固定状态
  config.fixedColumns.forEach(({ key, fixed }) => {
    const col = currentColumns.value.find(c => c.key === key)
    if (col) col.fixed = fixed
  })
  
  // 恢复密度
  tableSize.value = config.density
}
```

## 🎯 最佳实践

### 列固定建议
1. **左固定**：1-2 列（ID、名称）
2. **右固定**：1 列（操作）
3. **总计**：不超过 3 列

### 列顺序建议
1. **关键列在前**：ID、名称、状态
2. **辅助列在中**：描述、备注
3. **操作列在后**：按钮、链接

### 性能优化
1. **按需渲染**：只显示可见列
2. **虚拟滚动**：大数据量时使用
3. **防抖处理**：拖拽时减少更新频率

## 📚 相关文档

- **[组件文档](./README.md)** - 完整的 API 文档和使用指南
- **[拖拽排序演示](./DRAG_SORT_DEMO.md)** - 拖拽排序功能详解
- **[列固定演示](./COLUMN_FIXED_DEMO.md)** - 列固定功能详解

## 🚀 快速开始

```vue
<template>
  <div class="table-container">
    <TableToolbar
      title="数据列表"
      :columns="currentColumns"
      :visible-column-keys="visibleColumns"
      @refresh="handleRefresh"
      @column-change="handleColumnChange"
      @column-order-change="handleColumnOrderChange"
      @density-change="handleDensityChange"
    />
    
    <a-table
      :columns="displayColumns"
      :data-source="dataSource"
      :size="tableSize"
      :scroll="{ x: 1500 }"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import TableToolbar from '@/components/TableToolbar.vue'

// 配置
const originalColumns = [...]
const currentColumns = ref([...originalColumns])
const visibleColumns = ref([...])
const tableSize = ref('default')

// 计算
const displayColumns = computed(() => 
  currentColumns.value.filter(c => visibleColumns.value.includes(c.key))
)

// 事件
const handleRefresh = () => { /* 刷新逻辑 */ }
const handleColumnChange = (keys) => { visibleColumns.value = keys }
const handleColumnOrderChange = (cols) => { currentColumns.value = cols }
const handleDensityChange = (size) => { tableSize.value = size }
</script>
```

## 📌 总结

TableToolbar 组件现在是一个功能完整、交互友好的表格工具栏：

✅ **7 大核心功能**
- 刷新、密度、显示/隐藏、拖拽、固定、重置、全屏

✅ **3 种固定模式**
- 左固定、不固定、右固定

✅ **原生实现**
- 无额外依赖，基于 HTML5 拖拽 API

✅ **完善的文档**
- API 文档、演示文档、最佳实践

✅ **优秀的用户体验**
- 流畅的动画、清晰的反馈、直观的操作

---

**当前版本**: v1.2.0  
**最后更新**: 2025-10-28  
**维护状态**: ✅ 活跃维护

