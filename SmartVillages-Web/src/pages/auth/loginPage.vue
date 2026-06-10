<script setup>
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import { login } from "@/services/auth.api";
import { setToken } from "@/shared/auth/token";
import { currentUser, ensureUser, invalidateUserCache } from "@/shared/auth/session";

const route = useRoute();
const router = useRouter();

const form = reactive({ username: "", password: "" });
const loading = ref(false);
const error = ref("");

async function handleLogin() {
  error.value = "";

  if (!form.username.trim() || !form.password) {
    error.value = "请输入用户名和密码";
    return;
  }

  loading.value = true;
  try {
    const result = await login({ username: form.username, password: form.password });
    setToken(result.token);
    invalidateUserCache();
    await ensureUser();

    const redirect = route.query.redirect || getDefaultRoute();
    router.replace(redirect);
  } catch (e) {
    error.value = e?.message || "登录失败，请检查用户名和密码";
  } finally {
    loading.value = false;
  }
}

function getDefaultRoute() {
  const role = currentUser.value?.role?.replace(/^ROLE_/i, "").toUpperCase();
  if (role === "ADMIN") return "/admin";
  if (role === "CADRE") return "/cadre";
  if (role === "VILLAGER") return "/village";
  return "/";
}
</script>

<template>
  <div class="login-page">
    <section class="login-hero">
      <RouterLink to="/" class="brand">
        <span class="brand-mark">SV</span>
        <span>智慧乡村</span>
      </RouterLink>

      <div class="hero-copy">
        <p>统一身份入口</p>
        <h1>村务办理、民生工单和系统管理从这里进入</h1>
        <div class="role-strip">
          <span>村民中心</span>
          <span>干部工作台</span>
          <span>管理后台</span>
        </div>
      </div>
    </section>

    <section class="login-panel" aria-label="登录表单">
      <div class="panel-head">
        <p>账号登录</p>
        <h2>进入工作台</h2>
      </div>

      <form class="login-form" @submit.prevent="handleLogin">
        <label class="form-item">
          <span>用户名</span>
          <input
            v-model.trim="form.username"
            type="text"
            class="sv-input login-input"
            placeholder="请输入用户名"
            autocomplete="username"
          />
        </label>

        <label class="form-item">
          <span>密码</span>
          <input
            v-model="form.password"
            type="password"
            class="sv-input login-input"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </label>

        <p v-if="error" class="login-error">{{ error }}</p>

        <button type="submit" class="sv-btn sv-btn--primary login-btn" :disabled="loading">
          {{ loading ? "登录中..." : "登录" }}
        </button>
      </form>
    </section>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(380px, 0.58fr);
  background: var(--forest-950);
}

.login-hero {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
  padding: 36px clamp(28px, 6vw, 78px);
  color: #fff;
  background:
    linear-gradient(140deg, rgba(16, 35, 28, 0.96), rgba(47, 101, 80, 0.86)),
    var(--forest-950);
  overflow: hidden;
}

.login-hero::after {
  content: "";
  position: absolute;
  right: -8vw;
  bottom: -14vw;
  width: min(54vw, 620px);
  aspect-ratio: 1;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 50%;
  box-shadow:
    inset 0 0 0 48px rgba(255, 255, 255, 0.03),
    inset 0 0 0 108px rgba(255, 255, 255, 0.025);
}

.brand {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  font-weight: 820;
}

.brand-mark {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.13);
  font-size: 13px;
  font-weight: 850;
}

.hero-copy {
  position: relative;
  z-index: 1;
  max-width: 760px;
  padding-bottom: 7vh;
}

.hero-copy p {
  margin: 0 0 18px;
  color: rgba(255, 255, 255, 0.68);
  font-weight: 720;
}

.hero-copy h1 {
  margin: 0;
  font-size: clamp(34px, 6vw, 58px);
  line-height: 1.12;
  letter-spacing: 0;
}

.role-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 30px;
}

.role-strip span {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  color: rgba(255, 255, 255, 0.78);
  font-size: 13px;
  font-weight: 700;
}

.login-panel {
  align-self: center;
  width: min(100% - 48px, 430px);
  margin: 0 auto;
  padding: 34px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.24);
}

.panel-head {
  margin-bottom: 28px;
}

.panel-head p {
  margin: 0 0 4px;
  color: var(--text-placeholder);
  font-size: 13px;
  font-weight: 720;
}

.panel-head h2 {
  margin: 0;
  color: var(--text-primary);
  font-size: 26px;
  line-height: 1.25;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.form-item span {
  font-size: 14px;
  font-weight: 720;
  color: var(--text-secondary);
}

.login-input {
  width: 100%;
  height: 44px;
}

.login-error {
  margin: 0;
  font-size: 13px;
  color: var(--danger);
  background: var(--red-100);
  padding: 9px 12px;
  border-radius: var(--radius-control);
}

.login-btn {
  width: 100%;
  min-height: 46px;
  font-size: 15px;
  margin-top: 4px;
}

@media (max-width: 860px) {
  .login-page {
    grid-template-columns: 1fr;
    background: var(--forest-950);
  }

  .login-hero {
    min-height: 360px;
    padding: 26px 24px 40px;
  }

  .hero-copy {
    padding-bottom: 0;
  }

  .login-panel {
    width: min(100% - 32px, 430px);
    margin: -48px auto 40px;
    position: relative;
    z-index: 2;
  }
}

@media (max-width: 480px) {
  .login-panel {
    padding: 24px;
  }
}
</style>
