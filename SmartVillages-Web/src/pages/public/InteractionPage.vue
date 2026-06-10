<script setup>
import { onMounted, reactive, ref } from "vue";

import { createInteractionMessage, getInteractionMessagePage } from "@/services/interaction.api";
import { formatDate } from "@/shared/utils/format";
import { currentUser } from "@/shared/auth/session";

const rows = ref([]);
const loading = ref(false);
const submitting = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const form = reactive({ content: "" });
const formError = ref("");

async function loadMessages(page = 1) {
  loading.value = true;
  try {
    const data = await getInteractionMessagePage({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

async function submitMessage() {
  formError.value = "";
  if (!form.content.trim()) {
    formError.value = "请输入留言内容";
    return;
  }
  submitting.value = true;
  try {
    await createInteractionMessage({ content: form.content });
    form.content = "";
    await loadMessages(1);
  } catch (e) {
    formError.value = e?.message || "提交失败";
  } finally {
    submitting.value = false;
  }
}

onMounted(() => loadMessages());
</script>

<template>
  <div class="sv-page-shell interaction-shell">
    <div class="sv-page-head">
      <div>
        <p>村民留言</p>
        <h1>互动交流</h1>
      </div>
      <span>公开留言会同步给村干部处理，回复内容会展示在留言下方。</span>
    </div>

    <div v-if="currentUser" class="sv-card composer-card">
      <div class="composer-head">
        <div>
          <h3>发表留言</h3>
          <p>请写清楚地点、问题和希望处理的事项。</p>
        </div>
        <span>{{ form.content.length }} 字</span>
      </div>
      <textarea v-model="form.content" class="sv-textarea" placeholder="请输入留言内容..." rows="3" />
      <p v-if="formError" class="form-error">{{ formError }}</p>
      <div class="composer-actions">
        <button class="sv-btn sv-btn--primary" :disabled="submitting" @click="submitMessage">
          {{ submitting ? '提交中...' : '提交留言' }}
        </button>
      </div>
    </div>
    <div v-else class="sv-card login-hint">
      <p>登录后可以提交留言并查看处理回复。</p>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <template v-else-if="rows.length">
      <div v-for="item in rows" :key="item.id" class="sv-card msg-card">
        <div class="msg-head">
          <span class="msg-author">{{ item.username || '匿名用户' }}</span>
          <time>{{ formatDate(item.createTime) }}</time>
        </div>
        <p>{{ item.content }}</p>
        <div v-if="item.reply" class="msg-reply">
          <span class="reply-label">村干部回复：</span>
          {{ item.reply }}
        </div>
      </div>

      <div class="sv-pager" v-if="pages > 1">
        <button :disabled="current <= 1" @click="loadMessages(current - 1)">上一页</button>
        <span class="sv-pager-current">{{ current }}</span>
        <span class="sv-pager-total">/ {{ pages }}</span>
        <button :disabled="current >= pages" @click="loadMessages(current + 1)">下一页</button>
      </div>
    </template>

    <div v-else class="sv-empty">暂无留言，成为第一条反馈</div>
  </div>
</template>

<style scoped>
.interaction-shell {
  max-width: 920px;
}

.composer-card {
  margin-bottom: 16px;
}

.composer-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.composer-head h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 760;
}

.composer-head p,
.login-hint p {
  margin: 4px 0 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.composer-head span {
  color: var(--text-placeholder);
  font-size: 12px;
}

.composer-actions {
  margin-top: 10px;
  text-align: right;
}

.form-error {
  margin: 7px 0 0;
  color: var(--danger);
  font-size: 13px;
}

.login-hint {
  margin-bottom: 16px;
  background: var(--field-50);
}

.msg-card {
  margin-bottom: 12px;
}

.msg-card p { margin: 0; line-height: 1.6; }
.msg-head { display: flex; justify-content: space-between; margin-bottom: 8px; }
.msg-author { font-weight: 700; color: var(--forest-800); }
.msg-head time { font-size: 12px; color: var(--text-placeholder); }
.msg-reply { margin-top: 12px; padding: 12px 14px; background: var(--field-50); border: 1px solid var(--border-color); border-radius: var(--radius-control); font-size: 13px; }
.reply-label { font-weight: 700; color: var(--forest-800); }
</style>
