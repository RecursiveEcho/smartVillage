<template>
  <div class="public-layout">
    <!-- Navigation -->
    <header class="nav-header">
      <RouterLink to="/" class="logo">
        <span class="logo-icon">🌿</span>
        <span>智慧乡村</span>
      </RouterLink>
      <nav>
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/announcements">村务公告</RouterLink>
        <RouterLink to="/features">乡村风采</RouterLink>
        <RouterLink to="/interactions">村民互动</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/my/interactions">我的留言</RouterLink>
        <RouterLink v-if="!isLoggedIn" to="/login" class="btn btn-sm btn-primary">登录</RouterLink>
        <a v-else href="#" class="btn btn-sm btn-outline" @click.prevent="handleLogout">退出</a>
      </nav>
    </header>

    <!-- Page Content -->
    <main class="page-content">
      <RouterView />
    </main>

    <!-- Footer -->
    <footer class="site-footer">
      <p>© 2026 智慧乡村综合管理系统 | 用数字科技服务乡村振兴</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { RouterLink, RouterView } from "vue-router"
import { getToken, removeToken, removeSavedUser } from "@/shared/auth/token"

const isLoggedIn = computed(() => !!getToken())

function handleLogout() {
  removeToken()
  removeSavedUser()
  window.location.reload()
}
</script>

<style scoped>
.page-content {
  min-height: calc(100vh - var(--header-height) - 120px);
}
</style>
