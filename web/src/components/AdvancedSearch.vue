<template>
  <div class="advanced-search-wrapper">
    <!-- 快速搜索区域 -->
    <div v-if="showQuickSearch" class="quick-search">
      <a-input
        v-model:value="quickSearchValue"
        :placeholder="quickSearchPlaceholder"
        allow-clear
        @pressEnter="handleQuickSearch"
        style="width: 250px"
      >
        <template #prefix>
          <SearchOutlined />
        </template>
      </a-input>
      <a-button 
        type="primary" 
        @click="handleQuickSearch"
        style="margin-left: 8px"
      >
        搜索
      </a-button>
      <a-button 
        @click="showAdvancedDrawer = true"
        style="margin-left: 8px"
      >
        <FilterOutlined />
        高级搜索
      </a-button>
      <a-button 
        v-if="hasActiveFilters"
        @click="handleReset"
        style="margin-left: 8px"
      >
        <CloseCircleOutlined />
        重置
      </a-button>
    </div>

    <!-- 只有高级搜索按钮 -->
    <a-button 
      v-else
      @click="showAdvancedDrawer = true"
    >
      <FilterOutlined />
      高级搜索
      <a-badge 
        v-if="activeFilterCount > 0"
        :count="activeFilterCount" 
        :offset="[10, -2]"
        style="margin-left: 4px"
      />
    </a-button>

    <!-- 高级搜索抽屉 -->
    <a-drawer
      v-model:open="showAdvancedDrawer"
      :title="drawerTitle"
      :width="drawerWidth"
      placement="right"
      class="advanced-search-drawer"
    >
      <template #extra>
        <a-space>
          <a-button @click="handleDrawerReset">重置</a-button>
          <a-button type="primary" @click="handleDrawerSearch">搜索</a-button>
        </a-space>
      </template>

      <a-form
        ref="formRef"
        :model="formData"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
      >
        <slot name="search-form" :formData="formData">
          <!-- 默认搜索表单 -->
          <a-form-item
            v-for="field in searchFields"
            :key="field.name"
            :label="field.label"
            :name="field.name"
          >
            <!-- 输入框 -->
            <a-input
              v-if="field.type === 'input'"
              v-model:value="formData[field.name]"
              :placeholder="field.placeholder || `请输入${field.label}`"
              allow-clear
            />

            <!-- 选择框 -->
            <a-select
              v-else-if="field.type === 'select'"
              v-model:value="formData[field.name]"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :options="field.options"
              allow-clear
            />

            <!-- 日期选择 -->
            <a-date-picker
              v-else-if="field.type === 'date'"
              v-model:value="formData[field.name]"
              :placeholder="field.placeholder || `请选择${field.label}`"
              style="width: 100%"
            />

            <!-- 日期范围 -->
            <a-range-picker
              v-else-if="field.type === 'dateRange'"
              v-model:value="formData[field.name]"
              :placeholder="field.placeholder || ['开始日期', '结束日期']"
              style="width: 100%"
            />

            <!-- 数字输入 -->
            <a-input-number
              v-else-if="field.type === 'number'"
              v-model:value="formData[field.name]"
              :placeholder="field.placeholder || `请输入${field.label}`"
              style="width: 100%"
            />

            <!-- 数字范围 -->
            <a-space v-else-if="field.type === 'numberRange'">
              <a-input-number
                v-model:value="formData[field.name + 'Min']"
                :placeholder="'最小值'"
              />
              <span>-</span>
              <a-input-number
                v-model:value="formData[field.name + 'Max']"
                :placeholder="'最大值'"
              />
            </a-space>

            <!-- 多选框 -->
            <a-checkbox-group
              v-else-if="field.type === 'checkbox'"
              v-model:value="formData[field.name]"
              :options="field.options"
            />

            <!-- 单选框 -->
            <a-radio-group
              v-else-if="field.type === 'radio'"
              v-model:value="formData[field.name]"
              :options="field.options"
            />

            <!-- 级联选择 -->
            <a-cascader
              v-else-if="field.type === 'cascader'"
              v-model:value="formData[field.name]"
              :options="field.options"
              :placeholder="field.placeholder || `请选择${field.label}`"
              style="width: 100%"
            />

            <!-- 树形选择 -->
            <a-tree-select
              v-else-if="field.type === 'treeSelect'"
              v-model:value="formData[field.name]"
              :tree-data="field.options"
              :placeholder="field.placeholder || `请选择${field.label}`"
              tree-checkable
              allow-clear
              style="width: 100%"
            />
          </a-form-item>
        </slot>
      </a-form>

      <!-- 已选择的筛选条件 -->
      <div v-if="activeFilterCount > 0" class="active-filters">
        <a-divider>已选择的筛选条件 ({{ activeFilterCount }})</a-divider>
        <a-space wrap>
          <a-tag
            v-for="(value, key) in activeFilters"
            :key="key"
            closable
            @close="handleRemoveFilter(key)"
            color="blue"
          >
            {{ getFieldLabel(key) }}: {{ formatFilterValue(key, value) }}
          </a-tag>
        </a-space>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { SearchOutlined, FilterOutlined, CloseCircleOutlined } from '@ant-design/icons-vue'

interface SearchField {
  name: string
  label: string
  type: 'input' | 'select' | 'date' | 'dateRange' | 'number' | 'numberRange' | 'checkbox' | 'radio' | 'cascader' | 'treeSelect'
  placeholder?: string | string[]
  options?: any[]
  defaultValue?: any
}

interface Props {
  searchFields?: SearchField[]
  showQuickSearch?: boolean
  quickSearchPlaceholder?: string
  quickSearchField?: string
  drawerTitle?: string
  drawerWidth?: number | string
  labelCol?: object
  wrapperCol?: object
}

const props = withDefaults(defineProps<Props>(), {
  searchFields: () => [],
  showQuickSearch: true,
  quickSearchPlaceholder: '请输入关键词搜索',
  quickSearchField: 'keyword',
  drawerTitle: '高级搜索',
  drawerWidth: 480,
  labelCol: () => ({ span: 6 }),
  wrapperCol: () => ({ span: 18 })
})

const emit = defineEmits<{
  search: [params: Record<string, any>]
  reset: []
}>()

const showAdvancedDrawer = ref(false)
const quickSearchValue = ref('')
const formRef = ref()
const formData = reactive<Record<string, any>>({})

// 初始化表单数据
const initFormData = () => {
  props.searchFields.forEach(field => {
    if (field.defaultValue !== undefined) {
      formData[field.name] = field.defaultValue
    }
  })
}
initFormData()

// 活跃的筛选条件
const activeFilters = computed(() => {
  const filters: Record<string, any> = {}
  Object.keys(formData).forEach(key => {
    const value = formData[key]
    if (value !== undefined && value !== null && value !== '' && 
        !(Array.isArray(value) && value.length === 0)) {
      filters[key] = value
    }
  })
  return filters
})

// 活跃筛选条件数量
const activeFilterCount = computed(() => {
  return Object.keys(activeFilters.value).length
})

// 是否有活跃的筛选条件
const hasActiveFilters = computed(() => {
  return activeFilterCount.value > 0 || quickSearchValue.value !== ''
})

// 快速搜索
const handleQuickSearch = () => {
  const params: Record<string, any> = {
    ...activeFilters.value
  }
  
  if (quickSearchValue.value) {
    params[props.quickSearchField] = quickSearchValue.value
  }
  
  emit('search', params)
}

// 抽屉中的搜索
const handleDrawerSearch = () => {
  const params: Record<string, any> = {
    ...activeFilters.value
  }
  
  if (quickSearchValue.value) {
    params[props.quickSearchField] = quickSearchValue.value
  }
  
  emit('search', params)
  showAdvancedDrawer.value = false
}

// 重置
const handleReset = () => {
  Object.keys(formData).forEach(key => {
    delete formData[key]
  })
  quickSearchValue.value = ''
  formRef.value?.resetFields()
  emit('reset')
  emit('search', {})
}

// 抽屉中的重置
const handleDrawerReset = () => {
  Object.keys(formData).forEach(key => {
    delete formData[key]
  })
  formRef.value?.resetFields()
}

// 移除单个筛选条件
const handleRemoveFilter = (key: string) => {
  delete formData[key]
  handleQuickSearch()
}

// 获取字段标签
const getFieldLabel = (name: string) => {
  const field = props.searchFields.find(f => f.name === name)
  return field?.label || name
}

// 格式化筛选值显示
const formatFilterValue = (key: string, value: any) => {
  if (Array.isArray(value)) {
    return value.join(', ')
  }
  
  const field = props.searchFields.find(f => f.name === key)
  if (field?.type === 'select' && field.options) {
    const option = field.options.find((opt: any) => opt.value === value)
    return option?.label || value
  }
  
  return value
}

// 暴露方法给父组件
defineExpose({
  search: handleQuickSearch,
  reset: handleReset,
  openDrawer: () => { showAdvancedDrawer.value = true },
  closeDrawer: () => { showAdvancedDrawer.value = false }
})
</script>

<style scoped>
.advanced-search-wrapper {
  display: inline-block;
}

.quick-search {
  display: flex;
  align-items: center;
}

.advanced-search-drawer :deep(.ant-drawer-body) {
  padding-bottom: 80px;
}

.active-filters {
  margin-top: 24px;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
}
</style>

