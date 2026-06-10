<script setup>
import { onMounted, ref } from "vue";
import { RouterLink, RouterView, useRoute, useRouter } from "vue-router";

import { logout as apiLogout } from "@/services/auth.api";
import { currentUser, ensureUser, invalidateUserCache } from "@/shared/auth/session";
import { removeToken } from "@/shared/auth/token";

const route = useRoute();
const router = useRouter();

const sidebarCollapsed = ref(false);

onMounted(async () => {
  await ensureUser();
  if (!currentUser.value) {
    router.replace("/login");
  }
});

const menu = [
  { to: "/cadre", label: "工作首页", icon: "总", exact: true },
  { to: "/cadre/announcements", label: "公告管理", icon: "告" },
  { to: "/cadre/population", label: "人口台账", icon: "人" },
  { to: "/cadre/house-land", label: "房屋土地", icon: "地" },
  { to: "/cadre/party", label: "党建组织", icon: "党" },
  { to: "/cadre/affairs", label: "公示事项", icon: "示" },
  { to: "/cadre/features", label: "风采管理", icon: "景" },
  { to: "/cadre/interactions", label: "留言处理", icon: "言" },
  { to: "/cadre/tickets", label: "工单管理", icon: "单" },
  { to: "/cadre/media", label: "媒体管理", icon: "媒" },
];

function isActive(item) {
  if (item.exact) return route.path === item.to;
  return route.path === item.to || route.path.startsWith(item.to + "/");
}

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value;
}

async function handleLogout() {
  try {
    await apiLogout();
  } catch {
    /* ignore */
  }
  removeToken();
  invalidateUserCache();
  router.replace("/login");
}
</script>

<template>
  <div class="workspace-layout" :class="{ collapsed: sidebarCollapsed }">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <RouterLink to="/" class="brand-link">
          <span class="brand-mark">SV</span>
          <span class="brand-text">村务工作台</span>
        </RouterLink>
      </div>

      <nav class="sidebar-nav" aria-label="干部工作台导航">
        <RouterLink
          v-for="item in menu"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          :class="{ active: isActive(item) }"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <button class="collapse-btn" :title="sidebarCollapsed ? '展开侧栏' : '收起侧栏'" @click="toggleSidebar">
          {{ sidebarCollapsed ? ">" : "<" }}
        </button>
      </div>
    </aside>

    <div class="main-area">
      <header class="topbar">
        <div class="topbar-left">
          <span class="section-kicker">村务办理</span>
          <strong class="topbar-title">干部工作台</strong>
        </div>

        <div class="topbar-right">
          <RouterLink to="/profile" class="avatar-link" title="个人中心">
            <img v-if="currentUser?.avatar" :src="currentUser.avatar" alt="" class="avatar-img" />
            <span v-else class="avatar-placeholder">{{ (currentUser?.username || "?")[0] }}</span>
          </RouterLink>
          <span class="user-name">{{ currentUser?.username }}</span>
          <span class="user-role sv-tag sv-tag--processing">村干部</span>
          <button class="sv-btn sv-btn--sm sv-btn--secondary" @click="handleLogout">退出</button>
        </div>
      </header>

      <main class="content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.workspace-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-page);
}

.sidebar {
  width: var(--sidebar-w);
  flex-shrink: 0;
  background: var(--forest-950);
  color: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.2s ease;
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 100;
}

.collapsed .sidebar {
  width: 68px;
}

.sidebar-brand {
  min-height: var(--topbar-h);
  display: flex;
  align-items: center;
  padding: 0 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #fff;
  text-decoration: none;
  width: 100%;
}

.brand-mark {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-size: 12px;
  font-weight: 850;
}

.brand-text {
  font-size: 16px;
  font-weight: 760;
  white-space: nowrap;
}

.collapsed .brand-text {
  display: none;
}

.sidebar-nav {
  flex: 1;
  padding: 14px 10px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 42px;
  padding: 0 12px;
  color: rgba(255, 255, 255, 0.72);
  text-decoration: none;
  font-size: 14px;
  font-weight: 650;
  border-radius: 8px;
  transition: background 0.16s ease, color 0.16s ease;
}

.nav-item + .nav-item {
  margin-top: 4px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  text-decoration: none;
}

.nav-item.active,
:deep(.router-link-active) {
  background: #fff;
  color: var(--forest-950);
}

.nav-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  font-size: 12px;
  font-weight: 800;
}

.nav-item.active .nav-icon,
:deep(.router-link-active) .nav-icon {
  background: var(--field-100);
  color: var(--forest-800);
}

.collapsed .nav-label {
  display: none;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.collapse-btn {
  width: 100%;
  height: 34px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.main-area {
  flex: 1;
  margin-left: var(--sidebar-w);
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: margin-left 0.2s ease;
}

.collapsed .main-area {
  margin-left: 68px;
}

.topbar {
  min-height: var(--topbar-h);
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  gap: 16px;
  backdrop-filter: blur(14px);
}

.topbar-left {
  display: grid;
  gap: 1px;
}

.section-kicker {
  font-size: 12px;
  color: var(--text-placeholder);
}

.topbar-title {
  font-size: 16px;
  color: var(--text-primary);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-link {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid var(--border-color);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: var(--field-100);
  color: var(--forest-800);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
}

.user-name {
  font-size: 14px;
  font-weight: 700;
}

.content {
  flex: 1;
  padding: 24px;
  min-height: 0;
}

@media (max-width: 768px) {
  .sidebar {
    width: 68px;
  }

  .brand-text,
  .nav-label {
    display: none;
  }

  .main-area {
    margin-left: 68px;
  }

  .topbar {
    align-items: flex-start;
    flex-direction: column;
    padding: 12px 14px;
  }

  .topbar-right {
    flex-wrap: wrap;
  }

  .content {
    padding: 14px;
  }
}
</style>
