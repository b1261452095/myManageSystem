<template>
  <div class="pro-table-wrapper" :class="{ 'auto-height': autoHeight }">
    <!-- 高级搜索 -->
    <div v-if="showSearch && (mergedSearchFields.length > 0 || $slots['search-form'])" class="search-section">
      <AdvancedSearch
        ref="searchRef"
        :search-fields="mergedSearchFields"
        :show-quick-search="showQuickSearch"
        :quick-search-placeholder="quickSearchPlaceholder"
        :quick-search-field="quickSearchField"
        :drawer-title="searchDrawerTitle"
        :drawer-width="searchDrawerWidth"
        @search="handleSearch"
        @reset="handleSearchReset"
      >
        <template v-if="$slots['search-form']" #search-form="slotProps">
          <slot name="search-form" v-bind="slotProps"></slot>
        </template>
      </AdvancedSearch>
    </div>

    <!-- 表格工具栏 -->
    <div v-if="showToolbar" class="toolbar-section">
      <TableToolbar
        :title="toolbarTitle"
        :columns="currentColumns"
        :visible-column-keys="visibleColumns"
        :show-refresh="showRefresh"
        :show-density="showDensity"
        :show-column-setting="showColumnSetting"
        :show-fullscreen="showFullscreen"
        :show-export="showExport"
        :container-class="containerClass"
        @refresh="handleRefresh"
        @column-change="handleColumnChange"
        @column-order-change="handleColumnOrderChange"
        @density-change="handleDensityChange"
        @export-click="handleExportClick"
      >
        <template v-if="$slots['toolbar-left']" #left>
          <slot name="toolbar-left"></slot>
        </template>
        <template #extra>
          <slot name="toolbar-extra"></slot>
        </template>
      </TableToolbar>
    </div>

    <!-- 导出组件 -->
    <ExportData
      v-if="showExport"
      ref="exportRef"
      :columns="currentColumns"
      :selected-rows="selectedRows"
      :current-page-data="dataSource"
      :total-count="total"
      :default-file-name="exportFileName"
      :on-export-all="handleExportAll"
      @export="handleExportData"
    />

    <!-- 表格容器 -->
    <div class="table-section">
      <a-table
        v-bind="tableProps"
        :columns="displayColumns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="paginationConfig"
        :size="tableSize"
        :row-key="rowKey"
        :scroll="computedScroll"
        :row-selection="rowSelectionConfig"
        @change="handleTableChange"
      >
        <!-- 传递所有插槽 -->
        <template v-for="(_, name) in $slots" :key="name" #[name]="slotProps">
          <slot :name="name" v-bind="slotProps || {}"></slot>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import TableToolbar from './TableToolbar.vue'
import AdvancedSearch from './AdvancedSearch.vue'
import ExportData from './ExportData.vue'

interface SearchConfig {
  show?: boolean  // 是否在搜索中显示
  type?: 'input' | 'select' | 'date' | 'dateRange' | 'number' | 'numberRange' | 'checkbox' | 'radio' | 'cascader' | 'treeSelect'
  placeholder?: string | string[]
  options?: any[]
  defaultValue?: any
  order?: number  // 在搜索表单中的顺序
}

interface Column {
  key: string
  title: string
  dataIndex?: string
  fixed?: 'left' | 'right'
  width?: number | string
  search?: boolean | SearchConfig  // 搜索配置
  [key: string]: any
}

interface SearchField {
  name: string
  label: string
  type: 'input' | 'select' | 'date' | 'dateRange' | 'number' | 'numberRange' | 'checkbox' | 'radio' | 'cascader' | 'treeSelect'
  placeholder?: string | string[]
  options?: any[]
  defaultValue?: any
}

interface PaginationConfig {
  current?: number
  pageSize?: number
  total?: number
  showSizeChanger?: boolean
  showQuickJumper?: boolean
  showTotal?: (total: number) => string
  pageSizeOptions?: string[]
}

interface Props {
  // 表格相关
  columns: Column[]
  dataSource?: any[]
  rowKey?: string | ((record: any) => string)
  loading?: boolean
  scroll?: { x?: number | string; y?: number | string }
  tableProps?: Record<string, any>
  autoHeight?: boolean  // 是否自动计算表格高度（使用 CSS Flexbox）
  
  // 分页相关
  pagination?: false | PaginationConfig
  
  // 搜索相关
  showSearch?: boolean
  searchFields?: SearchField[]
  showQuickSearch?: boolean
  quickSearchPlaceholder?: string
  quickSearchField?: string
  searchDrawerTitle?: string
  searchDrawerWidth?: number | string
  
  // 工具栏相关
  showToolbar?: boolean
  toolbarTitle?: string
  showRefresh?: boolean
  showDensity?: boolean
  showColumnSetting?: boolean
  showFullscreen?: boolean
  containerClass?: string
  
  // 导出相关
  showExport?: boolean
  exportFileName?: string
  onExportAll?: (format: string) => Promise<any[]> | any[]
  
  // 行选择相关
  rowSelection?: any
  
  // 请求相关
  request?: (params: any) => Promise<{ data: any[]; total: number }>
  autoRequest?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: () => [],
  rowKey: 'id',
  loading: false,
  scroll: () => ({ x: 1200 }),
  tableProps: () => ({}),
  pagination: undefined,
  autoHeight: true,
  showSearch: true,
  searchFields: () => [],
  showQuickSearch: true,
  quickSearchPlaceholder: '请输入关键词搜索',
  quickSearchField: 'keyword',
  searchDrawerTitle: '高级搜索',
  searchDrawerWidth: 480,
  showToolbar: true,
  toolbarTitle: '',
  showRefresh: true,
  showDensity: true,
  showColumnSetting: true,
  showFullscreen: true,
  containerClass: '.pro-table-wrapper',
  showExport: false,
  exportFileName: 'export_data',
  autoRequest: true
})

const emit = defineEmits<{
  search: [params: Record<string, any>]
  reset: []
  refresh: []
  change: [pagination: any, filters: any, sorter: any]
  'update:dataSource': [data: any[]]
  'update:loading': [loading: boolean]
}>()

// 引用
const searchRef = ref()
const exportRef = ref()

// 状态
const tableSize = ref<'default' | 'middle' | 'small'>('default')
const currentColumns = ref<Column[]>([...props.columns])
const visibleColumns = ref<string[]>(props.columns.map(col => col.key))
const searchParams = ref<Record<string, any>>({})
const innerLoading = ref(false)
const innerDataSource = ref<any[]>([])
const innerPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})

// 行选择相关
const selectedRowKeys = ref<any[]>([])
const selectedRows = ref<any[]>([])

// 从 columns 中提取搜索字段配置
const extractSearchFields = (columns: Column[]): SearchField[] => {
  return columns
    .filter(col => col.search)
    .map(col => {
      const searchConfig: SearchConfig = typeof col.search === 'boolean' ? {} : (col.search || {})
      
      return {
        name: col.dataIndex || col.key,
        label: col.title,
        type: searchConfig?.type || 'input',
        placeholder: searchConfig?.placeholder,
        options: searchConfig?.options,
        defaultValue: searchConfig?.defaultValue,
        order: searchConfig?.order || 999
      }
    })
    .sort((a, b) => (a.order || 999) - (b.order || 999))
}

// 合并的搜索字段配置
const mergedSearchFields = computed(() => {
  const fromColumns = extractSearchFields(props.columns)
  
  // 如果提供了 searchFields，则与 columns 中的配置合并
  if (props.searchFields && props.searchFields.length > 0) {
    return props.searchFields
  }
  
  return fromColumns
})

// 初始化列配置
watch(() => props.columns, (newColumns) => {
  if (newColumns && newColumns.length > 0) {
    currentColumns.value = [...newColumns]
    visibleColumns.value = newColumns.map(col => col.key)
  }
}, { immediate: true })

// 计算显示的列
const displayColumns = computed(() => {
  return currentColumns.value.filter(col => visibleColumns.value.includes(col.key))
})

// 计算 loading 状态
const loading = computed({
  get: () => props.loading || innerLoading.value,
  set: (val) => {
    innerLoading.value = val
    emit('update:loading', val)
  }
})

// 计算数据源
const dataSource = computed({
  get: () => props.dataSource && props.dataSource.length > 0 ? props.dataSource : innerDataSource.value,
  set: (val) => {
    innerDataSource.value = val
    emit('update:dataSource', val)
  }
})

// 计算 scroll 配置
const computedScroll = computed(() => {
  if (props.autoHeight) {
    // 自动高度模式下，必须设置 scroll.y 才能触发表格滚动
    // 设置一个较大的值，实际高度由 CSS 控制
    return { 
      x: props.scroll?.x || 1200,
      y: 600  // 设置一个合理的默认值，CSS 会控制实际可见高度
    }
  }
  return props.scroll || { x: 1200 }
})

// 计算分页配置
const paginationConfig = computed(() => {
  if (props.pagination === false) {
    return false
  }
  
  const defaultPagination = {
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number) => `共 ${total} 条`,
    pageSizeOptions: ['10', '20', '50', '100']
  }
  
  if (props.request) {
    // 使用 request 时，优先使用 innerPagination（包含从 API 返回的 total）
    return {
      ...defaultPagination,
      ...innerPagination.value,
      ...(props.pagination || {})  // 用户自定义配置优先级最高
    }
  }
  
  // 不使用 request 时，使用默认配置和用户配置
  return {
    ...defaultPagination,
    ...(props.pagination || {})
  }
})

// 总数据量
const total = computed(() => {
  if (props.request) {
    return innerPagination.value.total
  }
  return dataSource.value.length
})

// 行选择配置
const rowSelectionConfig = computed(() => {
  if (!props.rowSelection) {
    return undefined
  }
  
  return {
    selectedRowKeys: selectedRowKeys.value,
    onChange: (keys: any[], rows: any[]) => {
      selectedRowKeys.value = keys
      selectedRows.value = rows
      props.rowSelection?.onChange?.(keys, rows)
    },
    ...props.rowSelection
  }
})

// 搜索
const handleSearch = (params: Record<string, any>) => {
  searchParams.value = params
  
  if (props.request) {
    // 重置到第一页
    innerPagination.value.current = 1
    fetchData()
  }
  
  emit('search', params)
}

// 重置搜索
const handleSearchReset = () => {
  searchParams.value = {}
  
  if (props.request) {
    innerPagination.value.current = 1
    fetchData()
  }
  
  emit('reset')
}

// 刷新
const handleRefresh = () => {
  if (props.request) {
    fetchData()
  }
  emit('refresh')
  message.success('刷新成功')
}

// 列变化
const handleColumnChange = (keys: string[]) => {
  visibleColumns.value = keys
}

// 列顺序变化
const handleColumnOrderChange = (columns: Column[]) => {
  currentColumns.value = columns
}

// 密度变化
const handleDensityChange = (density: 'default' | 'middle' | 'small') => {
  tableSize.value = density
}

// 导出点击
const handleExportClick = () => {
  exportRef.value?.openExportModal()
}

// 表格变化（分页、排序、筛选）
const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  if (props.request) {
    innerPagination.value.current = pagination.current
    innerPagination.value.pageSize = pagination.pageSize
    fetchData({ filters, sorter })
  }
  
  emit('change', pagination, filters, sorter)
}

// 导出全部数据
const handleExportAll = async (format: string) => {
  if (props.onExportAll) {
    return await props.onExportAll(format)
  }
  
  // 如果没有提供 onExportAll 方法，且有 request 方法，则请求全部数据
  if (props.request) {
    try {
      const result = await props.request({
        current: 1,
        pageSize: total.value,
        ...searchParams.value
      })
      return result.data || []
    } catch (error) {
      console.error('获取全部数据失败:', error)
      message.error('获取全部数据失败')
      return []
    }
  }
  
  return dataSource.value
}

// 导出数据回调
const handleExportData = (data: any) => {
  console.log('导出数据:', data)
}

// 请求数据
const fetchData = async (extraParams?: any) => {
  if (!props.request) return
  
  try {
    loading.value = true
    
    const params = {
      current: innerPagination.value.current,
      pageSize: innerPagination.value.pageSize,
      ...searchParams.value,
      ...extraParams
    }
    
    const result = await props.request(params)
    
    if (result) {
      innerDataSource.value = result.data || []
      innerPagination.value.total = result.total || 0
    }
  } catch (error) {
    console.error('请求数据失败:', error)
    message.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 暴露方法
defineExpose({
  search: handleSearch,
  reset: handleSearchReset,
  refresh: handleRefresh,
  reload: () => {
    innerPagination.value.current = 1
    fetchData()
  },
  fetchData,
  getSearchParams: () => searchParams.value,
  getPagination: () => innerPagination.value,
  openSearchDrawer: () => searchRef.value?.openDrawer(),
  closeSearchDrawer: () => searchRef.value?.closeDrawer()
})

// 自动请求
onMounted(() => {
  if (props.autoRequest && props.request) {
    fetchData()
  }
})
</script>

<style scoped>
.pro-table-wrapper {
  background: #fff;
  /* padding: 16px; */
  border-radius: 4px;
}

/* 自动高度模式 */
.pro-table-wrapper.auto-height {
  display: flex;
  flex-direction: column;
  /* 
   * 计算高度: 
   * 100vh - header(64px) - content上下padding(40px) - wrapper上下padding(40px) - footer(70px)
   * = 100vh - 190px
   */
  height: calc(100vh - 190px);
  min-height: 400px; /* 最小高度，确保表格可见 */
  max-height: calc(100vh - 190px);
  overflow: hidden;
}

.pro-table-wrapper.auto-height .search-section {
  flex-shrink: 0;
  margin-bottom: 16px;
}

.pro-table-wrapper.auto-height .toolbar-section {
  flex-shrink: 0;
}

.pro-table-wrapper.auto-height .table-section {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-table-wrapper) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-spin-nested-loading) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-spin-container) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-table) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-table-container) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-table-header) {
  flex-shrink: 0;
  overflow: hidden;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-table-body) {
  flex: 1;
  overflow-y: auto !important;
  overflow-x: auto !important;
  min-height: 0; /* 关键：允许 flex 子项缩小 */
  max-height: 100%;
}

/* Ant Design 固定表头时的样式 */
.pro-table-wrapper.auto-height .table-section :deep(.ant-table-body-inner) {
  max-height: none !important;
}

/* 确保表格主体内容能够滚动 */
.pro-table-wrapper.auto-height .table-section :deep(.ant-table-tbody) {
  min-height: 0;
}

.pro-table-wrapper.auto-height .table-section :deep(.ant-table-pagination) {
  flex-shrink: 0;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>

