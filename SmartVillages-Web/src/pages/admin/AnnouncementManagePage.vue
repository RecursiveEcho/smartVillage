<template>
  <div>
    <div class="page-header">
      <h2>📢 公告管理</h2>
      <p>{{ isCadre ? '发布和管理村务公告' : '审核和管理所有公告' }}</p>
    </div>

    <div class="toolbar">
      <div class="search-box">
        <input v-model="searchQuery" class="form-input" placeholder="搜索标题..." style="width:200px" @keyup.enter="loadPage(1)" />
        <button class="btn btn-primary btn-sm" @click="loadPage(1)">搜索</button>
      </div>
      <div style="display:flex;gap:var(--space-sm)">
        <select v-model="statusFilter" class="form-select" style="width:auto" @change="loadPage(1)">
          <option value="">全部状态</option>
          <option value="1">已发布</option>
          <option value="2">待审核</option>
          <option value="3">已下架</option>
        </select>
        <button v-if="isCadre" class="btn btn-primary" @click="showCreate = true">+ 发布公告</button>
      </div>
    </div>

    <div class="card" style="padding:0;overflow:hidden">
      <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
      <div v-else-if="announcements.length === 0" class="empty-state">
        <div class="icon">📢</div>
        <p>暂无公告</p>
      </div>
      <table v-else>
        <thead>
          <tr>
            <th>标题</th>
            <th>类型</th>
            <th>状态</th>
            <th>发布时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in announcements" :key="item.id">
            <td style="font-weight:500;max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ item.title }}</td>
            <td><span class="badge badge-info">{{ typeName(item.type) }}</span></td>
            <td><span class="badge" :class="statusClass(item.status)">{{ statusText(item.status) }}</span></td>
            <td class="text-sm text-secondary">{{ formatDate(item.createTime) }}</td>
            <td>
              <div style="display:flex;gap:4px;flex-wrap:wrap">
                <button class="btn btn-sm btn-ghost" @click="viewDetail(item)">详情</button>
                <button v-if="isCadre && item.status === 2" class="btn btn-sm btn-outline" @click="handleAudit(item.id, 1)">审核通过</button>
                <button v-if="isCadre" class="btn btn-sm btn-ghost" @click="handleToggleStatus(item)">
                  {{ item.status === 1 ? '下架' : '上架' }}
                </button>
                <button v-if="isCadre" class="btn btn-sm btn-danger" @click="handleDelete(item.id)">删除</button>
              </div>
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

    <!-- Create Modal -->
    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal-content">
        <h3>发布新公告</h3>
        <form @submit.prevent="handleCreate">
          <div class="form-group">
            <label>标题</label>
            <input v-model="createForm.title" class="form-input" placeholder="公告标题" />
          </div>
          <div class="form-group">
            <label>内容</label>
            <textarea v-model="createForm.content" class="form-textarea" placeholder="公告内容..."></textarea>
          </div>
          <div v-if="actionError" class="alert alert-danger">{{ actionError }}</div>
          <div v-if="actionSuccess" class="alert alert-success">{{ actionSuccess }}</div>
          <div style="display:flex;gap:var(--space-sm);justify-content:flex-end;margin-top:var(--space-md)">
            <button type="button" class="btn btn-ghost" @click="showCreate = false">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="actionLoading">{{ actionLoading ? '发布中...' : '发布' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Detail Modal -->
    <div v-if="detail" class="modal-overlay" @click.self="detail = null">
      <div class="modal-content" style="max-width:700px">
        <h3>{{ detail.title }}</h3>
        <div class="detail-meta" style="margin:var(--space-md) 0">
          <span class="text-xs text-secondary">📅 {{ formatDate(detail.createTime) }}</span>
          <span class="badge" :class="statusClass(detail.status)">{{ statusText(detail.status) }}</span>
        </div>
        <div class="detail-body">{{ detail.content || '暂无内容' }}</div>
        <div style="margin-top:var(--space-lg);text-align:right">
          <button class="btn btn-ghost" @click="detail = null">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue"
import { getSavedUser } from "@/shared/auth/token"
import {
  getCadreAnnouncementPage,
  createCadreAnnouncement,
  updateCadreAnnouncementStatus,
  deleteCadreAnnouncement,
  auditCadreAnnouncement,
} from "@/services/announcement.api"

const savedUser = getSavedUser()
const role = computed(() => savedUser?.role || "")
const isCadre = computed(() => role.value === "cadre" || role.value === "admin")

const announcements = ref([])
const loading = ref(true)
const current = ref(1)
const pages = ref(1)
const searchQuery = ref("")
const statusFilter = ref("")

const showCreate = ref(false)
const actionLoading = ref(false)
const actionError = ref("")
const actionSuccess = ref("")
const createForm = reactive({ title: "", content: "" })

const detail = ref(null)

onMounted(() => loadPage(1))

async function loadPage(page) {
  loading.value = true
  try {
    const params = { current: page, size: 10 }
    if (searchQuery.value) params.title = searchQuery.value
    if (statusFilter.value) params.status = statusFilter.value
    const res = await getCadreAnnouncementPage(params)
    announcements.value = res.data?.records || []
    current.value = res.data?.current || page
    pages.value = res.data?.pages || 1
  } catch {
    announcements.value = []
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  actionError.value = ""
  actionSuccess.value = ""
  if (!createForm.title || !createForm.content) {
    actionError.value = "请填写标题和内容"
    return
  }
  actionLoading.value = true
  try {
    await createCadreAnnouncement({ title: createForm.title, content: createForm.content })
    actionSuccess.value = "公告发布成功！"
    createForm.title = ""
    createForm.content = ""
    showCreate.value = false
    await loadPage(1)
  } catch (e) {
    actionError.value = e?.response?.data?.message || e?.message || "发布失败"
  } finally {
    actionLoading.value = false
  }
}

async function handleToggleStatus(item) {
  const newStatus = item.status === 1 ? 3 : 1
  try {
    await updateCadreAnnouncementStatus(item.id, newStatus)
    item.status = newStatus
  } catch {
    // silent
  }
}

async function handleDelete(id) {
  if (!confirm("确定删除此公告？")) return
  try {
    await deleteCadreAnnouncement(id)
    await loadPage(current.value)
  } catch {
    // silent
  }
}

async function handleAudit(id, status) {
  try {
    await auditCadreAnnouncement(id, status)
    await loadPage(current.value)
  } catch {
    // silent
  }
}

function viewDetail(item) {
  detail.value = item
}

function typeName(type) {
  const map = { 1: "通知", 2: "政策", 3: "公示" }
  return map[type] || "一般"
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
  return new Date(dateStr).toLocaleDateString("zh-CN")
}
</script>
