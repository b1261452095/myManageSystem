# TableToolbar 表格工具栏组件

一个通用的表格工具栏组件，提供刷新、密度调整、列设置、全屏等功能。

## 功能特性

- ✅ **刷新功能** - 快速刷新表格数据
- ✅ **密度调整** - 支持默认、中等、紧凑三种密度
- ✅ **列设置** - 动态显示/隐藏列
- ✅ **拖拽排序** - 拖拽调整列的显示顺序
- ✅ **列固定** - 支持左固定和右固定
- ✅ **重置功能** - 一键重置列设置、顺序和固定状态
- ✅ **全屏模式** - 支持表格全屏显示
- ✅ **自定义插槽** - 支持左侧和额外按钮区域

## 基本使用

```vue
<template>
  <div class="page-container table-container">
    <a-card>
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
      />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import TableToolbar from '@/components/TableToolbar.vue'

// 原始列配置
const originalColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '操作', key: 'action', fixed: 'right' },
]

// 当前列配置（支持拖拽排序）
const currentColumns = ref([...originalColumns])

// 可见列
const visibleColumns = ref(['id', 'name', 'action'])

// 表格密度
const tableSize = ref<'default' | 'middle' | 'small'>('default')

// 计算显示的列（按排序后的顺序显示）
const displayColumns = computed(() => {
  return currentColumns.value.filter(col => visibleColumns.value.includes(col.key))
})

// 刷新
const handleRefresh = () => {
  console.log('刷新数据')
}

// 列显示/隐藏变化
const handleColumnChange = (keys: string[]) => {
  visibleColumns.value = keys
}

// 列顺序变化（拖拽排序后触发）
const handleColumnOrderChange = (columns: any[]) => {
  currentColumns.value = columns
}

// 密度变化
const handleDensityChange = (density: 'default' | 'middle' | 'small') => {
  tableSize.value = density
}
</script>

<style scoped>
/* 全屏样式 */
.table-container:fullscreen {
  background: #f0f2f5;
  padding: 20px;
  overflow: auto;
}

.table-container:fullscreen .ant-card {
  height: calc(100vh - 40px);
}
</style>
```

## Props 参数

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| title | 工具栏标题 | string | '' |
| columns | 所有列配置（用于列设置） | Column[] | [] |
| visibleColumnKeys | 可见列的 key 数组 | string[] | [] |
| showRefresh | 是否显示刷新按钮 | boolean | true |
| showDensity | 是否显示密度调整 | boolean | true |
| showColumnSetting | 是否显示列设置 | boolean | true |
| showFullscreen | 是否显示全屏按钮 | boolean | true |
| containerClass | 全屏容器的 class 选择器 | string | '.table-container' |
| density | 默认密度 | 'default' \| 'middle' \| 'small' | 'default' |

## Events 事件

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| refresh | 点击刷新按钮时触发 | - |
| columnChange | 列显示/隐藏变化时触发 | (keys: string[]) |
| columnOrderChange | 列顺序变化时触发（拖拽排序后） | (columns: Column[]) |
| densityChange | 密度变化时触发 | (density: 'default' \| 'middle' \| 'small') |

## Slots 插槽

| 插槽名 | 说明 |
|--------|------|
| left | 左侧内容区域（默认显示 title） |
| extra | 右侧额外按钮区域（在工具按钮之前） |

### 使用插槽示例

```vue
<TableToolbar
  :columns="allColumns"
  :visible-column-keys="visibleColumns"
  @refresh="handleRefresh"
>
  <template #left>
    <div>
      <h3>自定义标题</h3>
      <p>副标题信息</p>
    </div>
  </template>
  
  <template #extra>
    <a-button type="primary">导出</a-button>
    <a-button>导入</a-button>
  </template>
</TableToolbar>
```

## 拖拽排序功能

列设置面板支持拖拽排序，用户可以：

1. **拖拽调整顺序**：按住列项左侧的拖拽图标（⋮⋮），拖动到目标位置
2. **实时预览**：拖拽过程中可以看到实时的位置变化
3. **保存顺序**：拖拽完成后，列的显示顺序会立即更新
4. **重置功能**：点击"重置"按钮可恢复到初始状态

## 列固定功能

列设置面板支持列固定，用户可以：

1. **固定选项**：鼠标悬停在列项上时，右侧会显示三个固定按钮
   - 📌 **左固定按钮** - 将列固定在表格左侧
   - 📄 **不固定按钮** - 取消列的固定状态
   - 📌 **右固定按钮** - 将列固定在表格右侧

2. **视觉反馈**：
   - 当前激活的固定状态按钮会高亮显示（蓝色）
   - 鼠标悬停时按钮会显示提示信息

3. **实时更新**：点击固定按钮后，表格列会立即应用固定效果

### 列固定示例

```vue
<script setup>
// 列固定会通过 columnOrderChange 事件传递
const handleColumnOrderChange = (columns) => {
  currentColumns.value = columns
  // columns 中的 fixed 属性已被更新
  console.log('列配置:', columns.map(c => ({
    title: c.title,
    fixed: c.fixed
  })))
}
</script>
```

### 拖拽排序示例

```vue
<script setup>
// 监听列顺序变化
const handleColumnOrderChange = (columns) => {
  console.log('新的列顺序:', columns)
  currentColumns.value = columns
  // 可以将顺序保存到 localStorage 或后端
  localStorage.setItem('columnOrder', JSON.stringify(columns))
}

// 从 localStorage 恢复列顺序
onMounted(() => {
  const saved = localStorage.getItem('columnOrder')
  if (saved) {
    currentColumns.value = JSON.parse(saved)
  }
})
</script>
```

## 注意事项

1. **容器类名**：使用全屏功能时，请确保容器元素有 `table-container` 类名，或通过 `containerClass` 属性自定义。

2. **列配置**：传入的 `columns` 数组每项必须包含 `key` 和 `title` 属性。

3. **列顺序和固定管理**：
   - 需要使用 `currentColumns` 来保存当前的列顺序和固定状态
   - 监听 `columnOrderChange` 事件来更新列配置（包括顺序和 fixed 属性）
   - 建议将列配置保存到本地存储或后端，以便下次访问时恢复

4. **全屏样式**：建议在使用组件的页面添加全屏样式，参考基本使用示例。

5. **密度调整**：密度变化后需要将返回的值设置到表格的 `size` 属性上。

## 完整示例

参考 `/web/src/views/user/list.vue` 文件查看完整使用示例。

