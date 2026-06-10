/**
 * 后端 JWT /admin/me 的 role 多为库中原始值：admin、cadre、villager（小写）。
 * Spring Security 侧为 ROLE_*。前端统一归一化后再判断。
 */
export function normalizeRole(role) {
  if (role == null || role === "") return "";
  let r = String(role).trim();
  if (r.length >= 5 && r.slice(0, 5).toUpperCase() === "ROLE_") {
    r = r.slice(5);
  }
  return `ROLE_${r.toUpperCase()}`;
}

export function isVillager(role) {
  return normalizeRole(role) === "ROLE_VILLAGER";
}

export function isAdmin(role) {
  return normalizeRole(role) === "ROLE_ADMIN";
}

export function isCadre(role) {
  return normalizeRole(role) === "ROLE_CADRE";
}

/** meta.roles 为 ['ROLE_ADMIN'] 等形式 */
export function userMatchesRoles(userRole, roles) {
  if (!roles?.length) return true;
  const n = normalizeRole(userRole);
  return roles.some((r) => normalizeRole(r) === n);
}
