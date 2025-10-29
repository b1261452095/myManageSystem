<template>
  <div class="permission-list">
    <ProTable
      ref="tableRef"
      :columns="columns"
      :request="fetchPermissionList"
      quick-search-placeholder="搜索权限名称、编码"
      row-key="id"
      :pagination="false"
    >
      <template #toolbar-left>
        <a-button type="primary" @click="handleAdd(0)">
          <PlusOutlined />
          新建权限
        </a-button>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">
          <a-tag :color="typeColorMap[record.type]">
            {{ typeNameMap[record.type] }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'icon'">
          <component v-if="record.icon" :is="getIcon(record.icon)" />
          <span v-else>-</span>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleAdd(record.id)">
              添加子权限
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
        <a-form-item label="权限类型" name="type">
          <a-radio-group v-model:value="formData.type">
            <a-radio value="menu">菜单</a-radio>
            <a-radio value="button">按钮</a-radio>
            <a-radio value="api">接口</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="权限名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入权限名称" />
        </a-form-item>

        <a-form-item label="权限编码" name="code">
          <a-input
            v-model:value="formData.code"
            placeholder="例如: system:user:add"
          />
        </a-form-item>

        <a-form-item v-if="formData.type === 'menu'" label="路由路径" name="path">
          <a-input v-model:value="formData.path" placeholder="例如: /system/user" />
        </a-form-item>

        <a-form-item v-if="formData.type === 'menu'" label="组件路径" name="component">
          <a-input
            v-model:value="formData.component"
            placeholder="例如: system/user/index"
          />
        </a-form-item>

        <a-form-item v-if="formData.type === 'menu'" label="图标" name="icon">
          <a-input v-model:value="formData.icon" placeholder="例如: UserOutlined" />
        </a-form-item>

        <a-form-item label="排序" name="sort">
          <a-input-number
            v-model:value="formData.sort"
            :min="0"
            placeholder="数字越小越靠前"
            style="width: 100%"
          />
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
  getPermissionTree,
  createPermission,
  updatePermission,
  deletePermission,
} from '@/api/permission'
import type { PermissionInfo } from '@/types'
import * as Icons from '@ant-design/icons-vue'

const tableRef = ref()
const formRef = ref<FormInstance>()

const typeNameMap: Record<string, string> = {
  menu: '菜单',
  button: '按钮',
  api: '接口',
}

const typeColorMap: Record<string, string> = {
  menu: 'blue',
  button: 'green',
  api: 'orange',
}

const columns = [
  {
    title: '权限名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    search: true,
  },
  {
    title: '权限编码',
    dataIndex: 'code',
    key: 'code',
    width: 200,
    search: {
      type: 'input',
      placeholder: '请输入权限编码',
      order: 2,
    },
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width: 100,
  },
  {
    title: '图标',
    dataIndex: 'icon',
    key: 'icon',
    width: 80,
  },
  {
    title: '路径',
    dataIndex: 'path',
    key: 'path',
    width: 200,
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 80,
  },
  {
    title: '操作',
    key: 'action',
    width: 280,
    fixed: 'right',
    search: false,
  },
] as any

const getIcon = (iconName: string) => {
  const IconComponent = (Icons as any)[iconName]
  return IconComponent || Icons.UserOutlined
}

const fetchPermissionList = async () => {
  try {
    const res: any = await getPermissionTree()
    return {
      data: res || [],
      total: 0,
    }
  } catch (error: any) {
    message.error(error.message || '获取权限列表失败')
    return {
      data: [],
      total: 0,
    }
  }
}

const modalVisible = ref(false)
const isEdit = ref(false)
const modalTitle = computed(() => (isEdit.value ? '编辑权限' : '新建权限'))

const formData = reactive<Partial<PermissionInfo>>({
  name: '',
  code: '',
  type: 'menu',
  path: '',
  component: '',
  icon: '',
  sort: 0,
  parentId: 0,
})

const formRules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
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
    type: record.type,
    path: record.path,
    component: record.component,
    icon: record.icon,
    sort: record.sort,
    parentId: record.parentId,
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    if (isEdit.value) {
      await updatePermission(formData.id!, formData)
      message.success('编辑成功')
    } else {
      await createPermission(formData)
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
    content: `确定要删除权限 "${record.name}" 吗？`,
    onOk: async () => {
      try {
        await deletePermission(record.id!)
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

