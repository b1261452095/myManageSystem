<template>
  <div class="dashboard">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="总用户数"
            :value="1128"
            :value-style="{ color: '#3f8600' }"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="今日访问"
            :value="93"
            :value-style="{ color: '#1890ff' }"
          >
            <template #prefix>
              <EyeOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="订单数"
            :value="1563"
            :value-style="{ color: '#cf1322' }"
          >
            <template #prefix>
              <ShoppingCartOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="收入"
            :value="93188"
            :precision="2"
            prefix="¥"
            :value-style="{ color: '#faad14' }"
          >
            <template #prefix>
              <MoneyCollectOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 20px">
      <a-col :span="12">
        <a-card title="快捷操作">
          <a-space direction="vertical" style="width: 100%">
            <a-button type="primary" block>
              <PlusOutlined />
              新建用户
            </a-button>
            <a-button block>
              <FileTextOutlined />
              查看报表
            </a-button>
            <a-button block>
              <SettingOutlined />
              系统设置
            </a-button>
          </a-space>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="最新动态">
          <a-list size="small" bordered :data-source="activities">
            <template #renderItem="{ item }">
              <a-list-item>{{ item }}</a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import {
  UserOutlined,
  EyeOutlined,
  ShoppingCartOutlined,
  MoneyCollectOutlined,
  PlusOutlined,
  FileTextOutlined,
  SettingOutlined,
} from '@ant-design/icons-vue'

const activities = [
  '用户 张三 登录了系统',
  '管理员 李四 创建了新用户',
  '系统自动备份完成',
  '用户 王五 修改了个人信息',
  '新订单 #12345 已创建',
]
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
</style>

