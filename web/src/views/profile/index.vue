<template>
  <div class="profile">
    <a-card title="个人信息">
      <a-descriptions bordered :column="2">
        <a-descriptions-item label="用户名">
          {{ userStore.userInfo?.username || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="昵称">
          {{ userStore.userInfo?.nickname || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="邮箱">
          {{ userStore.userInfo?.email || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="手机号">
          {{ userStore.userInfo?.phone || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag color="green">正常</a-tag>
        </a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <a-space>
        <a-button type="primary">
          <EditOutlined />
          编辑资料
        </a-button>
        <a-button>
          <LockOutlined />
          修改密码
        </a-button>
      </a-space>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { EditOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.profile {
  padding: 20px;
}
</style>

