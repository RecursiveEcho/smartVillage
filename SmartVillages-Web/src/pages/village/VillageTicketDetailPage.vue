<script setup>
import { onMounted, ref } from "vue";
import { RouterLink, useRoute } from "vue-router";

import { fetchMyServiceTicketDetail, closeMyServiceTicket } from "@/services/village.api";
import { formatDate } from "@/shared/utils/format";

const route = useRoute();
const id = route.params.id;

const detail = ref(null);
const loading = ref(true);
const error = ref("");

const STATUS_MAP = { 0: "待受理", 1: "处理中", 2: "已完成", 3: "已关闭" };
function statusClass(s) {
  if (s === 0) return "sv-tag--pending";
  if (s === 1) return "sv-tag--processing";
  if (s === 2) return "sv-tag--approved";
  return "sv-tag--closed";
}

onMounted(async () => {
  try {
    detail.value = await fetchMyServiceTicketDetail(id);
  } catch {
    error.value = "加载工单详情失败";
  } finally {
    loading.value = false;
  }
});

async function handleClose() {
  if (!confirm("确认关闭该工单？")) return;
  try {
    await closeMyServiceTicket(id);
    detail.value.status = 3;
  } catch { alert("操作失败"); }
}
</script>

<template>
  <div>
    <RouterLink to="/village/tickets" class="sv-back-link">← 返回我的工单</RouterLink>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
    <div v-else-if="error" class="sv-empty">{{ error }}</div>

    <article v-else class="sv-card ticket-detail-card">
      <div class="ticket-detail-head">
        <h2>{{ detail.title }}</h2>
        <span class="sv-tag" :class="statusClass(detail.status)">{{ STATUS_MAP[detail.status] ?? detail.status }}</span>
      </div>

      <div class="ticket-meta">
        提交时间：{{ formatDate(detail.createTime) }}
      </div>

      <div class="ticket-body">{{ detail.content }}</div>

      <div v-if="detail.status < 3" class="ticket-actions">
        <button class="sv-btn sv-btn--danger" @click="handleClose">关闭工单</button>
      </div>
    </article>
  </div>
</template>

<style scoped>
.ticket-detail-card {
  max-width: 760px;
}

.ticket-detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.ticket-detail-head h2 {
  margin: 0;
  color: var(--text-primary);
  font-size: 22px;
  line-height: 1.35;
}

.ticket-meta {
  margin-bottom: 16px;
  color: var(--text-secondary);
  font-size: 13px;
}

.ticket-body {
  margin-bottom: 20px;
  white-space: pre-wrap;
  color: var(--text-primary);
  line-height: 1.78;
}

.ticket-actions {
  text-align: right;
}
</style>
