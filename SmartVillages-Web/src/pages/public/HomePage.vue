<template>
  <div>
    <!-- Hero -->
    <section class="hero">
      <h1>🌿 智慧乡村</h1>
      <p>数字科技赋能乡村振兴，打造宜居、宜业、宜游的现代化新农村</p>
      <RouterLink to="/announcements" class="btn btn-lg" style="background:white;color:var(--primary);font-weight:600;">
        查看村务公告 →
      </RouterLink>
      <div class="hero-stats">
        <div><div class="num">12</div><div class="label">行政村</div></div>
        <div><div class="num">3,580</div><div class="label">服务人口</div></div>
        <div><div class="num">168</div><div class="label">今日待办</div></div>
        <div><div class="num">95%</div><div class="label">满意度</div></div>
      </div>
    </section>

    <!-- Features -->
    <section class="section">
      <div class="section-title">
        <h2>乡村服务</h2>
        <p>一站式数字化服务，让信息多跑路、村民少跑腿</p>
      </div>
      <div class="feature-grid page-container">
        <div class="feature-card" @click="$router.push('/announcements')" style="cursor:pointer">
          <div class="icon">📢</div>
          <h3>村务公告</h3>
          <p>查看最新村务通知、政策文件、公示信息</p>
        </div>
        <div class="feature-card" @click="$router.push('/features')" style="cursor:pointer">
          <div class="icon">🏘️</div>
          <h3>乡村风采</h3>
          <p>展示乡村美景、特色产业、文化传承</p>
        </div>
        <div class="feature-card" @click="$router.push('/interactions')" style="cursor:pointer">
          <div class="icon">💬</div>
          <h3>村民互动</h3>
          <p>留言反馈、村务咨询、干部回复一站式服务</p>
        </div>
        <div class="feature-card">
          <div class="icon">📋</div>
          <h3>民生服务</h3>
          <p>在线申办、进度查询、结果公示（即将上线）</p>
        </div>
      </div>
    </section>

    <!-- Latest announcements -->
    <section class="section" style="background: var(--bg-card);">
      <div class="page-container">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:var(--space-lg);flex-wrap:wrap;gap:var(--space-sm)">
          <h2>最新公告</h2>
          <RouterLink to="/announcements" class="btn btn-outline btn-sm">查看全部 →</RouterLink>
        </div>
        <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
        <div v-else-if="announcements.length === 0" class="empty-state">
          <div class="icon">📭</div>
          <p>暂无公告</p>
        </div>
        <div v-else class="card-grid">
          <div v-for="item in announcements" :key="item.id" class="card" style="cursor:pointer" @click="$router.push(`/announcements/${item.id}`)">
            <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:var(--space-sm)">
              <span class="badge" :class="statusClass(item.status)">{{ statusText(item.status) }}</span>
              <span class="text-xs text-light">{{ formatDate(item.createTime) }}</span>
            </div>
            <h3 style="font-size:var(--font-size-base);margin-bottom:var(--space-xs);">📄 {{ item.title }}</h3>
            <p class="text-sm text-secondary" style="display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;">
              {{ item.summary || item.content }}
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Quick Links -->
    <section class="section">
      <div class="page-container" style="text-align:center">
        <h2 style="margin-bottom:var(--space-md)">快速入口</h2>
        <div style="display:flex;justify-content:center;gap:var(--space-md);flex-wrap:wrap">
          <RouterLink to="/login" class="btn btn-primary">登录系统</RouterLink>
          <RouterLink to="/announcements" class="btn btn-outline">浏览公告</RouterLink>
          <RouterLink to="/interactions" class="btn btn-ghost">村民留言</RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue"
import { RouterLink } from "vue-router"
import { getAnnouncementPage } from "@/services/announcement.api"

const announcements = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getAnnouncementPage({ current: 1, size: 4 })
    announcements.value = res.data?.records || []
  } catch {
    // 静默失败
  } finally {
    loading.value = false
  }
})

function statusClass(status) {
  const map = { 1: "badge-success", 2: "badge-warning", 3: "badge-danger" }
  return map[status] || "badge-info"
}
function statusText(status) {
  const map = { 1: "已发布", 2: "待审核", 3: "已下架" }
  return map[status] || "未知"
}
function formatDate(dateStr) {
  if (!dateStr) return ""
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}
</script>
