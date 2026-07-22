import { ref } from "vue";

import { getCurrentUser } from "@/services/auth.api";
import { getToken, removeSavedUser, setSavedUser } from "@/shared/auth/token";

let cacheToken = null;

export const currentUser = ref(null);

export function invalidateUserCache() {
  cacheToken = null;
  currentUser.value = null;
  removeSavedUser();
}

/** 有 token 时请求 /admin/me。 */
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
