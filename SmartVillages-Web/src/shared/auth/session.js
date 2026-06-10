import { ref } from "vue";

import { getToken, removeSavedUser, setSavedUser } from "@/shared/auth/token";

let cacheToken = null;

export const currentUser = ref(null);

export function invalidateUserCache() {
  cacheToken = null;
  currentUser.value = null;
  removeSavedUser();
}

/** 有 token 时请求 /admin/me（动态 import 避免与 http 拦截器循环依赖） */
export async function ensureUser() {
  const t = getToken();
  if (!t) {
    invalidateUserCache();
    return null;
  }
  if (currentUser.value && cacheToken === t) {
    return currentUser.value;
  }
  try {
    const { getCurrentUser } = await import("@/services/auth.api");
    currentUser.value = await getCurrentUser();
    setSavedUser(currentUser.value);
    cacheToken = t;
    return currentUser.value;
  } catch {
    invalidateUserCache();
    return null;
  }
}

export function isRole(role) {
  return currentUser.value?.role === role;
}
