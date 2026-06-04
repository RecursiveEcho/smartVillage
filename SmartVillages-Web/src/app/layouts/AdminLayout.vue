<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="logo">
        <span>🌿</span>
        <span>智慧乡村</span>
      </div>

      <!-- Admin Section -->
      <template v-if="role === 'admin'">
        <div class="nav-section">管理控制台</div>
        <nav>
          <RouterLink to="/admin">
            <span>📊</span> 仪表盘
          </RouterLink>
          <RouterLink to="/admin/users">
            <span>👥</span> 用户管理
          </RouterLink>
          <RouterLink to="/admin/announcements">
            <span>📢</span> 公告管理
          </RouterLink>
          <RouterLink to="/admin/media">
            <span>🖼️</span> 媒体管理
          </RouterLink>
        </nav>
      </template>

      <!-- Cadre Section -->
      <template v-if="role === 'cadre'">
        <div class="nav-section">干部工作台</div>
        <nav>
          <RouterLink to="/cadre">
            <span>📊</span> 工作台
          </RouterLink>
          <RouterLink to="/cadre/announcements">
            <span>📢</span> 公告管理
          </RouterLink>
          <RouterLink to="/cadre/interactions">
            <span>💬</span> 留言处理
          </RouterLink>
          <RouterLink to="/cadre/media">
            <span>🖼️</span> 媒体管理
          </RouterLink>
        </nav>
      </template>

      <!-- Common bottom links -->
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

    <!-- Main Area -->
    <div class="admin-main">
      <header class="admin-topbar">
        <div>
          <span style="font-weight:600">欢迎，{{ username || '用户' }}</span>
          <span class="text-light text-sm" style="margin-left:var(--space-sm)">
            · {{ roleName }}
          </span>
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
const role = computed(() => savedUser?.role || "")
const username = computed(() => savedUser?.username || "")
const roleName = computed(() => {
  const map = { admin: "管理员", cadre: "村干部", villager: "村民" }
  return map[role.value] || role.value
})

function handleLogout() {
  removeToken()
  removeSavedUser()
  window.location.href = "/"
}
</script>
