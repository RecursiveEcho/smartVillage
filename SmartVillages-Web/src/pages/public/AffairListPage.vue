<script setup>
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";

import { fetchPublicAffairPage } from "@/services/villageAffair.api";
import { formatDate } from "@/shared/utils/format";

const records = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchPublicAffairPage({ current: page, size });
    records.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    records.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => load());
</script>

<template>
  <div class="sv-page-shell">
    <header class="sv-page-head">
      <div>
        <p>公开事项</p>
        <h1>村务公开</h1>
      </div>
      <span>公开事项、村务记录和公示内容会按时间归档，便于村民查阅。</span>
    </header>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <template v-else-if="records.length">
      <div class="list">
        <RouterLink v-for="row in records" :key="row.id" :to="`/affairs/${row.id}`" class="list-row">
          <div class="row-main">
            <h2>{{ row.title }}</h2>
            <p class="row-meta">
              <time>{{ formatDate(row.publishTime || row.createTime) }}</time>
              <span>·</span>
              <span>{{ row.orgName || row.villageName || '村委会' }}</span>
            </p>
          </div>
          <p v-if="row.summary" class="row-sum">{{ row.summary }}</p>
        </RouterLink>
      </div>

      <div class="sv-pager" v-if="pages > 1">
        <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
        <span class="sv-pager-current">{{ current }}</span>
        <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
        <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
      </div>
    </template>

    <div v-else class="sv-empty">暂无村务信息</div>
  </div>
</template>

<style scoped>
.list { border: 1px solid var(--border-color); border-radius: var(--radius-card); overflow: hidden; box-shadow: var(--shadow-card); }
.list-row {
  display: block; padding: 20px 24px; text-decoration: none; color: inherit;
  border-bottom: 1px solid var(--border-color); background: #fff;
}
.list-row:last-child { border-bottom: none; }
.list-row:hover { background: var(--field-50); text-decoration: none; }
.row-main h2 { margin: 0 0 6px; font-size: 17px; font-weight: 760; color: var(--text-primary); }
.row-meta { margin: 0; font-size: 12px; color: var(--text-placeholder); display: flex; gap: 6px; }
.row-sum { margin: 10px 0 0; font-size: 14px; color: var(--text-secondary); max-width: 68em; }
</style>
