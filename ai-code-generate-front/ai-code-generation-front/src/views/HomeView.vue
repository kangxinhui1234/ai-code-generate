<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goStart = () => router.push('/about')
const goDocs = () => window.open('https://www.codefather.cn', '_blank')
</script>

<template>
  <div class="home-view">
    <div class="hero">
      <h1 class="title">一键生成你的应用</h1>
      <p class="subtitle">基于智能模版与最佳实践，快速搭建现代化前端项目。</p>
      <div class="cta">
        <a-button type="primary" size="large" class="btn-primary" @click="goStart"
          >立即开始</a-button
        >
        <a-button size="large" class="btn-secondary" @click="goDocs">查看文档</a-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-view {
  background: linear-gradient(180deg, #f7fbff 0%, #ffffff 60%);
  padding: 48px 32px;
  border-radius: 12px;
}

.hero {
  text-align: center;
  padding: 48px 16px;
}

.title {
  margin: 0;
  font-size: 40px;
  line-height: 1.2;
  color: #1f2d3d;
}

.subtitle {
  margin: 16px 0 32px;
  font-size: 16px;
  color: #61758a;
}

.cta {
  display: inline-flex;
  gap: 16px;
}

.btn-primary,
.btn-secondary {
  border-radius: 10px;
  padding: 0 22px;
  height: 44px;
}
</style>
