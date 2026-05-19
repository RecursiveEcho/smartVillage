import { getSavedUser, getToken } from "@/shared/auth/token"

export function isAuthenticated() {
  return Boolean(getToken())
}

export function hasRequiredRole(userRole, requiredRoles) {
  if (!requiredRoles || requiredRoles.length === 0) {
    return true
  }

  return requiredRoles.includes(userRole)
}

export function getSavedUserRole() {
  return getSavedUser()?.role || ""
}
