<template>
  <div class="user-list">
    <!-- 搜索配置直接集成在 columns 中 -->
    <ProTable
      ref="tableRef"
      :columns="columns"
      :request="fetchUserList"
      quick-search-placeholder="搜索用户名、邮箱、手机号"
      row-key="id"
      :scroll="{ x: 1500 }"
      show-export
      export-file-name="用户列表"
      :row-selection="{
        selectedRowKeys: selectedRowKeys,
        onChange: onSelectChange,
        getCheckboxProps: (record: any) => ({
          disabled: record.username === 'admin',
          name: record.username,
        }),
      }"
    >
      <!-- 工具栏左侧按钮 -->
      <template #toolbar-left>
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <PlusOutlined />
            新建用户
          </a-button>
          <a-button 
            danger 
            :disabled="!hasSelected"
            @click="handleBatchDelete"
          >
            <DeleteOutlined />
            批量删除
          </a-button>
        </a-space>
      </template>

      <!-- 用户名、角色列和状态列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'username'">
          <a-space>
            <span>{{ record.username }}</span>
            <a-tag v-if="record.username === 'admin'" color="red">系统管理员</a-tag>
          </a-space>
        </template>

        <template v-else-if="column.key === 'roles'">
          <a-space v-if="record.roles && record.roles.length > 0">
            <a-tag v-for="role in record.roles" :key="role.id" color="blue">
              {{ role.name }}
            </a-tag>
          </a-space>
          <span v-else style="color: #999">-</span>
        </template>

        <template v-else-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">
            {{ record.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>

        <!-- 操作列 -->
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button 
              type="link" 
              size="small" 
              :disabled="record.username === 'admin'"
              @click="handleEdit(record)"
            >
              编辑
            </a-button>
            <a-button 
              type="link" 
              size="small" 
              danger 
              :disabled="record.username === 'admin'"
              @click="handleDelete(record)"
            >
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
        <a-form-item label="用户名" name="username">
          <a-input 
            v-model:value="formData.username" 
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </a-form-item>

        <a-form-item label="密码" name="password">
          <a-input-password 
            v-model:value="formData.password" 
            :placeholder="isEdit ? '不修改请留空' : '请输入密码'"
          />
        </a-form-item>

        <a-form-item label="昵称" name="nickname">
          <a-input v-model:value="formData.nickname" placeholder="请输入昵称" />
        </a-form-item>

        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
        </a-form-item>

        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="formData.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item label="角色" name="roleIds">
          <a-select
            v-model:value="formData.roleIds"
            mode="multiple"
            placeholder="请选择角色"
            :options="roleOptions"
            :field-names="{ label: 'name', value: 'id' }"
          />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import ProTable from '@/components/ProTable.vue'
import { getUserList, createUser, updateUser, deleteUser, batchDeleteUser } from '@/api/user'
import { getAllRoles } from '@/api/role'
import type { UserInfo, RoleInfo } from '@/types'

const tableRef = ref()
const formRef = ref<FormInstance>()

// 选中的行
const selectedRowKeys = ref<number[]>([])
const hasSelected = computed(() => selectedRowKeys.value.length > 0)

const onSelectChange = (keys: number[]) => {
  selectedRowKeys.value = keys
}

// 状态选项
const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

// 角色列表
const roleOptions = ref<RoleInfo[]>([])

// 获取所有角色
const loadRoles = async () => {
  try {
    const res: any = await getAllRoles()
    roleOptions.value = res || []
  } catch (error: any) {
    message.error(error.message || '获取角色列表失败')
  }
}

onMounted(() => {
  loadRoles()
})

// 表格列配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    width: 180,
    search: true
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
    width: 150,
    search: {
      type: 'input',
      placeholder: '请输入昵称',
      order: 2
    }
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    width: 200,
    search: {
      type: 'input',
      placeholder: '请输入邮箱',
      order: 3
    }
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 150,
    search: {
      type: 'input',
      placeholder: '请输入手机号',
      order: 4
    }
  },
  {
    title: '角色',
    dataIndex: 'roles',
    key: 'roles',
    width: 200,
    search: false,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    search: {
      type: 'select',
      options: statusOptions,
      placeholder: '请选择状态',
      order: 5
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
      order: 6
    }
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
    search: false
  },
] as any

// 请求用户列表
const fetchUserList = async (params: any) => {
  try {
    const res = await getUserList(params)
    if (res) {
      return {
        data: res.data || [],
        total: res.total || 0
      }
    }
    return {
      data: [],
      total: 0
    }
  } catch (error: any) {
    message.error(error.message || '获取用户列表失败')
    return {
      data: [],
      total: 0
    }
  }
}

// 弹窗相关
const modalVisible = ref(false)
const isEdit = ref(false)
const modalTitle = computed(() => isEdit.value ? '编辑用户' : '新建用户')

const formData = reactive<Partial<UserInfo & { roleIds: number[] }>>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  roleIds: [],
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { 
      validator: (_rule: any, value: string) => {
        if (!isEdit.value && !value) {
          return Promise.reject('请输入密码')
        }
        if (value && (value.length < 6 || value.length > 20)) {
          return Promise.reject('密码长度在 6 到 20 个字符')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.username = ''
  formData.password = ''
  formData.nickname = ''
  formData.email = ''
  formData.phone = ''
  formData.status = 1
  formData.roleIds = []
  formRef.value?.resetFields()
}

// 新建
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  modalVisible.value = true
}

// 编辑
const handleEdit = async (record: UserInfo) => {
  isEdit.value = true
  Object.assign(formData, {
    id: record.id,
    username: record.username,
    password: '',
    nickname: record.nickname,
    email: record.email,
    phone: record.phone,
    status: record.status,
    roleIds: record.roleIds || [],
  })
  modalVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    if (isEdit.value) {
      // 编辑
      await updateUser(formData.id!, formData)
      message.success('编辑成功')
    } else {
      // 新增
      await createUser(formData)
      message.success('创建成功')
    }
    
    modalVisible.value = false
    resetForm()
    tableRef.value?.refresh()
  } catch (error: any) {
    if (error.errorFields) {
      // 表单验证失败
      return
    }
    message.error(error.message || '操作失败')
  }
}

// 取消
const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 删除
const handleDelete = (record: UserInfo) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除用户 "${record.username}" 吗？`,
    onOk: async () => {
      try {
        await deleteUser(record.id!)
        message.success('删除成功')
        selectedRowKeys.value = selectedRowKeys.value.filter(id => id !== record.id)
        tableRef.value?.refresh()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    onOk: async () => {
      try {
        await batchDeleteUser(selectedRowKeys.value)
        message.success('批量删除成功')
        selectedRowKeys.value = []
        tableRef.value?.refresh()
      } catch (error: any) {
        message.error(error.message || '批量删除失败')
      }
    }
  })
}
</script>

<style scoped>
</style>
