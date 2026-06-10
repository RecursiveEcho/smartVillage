<script setup>
import { ref, watch } from "vue";
import { RouterLink, useRoute } from "vue-router";

import { fetchFeatureDetail } from "@/services/feature.api";
import { formatDate } from "@/shared/utils/format";

const route = useRoute();
const item = ref(null);
const loading = ref(false);
const error = ref("");

async function load() {
  loading.value = true;
  error.value = "";
  try {
    item.value = await fetchFeatureDetail(route.params.id);
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
    <RouterLink to="/features" class="sv-back-link">← 返回乡村风采</RouterLink>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
    <p v-else-if="error" class="sv-alert">{{ error }}</p>

    <article v-else-if="item" class="sv-card sv-article-card">
      <p class="sv-article-meta">
        <span class="sv-tag sv-tag--approved">乡村风采</span>
        <time>{{ formatDate(item.createTime) }}</time>
      </p>
      <h1 class="sv-article-title">{{ item.title }}</h1>
      <div v-if="item.cover" class="sv-detail-hero">
        <img :src="item.cover" :alt="item.title" />
      </div>
      <div class="sv-article-body" v-html="item.content || ''" />
    </article>
  </div>
</template>

<style scoped>
.detail-shell {
  max-width: 920px;
}
</style>
