<script setup>
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";

import { fetchFeaturePage } from "@/services/feature.api";
import { formatDate } from "@/shared/utils/format";

const records = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await fetchFeaturePage({ current: page, size });
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
        <p>建设展示</p>
        <h1>乡村风采</h1>
      </div>
      <span>展示乡村建设、产业发展、公共活动和村庄风貌。</span>
    </header>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else-if="records.length" class="grid">
      <RouterLink v-for="row in records" :key="row.id" :to="`/features/${row.id}`" class="card">
        <div class="cover">
          <img v-if="row.cover" :src="row.cover" :alt="row.title" />
          <div v-else class="cover-placeholder" />
        </div>
        <div class="card-body">
          <h2>{{ row.title }}</h2>
          <time>{{ formatDate(row.createTime) }}</time>
        </div>
      </RouterLink>
    </div>

    <div v-else class="sv-empty">暂无风采展示</div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span style="color:var(--text-placeholder)">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>
  </div>
</template>

<style scoped>
.grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.card {
  background: #fff; border-radius: var(--radius-card); overflow: hidden;
  border: 1px solid var(--border-color); box-shadow: var(--shadow-card);
  text-decoration: none; color: inherit;
  transition: transform 0.16s ease, box-shadow 0.16s ease, border-color 0.16s ease;
}
.card:hover { transform: translateY(-2px); border-color: var(--border-strong); box-shadow: var(--shadow-card-hover); text-decoration: none; }
.cover { aspect-ratio: 16/10; overflow: hidden; background: var(--field-100); }
.cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder { width: 100%; height: 100%; background: linear-gradient(145deg, var(--field-100), #fff); }
.card-body { padding: 16px; }
.card h2 { margin: 0 0 6px; font-size: 17px; font-weight: 760; color: var(--text-primary); }
.card time { font-size: 12px; color: var(--text-placeholder); }
@media (max-width: 900px) { .grid { grid-template-columns: 1fr; } }
</style>
