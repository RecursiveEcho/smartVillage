<template>
  <div class="page-container">
    <div v-if="isDetail && detail">
      <!-- Detail View -->
      <a href="#" class="text-sm text-secondary" @click.prevent="$router.push('/announcements')" style="display:inline-block;margin-bottom:var(--space-md)">
        ← 返回公告列表
      </a>
      <div class="card">
        <div class="detail-header">
          <h1>{{ detail.title }}</h1>
          <div class="detail-meta">
            <span>📅 {{ formatDate(detail.createTime) }}</span>
            <span>👤 {{ detail.createUserName || '管理员' }}</span>
            <span class="badge" :class="statusClass(detail.status)">{{ statusText(detail.status) }}</span>
          </div>
        </div>
        <div class="detail-body">
          {{ detail.content || '暂无内容' }}
        </div>
      </div>
    </div>

    <div v-else>
      <!-- List View -->
      <div class="page-header">
        <h2>📢 村务公告</h2>
        <p>查看最新的村务通知、政策文件和公示信息</p>
      </div>

      <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
      <div v-else-if="announcements.length === 0" class="empty-state">
        <div class="icon">📭</div>
        <p>暂无公告</p>
      </div>
      <div v-else>
        <div class="card" style="padding:0;overflow:hidden">
          <table>
            <thead>
              <tr>
                <th>标题</th>
                <th>状态</th>
                <th>发布时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in announcements" :key="item.id">
                <td style="font-weight:500">{{ item.title }}</td>
                <td><span class="badge" :class="statusClass(item.status)">{{ statusText(item.status) }}</span></td>
                <td class="text-sm text-secondary">{{ formatDate(item.createTime) }}</td>
                <td>
                  <button class="btn btn-ghost btn-sm" @click="goDetail(item.id)">查看</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="pagination">
          <button :disabled="current <= 1" @click="loadPage(current - 1)">上一页</button>
          <span class="page-info">第 {{ current }} / {{ pages }} 页</span>
          <button :disabled="current >= pages" @click="loadPage(current + 1)">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getAnnouncementPage, getAnnouncementDetail } from "@/services/announcement.api"

const route = useRoute()
const router = useRouter()

const announcements = ref([])
const detail = ref(null)
const loading = ref(true)
const current = ref(1)
const pages = ref(1)
const isDetail = ref(false)

onMounted(async () => {
  if (route.params.id) {
    isDetail.value = true
    await loadDetail(route.params.id)
  } else {
    await loadPage(1)
  }
})

watch(() => route.params.id, async (newId) => {
  if (newId) {
    isDetail.value = true
    await loadDetail(newId)
  } else {
    isDetail.value = false
    await loadPage(1)
  }
})

async function loadPage(page) {
  loading.value = true
  try {
    const res = await getAnnouncementPage({ current: page, size: 10 })
    announcements.value = res.data?.records || []
    current.value = res.data?.current || page
    pages.value = res.data?.pages || 1
  } catch {
    announcements.value = []
  } finally {
    loading.value = false
  }
}

async function loadDetail(id) {
  loading.value = true
  try {
    const res = await getAnnouncementDetail(id)
    detail.value = res.data
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/announcements/${id}`)
}

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
  return d.toLocaleDateString("zh-CN")
}
</script>
