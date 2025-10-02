<template>
  <a-layout-header class="header">
    <div class="header-inner">
      <!-- 左侧：Logo和标题 -->
      <div class="header-left">
        <RouterLink to="/">
          <div class="brand">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">应用自动生成</h1>
          </div>
        </RouterLink>
      </div>

      <!-- 中间：导航菜单 -->
      <div class="header-center">
        <a-menu
          class="main-menu"
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </div>

      <div class="user-login-status">

        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
          <a-space>
            <a-avatar :src="loginUserStore.loginUser.userAvatar" />
            {{ loginUserStore.loginUser.userAccount ?? '无名' }}
          </a-space>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="doLogout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
          </a-dropdown>
        </div>

        <div v-else>
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </div>



      <!-- 右侧：用户操作区域 -->
<!--      <div class="header-right">-->
<!--        <div class="user-login-status">-->
<!--          <a-button type="default" class="btn-secondary">注册</a-button>-->
<!--          <a-button type="primary" class="btn-primary">登录</a-button>-->
<!--        </div>-->
<!--      </div>-->
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { h, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'

import { LogoutOutlined } from '@ant-design/icons-vue'
import {userLogout} from "@/api/userController";
import {message} from "ant-design-vue";

const router = useRouter()

const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()




// 用户注销
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}




// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const menuItems = ref([
  {
    key: '/',
    label: '首页',
    title: '首页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于我们',
  },
  {
    key: '/profile',
    label: '个人信息',
    title: '个人信息',
  },
])

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}
</script>

<style scoped>
.header {
  background: #ffffff;
  padding: 0 16px;
  height: 56px;
  line-height: 56px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 1px 0 rgba(5, 5, 5, 0.06);
}

.header-inner {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: none;
  min-width: 280px;
  flex-shrink: 0;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  margin-left: -12px; /* 让中间菜单整体略向左移动，更居中视觉 */
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  white-space: nowrap;
  padding-right: 12px;
  border-right: 1px solid rgba(5, 5, 5, 0.06);
}

.logo {
  height: 40px;
  width: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #1677ff;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}

:deep(.main-menu) {
  height: 56px;
  line-height: 56px;
  background: transparent;
}

:deep(.ant-menu-horizontal) {
  height: 56px;
  line-height: 56px;
  background: transparent;
  align-items: center;
  --ant-menu-item-color: #334155;
  --ant-menu-item-hover-color: #1677ff;
  --ant-menu-item-selected-color: #1677ff;
}

:deep(.ant-menu-horizontal > .ant-menu-item),
:deep(.ant-menu-horizontal > .ant-menu-submenu) {
  height: 56px;
  display: flex;
  align-items: center;
  color: #334155;
}

.user-login-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  min-width: 200px;
  display: flex;
  justify-content: flex-end;
}

.btn-primary {
  border-radius: 8px;
  padding: 0 16px;
  height: 36px;
}

.btn-secondary {
  border-radius: 8px;
  padding: 0 16px;
  height: 36px;
}

:deep(.ant-menu-horizontal > .ant-menu-item-selected) {
  color: #1677ff;
}

:deep(.ant-menu-horizontal > .ant-menu-item:hover) {
  color: #1677ff;
}

/* 增强菜单可读性与选中态表现 */
::deep(.ant-menu-horizontal) {
  font-size: 15px;
}

::deep(.ant-menu-horizontal > .ant-menu-item),
::deep(.ant-menu-horizontal > .ant-menu-submenu) {
  padding-inline: 20px;
}

::deep(.ant-menu-horizontal > .ant-menu-item-selected) {
  font-weight: 600;
}

/* 选中项下划线指示条 */
::deep(.ant-menu-horizontal > .ant-menu-item::after) {
  transition:
    transform 0.2s ease,
    opacity 0.2s ease;
  border-bottom: 2px solid currentColor;
  bottom: 0;
}

::deep(.ant-menu-horizontal > .ant-menu-item-selected::after) {
  transform: scaleX(1);
  opacity: 1;
}
</style>
