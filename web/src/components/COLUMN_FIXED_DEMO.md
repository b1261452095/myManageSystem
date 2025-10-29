# 列固定功能演示

## 🎯 功能概览

表格工具栏的列设置现在支持列固定功能！您可以将重要的列固定在表格的左侧或右侧，无论如何滚动，这些列都会保持可见。

```
┌─────────────────────────────────────────────────────────┐
│  列展示                                     重置         │
├─────────────────────────────────────────────────────────┤
│  ⋮⋮ ☑ ID                        [📍] [📄] [📍]         │  ← 鼠标悬停显示固定按钮
│  ⋮⋮ ☑ 用户名                    [📍] [📄] [📍]         │
│  ⋮⋮ ☑ 昵称                      [📍] [📄] [📍]         │
│  ⋮⋮ ☑ 邮箱                      [📍] [📄] [📍]         │
│  ⋮⋮ ☑ 手机号                    [📍] [📄] [📍]         │
│  ⋮⋮ ☑ 状态                      [📍] [📄] [📍]         │
│  ⋮⋮ ☑ 操作                      [📍] [📄] [📍]         │
└─────────────────────────────────────────────────────────┘

图例：
📍 左固定    📄 不固定    📍 右固定
```

## ✨ 固定按钮说明

### 三个固定按钮

当鼠标悬停在列项上时，右侧会显示三个按钮：

1. **左固定按钮** `|◀`
   - 图标：`VerticalLeftOutlined`
   - 功能：将列固定在表格左侧
   - 适用场景：ID、名称等关键列

2. **不固定按钮** `|—|`
   - 图标：`ColumnWidthOutlined`
   - 功能：取消列的固定状态
   - 适用场景：普通数据列

3. **右固定按钮** `▶|`
   - 图标：`VerticalRightOutlined`
   - 功能：将列固定在表格右侧
   - 适用场景：操作列、按钮列

### 视觉反馈

- **默认状态**：按钮为灰色，鼠标悬停时才显示（opacity: 0 → 1）
- **激活状态**：当前固定状态的按钮会高亮显示（蓝色背景）
- **悬停效果**：鼠标悬停时按钮变为蓝色，背景为浅蓝色

## 🎨 使用场景演示

### 场景一：固定 ID 列在左侧

**操作步骤**：
1. 打开列设置面板
2. 鼠标悬停在"ID"列上
3. 点击左固定按钮 `|◀`
4. ID 列固定在表格左侧

**效果**：
```
┌─────┬──────────────────────────────────┐
│ ID  │ 用户名 | 昵称 | 邮箱 | 手机号... │  ← ID 固定在左侧
│ (固定)        (可滚动区域)            │
└─────┴──────────────────────────────────┘
```

### 场景二：固定操作列在右侧

**操作步骤**：
1. 打开列设置面板
2. 鼠标悬停在"操作"列上
3. 点击右固定按钮 `▶|`
4. 操作列固定在表格右侧

**效果**：
```
┌──────────────────────────────────┬────────┐
│ ID | 用户名 | 昵称 | 邮箱 | ...  │  操作  │  ← 操作固定在右侧
│        (可滚动区域)            │ (固定) │
└──────────────────────────────────┴────────┘
```

### 场景三：同时固定左右两侧

**操作步骤**：
1. 固定"ID"列在左侧
2. 固定"操作"列在右侧

**效果**：
```
┌─────┬────────────────────────┬────────┐
│ ID  │ 用户名 | 昵称 | 邮箱... │  操作  │
│(固定)    (可滚动区域)      │ (固定) │
└─────┴────────────────────────┴────────┘
```

### 场景四：取消固定

**操作步骤**：
1. 打开列设置面板
2. 鼠标悬停在已固定的列上（按钮会高亮）
3. 点击不固定按钮 `|—|`
4. 列的固定状态被取消

## 🔧 技术实现

### 列配置中的 fixed 属性

```typescript
interface Column {
  key: string
  title: string
  fixed?: 'left' | 'right'  // 固定属性
  // ... 其他属性
}
```

### 固定状态管理

```typescript
// 设置固定
const handleFixedChange = (key: string, fixed: 'left' | 'right' | undefined) => {
  const column = sortedColumns.value.find(col => col.key === key)
  if (column) {
    if (fixed) {
      column.fixed = fixed  // 设置固定
    } else {
      delete column.fixed   // 删除固定
    }
    emit('columnOrderChange', sortedColumns.value)
  }
}
```

### Ant Design Vue 表格固定列

Ant Design Vue 的 Table 组件原生支持 `fixed` 属性：

```vue
<a-table :columns="columns">
  <!-- 
    columns 中的 fixed 属性：
    - 'left': 固定在左侧
    - 'right': 固定在右侧
    - undefined: 不固定
  -->
</a-table>
```

## 📝 完整使用示例

```vue
<template>
  <div class="table-container">
    <TableToolbar
      :columns="currentColumns"
      :visible-column-keys="visibleColumns"
      @column-order-change="handleColumnOrderChange"
    />

    <a-table
      :columns="displayColumns"
      :data-source="dataSource"
      :scroll="{ x: 1500 }"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import TableToolbar from '@/components/TableToolbar.vue'

// 原始列配置
const originalColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 150 },
  { title: '昵称', dataIndex: 'nickname', key: 'nickname', width: 150 },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 200 },
  { title: '手机号', dataIndex: 'phone', key: 'phone', width: 150 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { 
    title: '操作', 
    key: 'action', 
    width: 150, 
    fixed: 'right'  // 默认固定在右侧
  },
]

// 当前列配置
const currentColumns = ref([...originalColumns])

// 可见列
const visibleColumns = ref(['id', 'username', 'nickname', 'email', 'phone', 'status', 'action'])

// 显示的列
const displayColumns = computed(() => {
  return currentColumns.value.filter(col => visibleColumns.value.includes(col.key))
})

// 列配置变化（包括固定状态）
const handleColumnOrderChange = (columns: any[]) => {
  currentColumns.value = columns
  
  // 打印固定状态
  console.log('列固定状态:', columns.map(c => ({
    title: c.title,
    fixed: c.fixed || '不固定'
  })))
  
  // 保存到 localStorage
  localStorage.setItem('table_columns', JSON.stringify(columns))
}

// 从 localStorage 恢复
onMounted(() => {
  const saved = localStorage.getItem('table_columns')
  if (saved) {
    try {
      currentColumns.value = JSON.parse(saved)
    } catch (e) {
      console.error('恢复列配置失败:', e)
    }
  }
})
</script>
```

## 💡 最佳实践

### 1. 固定列的选择

推荐固定的列：
- ✅ **ID 列**：用户需要经常查看 ID
- ✅ **名称列**：主要标识列
- ✅ **操作列**：快速访问操作按钮
- ✅ **状态列**：关键状态信息

不推荐固定的列：
- ❌ **描述列**：通常很宽，固定会占用空间
- ❌ **备注列**：非关键信息
- ❌ **时间戳列**：相对次要的信息

### 2. 固定列的数量

- **左侧**：建议 1-2 列（如 ID + 名称）
- **右侧**：建议 1 列（如操作）
- **总计**：不超过 3 列，避免占用过多固定空间

### 3. 与拖拽排序配合

固定列的顺序也可以拖拽调整：
1. 固定的列依然可以拖拽
2. 拖拽后固定状态保持不变
3. 左固定列之间可以调整顺序
4. 右固定列之间可以调整顺序

### 4. 持久化存储

```typescript
// 保存列配置（包括固定状态）
const saveColumnConfig = (columns: Column[]) => {
  const config = columns.map(col => ({
    key: col.key,
    fixed: col.fixed,
    visible: visibleColumns.value.includes(col.key)
  }))
  localStorage.setItem('table_column_config', JSON.stringify(config))
}

// 恢复列配置
const restoreColumnConfig = () => {
  const saved = localStorage.getItem('table_column_config')
  if (!saved) return
  
  const config = JSON.parse(saved)
  
  // 应用固定状态
  config.forEach((item: any) => {
    const column = currentColumns.value.find(c => c.key === item.key)
    if (column) {
      if (item.fixed) {
        column.fixed = item.fixed
      } else {
        delete column.fixed
      }
    }
  })
  
  // 应用可见性
  visibleColumns.value = config
    .filter((item: any) => item.visible)
    .map((item: any) => item.key)
}
```

## 🎁 交互细节

### 1. 按钮显示逻辑
- 默认隐藏（opacity: 0）
- 鼠标悬停在列项上时显示（opacity: 1）
- 平滑过渡动画（transition: 0.2s）

### 2. 按钮激活状态
- 当前固定状态的按钮高亮显示
- 蓝色文字 + 浅蓝色背景（#e6f7ff）
- 其他按钮为灰色

### 3. 提示信息
- 每个按钮都有 tooltip 提示
- "固定在左侧" / "不固定" / "固定在右侧"
- 操作后显示成功消息

### 4. 一键重置
- 重置按钮会同时重置：
  - 列的显示/隐藏状态
  - 列的排序顺序
  - 列的固定状态

## 🚀 快速上手

1. **打开列设置**：点击表格工具栏的 ⚙️ 按钮

2. **查看固定按钮**：鼠标悬停在任意列项上

3. **设置固定**：
   - 点击 `|◀` 固定在左侧
   - 点击 `▶|` 固定在右侧
   - 点击 `|—|` 取消固定

4. **查看效果**：表格列立即应用固定效果

5. **重置（可选）**：点击"重置"按钮恢复默认状态

## 📌 兼容性

- ✅ 完全兼容 Ant Design Vue Table 的 fixed 属性
- ✅ 支持所有现代浏览器
- ✅ 响应式设计，移动端友好

---

**更新时间**: 2025-10-28  
**版本**: v1.2.0  
**新增功能**: 列固定（左固定/右固定）

