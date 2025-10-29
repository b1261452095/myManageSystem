# ProTable 高级表格组件使用指南

## 🎯 简介

ProTable 是一个集成了**高级搜索**、**工具栏**、**表格**的高级表格组件，提供开箱即用的表格解决方案。

### ✨ 核心特性

- 🔍 **高级搜索** - 集成快速搜索和详细筛选
- 🛠️ **工具栏** - 刷新、密度、列设置、全屏等功能
- 📊 **表格** - 基于 Ant Design Table
- 🔄 **自动请求** - 支持自动请求和分页
- 🎨 **高度可配置** - 丰富的配置选项
- 📦 **开箱即用** - 最小化配置即可使用

## 🚀 快速开始

### 最简单的使用（搜索配置集成在 column 中）

```vue
<template>
  <ProTable
    :columns="columns"
    :request="fetchData"
  />
</template>

<script setup>
import ProTable from '@/components/ProTable.vue'

const columns = [
  { 
    title: 'ID', 
    dataIndex: 'id', 
    key: 'id' 
  },
  { 
    title: '名称', 
    dataIndex: 'name', 
    key: 'name',
    search: true  // 开启搜索，使用默认 input 类型
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    search: {  // 自定义搜索配置
      type: 'select',
      options: [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 }
      ]
    }
  }
]

const fetchData = async (params) => {
  const res = await api.getList(params)
  return {
    data: res.data.list,
    total: res.data.total
  }
}
</script>
```

## 📋 完整示例

```vue
<template>
  <div class="page-wrapper">
    <div class="page-header">
      <h2>用户管理</h2>
      <a-button type="primary" @click="handleAdd">
        <PlusOutlined />
        新建
      </a-button>
    </div>

    <ProTable
      ref="tableRef"
      :columns="columns"
      :search-fields="searchFields"
      :request="fetchUserList"
      toolbar-title="用户数据"
      quick-search-placeholder="搜索用户名、邮箱、手机号"
      row-key="id"
      :scroll="{ x: 1500 }"
      @search="handleSearch"
      @refresh="handleRefresh"
    >
      <!-- 自定义搜索表单 -->
      <template #search-form="{ formData }">
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="formData.username" allow-clear />
        </a-form-item>
        
        <a-form-item label="状态" name="status">
          <a-select v-model:value="formData.status" :options="statusOptions" allow-clear />
        </a-form-item>
      </template>

      <!-- 工具栏额外按钮 -->
      <template #toolbar-extra>
        <a-button @click="handleExport">导出</a-button>
        <a-button @click="handleImport">导入</a-button>
      </template>

      <!-- 表格单元格 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">
            {{ record.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-button type="link" danger @click="handleDelete(record)">删除</a-button>
          </a-space>
        </template>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import ProTable from '@/components/ProTable.vue'

const tableRef = ref()

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 150 },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 200 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 150, fixed: 'right' }
]

const searchFields = [
  { name: 'username', label: '用户名', type: 'input' },
  { name: 'status', label: '状态', type: 'select', options: [...] }
]

const fetchUserList = async (params) => {
  const res = await api.getUserList(params)
  return {
    data: res.data.list,
    total: res.data.total
  }
}

const handleSearch = (params) => {
  console.log('搜索:', params)
}

const handleRefresh = () => {
  message.success('刷新成功')
}
</script>
```

## 📋 Props 参数

### 表格相关

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| columns | 表格列配置 | Column[] | [] |
| dataSource | 表格数据（静态数据时使用） | any[] | [] |
| rowKey | 行的 key | string \| Function | 'id' |
| loading | 加载状态 | boolean | false |
| scroll | 滚动配置 | { x, y } | { x: 1200 } |
| tableProps | 透传给 a-table 的其他属性 | object | {} |

### 分页相关

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| pagination | 分页配置，false 则不显示 | false \| PaginationConfig | 见下方 |

**默认分页配置：**
```typescript
{
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条`,
  pageSizeOptions: ['10', '20', '50', '100']
}
```

### 搜索相关

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| showSearch | 是否显示搜索 | boolean | true |
| searchFields | 搜索字段配置 | SearchField[] | [] |
| showQuickSearch | 是否显示快速搜索 | boolean | true |
| quickSearchPlaceholder | 快速搜索占位符 | string | '请输入关键词搜索' |
| quickSearchField | 快速搜索字段名 | string | 'keyword' |
| searchDrawerTitle | 搜索抽屉标题 | string | '高级搜索' |
| searchDrawerWidth | 搜索抽屉宽度 | number \| string | 480 |

### 工具栏相关

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| showToolbar | 是否显示工具栏 | boolean | true |
| toolbarTitle | 工具栏标题 | string | '' |
| showRefresh | 是否显示刷新按钮 | boolean | true |
| showDensity | 是否显示密度调整 | boolean | true |
| showColumnSetting | 是否显示列设置 | boolean | true |
| showFullscreen | 是否显示全屏按钮 | boolean | true |
| containerClass | 全屏容器类名 | string | '.pro-table-wrapper' |

### 请求相关

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| request | 请求函数 | (params) => Promise<{data, total}> | - |
| autoRequest | 是否自动请求 | boolean | true |

## 🎪 Events 事件

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| search | 搜索时触发 | (params: Record<string, any>) |
| reset | 重置搜索时触发 | - |
| refresh | 刷新时触发 | - |
| change | 表格变化时触发 | (pagination, filters, sorter) |

## 🎨 Slots 插槽

| 插槽名 | 说明 | 作用域参数 |
|--------|------|------------|
| search-form | 自定义搜索表单 | { formData } |
| toolbar-left | 工具栏左侧内容 | - |
| toolbar-extra | 工具栏额外按钮 | - |
| bodyCell | 表格单元格 | { column, record, index, text } |
| ...其他 | 所有 a-table 的插槽 | 根据插槽而定 |

## 🔧 Methods 方法

通过 ref 调用：

```vue
<template>
  <ProTable ref="tableRef" />
</template>

<script setup>
const tableRef = ref()

// 搜索
tableRef.value.search({ keyword: 'test' })

// 重置
tableRef.value.reset()

// 刷新
tableRef.value.refresh()

// 重新加载（重置到第一页）
tableRef.value.reload()

// 手动请求数据
tableRef.value.fetchData()

// 获取搜索参数
const params = tableRef.value.getSearchParams()

// 获取分页信息
const pagination = tableRef.value.getPagination()

// 打开/关闭搜索抽屉
tableRef.value.openSearchDrawer()
tableRef.value.closeSearchDrawer()
</script>
```

## 💡 使用场景

### 场景一：静态数据

不使用 request，直接传入数据：

```vue
<ProTable
  :columns="columns"
  :data-source="dataSource"
  :show-search="false"
/>
```

### 场景二：自动请求数据

提供 request 函数，组件自动请求：

```vue
<ProTable
  :columns="columns"
  :request="fetchData"
/>
```

### 场景三：禁用自动请求

手动控制请求时机：

```vue
<ProTable
  ref="tableRef"
  :columns="columns"
  :request="fetchData"
  :auto-request="false"
/>

<script setup>
const tableRef = ref()

onMounted(() => {
  // 手动触发请求
  tableRef.value.fetchData()
})
</script>
```

### 场景四：无分页

```vue
<ProTable
  :columns="columns"
  :request="fetchData"
  :pagination="false"
/>
```

### 场景五：仅显示表格

```vue
<ProTable
  :columns="columns"
  :data-source="dataSource"
  :show-search="false"
  :show-toolbar="false"
/>
```

## 🎯 Request 函数说明

request 函数会接收以下参数：

```typescript
{
  current: number      // 当前页码
  pageSize: number     // 每页条数
  keyword?: string     // 快速搜索关键词
  ...searchParams      // 其他搜索参数
  filters?: any        // 表格筛选参数
  sorter?: any         // 表格排序参数
}
```

返回值格式：

```typescript
{
  data: any[]         // 数据列表
  total: number       // 总条数
}
```

示例：

```typescript
const fetchData = async (params) => {
  // 处理日期范围
  if (params.createTime) {
    params.startDate = params.createTime[0]
    params.endDate = params.createTime[1]
    delete params.createTime
  }
  
  // 调用API
  const res = await api.getList(params)
  
  return {
    data: res.data.list,
    total: res.data.total
  }
}
```

## 🎨 Column 配置

### 基本配置

除了 Ant Design Table 的列配置外，还支持：

```typescript
{
  key: string          // 列的唯一标识（必需）
  title: string        // 列标题
  dataIndex: string    // 数据字段
  width: number        // 列宽度
  fixed: 'left' | 'right'  // 固定列
  search: boolean | SearchConfig  // 搜索配置（新增）
  // ...其他 a-table 列配置
}
```

### 搜索配置（推荐）

**方式一：直接开启搜索**

```typescript
{
  title: '用户名',
  dataIndex: 'username',
  key: 'username',
  search: true  // 使用默认 input 类型
}
```

**方式二：自定义搜索配置**

```typescript
{
  title: '状态',
  dataIndex: 'status',
  key: 'status',
  search: {
    type: 'select',  // 搜索类型
    placeholder: '请选择状态',
    options: [
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ],
    order: 2  // 在搜索表单中的显示顺序
  }
}
```

### SearchConfig 接口

```typescript
interface SearchConfig {
  show?: boolean     // 是否显示（默认 true）
  type?: 'input' | 'select' | 'date' | 'dateRange' | 'number' | 'numberRange' | 'checkbox' | 'radio' | 'cascader' | 'treeSelect'
  placeholder?: string | string[]  // 占位符
  options?: any[]    // 选项（select、checkbox等需要）
  defaultValue?: any // 默认值
  order?: number     // 显示顺序（数字越小越靠前）
}
```

### 完整示例

```typescript
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
    // ID 不需要搜索
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    width: 150,
    search: true,  // 简单开启
    order: 1
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    width: 200,
    search: {
      type: 'input',
      placeholder: '请输入邮箱',
      order: 2
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    search: {
      type: 'select',
      options: statusOptions,
      order: 3
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    search: {
      type: 'dateRange',
      placeholder: ['开始日期', '结束日期'],
      order: 4
    }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    search: false  // 明确不搜索
  }
]
```

## 📚 完整功能示例

### 示例文件

- `/web/src/views/user/list-simple.vue` - **推荐**：搜索配置集成在 column 中
- `/web/src/views/user/list-new.vue` - 使用独立的 searchFields 配置

### 两种配置方式对比

**方式一：搜索配置集成在 column 中（推荐）✨**

```vue
<ProTable :columns="columns" />

<script setup>
const columns = [
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    search: true  // 直接在 column 中配置
  }
]
</script>
```

**优点：**
- ✅ 配置集中，一个地方管理列和搜索
- ✅ 减少重复代码
- ✅ 更符合直觉
- ✅ 代码更简洁

**方式二：使用独立的 searchFields**

```vue
<ProTable 
  :columns="columns" 
  :search-fields="searchFields" 
/>

<script setup>
const columns = [...]
const searchFields = [
  { name: 'username', label: '用户名', type: 'input' }
]
</script>
```

**使用场景：**
- 搜索字段和表格列不完全一致时
- 需要更复杂的搜索表单布局时

## 🔗 相关组件

- [AdvancedSearch](./ADVANCED_SEARCH_GUIDE.md) - 高级搜索组件
- [TableToolbar](./README.md) - 表格工具栏组件

---

**版本**: v1.0.0  
**更新时间**: 2025-10-28  
**功能**: 高级表格组件，集成搜索、工具栏、表格

