<script setup>
import { onMounted, ref } from "vue";

import { getCurrentUser } from "@/services/auth.api";
import { currentUser } from "@/shared/auth/session";
import { formatDate } from "@/shared/utils/format";

const loading = ref(true);
const user = ref(null);
const error = ref("");

const ROLE_LABEL = { ADMIN: "管理员", CADRE: "村干部", VILLAGER: "村民" };

function roleLabel(r) {
  return ROLE_LABEL[r?.replace(/^ROLE_/i, "").toUpperCase()] ?? r ?? "-";
}

onMounted(async () => {
  try {
    user.value = await getCurrentUser();
    currentUser.value = user.value;
  } catch {
    error.value = "加载用户信息失败";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="sv-page-shell profile-page">
    <div class="sv-page-head">
      <div>
        <p>账户中心</p>
        <h1>个人资料</h1>
      </div>
      <span>这里显示当前登录账号的身份信息，便于确认权限和后续办理记录。</span>
    </div>

    <div v-if="loading" class="sv-loading-inline"><span class="sv-spinner" /> 加载中...</div>

    <div v-else-if="error" class="sv-empty">{{ error }}</div>

    <div v-else class="sv-card profile-card">
      <div class="profile-header">
        <div class="avatar">
          <img v-if="user?.avatar" :src="user.avatar" alt="头像" />
          <span v-else class="avatar-fallback">{{ (user?.username || "U")[0].toUpperCase() }}</span>
        </div>
        <div class="profile-meta">
          <h2>{{ user?.username }}</h2>
          <span class="sv-tag" :class="roleLabel(user?.role) === '管理员' ? 'sv-tag--approved' : roleLabel(user?.role) === '村干部' ? 'sv-tag--processing' : 'sv-tag--draft'">
            {{ roleLabel(user?.role) }}
          </span>
        </div>
      </div>

      <div class="profile-fields">
        <div class="field">
          <span class="field-label">用户 ID</span>
          <span class="field-value">{{ user?.id }}</span>
        </div>
        <div class="field">
          <span class="field-label">用户名</span>
          <span class="field-value">{{ user?.username }}</span>
        </div>
        <div class="field">
          <span class="field-label">角色</span>
          <span class="field-value">{{ roleLabel(user?.role) }}</span>
        </div>
        <div v-if="user?.phone" class="field">
          <span class="field-label">手机号</span>
          <span class="field-value">{{ user.phone }}</span>
        </div>
        <div v-if="user?.createTime" class="field">
          <span class="field-label">注册时间</span>
          <span class="field-value">{{ formatDate(user.createTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 560px;
}

.profile-card {
  padding: 32px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 24px;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--forest-800);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-fallback {
  color: #fff;
  font-size: 28px;
  font-weight: 700;
}

.profile-meta h2 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 700;
}

.profile-fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-color);
}

.field:last-child {
  border-bottom: none;
}

.field-label {
  width: 80px;
  flex-shrink: 0;
  font-size: 13px;
  color: var(--text-placeholder);
  text-align: right;
}

.field-value {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

@media (max-width: 480px) {
  .profile-card { padding: 20px; }
  .profile-header { flex-direction: column; text-align: center; }
  .field { flex-direction: column; align-items: flex-start; gap: 4px; }
  .field-label { text-align: left; width: auto; }
}
</style>
