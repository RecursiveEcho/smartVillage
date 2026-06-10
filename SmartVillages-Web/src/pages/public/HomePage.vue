<script setup>
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";

import { getHotAnnouncements } from "@/services/announcement.api";
import { formatDate } from "@/shared/utils/format";

const serviceModules = [
  { to: "/announcements", title: "通知公告", desc: "集中查看村委通知、政策提醒和重要公示", tag: "公告" },
  { to: "/affairs", title: "村务公开", desc: "查看公开事项、村务记录和办理信息", tag: "公开" },
  { to: "/interactions", title: "互动交流", desc: "提交留言，跟进村干部回复", tag: "互动" },
  { to: "/features", title: "乡村风采", desc: "浏览乡村建设、产业和活动展示", tag: "风采" },
];

const governanceModules = [
  {
    to: "/cadre/population",
    title: "人口台账",
    desc: "围绕户号、成员关系、住址和基础备注形成常住人口管理底册。",
    meta: "基层底册",
  },
  {
    to: "/cadre/house-land",
    title: "房屋土地台账",
    desc: "覆盖房屋、地块、面积、权利人和权证信息，支撑资产与地块核查。",
    meta: "资产核验",
  },
  {
    to: "/cadre/party",
    title: "党建组织信息",
    desc: "维护党组织名称、组织类型、书记、党员人数和联系方式。",
    meta: "组织治理",
  },
  {
    to: "/cadre/affairs",
    title: "公示事项",
    desc: "承接村务事项、公示公开、议事决议和结果反馈等常见公开场景。",
    meta: "公开事项",
  },
];

const metrics = [
  { value: "4", label: "服务入口" },
  { value: "3", label: "角色工作台" },
  { value: "4", label: "基层高频模块" },
  { value: "24h", label: "线上可达" },
];

const hot = ref([]);
const hotLoading = ref(false);

onMounted(async () => {
  hotLoading.value = true;
  try {
    hot.value = await getHotAnnouncements({ size: 5 });
  } catch {
    hot.value = [];
  } finally {
    hotLoading.value = false;
  }
});
</script>

<template>
  <div class="home">
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-copy">
          <p class="hero-kicker">智慧乡村服务门户</p>
          <h1>村务公开、民生诉求、干部办理，一个入口查清楚</h1>
          <p class="hero-sub">
            面向村民、村干部和管理员的日常治理系统。除了公告、工单和留言，还覆盖人口、房屋土地、党建组织、公示事项等基层常见事务。
          </p>
          <div class="hero-actions">
            <RouterLink to="/village" class="sv-btn sv-btn--primary">进入村民中心</RouterLink>
            <RouterLink to="/announcements" class="sv-btn sv-btn--secondary">查看通知公告</RouterLink>
          </div>
        </div>

        <div class="operations-panel" aria-label="村务运行概览">
          <div class="panel-top">
            <span>村务运行</span>
            <strong>今日服务台</strong>
          </div>
          <div class="panel-grid">
            <div class="panel-tile tile-main">
              <span>待办工单</span>
              <strong>民生诉求</strong>
              <small>提交后进入干部办理流程</small>
            </div>
            <div class="panel-tile">
              <span>基础台账</span>
              <strong>人口与房屋土地</strong>
            </div>
            <div class="panel-tile">
              <span>组织治理</span>
              <strong>党建与公示事项</strong>
            </div>
          </div>
          <div class="panel-route">
            <span>村民提交</span>
            <i />
            <span>干部办理</span>
            <i />
            <span>结果反馈</span>
          </div>
        </div>
      </div>
    </section>

    <div class="container">
      <section class="metrics" aria-label="平台能力">
        <div v-for="item in metrics" :key="item.label" class="metric">
          <strong>{{ item.value }}</strong>
          <span>{{ item.label }}</span>
        </div>
      </section>

      <section class="service-grid" aria-label="公共服务入口">
        <RouterLink v-for="m in serviceModules" :key="m.to" :to="m.to" class="service-card">
          <span class="service-tag">{{ m.tag }}</span>
          <strong>{{ m.title }}</strong>
          <p>{{ m.desc }}</p>
          <span class="service-arrow">进入</span>
        </RouterLink>
      </section>

      <section class="governance-section">
        <div class="section-head">
          <div>
            <p>治理覆盖</p>
            <h2>基层常见事务管理</h2>
          </div>
        </div>
        <div class="governance-grid">
          <RouterLink
            v-for="item in governanceModules"
            :key="item.title"
            :to="item.to"
            class="governance-card"
          >
            <span class="governance-meta">{{ item.meta }}</span>
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
            <span class="governance-arrow">进入管理</span>
          </RouterLink>
        </div>
      </section>

      <section class="announcement-section">
        <div class="section-head">
          <div>
            <p>公开信息</p>
            <h2>热门公告</h2>
          </div>
          <RouterLink to="/announcements" class="sv-btn sv-btn--ghost">查看全部</RouterLink>
        </div>

        <div v-if="hotLoading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>
        <ul v-else-if="hot.length" class="hot-list">
          <li v-for="item in hot" :key="item.id">
            <RouterLink :to="`/announcements/${item.id}`" class="hot-row">
              <time>{{ formatDate(item.publishTime || item.createTime) }}</time>
              <span class="hot-title">{{ item.title }}</span>
            </RouterLink>
          </li>
        </ul>
        <div v-else class="sv-empty">暂无公告</div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home {
  padding-bottom: 48px;
}

.hero {
  background:
    linear-gradient(135deg, rgba(16, 35, 28, 0.96), rgba(33, 72, 58, 0.92)),
    var(--forest-950);
  color: #fff;
}

.hero-inner {
  width: min(1180px, calc(100% - 32px));
  min-height: 470px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(340px, 0.72fr);
  align-items: center;
  gap: 56px;
  padding: 54px 0 48px;
}

.hero-copy {
  max-width: 720px;
}

.hero-kicker {
  margin: 0 0 18px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  font-weight: 760;
}

.hero h1 {
  margin: 0;
  max-width: 760px;
  font-size: clamp(34px, 5.6vw, 54px);
  line-height: 1.12;
  font-weight: 860;
  letter-spacing: 0;
}

.hero-sub {
  max-width: 620px;
  margin: 20px 0 0;
  color: rgba(255, 255, 255, 0.78);
  font-size: 17px;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 30px;
}

.hero-actions .sv-btn--secondary {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.22);
  color: #fff;
}

.operations-panel {
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  padding: 18px;
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.26);
}

.panel-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 13px;
}

.panel-top strong {
  color: #fff;
}

.panel-grid {
  display: grid;
  grid-template-columns: 1.3fr 1fr;
  gap: 10px;
}

.panel-tile {
  min-height: 112px;
  border-radius: 8px;
  padding: 14px;
  display: grid;
  align-content: space-between;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.panel-tile span,
.panel-tile small {
  color: rgba(255, 255, 255, 0.64);
  font-size: 12px;
}

.panel-tile strong {
  color: #fff;
  font-size: 22px;
}

.tile-main {
  min-height: 234px;
  grid-row: span 2;
  background:
    linear-gradient(160deg, rgba(184, 135, 59, 0.38), rgba(255, 255, 255, 0.08)),
    rgba(255, 255, 255, 0.1);
}

.tile-main strong {
  font-size: 28px;
}

.panel-route {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 9px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 12px;
}

.panel-route i {
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.22);
}

.container {
  width: min(1180px, calc(100% - 32px));
  margin: 0 auto;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  margin-top: -34px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--border-color);
  box-shadow: var(--shadow-card);
}

.metric {
  min-height: 86px;
  display: grid;
  align-content: center;
  gap: 2px;
  padding: 16px 22px;
  background: #fff;
}

.metric strong {
  font-size: 26px;
  color: var(--forest-900);
}

.metric span {
  color: var(--text-secondary);
  font-size: 13px;
}

.service-grid {
  margin-top: 28px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.service-card {
  min-height: 178px;
  display: grid;
  align-content: start;
  gap: 10px;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  color: var(--text-primary);
  text-decoration: none;
  box-shadow: var(--shadow-card);
  transition: transform 0.16s ease, box-shadow 0.16s ease, border-color 0.16s ease;
}

.service-card:hover {
  transform: translateY(-2px);
  border-color: var(--border-strong);
  box-shadow: var(--shadow-card-hover);
  text-decoration: none;
  color: var(--text-primary);
}

.service-tag {
  width: fit-content;
  padding: 3px 8px;
  border-radius: 999px;
  background: var(--field-100);
  color: var(--forest-800);
  font-size: 12px;
  font-weight: 760;
}

.service-card strong {
  font-size: 18px;
}

.service-card p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.service-arrow {
  margin-top: auto;
  color: var(--forest-800);
  font-weight: 760;
  font-size: 13px;
}

.governance-section,
.announcement-section {
  margin-top: 28px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  padding: 24px;
  box-shadow: var(--shadow-card);
}

.governance-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.governance-card {
  min-height: 164px;
  display: grid;
  align-content: start;
  gap: 10px;
  padding: 18px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: linear-gradient(180deg, rgba(248, 250, 247, 0.94), rgba(255, 255, 255, 1));
  color: inherit;
  text-decoration: none;
  box-shadow: var(--shadow-card);
  transition: transform 0.16s ease, box-shadow 0.16s ease, border-color 0.16s ease;
}

.governance-card:hover {
  transform: translateY(-2px);
  border-color: var(--border-strong);
  box-shadow: var(--shadow-card-hover);
  color: inherit;
  text-decoration: none;
}

.governance-meta {
  color: var(--text-placeholder);
  font-size: 12px;
  font-weight: 720;
}

.governance-card strong {
  color: var(--text-primary);
  font-size: 18px;
  line-height: 1.35;
}

.governance-card p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.governance-arrow {
  margin-top: auto;
  color: var(--forest-800);
  font-size: 13px;
  font-weight: 760;
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
  font-size: 12px;
  color: var(--text-placeholder);
}

.section-head h2 {
  margin: 0;
  font-size: 22px;
}

.hot-list {
  list-style: none;
  margin: 0;
  padding: 0;
  border-top: 1px solid var(--border-color);
}

.hot-row {
  display: grid;
  grid-template-columns: 112px minmax(0, 1fr);
  gap: 18px;
  align-items: center;
  padding: 15px 0;
  color: inherit;
  text-decoration: none;
  border-bottom: 1px solid var(--border-color);
}

.hot-row:hover .hot-title {
  color: var(--forest-800);
}

.hot-row time {
  color: var(--text-placeholder);
  font-size: 12px;
}

.hot-title {
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 980px) {
  .hero-inner {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .operations-panel {
    max-width: 560px;
  }

  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .governance-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .hero-inner,
  .container {
    width: min(100% - 24px, 1180px);
  }

  .hero-inner {
    min-height: 0;
    padding: 38px 0 56px;
  }

  .hero h1 {
    font-size: 31px;
    line-height: 1.18;
  }

  .hero-sub {
    font-size: 15px;
  }

  .panel-grid,
  .service-grid,
  .governance-grid,
  .metrics {
    grid-template-columns: 1fr;
  }

  .metrics {
    margin-top: -28px;
  }

  .hot-row {
    grid-template-columns: 1fr;
    gap: 4px;
  }

  .hot-title {
    white-space: normal;
  }
}
</style>
