<script setup>
import { onMounted, ref } from "vue";

import { getMyInteractionMessagePage } from "@/services/interaction.api";
import { formatDate } from "@/shared/utils/format";

const rows = ref([]);
const loading = ref(false);
const current = ref(1);
const pages = ref(1);
const size = 10;

async function load(page = 1) {
  loading.value = true;
  try {
    const data = await getMyInteractionMessagePage({ current: page, size });
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
        <h2>我的留言</h2>
        <p>查看已提交留言和村委会回复。</p>
      </div>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else-if="rows.length" class="sv-card sv-table-card">
      <div class="sv-table-wrap">
        <table class="sv-table">
          <thead>
            <tr>
              <th class="col-num">ID</th>
              <th>内容</th>
              <th>状态</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in rows" :key="r.id">
              <td class="col-num">{{ r.id }}</td>
              <td class="content-cell">{{ r.content }}</td>
              <td>
                <span class="sv-tag" :class="r.status === 0 ? 'sv-tag--pending' : r.status === 1 ? 'sv-tag--processing' : 'sv-tag--approved'">
                  {{ {0:'待处理',1:'处理中',2:'已回复'}[r.status] ?? r.status }}
                </span>
              </td>
              <td>{{ formatDate(r.createTime) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div v-else class="sv-empty">暂无留言，可在互动交流页提交反馈</div>

    <div class="sv-pager" v-if="pages > 1">
      <button :disabled="current <= 1" @click="load(current - 1)">上一页</button>
      <span class="sv-pager-current">{{ current }}</span>
      <span class="sv-pager-total">/ {{ pages }}</span>
      <button :disabled="current >= pages" @click="load(current + 1)">下一页</button>
    </div>
  </div>
</template>

<style scoped>
.content-cell { max-width: 320px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
