<script setup>
import { onMounted, ref } from "vue";
import { RouterLink, useRouter } from "vue-router";

import { fetchMyServiceTickets } from "@/services/village.api";
import { formatDate } from "@/shared/utils/format";

const latest = ref(null);
const loading = ref(true);
const router = useRouter();

const STATUS_MAP = { 0: "待受理", 1: "处理中", 2: "已完成", 3: "已关闭" };

function statusClass(s) {
  if (s === 0) return "sv-tag sv-tag--pending";
  if (s === 1) return "sv-tag sv-tag--processing";
  if (s === 2) return "sv-tag sv-tag--approved";
  return "sv-tag sv-tag--closed";
}

const actions = [
  { label: "提交工单", desc: "把民生诉求提交给村干部跟进", to: "/village/tickets/new", primary: true },
  { label: "我的工单", desc: "查看处理进度、回复和结果", to: "/village/tickets" },
  { label: "我的留言", desc: "查看个人留言与干部回复", to: "/village/messages" },
];

onMounted(async () => {
  try {
    const data = await fetchMyServiceTickets({ current: 1, size: 1 });
    latest.value = (data.records ?? [])[0] ?? null;
  } catch {
    latest.value = null;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="home">
    <section class="service-hero">
      <div>
        <p>村民服务中心</p>
        <h1>提交诉求，跟进办理，查看回复</h1>
      </div>
      <button class="sv-btn sv-btn--primary" @click="router.push('/village/tickets/new')">提交工单</button>
    </section>

    <section class="action-grid" aria-label="村民服务入口">
      <button
        v-for="a in actions"
        :key="a.to"
        class="action-card"
        :class="{ 'action-card--primary': a.primary }"
        @click="router.push(a.to)"
      >
        <span class="action-label">{{ a.label }}</span>
        <span class="action-desc">{{ a.desc }}</span>
        <span class="action-arrow">进入</span>
      </button>
    </section>

    <section class="latest-panel">
      <div class="section-head">
        <div>
          <p>办理进度</p>
          <h2>最近工单</h2>
        </div>
        <RouterLink to="/village/tickets" class="sv-btn sv-btn--ghost">查看全部</RouterLink>
      </div>

      <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

      <template v-else-if="latest">
        <div class="ticket-row">
          <div class="ticket-main">
            <h3>{{ latest.title }}</h3>
            <time>{{ formatDate(latest.createTime) }}</time>
          </div>
          <span :class="statusClass(latest.status)">{{ STATUS_MAP[latest.status] ?? latest.status }}</span>
          <RouterLink :to="`/village/tickets/${latest.id}`" class="sv-btn sv-btn--secondary">查看详情</RouterLink>
        </div>
      </template>

      <div v-else class="empty-ticket">
        <strong>还没有提交过工单</strong>
        <p>遇到道路、设施、环境、生活服务等问题，可以从这里发起诉求。</p>
        <RouterLink to="/village/tickets/new" class="sv-btn sv-btn--primary">提交第一条工单</RouterLink>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  gap: 20px;
}

.service-hero {
  min-height: 170px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  padding: 26px;
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(16, 35, 28, 0.96), rgba(47, 101, 80, 0.88)),
    var(--forest-950);
  color: #fff;
}

.service-hero p {
  margin: 0 0 4px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 13px;
  font-weight: 720;
}

.service-hero h1 {
  margin: 0;
  max-width: 620px;
  font-size: 30px;
  line-height: 1.24;
  letter-spacing: 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.action-card {
  min-height: 150px;
  display: grid;
  align-content: start;
  gap: 9px;
  padding: 20px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  text-align: left;
  font: inherit;
  transition: border-color 0.16s ease, box-shadow 0.16s ease, transform 0.16s ease;
}

.action-card:hover {
  border-color: var(--border-strong);
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.action-card--primary {
  background: linear-gradient(180deg, #fff, var(--field-50));
}

.action-label {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
}

.action-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

.action-arrow {
  margin-top: auto;
  color: var(--forest-800);
  font-size: 13px;
  font-weight: 760;
}

.latest-panel {
  padding: 24px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  box-shadow: var(--shadow-card);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.section-head p {
  margin: 0 0 2px;
  color: var(--text-placeholder);
  font-size: 12px;
}

.section-head h2 {
  margin: 0;
  font-size: 20px;
}

.ticket-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 14px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.ticket-main h3 {
  margin: 0 0 5px;
  font-size: 17px;
}

.ticket-main time {
  color: var(--text-placeholder);
  font-size: 13px;
}

.empty-ticket {
  padding: 22px;
  border-radius: 8px;
  background: var(--field-50);
  border: 1px solid var(--border-color);
}

.empty-ticket strong {
  display: block;
  margin-bottom: 4px;
  font-size: 17px;
}

.empty-ticket p {
  max-width: 560px;
  margin: 0 0 16px;
  color: var(--text-secondary);
}

@media (max-width: 820px) {
  .action-grid,
  .ticket-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .service-hero {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
