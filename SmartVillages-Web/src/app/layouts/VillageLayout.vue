<script setup>
import { onMounted } from "vue";
import { RouterLink, RouterView, useRoute, useRouter } from "vue-router";

import { logout as apiLogout } from "@/services/auth.api";
import { currentUser, ensureUser, invalidateUserCache } from "@/shared/auth/session";
import { removeToken } from "@/shared/auth/token";

const route = useRoute();
const router = useRouter();

const menu = [
  { to: "/village", label: "服务首页", exact: true },
  { to: "/village/tickets", label: "我的工单", match: "tickets" },
  { to: "/village/tickets/new", label: "提交工单", match: "new" },
  { to: "/village/messages", label: "我的留言" },
];

function tabActive(item) {
  const p = route.path.replace(/\/$/, "") || "/";
  if (item.exact) return p === "/village";
  if (item.match === "new") return p === "/village/tickets/new";
  if (item.match === "tickets") return p === "/village/tickets" || /^\/village\/tickets\/\d+$/.test(p);
  return p === item.to || p.startsWith(item.to + "/");
}

onMounted(async () => {
  await ensureUser();
  if (!currentUser.value) {
    router.replace("/login");
  }
});

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
  <div class="village-layout">
    <header class="topbar">
      <div class="top-inner">
        <RouterLink to="/" class="brand-link">
          <span class="brand-mark">SV</span>
          <span>返回门户</span>
        </RouterLink>

        <nav class="tabs" aria-label="村民中心导航">
          <RouterLink
            v-for="m in menu"
            :key="m.to"
            :to="m.to"
            class="tab"
            :class="{ on: tabActive(m) }"
          >
            {{ m.label }}
          </RouterLink>
        </nav>

        <div class="user-actions">
          <RouterLink to="/profile" class="avatar-link" title="个人中心">
            <img v-if="currentUser?.avatar" :src="currentUser.avatar" alt="" class="avatar-img" />
            <span v-else class="avatar-placeholder">{{ (currentUser?.username || "?")[0] }}</span>
          </RouterLink>
          <span class="username">{{ currentUser?.username }}</span>
          <button class="sv-btn sv-btn--sm sv-btn--secondary" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <main class="main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.village-layout {
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(233, 241, 235, 0.72), rgba(244, 247, 242, 1) 260px),
    var(--bg-page);
  display: flex;
  flex-direction: column;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 30;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid var(--border-color);
  backdrop-filter: blur(14px);
}

.top-inner {
  max-width: 1120px;
  width: calc(100% - 32px);
  min-height: 66px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-link {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  color: var(--text-primary);
  text-decoration: none;
  flex-shrink: 0;
  font-weight: 760;
}

.brand-mark {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--forest-900);
  color: #fff;
  font-size: 11px;
  font-weight: 850;
}

.tabs {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.tab {
  min-height: 36px;
  display: inline-flex;
  align-items: center;
  padding: 0 13px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-secondary);
  text-decoration: none;
}

.tab:hover,
.tab.on {
  color: var(--forest-900);
  background: var(--field-100);
  text-decoration: none;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
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

.username {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 760;
}

.main {
  flex: 1;
  padding: 28px 16px 44px;
  width: 100%;
  max-width: 1120px;
  margin: 0 auto;
}

@media (max-width: 780px) {
  .top-inner {
    align-items: flex-start;
    flex-wrap: wrap;
    padding: 12px 0;
  }

  .tabs {
    order: 3;
    width: 100%;
    justify-content: flex-start;
    overflow-x: auto;
    flex-wrap: nowrap;
  }

  .user-actions {
    margin-left: auto;
  }
}

@media (max-width: 520px) {
  .top-inner {
    width: calc(100% - 24px);
  }

  .username {
    display: none;
  }

  .main {
    padding: 18px 12px 32px;
  }
}
</style>
