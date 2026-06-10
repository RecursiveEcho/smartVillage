<script setup>
import { ref, watch } from "vue";
import { RouterLink, useRoute } from "vue-router";

import { getAnnouncementDetail } from "@/services/announcement.api";
import { formatDate } from "@/shared/utils/format";

const route = useRoute();
const item = ref(null);
const loading = ref(false);
const error = ref("");

const typeMap = { 1: '村务', 2: '政策', 3: '民生', 4: '应急' };
function typeLabel(t) { return typeMap[t] ?? '公告'; }

async function load() {
  loading.value = true;
  error.value = "";
  try {
    item.value = await getAnnouncementDetail(route.params.id);
  } catch {
    error.value = "加载失败或公告不存在";
  } finally {
    loading.value = false;
  }
}

watch(() => route.params.id, load, { immediate: true });
</script>

<template>
  <div class="sv-page-shell detail-shell">
    <RouterLink to="/announcements" class="sv-back-link">← 返回通知公告</RouterLink>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
    <p v-else-if="error" class="sv-alert">{{ error }}</p>

    <article v-else-if="item" class="sv-card sv-article-card">
      <p class="sv-article-meta">
        <span class="sv-tag sv-tag--draft">{{ typeLabel(item.type) }}</span>
        <time>{{ formatDate(item.publishTime || item.createTime) }}</time>
      </p>
      <h1 class="sv-article-title">{{ item.title }}</h1>
      <div class="sv-article-body" v-html="item.content || ''" />
    </article>
  </div>
</template>

<style scoped>
.detail-shell {
  max-width: 920px;
}
</style>
