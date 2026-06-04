<template>
  <div class="page-container">
    <div class="page-header">
      <h2>💬 村民互动</h2>
      <p>留言反馈、村务咨询，干部在线回复</p>
    </div>

    <!-- Create Message -->
    <div class="card" style="margin-bottom: var(--space-lg);">
      <div class="card-header">
        <h3>📝 发布留言</h3>
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="msgTitle">标题</label>
          <input id="msgTitle" v-model="form.title" class="form-input" placeholder="请输入留言标题" />
        </div>
        <div class="form-group">
          <label for="msgContent">内容</label>
          <textarea id="msgContent" v-model="form.content" class="form-textarea" placeholder="请输入留言内容..."></textarea>
        </div>
        <div v-if="submitError" class="alert alert-danger">{{ submitError }}</div>
        <div v-if="submitSuccess" class="alert alert-success">{{ submitSuccess }}</div>
        <button type="submit" class="btn btn-primary" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交留言' }}
        </button>
      </form>
    </div>

    <!-- Message List -->
    <div class="card" style="padding:0;overflow:hidden">
      <div class="card-header" style="padding: var(--space-lg); margin-bottom: 0; border-bottom: 1px solid var(--border-light);">
        <h3>📋 全部留言</h3>
      </div>
      <div v-if="loading" class="loading-center"><div class="spinner"></div></div>
      <div v-else-if="messages.length === 0" class="empty-state">
        <div class="icon">💬</div>
        <p>暂无留言</p>
      </div>
      <div v-else>
        <table>
          <thead>
            <tr>
              <th>标题</th>
              <th>发布人</th>
              <th>状态</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="msg in messages" :key="msg.id">
              <td style="font-weight:500">{{ msg.title }}</td>
              <td class="text-sm text-secondary">{{ msg.username || '村民' }}</td>
              <td><span class="badge" :class="msgClass(msg.status)">{{ msgText(msg.status) }}</span></td>
              <td class="text-sm text-secondary">{{ formatDate(msg.createTime) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="pagination">
      <button :disabled="current <= 1" @click="loadPage(current - 1)">上一页</button>
      <span class="page-info">第 {{ current }} / {{ pages }} 页</span>
      <button :disabled="current >= pages" @click="loadPage(current + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue"
import { getInteractionMessagePage, createInteractionMessage } from "@/services/interaction.api"
import { getToken } from "@/shared/auth/token"

const messages = ref([])
const loading = ref(true)
const current = ref(1)
const pages = ref(1)

const form = reactive({ title: "", content: "" })
const submitting = ref(false)
const submitError = ref("")
const submitSuccess = ref("")

onMounted(async () => {
  await loadPage(1)
})

async function loadPage(page) {
  loading.value = true
  try {
    const res = await getInteractionMessagePage({ current: page, size: 10 })
    messages.value = res.data?.records || []
    current.value = res.data?.current || page
    pages.value = res.data?.pages || 1
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitError.value = ""
  submitSuccess.value = ""
  if (!form.title || !form.content) {
    submitError.value = "请填写标题和内容"
    return
  }
  if (!getToken()) {
    submitError.value = "请先登录后再留言"
    return
  }
  submitting.value = true
  try {
    await createInteractionMessage({ title: form.title, content: form.content })
    submitSuccess.value = "留言提交成功！"
    form.title = ""
    form.content = ""
    await loadPage(1)
  } catch (e) {
    submitError.value = e?.response?.data?.message || e?.message || "提交失败"
  } finally {
    submitting.value = false
  }
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
