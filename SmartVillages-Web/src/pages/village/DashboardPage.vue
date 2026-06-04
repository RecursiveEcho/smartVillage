<template>
  <div>
    <div class="page-header">
      <h2>🏠 村民中心</h2>
      <p>欢迎回来，{{ username }}</p>
    </div>

    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon">💬</div>
        <div class="stat-value">{{ stats.messages }}</div>
        <div class="stat-label">我的留言</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-value">{{ stats.services }}</div>
        <div class="stat-label">服务工单</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📢</div>
        <div class="stat-value">{{ stats.announcements }}</div>
        <div class="stat-label">村务公告</div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <h3>⚡ 快捷入口</h3>
      </div>
      <div style="display:flex;gap:var(--space-sm);flex-wrap:wrap">
        <RouterLink to="/my/interactions" class="btn btn-primary">我的留言</RouterLink>
        <RouterLink to="/announcements" class="btn btn-outline">查看公告</RouterLink>
        <RouterLink to="/" class="btn btn-ghost">返回门户</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue"
import { RouterLink } from "vue-router"
import { getSavedUser } from "@/shared/auth/token"

const savedUser = getSavedUser()
const username = ref(savedUser?.username || "村民")
const stats = reactive({ messages: 0, services: 0, announcements: 0 })

onMounted(() => {
  // Future: load stats from API
})
</script>
