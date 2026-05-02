import http from "@/shared/api/http"

export function login(payload) {
  return http.post("/auth/login", payload)
}

export function logout() {
  return http.delete("/auth/logout")
}

export function getCurrentUser() {
  return http.get("/admin/me")
}
