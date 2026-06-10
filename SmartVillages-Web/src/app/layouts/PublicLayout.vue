<script setup>
import { computed } from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";

import { currentUser, invalidateUserCache } from "@/shared/auth/session";
import { isAuthenticated } from "@/shared/auth/guards";
import { removeToken } from "@/shared/auth/token";
import { logout as apiLogout } from "@/services/auth.api";

const router = useRouter();

const loggedIn = computed(() => isAuthenticated());
const user = computed(() => currentUser.value);

const adminLinks = computed(() => {
  const role = (user.value?.role || "").replace(/^ROLE_/i, "").toUpperCase();
  if (role === "ADMIN") return [{ to: "/admin", label: "管理后台" }];
  if (role === "CADRE") return [{ to: "/cadre", label: "干部工作台" }];
  if (role === "VILLAGER") return [{ to: "/village", label: "村民中心" }];
  return [];
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
  <div class="public-layout">
    <header class="site-header">
      <div class="header-inner">
        <RouterLink to="/" class="brand-link">
          <span class="brand-mark">SV</span>
          <span class="brand-copy">
            <strong>智慧乡村</strong>
            <small>公共服务门户</small>
          </span>
        </RouterLink>

        <nav class="site-nav" aria-label="主导航">
          <RouterLink to="/announcements" class="nav-link">通知公告</RouterLink>
          <RouterLink to="/features" class="nav-link">乡村风采</RouterLink>
          <RouterLink to="/affairs" class="nav-link">村务公开</RouterLink>
          <RouterLink to="/interactions" class="nav-link">互动交流</RouterLink>
        </nav>

        <div class="header-actions">
          <template v-if="loggedIn">
            <RouterLink to="/profile" class="avatar-link" title="个人中心">
              <img v-if="user?.avatar" :src="user.avatar" alt="" class="avatar-img" />
              <span v-else class="avatar-placeholder">{{ (user?.username || "?")[0] }}</span>
            </RouterLink>
            <RouterLink v-for="link in adminLinks" :key="link.to" :to="link.to" class="role-link">
              {{ link.label }}
            </RouterLink>
            <button class="sv-btn sv-btn--sm sv-btn--ghost" @click="handleLogout">退出</button>
          </template>
          <RouterLink v-else to="/login" class="sv-btn sv-btn--sm sv-btn--primary">登录</RouterLink>
        </div>
      </div>
    </header>

    <main class="main">
      <RouterView />
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <span>智慧乡村综合管理系统</span>
        <span>村务公开 · 民生工单 · 互动交流</span>
      </div>
    </footer>
  </div>
</template>

<style>
@import "@/styles/theme.css";
</style>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-page);
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid rgba(220, 229, 221, 0.9);
  backdrop-filter: blur(14px);
}

.header-inner {
  width: min(1180px, calc(100% - 32px));
  min-height: 68px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 22px;
}

.brand-link {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--text-primary);
  text-decoration: none;
  flex-shrink: 0;
}

.brand-link:hover {
  color: var(--text-primary);
}

.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--forest-900);
  color: #fff;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0;
  box-shadow: 0 10px 24px rgba(16, 35, 28, 0.16);
}

.brand-copy {
  display: grid;
  gap: 1px;
}

.brand-copy strong {
  font-size: 16px;
  line-height: 1.2;
}

.brand-copy small {
  font-size: 12px;
  color: var(--text-secondary);
}

.site-nav {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 4px;
  min-width: 0;
}

.nav-link,
.role-link {
  min-height: 36px;
  display: inline-flex;
  align-items: center;
  padding: 0 12px;
  border-radius: 999px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 650;
  text-decoration: none;
  white-space: nowrap;
}

.nav-link:hover,
.nav-link.router-link-active,
.role-link:hover {
  background: var(--field-100);
  color: var(--forest-900);
  text-decoration: none;
}

.header-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
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
  transition: border-color 0.16s ease, box-shadow 0.16s ease;
}

.avatar-link:hover {
  border-color: var(--forest-700);
  box-shadow: 0 0 0 3px var(--color-primary-soft);
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

.main {
  flex: 1;
}

.footer {
  background: var(--forest-950);
  color: rgba(255, 255, 255, 0.72);
  padding: 24px 16px;
  font-size: 13px;
}

.footer-inner {
  width: min(1180px, 100%);
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 900px) {
  .header-inner {
    min-height: 78px;
    align-items: flex-start;
    padding: 12px 0;
    flex-wrap: wrap;
    column-gap: 12px;
    row-gap: 10px;
  }

  .brand-link {
    order: 1;
  }

  .header-actions {
    order: 2;
    margin-left: auto;
  }

  .site-nav {
    order: 3;
    flex: 0 0 100%;
    width: 100%;
    justify-content: flex-start;
    overflow-x: auto;
    gap: 6px;
    padding-bottom: 2px;
  }

  .nav-link {
    background: #fff;
    border: 1px solid var(--border-color);
  }
}

@media (max-width: 520px) {
  .header-inner {
    width: min(100% - 24px, 1180px);
  }

  .brand-copy small,
  .role-link {
    display: none;
  }

  .nav-link {
    min-height: 34px;
    padding: 0 11px;
    font-size: 13px;
  }
}
</style>
