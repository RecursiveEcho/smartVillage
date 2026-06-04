<template>
  <div>
    <div class="page-header">
      <h2>📊 管理仪表盘</h2>
      <p>{{ isCadre ? '村干部工作台' : '系统概览与运维数据' }}</p>
    </div>

    <!-- Stats -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-value">{{ stats.users }}</div>
        <div class="stat-label">注册用户</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📢</div>
        <div class="stat-value">{{ stats.announcements }}</div>
        <div class="stat-label">{{ isCadre ? '我的公告' : '公告总数' }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💬</div>
        <div class="stat-value">{{ stats.messages }}</div>
        <div class="stat-label">村民留言</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-value">{{ stats.pending }}</div>
        <div class="stat-label">待处理</div>
      </div>
    </div>

    <!-- Quick actions -->
    <div class="card">
      <div class="card-header">
        <h3>⚡ 快捷操作</h3>
      </div>
      <div style="display:flex;gap:var(--space-sm);flex-wrap:wrap">
        <RouterLink v-if="isCadre" to="/cadre/announcements" class="btn btn-primary">发布公告</RouterLink>
        <RouterLink v-if="isCadre" to="/cadre/interactions" class="btn btn-outline">处理留言</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin/users" class="btn btn-outline">用户管理</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin/announcements" class="btn btn-ghost">公告管理</RouterLink>
      </div>
    </div>

    <!-- Recent activity preview -->
    <div class="card" style="margin-top: var(--space-lg);">
      <div class="card-header">
        <h3>🕐 最近动态</h3>
      </div>
      <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
      <div v-else-if="activities.length === 0" class="empty-state">
        <p class="text-secondary">暂无动态数据</p>
      </div>
      <div v-else>
        <div v-for="(item, i) in activities" :key="i" style="padding:var(--space-sm) 0;border-bottom:1px solid var(--border-light);display:flex;justify-content:space-between;align-items:center">
          <div>
            <span style="font-weight:500">{{ item.title }}</span>
            <span class="text-xs text-light" style="margin-left:var(--space-sm)">{{ item.desc }}</span>
          </div>
          <span class="text-xs text-light">{{ item.time }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue"
import { RouterLink } from "vue-router"
import { getSavedUser } from "@/shared/auth/token"
import { getAnnouncementPage } from "@/services/announcement.api"
import { getInteractionMessagePage } from "@/services/interaction.api"
import { getAdminUserPage } from "@/services/admin.api"

const savedUser = getSavedUser()
const role = computed(() => savedUser?.role || "admin")
const isAdmin = computed(() => role.value === "admin")
const isCadre = computed(() => role.value === "cadre")

const stats = reactive({ users: 0, announcements: 0, messages: 0, pending: 0 })
const activities = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const [aRes, mRes] = await Promise.allSettled([
      getAnnouncementPage({ current: 1, size: 1 }),
      getInteractionMessagePage({ current: 1, size: 1 }),
    ])

    if (aRes.status === "fulfilled") {
      stats.announcements = aRes.value.data?.total || 0
    }
    if (mRes.status === "fulfilled") {
      stats.messages = mRes.value.data?.total || 0
    }

    // Build activity feed from announcements
    const annData = aRes.status === "fulfilled" ? aRes.value.data?.records || [] : []
    activities.value = annData.slice(0, 5).map(item => ({
      title: "📢 " + (item.title || "公告"),
      desc: "新公告发布",
      time: item.createTime ? new Date(item.createTime).toLocaleDateString("zh-CN") : ""
    }))
  } catch {
    // silent
  } finally {
    loading.value = false
  }
})
</script>
