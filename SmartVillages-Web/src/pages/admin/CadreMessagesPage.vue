<script setup>
import { onMounted, ref } from "vue";

import { getCadreInteractionMessagePage, replyCadreInteractionMessage, markCadreInteractionMessageProcessing } from "@/services/interaction.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const replyId = ref(null);
const replyContent = ref("");
const replyLoading = ref(false);

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await getCadreInteractionMessagePage({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

function openReply(r) {
  replyId.value = r.id;
  replyContent.value = "";
}

function cancelReply() {
  replyId.value = null;
  replyContent.value = "";
}

async function submitReply() {
  if (!replyContent.value.trim()) return;
  replyLoading.value = true;
  try {
    await replyCadreInteractionMessage(replyId.value, { content: replyContent.value });
    cancelReply();
    load(current.value);
  } catch { alert("回复失败"); }
  finally { replyLoading.value = false; }
}

async function markProcessing(r) {
  try {
    await markCadreInteractionMessageProcessing(r.id);
    load(current.value);
  } catch { alert("操作失败"); }
}

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
      <h2>留言处理</h2>
        <p>查看、标记并回复村民留言，让处理状态保持清楚。</p>
      </div>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>留言人</th>
              <th>内容</th>
              <th>状态</th>
              <th>时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td>{{ r.username || r.createUser || '-' }}</td>
              <td class="content-cell">{{ r.content }}</td>
              <td>
                <span class="sv-tag" :class="r.status === 0 ? 'sv-tag--pending' : r.status === 1 ? 'sv-tag--processing' : 'sv-tag--approved'">
                  {{ {0:'待处理',1:'处理中',2:'已回复'}[r.status] ?? r.status }}
                </span>
              </td>
              <td>{{ formatDate(r.createTime) }}</td>
              <td class="col-actions">
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="openReply(r)">回复</button>
                <button v-if="r.status === 0" class="sv-btn sv-btn--ghost sv-btn--sm" @click="markProcessing(r)">标记处理中</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无留言</div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>

    <!-- 回复弹窗 -->
    <div v-if="replyId" class="sv-modal-mask" @click.self="cancelReply">
      <div class="sv-modal">
        <div class="sv-modal-header">
          <h3>回复留言</h3>
          <button class="sv-modal-close" @click="cancelReply">✕</button>
        </div>
        <div class="sv-modal-body">
          <textarea v-model="replyContent" class="sv-textarea" rows="4" placeholder="请输入回复内容..."></textarea>
        </div>
        <div class="sv-modal-footer">
          <button class="sv-btn sv-btn--secondary" @click="cancelReply">取消</button>
          <button class="sv-btn sv-btn--primary" :disabled="replyLoading" @click="submitReply">
            {{ replyLoading ? '发送中...' : '发送回复' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-cell { max-width: 240px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
