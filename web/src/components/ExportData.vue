<template>
  <a-modal
    v-model:open="showConfigModal"
    title="导出数据"
    :width="600"
    @ok="handleExport"
    @cancel="showConfigModal = false"
    ok-text="确认导出"
    cancel-text="取消"
  >
    <a-form :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
      <a-form-item label="导出范围">
        <a-radio-group v-model:value="exportRange">
          <a-space direction="vertical">
            <a-radio value="selected" :disabled="!hasSelected">
              <CheckSquareOutlined />
              导出选中 {{ selectedCount > 0 ? `(${selectedCount}条)` : '' }}
            </a-radio>
            <a-radio value="current">
              <FileOutlined />
              导出当前页 ({{ currentPageCount }}条)
            </a-radio>
            <a-radio value="all">
              <DatabaseOutlined />
              导出全部 {{ totalCount > 0 ? `(${totalCount}条)` : '' }}
            </a-radio>
          </a-space>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="导出格式">
        <a-radio-group v-model:value="exportFormat">
          <a-space direction="vertical">
            <a-radio value="excel">
              <FileExcelOutlined />
              Excel (.xlsx)
            </a-radio>
            <a-radio value="csv">
              <FileTextOutlined />
              CSV (.csv)
            </a-radio>
            <a-radio value="json">
              <FileMarkdownOutlined />
              JSON (.json)
            </a-radio>
            <a-radio value="pdf" :disabled="true">
              <FilePdfOutlined />
              PDF (.pdf) <a-tag size="small">即将支持</a-tag>
            </a-radio>
          </a-space>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="导出列">
        <a-checkbox-group v-model:value="selectedColumns" style="width: 100%">
          <a-row>
            <a-col :span="12" v-for="col in exportableColumns" :key="col.key">
              <a-checkbox :value="col.key">{{ col.title }}</a-checkbox>
            </a-col>
          </a-row>
        </a-checkbox-group>
        <div style="margin-top: 8px">
          <a-button size="small" @click="selectAllColumns">全选</a-button>
          <a-button size="small" style="margin-left: 8px" @click="deselectAllColumns">取消全选</a-button>
        </div>
      </a-form-item>

      <a-form-item label="文件名">
        <a-input v-model:value="fileName" placeholder="请输入文件名" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  ExportOutlined,
  CheckSquareOutlined,
  FileOutlined,
  DatabaseOutlined,
  FileExcelOutlined,
  FileTextOutlined,
  FileMarkdownOutlined,
  FilePdfOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import * as XLSX from 'xlsx'

interface Column {
  key: string
  title: string
  dataIndex?: string
  [key: string]: any
}

interface Props {
  columns: Column[]
  selectedRows?: any[]
  currentPageData?: any[]
  totalCount?: number
  buttonText?: string
  defaultFileName?: string
  onExportAll?: (format: string) => Promise<any[]> | any[]
}

const props = withDefaults(defineProps<Props>(), {
  selectedRows: () => [],
  currentPageData: () => [],
  totalCount: 0,
  buttonText: '导出',
  defaultFileName: 'export_data'
})

const emit = defineEmits<{
  export: [data: { range: string; format: string; data: any[]; columns: string[] }]
}>()

// 状态
const showConfigModal = ref(false)
const exportRange = ref<'selected' | 'current' | 'all'>('current')
const exportFormat = ref<'excel' | 'csv' | 'json' | 'pdf'>('excel')
const selectedColumns = ref<string[]>([])
const fileName = ref(props.defaultFileName)

// 计算属性
const hasSelected = computed(() => props.selectedRows.length > 0)
const selectedCount = computed(() => props.selectedRows.length)
const currentPageCount = computed(() => props.currentPageData.length)

// 可导出的列（排除操作列等）
const exportableColumns = computed(() => {
  return props.columns.filter(col => 
    col.dataIndex && 
    col.key !== 'action' && 
    !col.key?.includes('action')
  )
})

// 初始化选中的列
const initSelectedColumns = () => {
  selectedColumns.value = exportableColumns.value.map(col => col.key)
}

// 全选列
const selectAllColumns = () => {
  selectedColumns.value = exportableColumns.value.map(col => col.key)
}

// 取消全选
const deselectAllColumns = () => {
  selectedColumns.value = []
}

// 打开导出模态框
const openExportModal = () => {
  // 初始化默认值
  if (!hasSelected.value && exportRange.value === 'selected') {
    exportRange.value = 'current'
  }
  initSelectedColumns()
  fileName.value = props.defaultFileName
  showConfigModal.value = true
}

// 获取要导出的数据
const getExportData = async (): Promise<any[]> => {
  switch (exportRange.value) {
    case 'selected':
      return props.selectedRows
    case 'current':
      return props.currentPageData
    case 'all':
      if (props.onExportAll) {
        const result = await props.onExportAll(exportFormat.value)
        return result || []
      }
      message.warning('请提供获取全部数据的方法')
      return []
    default:
      return []
  }
}

// 过滤数据列
const filterDataByColumns = (data: any[]) => {
  return data.map(row => {
    const filteredRow: any = {}
    selectedColumns.value.forEach(colKey => {
      const column = exportableColumns.value.find(col => col.key === colKey)
      if (column && column.dataIndex) {
        filteredRow[column.title] = row[column.dataIndex]
      }
    })
    return filteredRow
  })
}

// 导出为 Excel
const exportToExcel = (data: any[], filename: string) => {
  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
  XLSX.writeFile(wb, `${filename}.xlsx`)
}

// 导出为 CSV
const exportToCSV = (data: any[], filename: string) => {
  const ws = XLSX.utils.json_to_sheet(data)
  const csv = XLSX.utils.sheet_to_csv(ws)
  const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${filename}.csv`
  link.click()
}

// 导出为 JSON
const exportToJSON = (data: any[], filename: string) => {
  const json = JSON.stringify(data, null, 2)
  const blob = new Blob([json], { type: 'application/json' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${filename}.json`
  link.click()
}

// 执行导出
const handleExport = async () => {
  if (selectedColumns.value.length === 0) {
    message.warning('请至少选择一列')
    return
  }

  try {
    message.loading({ content: '正在导出...', key: 'export', duration: 0 })

    // 获取数据
    const rawData = await getExportData()
    
    if (!rawData || rawData.length === 0) {
      message.warning('没有可导出的数据')
      return
    }

    // 过滤列
    const filteredData = filterDataByColumns(rawData)

    // 触发导出事件
    emit('export', {
      range: exportRange.value,
      format: exportFormat.value,
      data: filteredData,
      columns: selectedColumns.value
    })

    // 根据格式导出
    switch (exportFormat.value) {
      case 'excel':
        exportToExcel(filteredData, fileName.value)
        break
      case 'csv':
        exportToCSV(filteredData, fileName.value)
        break
      case 'json':
        exportToJSON(filteredData, fileName.value)
        break
      case 'pdf':
        message.info('PDF 导出功能即将支持')
        return
    }

    message.success({ content: '导出成功', key: 'export' })
    showConfigModal.value = false
  } catch (error) {
    console.error('导出失败:', error)
    message.error({ content: '导出失败', key: 'export' })
  }
}

// 暴露方法
defineExpose({
  exportData: handleExport,
  openExportModal
})
</script>

<style scoped>
:deep(.ant-tag) {
  margin: 0;
}
</style>

