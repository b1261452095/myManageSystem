# 通用导出功能使用指南

## 概述

`ExportData` 组件提供了一个功能强大的通用导出解决方案，支持多种导出范围和格式。

## 功能特性

### 1. 导出范围
- **导出选中**: 导出表格中选中的行数据
- **导出当前页**: 导出当前页面显示的数据
- **导出全部**: 导出所有数据（支持自定义获取方法）

### 2. 导出格式
- **Excel (.xlsx)**: 使用 xlsx 库生成标准 Excel 文件
- **CSV (.csv)**: 生成 CSV 格式文件，支持中文（UTF-8 BOM）
- **JSON (.json)**: 导出为 JSON 格式
- **PDF (.pdf)**: 即将支持

### 3. 列配置
- 自动过滤不可导出的列（如操作列）
- 支持自定义选择要导出的列
- 一键全选/取消全选

### 4. 文件命名
- 支持自定义文件名
- 自动添加格式扩展名

## 在 ProTable 中使用

### 基础使用

```vue
<ProTable
  :columns="columns"
  :request="fetchData"
  show-export
  export-file-name="用户列表"
  :row-selection="{}"
/>
```

### Props 说明

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| show-export | 是否显示导出按钮 | boolean | false |
| export-file-name | 导出文件名 | string | 'export_data' |
| row-selection | 行选择配置（必须启用才能导出选中行） | object | - |
| on-export-all | 自定义获取全部数据的方法 | (format: string) => Promise<any[]> \| any[] | - |

### 完整示例

```vue
<template>
  <ProTable
    ref="tableRef"
    :columns="columns"
    :request="fetchUserList"
    show-export
    export-file-name="用户数据"
    :row-selection="{}"
    :on-export-all="handleExportAll"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ProTable from '@/components/ProTable.vue'

const tableRef = ref()

const columns = [
  { key: 'id', title: 'ID', dataIndex: 'id' },
  { key: 'name', title: '姓名', dataIndex: 'name' },
  { key: 'email', title: '邮箱', dataIndex: 'email' },
  { key: 'action', title: '操作' } // 自动排除
]

const fetchUserList = async (params: any) => {
  // 请求数据...
  return { data: [], total: 0 }
}

// 自定义获取全部数据（可选）
const handleExportAll = async (format: string) => {
  console.log('导出格式:', format)
  // 请求所有数据...
  const response = await fetch('/api/users/all')
  return response.data
}
</script>
```

## 单独使用 ExportData 组件

如果不使用 ProTable，也可以直接使用 ExportData 组件：

```vue
<template>
  <ExportData
    :columns="columns"
    :selected-rows="selectedRows"
    :current-page-data="currentPageData"
    :total-count="totalCount"
    button-text="导出数据"
    default-file-name="my_data"
    :on-export-all="handleExportAll"
    @export="handleExport"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ExportData from '@/components/ExportData.vue'

const columns = ref([
  { key: 'id', title: 'ID', dataIndex: 'id' },
  { key: 'name', title: '姓名', dataIndex: 'name' }
])

const selectedRows = ref([])
const currentPageData = ref([])
const totalCount = ref(0)

const handleExportAll = async () => {
  // 返回全部数据
  return []
}

const handleExport = (data: any) => {
  console.log('导出数据:', data)
}
</script>
```

## Props 详细说明

### ExportData Props

| 参数 | 说明 | 类型 | 默认值 | 必填 |
|------|------|------|--------|------|
| columns | 列配置 | Column[] | - | 是 |
| selectedRows | 选中的行数据 | any[] | [] | 否 |
| currentPageData | 当前页数据 | any[] | [] | 否 |
| totalCount | 总数据量 | number | 0 | 否 |
| buttonText | 按钮文字 | string | '导出' | 否 |
| defaultFileName | 默认文件名 | string | 'export_data' | 否 |
| onExportAll | 获取全部数据的方法 | (format: string) => Promise<any[]> \| any[] | - | 否 |

### Column 接口

```typescript
interface Column {
  key: string          // 列的唯一标识
  title: string        // 列标题
  dataIndex?: string   // 数据字段名
  [key: string]: any   // 其他属性
}
```

**注意**: 
- 只有包含 `dataIndex` 的列才会被导出
- `key` 为 'action' 或包含 'action' 的列会自动排除

## 事件

### @export

导出完成后触发，返回导出的详细信息：

```typescript
interface ExportEvent {
  range: 'selected' | 'current' | 'all'  // 导出范围
  format: 'excel' | 'csv' | 'json'        // 导出格式
  data: any[]                              // 导出的数据
  columns: string[]                        // 导出的列
}
```

示例：

```vue
<ExportData
  @export="handleExport"
/>

<script setup>
const handleExport = (event) => {
  console.log('导出范围:', event.range)
  console.log('导出格式:', event.format)
  console.log('导出数据量:', event.data.length)
}
</script>
```

## 导出流程

1. 用户点击"导出"按钮
2. 打开导出配置窗口
3. 在配置窗口中选择：
   - **导出范围**: 选中行/当前页/全部数据
   - **导出格式**: Excel/CSV/JSON/PDF
   - **导出列**: 选择要导出的列（支持全选/取消全选）
   - **文件名**: 自定义文件名
4. 点击"确认导出"开始导出
5. 自动下载文件

## 注意事项

1. **行选择功能**: 如果要支持"导出选中"，必须在 ProTable 中启用 `row-selection`
   ```vue
   <ProTable :row-selection="{}" />
   ```

2. **依赖安装**: 使用导出功能需要安装 xlsx 库
   ```bash
   npm install xlsx
   ```

3. **导出全部数据**: 
   - 如果提供了 `onExportAll` 方法，会使用该方法获取数据
   - 否则，如果 ProTable 有 `request` 方法，会自动请求全部数据
   - 都没有则使用当前的 dataSource

4. **列过滤**: 
   - 操作列（key 为 'action' 或包含 'action'）会自动排除
   - 没有 dataIndex 的列会被排除
   - 可以在配置模态框中手动选择要导出的列

5. **文件名**: 导出的文件会自动添加格式扩展名（.xlsx, .csv, .json）

## 样式自定义

组件使用了 Ant Design Vue 的样式系统，可以通过全局样式或 CSS 变量进行自定义。

## 最佳实践

1. **为导出提供有意义的文件名**
   ```vue
   <ProTable export-file-name="用户管理_2024" />
   ```

2. **使用自定义导出方法处理大数据量**
   ```vue
   <ProTable :on-export-all="exportLargeData" />
   ```
   ```typescript
   const exportLargeData = async (format: string) => {
     // 使用流式请求或分批获取
     const result = await api.getAllUsers({ stream: true })
     return result
   }
   ```

3. **监听导出事件进行日志记录**
   ```vue
   <ExportData @export="logExport" />
   ```
   ```typescript
   const logExport = (event: any) => {
     // 记录导出操作
     analytics.track('data_export', {
       range: event.range,
       format: event.format,
       count: event.data.length
     })
   }
   ```

## 后续计划

- [ ] PDF 导出支持
- [ ] 自定义导出模板
- [ ] 导出进度显示
- [ ] 大数据量分批导出
- [ ] 导出历史记录

