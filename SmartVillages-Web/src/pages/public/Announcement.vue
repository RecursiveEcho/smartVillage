<script setup>
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";

import { getAnnouncementPage } from "@/services/announcement.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await getAnnouncementPage({ current: page, size });
    rows.value = data.records ?? [];
    current.value = Number(data.current) || page;
    pages.value = Number(data.pages) || 1;
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

const typeMap = { 1: '村务', 2: '政策', 3: '民生', 4: '应急' };
function typeLabel(t) { return typeMap[t] ?? '公告'; }

onMounted(() => load());
</script>

<template>
  <div class="sv-page-shell">
    <header class="sv-page-head">
      <div>
        <p>公开信息</p>
        <h1>通知公告</h1>
      </div>
      <span>村委通知、政策提醒和应急信息会在这里集中展示。</span>
    </header>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <template v-else-if="rows.length">
      <div class="list">
        <RouterLink v-for="item in rows" :key="item.id" :to="`/announcements/${item.id}`" class="list-row">
          <span class="list-tag" :class="`tag-${item.type}`">{{ typeLabel(item.type) }}</span>
          <span class="list-title">{{ item.title }}</span>
          <time>{{ formatDate(item.publishTime || item.createTime) }}</time>
        </RouterLink>
      </div>

      <div class="sv-pager" v-if="pages > 1">
        <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
        <span class="sv-pager-current">{{ current }}</span>
        <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
        <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
      </div>
    </template>

    <div v-else class="sv-empty">暂无公告</div>
  </div>
</template>

<style scoped>
.list { border: 1px solid var(--border-color); border-radius: var(--radius-card); overflow: hidden; box-shadow: var(--shadow-card); }
.list-row {
  display: flex; align-items: center; gap: 16px;
  min-height: 58px; padding: 13px 18px; text-decoration: none; color: inherit;
  border-bottom: 1px solid var(--border-color); background: #fff;
}
.list-row:last-child { border-bottom: none; }
.list-row:hover { background: var(--field-50); text-decoration: none; }
.list-tag {
  font-size: 12px; font-weight: 700; padding: 2px 8px; border-radius: 999px;
  min-width: 48px; text-align: center; flex-shrink: 0;
}
.tag-1 { background: var(--blue-100); color: var(--info); }
.tag-2 { background: var(--gold-100); color: var(--warning); }
.tag-3 { background: var(--field-100); color: var(--success); }
.tag-4 { background: var(--red-100); color: var(--danger); }
.list-title { flex: 1; font-weight: 720; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--text-primary); }
.list-row time { font-size: 12px; color: var(--text-placeholder); white-space: nowrap; }
@media (max-width: 600px) {
  .list-row { flex-wrap: wrap; gap: 8px; }
  .list-title { white-space: normal; width: 100%; }
}
</style>
