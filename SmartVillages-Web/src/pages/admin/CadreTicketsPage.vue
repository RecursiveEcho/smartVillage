<script setup>
import { onMounted, ref } from "vue";

import { fetchCadreServiceTickets, acceptCadreServiceTicket } from "@/services/village.api";
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
    const data = await fetchCadreServiceTickets({ current: page, size });
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

function viewDetail(r) {
  alert(`查看工单 —— 功能开发中\n标题：${r.title}`);
}

async function acceptTicket(r) {
  if (!confirm(`确认受理工单「${r.title}」？`)) return;
  try {
    await acceptCadreServiceTicket(r.id);
    load(current.value);
  } catch { alert("操作失败"); }
}
</script>

<template>
  <div>
    <div class="sv-manager-head">
      <div>
      <h2>工单管理</h2>
        <p>受理和处理村民提交的民生服务工单，优先处理待受理事项。</p>
      </div>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>标题</th>
              <th>提交人</th>
              <th>状态</th>
              <th>提交时间</th>
              <th class="col-actions">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td class="title-cell">{{ r.title }}</td>
              <td>{{ r.username || r.createUser || '-' }}</td>
              <td><span class="sv-tag" :class="statusClass(r.status)">{{ STATUS_MAP[r.status] ?? r.status }}</span></td>
              <td>{{ formatDate(r.createTime) }}</td>
              <td class="col-actions">
                <button class="sv-btn sv-btn--ghost sv-btn--sm" @click="viewDetail(r)">查看</button>
                <button v-if="r.status === 0" class="sv-btn sv-btn--primary sv-btn--sm" @click="acceptTicket(r)">受理</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!rows.length" class="sv-empty">暂无工单</div>
    </div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>
  </div>
</template>

<style scoped>
.title-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
