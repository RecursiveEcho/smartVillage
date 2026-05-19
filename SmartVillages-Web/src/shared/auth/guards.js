import { getSavedUser, getToken } from "@/shared/auth/token"

export function normalizeRole(role) {
  return String(role || "").replace(/^ROLE_/i, "").toUpperCase()
}

export function isAuthenticated() {
  return Boolean(getToken())
}

export function hasRequiredRole(userRole, requiredRoles) {
  if (!requiredRoles || requiredRoles.length === 0) {
    return true
  }

  return requiredRoles.map(normalizeRole).includes(normalizeRole(userRole))
}

export function getSavedUserRole() {
  return normalizeRole(getSavedUser()?.role)
}
