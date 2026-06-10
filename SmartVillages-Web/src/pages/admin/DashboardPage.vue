<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import { getAdminUserPage } from "@/services/admin.api";
import { getAnnouncementPage } from "@/services/announcement.api";
import { fetchMediaPage } from "@/services/media.api";

const stats = ref({ users: "-", announcements: "-", media: "-" });
const loading = ref(true);
const router = useRouter();

onMounted(async () => {
  try {
    const [users, announcements, media] = await Promise.allSettled([
      getAdminUserPage({ current: 1, size: 1 }),
      getAnnouncementPage({ current: 1, size: 1 }),
      fetchMediaPage({ current: 1, size: 1 }),
    ]);
    stats.value.users = users.status === "fulfilled" ? (users.value.total ?? "-") : "-";
    stats.value.announcements = announcements.status === "fulfilled" ? (announcements.value.total ?? "-") : "-";
    stats.value.media = media.status === "fulfilled" ? (media.value.total ?? "-") : "-";
  } finally {
    loading.value = false;
  }
});

const shortcuts = [
  { label: "用户管理", desc: "维护村民、干部和管理员账号", to: "/admin/users", tone: "primary" },
  { label: "公共门户", desc: "查看村民侧信息展示效果", to: "/", tone: "plain" },
  { label: "个人中心", desc: "查看当前登录身份信息", to: "/profile", tone: "plain" },
];

const governanceCoverage = [
  "人口台账",
  "房屋土地台账",
  "党建组织信息",
  "公示事项",
];
</script>

<template>
  <div class="dashboard">
    <header class="page-hero">
      <div>
        <p>系统管理</p>
        <h1>管理员概览</h1>
      </div>
      <button class="sv-btn sv-btn--primary" @click="router.push('/admin/users')">管理用户</button>
    </header>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <template v-else>
      <section class="stat-grid" aria-label="系统统计">
        <article class="stat-card">
          <span>系统用户</span>
          <strong>{{ stats.users }}</strong>
          <small>账号与角色权限</small>
        </article>
        <article class="stat-card">
          <span>公告总数</span>
          <strong>{{ stats.announcements }}</strong>
          <small>公共通知内容</small>
        </article>
        <article class="stat-card">
          <span>媒体文件</span>
          <strong>{{ stats.media }}</strong>
          <small>图片与视频资源</small>
        </article>
      </section>

      <section class="action-section">
        <div class="section-head">
          <p>常用入口</p>
          <h2>下一步操作</h2>
        </div>
        <div class="shortcut-grid">
          <button
            v-for="s in shortcuts"
            :key="s.to"
            class="shortcut-card"
            :class="`shortcut-card--${s.tone}`"
            @click="router.push(s.to)"
          >
            <span class="shortcut-label">{{ s.label }}</span>
            <span class="shortcut-desc">{{ s.desc }}</span>
            <span class="shortcut-arrow">进入</span>
          </button>
        </div>
      </section>

      <section class="coverage-section">
        <div class="section-head">
          <p>业务覆盖</p>
          <h2>基层常见事务</h2>
        </div>
        <div class="coverage-list">
          <span v-for="item in governanceCoverage" :key="item" class="coverage-chip">{{ item }}</span>
        </div>
        <p class="coverage-note">
          这些模块的后端接口能力已经存在，人口台账、房屋土地台账、党建组织信息、公示事项页面现已接入到干部端，可继续按权限范围扩展到管理员侧。
        </p>
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

.coverage-section,
.action-section {
  padding: 24px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  background: #fff;
  box-shadow: var(--shadow-card);
}

.coverage-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.coverage-chip {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  background: var(--field-50);
  border: 1px solid var(--border-color);
  color: var(--forest-900);
  font-size: 13px;
  font-weight: 720;
}

.coverage-note {
  margin: 14px 0 0;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.75;
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
  min-height: 138px;
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

.shortcut-card--primary {
  background: var(--field-50);
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

@media (max-width: 900px) {
  .stat-grid,
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
