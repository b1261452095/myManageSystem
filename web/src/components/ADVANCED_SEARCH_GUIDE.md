# AdvancedSearch 高级搜索组件使用指南

## 🎯 功能概览

AdvancedSearch 是一个基于 Ant Design 的高级搜索组件，提供了快速搜索和详细筛选功能。

### ✨ 核心特性

- 🔍 **快速搜索** - 顶部输入框，支持关键词快速搜索
- 🎨 **高级搜索** - 抽屉式详细筛选表单
- 🏷️ **筛选标签** - 显示已选择的筛选条件
- 🔢 **条件计数** - Badge 显示活跃筛选条件数量
- 📋 **多种字段类型** - 支持输入框、选择器、日期等多种类型
- 🎛️ **自定义表单** - 支持插槽自定义搜索表单
- 🔄 **一键重置** - 快速清除所有搜索条件

## 📦 安装使用

### 基本使用

```vue
<template>
  <AdvancedSearch
    :search-fields="searchFields"
    @search="handleSearch"
    @reset="handleReset"
  />
</template>

<script setup lang="ts">
import AdvancedSearch from '@/components/AdvancedSearch.vue'

const searchFields = [
  { name: 'username', label: '用户名', type: 'input' },
  { name: 'status', label: '状态', type: 'select', options: [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ]},
  { name: 'createTime', label: '创建时间', type: 'dateRange' }
]

const handleSearch = (params) => {
  console.log('搜索参数:', params)
  // 调用API进行搜索
}

const handleReset = () => {
  console.log('重置搜索')
  // 重置数据
}
</script>
```

### 完整示例（自定义表单）

```vue
<template>
  <AdvancedSearch
    :search-fields="searchFields"
    show-quick-search
    quick-search-placeholder="搜索用户名、邮箱、手机号"
    quick-search-field="keyword"
    @search="handleSearch"
    @reset="handleReset"
  >
    <template #search-form="{ formData }">
      <!-- 自定义搜索表单 -->
      <a-form-item label="用户名" name="username">
        <a-input
          v-model:value="formData.username"
          placeholder="请输入用户名"
          allow-clear
        />
      </a-form-item>

      <a-form-item label="状态" name="status">
        <a-select
          v-model:value="formData.status"
          placeholder="请选择状态"
          :options="statusOptions"
          allow-clear
        />
      </a-form-item>

      <a-form-item label="创建时间" name="createTime">
        <a-range-picker
          v-model:value="formData.createTime"
          style="width: 100%"
        />
      </a-form-item>
    </template>
  </AdvancedSearch>
</template>
```

## 📋 Props 参数

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| searchFields | 搜索字段配置 | SearchField[] | [] |
| showQuickSearch | 是否显示快速搜索 | boolean | true |
| quickSearchPlaceholder | 快速搜索占位符 | string | '请输入关键词搜索' |
| quickSearchField | 快速搜索字段名 | string | 'keyword' |
| drawerTitle | 抽屉标题 | string | '高级搜索' |
| drawerWidth | 抽屉宽度 | number \| string | 480 |
| labelCol | 表单标签列配置 | object | { span: 6 } |
| wrapperCol | 表单包裹列配置 | object | { span: 18 } |

## 🔧 SearchField 配置

```typescript
interface SearchField {
  name: string          // 字段名
  label: string         // 显示标签
  type: FieldType       // 字段类型
  placeholder?: string  // 占位符
  options?: any[]       // 选项（select、checkbox等需要）
  defaultValue?: any    // 默认值
}
```

### 支持的字段类型

| 类型 | 说明 | 示例 |
|------|------|------|
| input | 文本输入框 | `{ name: 'username', label: '用户名', type: 'input' }` |
| select | 下拉选择框 | `{ name: 'status', label: '状态', type: 'select', options: [...] }` |
| date | 日期选择器 | `{ name: 'birthday', label: '生日', type: 'date' }` |
| dateRange | 日期范围选择 | `{ name: 'createTime', label: '创建时间', type: 'dateRange' }` |
| number | 数字输入框 | `{ name: 'age', label: '年龄', type: 'number' }` |
| numberRange | 数字范围 | `{ name: 'price', label: '价格', type: 'numberRange' }` |
| checkbox | 多选框 | `{ name: 'tags', label: '标签', type: 'checkbox', options: [...] }` |
| radio | 单选框 | `{ name: 'gender', label: '性别', type: 'radio', options: [...] }` |
| cascader | 级联选择器 | `{ name: 'area', label: '地区', type: 'cascader', options: [...] }` |
| treeSelect | 树形选择器 | `{ name: 'dept', label: '部门', type: 'treeSelect', options: [...] }` |

## 🎪 Events 事件

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| search | 执行搜索时触发 | (params: Record<string, any>) |
| reset | 重置搜索时触发 | - |

## 🎨 Slots 插槽

| 插槽名 | 说明 | 作用域参数 |
|--------|------|------------|
| search-form | 自定义搜索表单 | { formData: Record<string, any> } |

## 💡 使用场景

### 场景一：简单搜索

只需要快速搜索框，不需要复杂筛选：

```vue
<AdvancedSearch
  show-quick-search
  quick-search-placeholder="搜索..."
  :search-fields="[]"
  @search="handleSearch"
/>
```

### 场景二：仅高级搜索

不显示快速搜索，只提供高级搜索按钮：

```vue
<AdvancedSearch
  :show-quick-search="false"
  :search-fields="searchFields"
  @search="handleSearch"
/>
```

### 场景三：完整功能

快速搜索 + 高级搜索 + 自定义表单：

```vue
<AdvancedSearch
  show-quick-search
  :search-fields="searchFields"
  @search="handleSearch"
>
  <template #search-form="{ formData }">
    <!-- 自定义表单内容 -->
  </template>
</AdvancedSearch>
```

### 场景四：数字范围搜索

```vue
<script setup>
const searchFields = [
  {
    name: 'price',
    label: '价格范围',
    type: 'numberRange'
  }
]

const handleSearch = (params) => {
  // params 将包含 priceMin 和 priceMax
  console.log(params)
}
</script>
```

## 🎯 完整示例

### 用户管理搜索

```vue
<template>
  <AdvancedSearch
    :search-fields="userSearchFields"
    show-quick-search
    quick-search-placeholder="搜索用户名、邮箱、手机号"
    @search="handleUserSearch"
    @reset="handleUserReset"
  >
    <template #search-form="{ formData }">
      <a-form-item label="用户名" name="username">
        <a-input v-model:value="formData.username" allow-clear />
      </a-form-item>

      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="formData.email" allow-clear />
      </a-form-item>

      <a-form-item label="状态" name="status">
        <a-select
          v-model:value="formData.status"
          :options="statusOptions"
          allow-clear
        />
      </a-form-item>

      <a-form-item label="角色" name="roles">
        <a-select
          v-model:value="formData.roles"
          :options="roleOptions"
          mode="multiple"
          allow-clear
        />
      </a-form-item>

      <a-form-item label="创建时间" name="createTime">
        <a-range-picker v-model:value="formData.createTime" style="width: 100%" />
      </a-form-item>

      <a-form-item label="年龄范围" name="age">
        <a-space>
          <a-input-number v-model:value="formData.ageMin" placeholder="最小" />
          <span>-</span>
          <a-input-number v-model:value="formData.ageMax" placeholder="最大" />
        </a-space>
      </a-form-item>
    </template>
  </AdvancedSearch>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AdvancedSearch from '@/components/AdvancedSearch.vue'

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const roleOptions = [
  { label: '管理员', value: 'admin' },
  { label: '用户', value: 'user' },
  { label: '访客', value: 'guest' }
]

const userSearchFields = [
  { name: 'username', label: '用户名', type: 'input' },
  { name: 'email', label: '邮箱', type: 'input' },
  { name: 'status', label: '状态', type: 'select', options: statusOptions },
  { name: 'createTime', label: '创建时间', type: 'dateRange' }
]

const handleUserSearch = (params: Record<string, any>) => {
  console.log('搜索参数:', params)
  // 调用API
  // fetchUserList(params)
}

const handleUserReset = () => {
  console.log('重置搜索')
  // 重新获取数据
  // fetchUserList({})
}
</script>
```

## 🎨 UI 展示

### 快速搜索区域

```
┌────────────────────────────────────────────────────────┐
│  [🔍 搜索...        ] [搜索] [🎨 高级搜索] [✖ 重置]  │
└────────────────────────────────────────────────────────┘
```

### 高级搜索抽屉

```
┌──────────────────────────────────────────┐
│  高级搜索                    [重置] [搜索] │
├──────────────────────────────────────────┤
│                                          │
│  用户名:  [____________]                 │
│                                          │
│  邮箱:    [____________]                 │
│                                          │
│  状态:    [▼ 请选择]                     │
│                                          │
│  创建时间: [______ - ______]             │
│                                          │
├──────────────────────────────────────────┤
│  已选择的筛选条件 (2)                     │
│  [用户名: admin ✖] [状态: 启用 ✖]        │
└──────────────────────────────────────────┘
```

### 筛选条件标签

点击 tag 的关闭按钮可以快速移除单个筛选条件。

## 📌 注意事项

1. **字段名唯一性**：确保 `searchFields` 中的 `name` 字段唯一
2. **日期范围处理**：`dateRange` 类型返回的是数组，需要在后端处理
3. **数字范围**：`numberRange` 会生成两个字段：`{name}Min` 和 `{name}Max`
4. **自定义表单**：使用插槽时，必须使用 `formData` 绑定数据
5. **选项配置**：select、checkbox 等类型必须提供 `options`

## 🚀 API 集成示例

```typescript
const handleSearch = async (params: Record<string, any>) => {
  try {
    loading.value = true
    
    // 处理日期范围
    if (params.createTime) {
      params.startDate = params.createTime[0]
      params.endDate = params.createTime[1]
      delete params.createTime
    }
    
    // 调用API
    const res = await getUserList(params)
    dataSource.value = res.data.list
    pagination.total = res.data.total
    
    message.success('搜索成功')
  } catch (error) {
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}
```

## 📚 相关文档

- [Ant Design Form](https://antdv.com/components/form-cn)
- [Ant Design Drawer](https://antdv.com/components/drawer-cn)
- [TableToolbar 组件](./README.md)

---

**版本**: v1.0.0  
**更新时间**: 2025-10-28  
**功能**: 高级搜索组件

