<template>
  <div class="role-list">
    <ProTable
      ref="tableRef"
      :columns="columns"
      :request="fetchRoleList"
      quick-search-placeholder="搜索角色名称、编码"
      row-key="id"
      show-export
      export-file-name="角色列表"
      :row-selection="{
        selectedRowKeys: selectedRowKeys,
        onChange: onSelectChange,
      }"
    >
      <template #toolbar-left>
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <PlusOutlined />
            新建角色
          </a-button>
          <a-button danger :disabled="!hasSelected" @click="handleBatchDelete">
            <DeleteOutlined />
            批量删除
          </a-button>
        </a-space>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleEdit(record)">
              编辑
            </a-button>
            <a-button type="link" size="small" @click="handleSetPermissions(record)">
              分配权限
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
      :width="600"
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
        <a-form-item label="角色名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入角色名称" />
        </a-form-item>

        <a-form-item label="角色编码" name="code">
          <a-input
            v-model:value="formData.code"
            placeholder="请输入角色编码"
            :disabled="isEdit"
          />
        </a-form-item>

        <a-form-item label="角色描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入角色描述"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配权限弹窗 -->
    <a-modal
      v-model:open="permissionModalVisible"
      title="分配权限"
      :width="600"
      @ok="handlePermissionSubmit"
      @cancel="permissionModalVisible = false"
    >
      <a-tree
        v-model:checkedKeys="checkedPermissions"
        checkable
        :tree-data="permissionTreeData"
        :field-names="{ title: 'name', key: 'id', children: 'children' }"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import ProTable from '@/components/ProTable.vue'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  batchDeleteRole,
  assignPermissions,
  getRolePermissions,
} from '@/api/role'
import { getPermissionTree } from '@/api/permission'
import type { RoleInfo } from '@/types'

const tableRef = ref()
const formRef = ref<FormInstance>()

const selectedRowKeys = ref<number[]>([])
const hasSelected = computed(() => selectedRowKeys.value.length > 0)

const onSelectChange = (keys: number[]) => {
  selectedRowKeys.value = keys
}

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '角色名称',
    dataIndex: 'name',
    key: 'name',
    width: 150,
    search: true,
  },
  {
    title: '角色编码',
    dataIndex: 'code',
    key: 'code',
    width: 150,
    search: {
      type: 'input',
      placeholder: '请输入角色编码',
      order: 2,
    },
  },
  {
    title: '角色描述',
    dataIndex: 'description',
    key: 'description',
    width: 250,
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 220,
    fixed: 'right',
    search: false,
  },
] as any

const fetchRoleList = async (params: any) => {
  try {
    const res: any = await getRoleList(params)
    if (res) {
      return {
        data: res.data || [],
        total: res.total || 0,
      }
    }
    return { data: [], total: 0 }
  } catch (error: any) {
    message.error(error.message || '获取角色列表失败')
    return { data: [], total: 0 }
  }
}

const modalVisible = ref(false)
const isEdit = ref(false)
const modalTitle = computed(() => (isEdit.value ? '编辑角色' : '新建角色'))

const formData = reactive<Partial<RoleInfo>>({
  name: '',
  code: '',
  description: '',
})

const formRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' },
  ],
}

const resetForm = () => {
  formData.id = undefined
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formRef.value?.resetFields()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  modalVisible.value = true
}

const handleEdit = (record: RoleInfo) => {
  isEdit.value = true
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    code: record.code,
    description: record.description,
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    if (isEdit.value) {
      await updateRole(formData.id!, formData)
      message.success('编辑成功')
    } else {
      await createRole(formData)
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

const handleDelete = (record: RoleInfo) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除角色 "${record.name}" 吗？`,
    onOk: async () => {
      try {
        await deleteRole(record.id!)
        message.success('删除成功')
        selectedRowKeys.value = selectedRowKeys.value.filter((id) => id !== record.id)
        tableRef.value?.refresh()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    },
  })
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个角色吗？`,
    onOk: async () => {
      try {
        await batchDeleteRole(selectedRowKeys.value)
        message.success('批量删除成功')
        selectedRowKeys.value = []
        tableRef.value?.refresh()
      } catch (error: any) {
        message.error(error.message || '批量删除失败')
      }
    },
  })
}

// 分配权限相关
const permissionModalVisible = ref(false)
const currentRoleId = ref<number>()
const checkedPermissions = ref<number[]>([])
const permissionTreeData = ref<any[]>([])

const handleSetPermissions = async (record: RoleInfo) => {
  try {
    currentRoleId.value = record.id
    
    // 获取权限树
    const treeRes: any = await getPermissionTree()
    permissionTreeData.value = treeRes || []
    
    // 获取角色已有的权限
    const permRes: any = await getRolePermissions(record.id!)
    checkedPermissions.value = permRes || []
    
    permissionModalVisible.value = true
  } catch (error: any) {
    message.error(error.message || '获取权限失败')
  }
}

const handlePermissionSubmit = async () => {
  try {
    await assignPermissions(currentRoleId.value!, checkedPermissions.value)
    message.success('权限分配成功')
    permissionModalVisible.value = false
  } catch (error: any) {
    message.error(error.message || '权限分配失败')
  }
}
</script>

<style scoped>

</style>

