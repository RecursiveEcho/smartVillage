<script setup>
import { ref, watch } from "vue";
import { RouterLink, useRoute } from "vue-router";

import { fetchPublicAffairDetail } from "@/services/villageAffair.api";
import { formatDate } from "@/shared/utils/format";

const route = useRoute();
const item = ref(null);
const loading = ref(false);
const error = ref("");

async function load() {
  loading.value = true;
  error.value = "";
  try {
    item.value = await fetchPublicAffairDetail(route.params.id);
  } catch {
    error.value = "加载失败";
  } finally {
    loading.value = false;
  }
}

watch(() => route.params.id, load, { immediate: true });
</script>

<template>
  <div class="sv-page-shell detail-shell">
    <RouterLink to="/affairs" class="sv-back-link">← 返回村务公开</RouterLink>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
    <p v-else-if="error" class="sv-alert">{{ error }}</p>

    <article v-else-if="item" class="sv-card sv-article-card">
      <p class="sv-article-meta">
        <span class="sv-tag sv-tag--approved">村务公开</span>
        <time>{{ formatDate(item.publishTime || item.createTime) }}</time>
      </p>
      <h1 class="sv-article-title">{{ item.title }}</h1>
      <p v-if="item.summary" class="sv-article-lead">{{ item.summary }}</p>
      <div class="sv-article-body" v-html="item.content || ''" />
    </article>
  </div>
</template>

<style scoped>
.detail-shell {
  max-width: 920px;
}
</style>
