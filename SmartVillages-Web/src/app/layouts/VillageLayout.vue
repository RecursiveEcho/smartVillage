<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">
        <span>🌿</span>
        <span>智慧乡村</span>
      </div>

      <div class="nav-section">村民中心</div>
      <nav>
        <RouterLink to="/village">
          <span>🏠</span> 我的首页
        </RouterLink>
        <RouterLink to="/my/interactions">
          <span>💬</span> 我的留言
        </RouterLink>
        <RouterLink to="/village/services">
          <span>📋</span> 民生服务
        </RouterLink>
      </nav>

      <div style="flex:1"></div>
      <nav style="border-top:1px solid rgba(255,255,255,0.08); padding-top:var(--space-sm)">
        <RouterLink to="/">
          <span>🏠</span> 返回门户
        </RouterLink>
        <a href="#" @click.prevent="handleLogout">
          <span>🚪</span> 退出登录
        </a>
      </nav>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <div>
          <span style="font-weight:600">欢迎，{{ username || '村民' }}</span>
        </div>
        <div class="text-sm text-secondary">
          {{ new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }) }}
        </div>
      </header>

      <div class="admin-content">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { RouterLink, RouterView } from "vue-router"
import { getSavedUser, removeToken, removeSavedUser } from "@/shared/auth/token"

const savedUser = getSavedUser()
const username = computed(() => savedUser?.username || "")

function handleLogout() {
  removeToken()
  removeSavedUser()
  window.location.href = "/"
}
</script>
