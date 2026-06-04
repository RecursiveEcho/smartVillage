<template>
  <div>
    <!-- Admin: User Management -->
    <template v-if="isAdmin">
      <div class="page-header">
        <h2>👥 用户管理</h2>
        <p>管理系统用户账号、角色和状态</p>
      </div>

      <div class="toolbar">
        <div class="search-box">
          <input v-model="searchQuery" class="form-input" placeholder="搜索用户名..." style="width:200px" @keyup.enter="loadPage(1)" />
          <button class="btn btn-primary btn-sm" @click="loadPage(1)">搜索</button>
        </div>
        <button class="btn btn-outline btn-sm" @click="showCreate = true">+ 创建村干部</button>
      </div>

      <div class="card" style="padding:0;overflow:hidden">
        <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
        <div v-else-if="users.length === 0" class="empty-state">
          <div class="icon">👥</div>
          <p>暂无用户</p>
        </div>
        <table v-else>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td class="text-sm text-secondary">{{ user.id }}</td>
              <td style="font-weight:500">{{ user.username }}</td>
              <td><span class="badge" :class="roleBadge(user.role)">{{ roleName(user.role) }}</span></td>
              <td><span class="badge" :class="user.status === 1 ? 'badge-success' : 'badge-danger'">{{ user.status === 1 ? '启用' : '禁用' }}</span></td>
              <td class="text-sm text-secondary">{{ formatDate(user.createTime) }}</td>
              <td>
                <button
                  class="btn btn-sm"
                  :class="user.status === 1 ? 'btn-ghost' : 'btn-primary'"
                  @click="toggleStatus(user)"
                >
                  {{ user.status === 1 ? '禁用' : '启用' }}
                </button>
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
    </template>

    <!-- Cadre: Interaction Management -->
    <template v-else-if="isCadre">
      <div class="page-header">
        <h2>💬 留言处理</h2>
        <p>查看和回复村民留言</p>
      </div>

      <div class="card" style="padding:0;overflow:hidden">
        <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
        <div v-else-if="messages.length === 0" class="empty-state">
          <div class="icon">💬</div>
          <p>暂无留言</p>
        </div>
        <table v-else>
          <thead>
            <tr>
              <th>标题</th>
              <th>发布人</th>
              <th>状态</th>
              <th>时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="msg in messages" :key="msg.id">
              <td style="font-weight:500">{{ msg.title }}</td>
              <td class="text-sm text-secondary">{{ msg.username || '村民' }}</td>
              <td><span class="badge" :class="msgClass(msg.status)">{{ msgText(msg.status) }}</span></td>
              <td class="text-sm text-secondary">{{ formatDate(msg.createTime) }}</td>
              <td>
                <button class="btn btn-sm btn-ghost" @click="selectedMsg = msg">回复</button>
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
    </template>

    <!-- Create Cadre Modal -->
    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal-content">
        <h3>创建村干部账号</h3>
        <form @submit.prevent="handleCreateCadre">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="cadreForm.username" class="form-input" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="cadreForm.password" class="form-input" type="password" placeholder="默认 123456" />
          </div>
          <div v-if="createError" class="alert alert-danger">{{ createError }}</div>
          <div v-if="createSuccess" class="alert alert-success">{{ createSuccess }}</div>
          <div style="display:flex;gap:var(--space-sm);justify-content:flex-end;margin-top:var(--space-md)">
            <button type="button" class="btn btn-ghost" @click="showCreate = false">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="creating">创建</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Reply Modal -->
    <div v-if="selectedMsg" class="modal-overlay" @click.self="selectedMsg = null">
      <div class="modal-content">
        <h3>回复留言</h3>
        <div class="card" style="margin-bottom:var(--space-md);background:var(--bg)">
          <p class="text-sm"><strong>{{ selectedMsg.title }}</strong></p>
          <p class="text-xs text-secondary" style="margin-top:var(--space-xs)">{{ selectedMsg.content }}</p>
        </div>
        <div class="form-group">
          <label>回复内容</label>
          <textarea v-model="replyContent" class="form-textarea" placeholder="请输入回复..."></textarea>
        </div>
        <div style="display:flex;gap:var(--space-sm);justify-content:flex-end">
          <button class="btn btn-ghost" @click="selectedMsg = null">取消</button>
          <button class="btn btn-primary" :disabled="replying" @click="handleReply">提交回复</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue"
import { getSavedUser } from "@/shared/auth/token"
import { getAdminUserPage, updateAdminUserStatus, createCadreUser } from "@/services/admin.api"
import { getCadreInteractionMessagePage } from "@/services/interaction.api"

const savedUser = getSavedUser()
const role = computed(() => savedUser?.role || "admin")
const isAdmin = computed(() => role.value === "admin")
const isCadre = computed(() => role.value === "cadre")

// --- User Management ---
const users = ref([])
const searchQuery = ref("")
const loading = ref(true)
const current = ref(1)
const pages = ref(1)

// --- Create Cadre ---
const showCreate = ref(false)
const creating = ref(false)
const createError = ref("")
const createSuccess = ref("")
const cadreForm = reactive({ username: "", password: "123456" })

// --- Cadre Interactions ---
const messages = ref([])
const selectedMsg = ref(null)
const replyContent = ref("")
const replying = ref(false)

onMounted(async () => {
  if (isAdmin.value) await loadPage(1)
  else if (isCadre.value) await loadMessages(1)
})

async function loadPage(page) {
  loading.value = true
  try {
    const params = { current: page, size: 10 }
    if (searchQuery.value) params.username = searchQuery.value
    const res = await getAdminUserPage(params)
    users.value = res.data?.records || []
    current.value = res.data?.current || page
    pages.value = res.data?.pages || 1
  } catch {
    users.value = []
  } finally {
    loading.value = false
  }
}

async function toggleStatus(user) {
  try {
    await updateAdminUserStatus(user.id, user.status === 1 ? 0 : 1)
    user.status = user.status === 1 ? 0 : 1
  } catch {
    // silent
  }
}

async function handleCreateCadre() {
  createError.value = ""
  createSuccess.value = ""
  if (!cadreForm.username) {
    createError.value = "请输入用户名"
    return
  }
  creating.value = true
  try {
    await createCadreUser({ username: cadreForm.username, password: cadreForm.password })
    createSuccess.value = "村干部账号创建成功！"
    cadreForm.username = ""
    cadreForm.password = "123456"
    await loadPage(1)
  } catch (e) {
    createError.value = e?.response?.data?.message || e?.message || "创建失败"
  } finally {
    creating.value = false
  }
}

async function loadMessages(page) {
  loading.value = true
  try {
    const res = await getCadreInteractionMessagePage({ current: page, size: 10 })
    messages.value = res.data?.records || []
    current.value = res.data?.current || page
    pages.value = res.data?.pages || 1
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

function roleBadge(role) {
  const map = { admin: "badge-danger", cadre: "badge-warning", villager: "badge-info" }
  return map[role] || "badge-info"
}
function roleName(role) {
  const map = { admin: "管理员", cadre: "村干部", villager: "村民" }
  return map[role] || role
}
function msgClass(status) {
  const map = { 0: "badge-warning", 1: "badge-info", 2: "badge-success", 3: "badge-danger" }
  return map[status] || "badge-info"
}
function msgText(status) {
  const map = { 0: "待处理", 1: "处理中", 2: "已回复", 3: "已关闭" }
  return map[status] || "未知"
}
function formatDate(dateStr) {
  if (!dateStr) return ""
  return new Date(dateStr).toLocaleDateString("zh-CN")
}
</script>
