<template>
  <a-layout class="basic-layout">
    <!-- 顶部导航栏 -->
    <GlobalHeader />

    <!-- 主要内容区域 -->
    <a-layout-content class="main-content">
      <router-view />
    </a-layout-content>

    <!-- 底部版权信息 -->
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  background: none;
}

.main-content {
  max-width: 1200px;
  padding: 32px;
  background: #ffffff;
  margin: 24px auto 64px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
</style>
