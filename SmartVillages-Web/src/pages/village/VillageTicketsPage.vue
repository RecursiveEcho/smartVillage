<script setup>
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";

import { fetchMyServiceTickets } from "@/services/village.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

const STATUS_MAP = { 0: "待受理", 1: "处理中", 2: "已完成", 3: "已关闭" };
function statusClass(s) {
  if (s === 0) return "sv-tag--pending";
  if (s === 1) return "sv-tag--processing";
  if (s === 2) return "sv-tag--approved";
  return "sv-tag--closed";
}

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchMyServiceTickets({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => load());
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
        <h2>我的工单</h2>
        <p>查看已提交的民生工单和处理进度，需要新反馈时可直接提交。</p>
      </div>
      <RouterLink to="/village/tickets/new" class="sv-btn sv-btn--primary">提交工单</RouterLink>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else-if="rows.length" class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>标题</th>
              <th>状态</th>
              <th>提交时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td class="title-cell">{{ r.title }}</td>
              <td><span class="sv-tag" :class="statusClass(r.status)">{{ STATUS_MAP[r.status] ?? r.status }}</span></td>
              <td>{{ formatDate(r.createTime) }}</td>
              <td class="col-actions">
                <RouterLink :to="`/village/tickets/${r.id}`" class="sv-btn sv-btn--ghost sv-btn--sm">详情</RouterLink>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div v-else class="sv-empty">暂无工单，点击“提交工单”发起服务请求</div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span class="sv-pager-total">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>
  </div>
</template>

<style scoped>
.title-cell { max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
