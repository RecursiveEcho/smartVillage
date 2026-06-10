<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import { getCadreAnnouncementPage } from "@/services/announcement.api";
import { fetchCadreServiceTickets } from "@/services/village.api";
import { getCadreInteractionMessagePage } from "@/services/interaction.api";

const stats = ref({ announcements: "-", tickets: "-", messages: "-" });
const loading = ref(true);
const router = useRouter();

onMounted(async () => {
  try {
    const results = await Promise.allSettled([
      getCadreAnnouncementPage({ current: 1, size: 1 }),
      fetchCadreServiceTickets({ current: 1, size: 1 }),
      getCadreInteractionMessagePage({ current: 1, size: 1 }),
    ]);
    stats.value.announcements =
      results[0].status === "fulfilled" ? (results[0].value.total ?? "-") : "-";
    stats.value.tickets =
      results[1].status === "fulfilled" ? (results[1].value.total ?? "-") : "-";
    stats.value.messages =
      results[2].status === "fulfilled" ? (results[2].value.total ?? "-") : "-";
  } finally {
    loading.value = false;
  }
});

const shortcuts = [
  { label: "发布公告", desc: "撰写村务通知和活动提醒", to: "/cadre/announcements" },
  { label: "人口台账", desc: "维护户号、成员和住址信息", to: "/cadre/population" },
  { label: "房屋土地", desc: "维护地块、房屋和权证信息", to: "/cadre/house-land" },
  { label: "党建组织", desc: "维护党组织和党员基础信息", to: "/cadre/party" },
  { label: "公示事项", desc: "维护公开事项、公示结果和发布状态", to: "/cadre/affairs" },
  { label: "处理工单", desc: "查看村民提交的民生诉求", to: "/cadre/tickets" },
  { label: "回复留言", desc: "处理互动交流中的村民留言", to: "/cadre/interactions" },
  { label: "乡村风采", desc: "发布建设成果和活动内容", to: "/cadre/features" },
  { label: "媒体管理", desc: "维护图片和视频素材", to: "/cadre/media" },
];

const governanceBlocks = [
  { title: "人口台账", desc: "已接入户号、姓名、成员关系、地址等基础信息维护页面。", status: "已前端接入" },
  { title: "房屋土地台账", desc: "已接入地块编号、坐落、面积、户主和权证信息管理页面。", status: "已前端接入" },
  { title: "党建组织信息", desc: "已接入党组织名称、组织类型、书记、党员人数和联系电话维护页面。", status: "已前端接入" },
  { title: "公示事项", desc: "已接入公开事项、公示结果、发布状态和详情维护页面。", status: "已前端接入" },
];
</script>

<template>
  <div class="dashboard">
    <header class="page-hero">
      <div>
        <p>村务办理</p>
        <h1>干部工作台</h1>
      </div>
      <button class="sv-btn sv-btn--primary" @click="router.push('/cadre/tickets')">处理工单</button>
    </header>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <template v-else>
      <section class="stat-grid" aria-label="办理统计">
        <article class="stat-card stat-card--gold">
          <span>公告管理</span>
          <strong>{{ stats.announcements }}</strong>
          <small>通知与公开发布</small>
        </article>
        <article class="stat-card">
          <span>民生工单</span>
          <strong>{{ stats.tickets }}</strong>
          <small>诉求受理与反馈</small>
        </article>
        <article class="stat-card">
          <span>留言总数</span>
          <strong>{{ stats.messages }}</strong>
          <small>互动回复记录</small>
        </article>
      </section>

      <section class="ledger-section">
        <div class="section-head">
          <p>基层治理</p>
          <h2>常见事务模块</h2>
        </div>
        <div class="ledger-grid">
          <article v-for="item in governanceBlocks" :key="item.title" class="ledger-card">
            <span class="sv-tag sv-tag--approved">
              {{ item.status }}
            </span>
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </article>
        </div>
      </section>

      <section class="action-section">
        <div class="section-head">
          <p>常用办理</p>
          <h2>村务操作入口</h2>
        </div>
        <div class="shortcut-grid">
          <button
            v-for="s in shortcuts"
            :key="s.to"
            class="shortcut-card"
            @click="router.push(s.to)"
          >
            <span class="shortcut-label">{{ s.label }}</span>
            <span class="shortcut-desc">{{ s.desc }}</span>
            <span class="shortcut-arrow">进入</span>
          </button>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.dashboard {
  display: grid;
  gap: 22px;
}

.page-hero {
  min-height: 150px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  padding: 26px;
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(16, 35, 28, 0.96), rgba(47, 101, 80, 0.9)),
    var(--forest-950);
  color: #fff;
}

.page-hero p {
  margin: 0 0 4px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 13px;
  font-weight: 720;
}

.page-hero h1 {
  margin: 0;
  font-size: 30px;
  line-height: 1.2;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.stat-card {
  min-height: 142px;
  display: grid;
  align-content: space-between;
  gap: 12px;
  padding: 22px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-card);
}

.stat-card--gold {
  background: linear-gradient(180deg, #fff, var(--gold-100));
}

.stat-card span,
.stat-card small {
  color: var(--text-secondary);
  font-size: 13px;
}

.stat-card strong {
  color: var(--forest-900);
  font-size: 34px;
  line-height: 1;
}

.action-section {
  padding: 24px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  box-shadow: var(--shadow-card);
}

.ledger-section,
.action-section {
  padding: 24px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  box-shadow: var(--shadow-card);
}

.ledger-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.ledger-card {
  min-height: 156px;
  display: grid;
  align-content: start;
  gap: 10px;
  padding: 18px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: linear-gradient(180deg, #fff, var(--field-50));
}

.ledger-card strong {
  color: var(--text-primary);
  font-size: 18px;
  line-height: 1.35;
}

.ledger-card p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.section-head {
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

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.shortcut-card {
  min-height: 132px;
  display: grid;
  align-content: start;
  gap: 8px;
  padding: 18px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  text-align: left;
  font: inherit;
  transition: border-color 0.16s ease, box-shadow 0.16s ease, transform 0.16s ease;
}

.shortcut-card:hover {
  border-color: var(--border-strong);
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.shortcut-label {
  font-size: 16px;
  font-weight: 780;
  color: var(--text-primary);
}

.shortcut-desc {
  color: var(--text-secondary);
  font-size: 13px;
}

.shortcut-arrow {
  margin-top: auto;
  color: var(--forest-800);
  font-size: 13px;
  font-weight: 760;
}

@media (max-width: 980px) {
  .stat-grid,
  .ledger-grid,
  .shortcut-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .page-hero {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
