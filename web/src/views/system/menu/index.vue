<template>
  <div class="menu-list">
    <ProTable
      ref="tableRef"
      :columns="columns"
      :request="fetchMenuList"
      row-key="id"
      :pagination="false"
      :default-expand-all-rows="true"
    >
      <template #toolbar-left>
        <a-button type="primary" @click="handleAdd(0)">
          <PlusOutlined />
          新建菜单
        </a-button>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'icon'">
          <component v-if="record.icon" :is="getIcon(record.icon)" />
          <span v-else>-</span>
        </template>

        <template v-else-if="column.key === 'visible'">
          <a-tag :color="record.visible === 1 ? 'green' : 'red'">
            {{ record.visible === 1 ? '显示' : '隐藏' }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'error'">
            {{ record.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleAdd(record.id)">
              添加子菜单
            </a-button>
            <a-button type="link" size="small" @click="handleEdit(record)">
              编辑
            </a-button>
            <a-button type="link" size="small" danger @click="handleDelete(record)">
              删除
            </a-button>
          </a-space>
        </template>
      </template>
    </ProTable>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="700"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="父菜单" name="parentId">
          <a-tree-select
            v-model:value="formData.parentId"
            :tree-data="parentMenuOptions"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="选择父菜单（不选则为顶级菜单）"
            allow-clear
            tree-default-expand-all
          />
        </a-form-item>

        <a-form-item label="菜单名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入菜单名称" />
        </a-form-item>

        <a-form-item label="菜单编码" name="code">
          <a-input
            v-model:value="formData.code"
            placeholder="例如: system:menu"
          />
        </a-form-item>

        <a-form-item label="路由路径" name="path">
          <a-input v-model:value="formData.path" placeholder="例如: /system/menu" />
        </a-form-item>

        <a-form-item label="组件路径" name="component">
          <a-input
            v-model:value="formData.component"
            placeholder="例如: system/menu/index"
          />
        </a-form-item>

        <a-form-item label="图标" name="icon">
          <a-select v-model:value="formData.icon" placeholder="请选择图标" allow-clear>
            <a-select-option v-for="icon in iconOptions" :key="icon.value" :value="icon.value">
              <component :is="getIcon(icon.value)" />
              {{ icon.label }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="排序" name="sort">
          <a-input-number
            v-model:value="formData.sort"
            :min="0"
            placeholder="数字越小越靠前"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="重定向" name="redirect">
          <a-input v-model:value="formData.redirect" placeholder="例如: /system/menu/list（可选）" />
        </a-form-item>

        <a-form-item label="是否显示" name="visible">
          <a-radio-group v-model:value="formData.visible">
            <a-radio :value="1">显示</a-radio>
            <a-radio :value="0">隐藏</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="总是显示" name="alwaysShow">
          <a-radio-group v-model:value="formData.alwaysShow">
            <a-radio :value="1">是</a-radio>
            <a-radio :value="0">否</a-radio>
          </a-radio-group>
          <div style="color: #999; font-size: 12px; margin-top: 4px">
            即使只有一个子菜单也会显示父菜单
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import ProTable from '@/components/ProTable.vue'
import {
  getMenuTree,
  createMenu,
  updateMenu,
  deleteMenu,
} from '@/api/menu'
import type { PermissionInfo } from '@/types'
import * as Icons from '@ant-design/icons-vue'

const tableRef = ref()
const formRef = ref<FormInstance>()
const parentMenuOptions = ref<any[]>([])

// 图标选项
const iconOptions = [
  { label: 'Dashboard', value: 'DashboardOutlined' },
  { label: 'User', value: 'UserOutlined' },
  { label: 'Team', value: 'TeamOutlined' },
  { label: 'Safety', value: 'SafetyOutlined' },
  { label: 'Setting', value: 'SettingOutlined' },
  { label: 'Home', value: 'HomeOutlined' },
  { label: 'Appstore', value: 'AppstoreOutlined' },
  { label: 'Menu', value: 'MenuOutlined' },
  { label: 'File', value: 'FileOutlined' },
  { label: 'Folder', value: 'FolderOutlined' },
  { label: 'UnorderedList', value: 'UnorderedListOutlined' },
]

// 获取图标组件
const getIcon = (iconName: string) => {
  const IconComponent = (Icons as any)[iconName]
  return IconComponent || Icons.MenuOutlined
}

const columns = [
  {
    title: '菜单名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    search: true,
  },
  {
    title: '图标',
    dataIndex: 'icon',
    key: 'icon',
    width: 80,
    align: 'center',
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 80,
    align: 'center',
  },
  {
    title: '路由路径',
    dataIndex: 'path',
    key: 'path',
    width: 200,
  },
  {
    title: '组件路径',
    dataIndex: 'component',
    key: 'component',
    width: 200,
  },
  {
    title: '菜单编码',
    dataIndex: 'code',
    key: 'code',
    width: 180,
    search: true,
  },
  {
    title: '是否显示',
    dataIndex: 'visible',
    key: 'visible',
    width: 100,
    align: 'center',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    width: 250,
    fixed: 'right',
    search: false,
  },
] as any

// 获取菜单列表
const fetchMenuList = async () => {
  try {
    const res: any = await getMenuTree()
    // 同时更新父菜单选项
    parentMenuOptions.value = [
      { id: 0, name: '顶级菜单', children: [] },
      ...(res || [])
    ]
    return {
      data: res || [],
      total: 0,
    }
  } catch (error: any) {
    message.error(error.message || '获取菜单列表失败')
    return {
      data: [],
      total: 0,
    }
  }
}

const modalVisible = ref(false)
const isEdit = ref(false)
const modalTitle = computed(() => (isEdit.value ? '编辑菜单' : '新建菜单'))

const formData = reactive<Partial<PermissionInfo>>({
  name: '',
  code: '',
  type: 'menu',
  path: '',
  component: '',
  icon: '',
  sort: 0,
  parentId: 0,
  visible: 1,
  status: 1,
  redirect: '',
  alwaysShow: 0,
})

const formRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入菜单编码', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }],
}

const resetForm = () => {
  formData.id = undefined
  formData.name = ''
  formData.code = ''
  formData.type = 'menu'
  formData.path = ''
  formData.component = ''
  formData.icon = ''
  formData.sort = 0
  formData.parentId = 0
  formData.visible = 1
  formData.status = 1
  formData.redirect = ''
  formData.alwaysShow = 0
  formRef.value?.resetFields()
}

const handleAdd = (parentId: number) => {
  isEdit.value = false
  resetForm()
  formData.parentId = parentId
  modalVisible.value = true
}

const handleEdit = (record: PermissionInfo) => {
  isEdit.value = true
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    code: record.code,
    type: 'menu',
    path: record.path,
    component: record.component,
    icon: record.icon,
    sort: record.sort,
    parentId: record.parentId,
    visible: record.visible,
    status: record.status,
    redirect: record.redirect,
    alwaysShow: record.alwaysShow,
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    if (isEdit.value) {
      await updateMenu(formData.id!, formData)
      message.success('编辑成功')
    } else {
      await createMenu(formData)
      message.success('创建成功')
    }

    modalVisible.value = false
    resetForm()
    tableRef.value?.refresh()
  } catch (error: any) {
    if (error.errorFields) return
    message.error(error.message || '操作失败')
  }
}

const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

const handleDelete = (record: PermissionInfo) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除菜单 "${record.name}" 吗？`,
    onOk: async () => {
      try {
        await deleteMenu(record.id!)
        message.success('删除成功')
        tableRef.value?.refresh()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    },
  })
}
</script>

<style scoped>
</style>
