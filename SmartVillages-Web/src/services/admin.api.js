import http from "@/shared/api/http"

export function getAdminCurrentUser() {
  return http.get("/admin/me")
}

export function getAdminUserPage(params = {}) {
  return http.get("/admin/users", { params })
}

export function getAdminUserDetail(id) {
  return http.get(`/admin/users/${id}`)
}

export function updateAdminUserStatus(id, status) {
  return http.put(`/admin/users/${id}/status`, null, {
    params: { status },
  })
}

export function createCadreUser(payload) {
  return http.post("/admin/users/cadre", payload)
}

export function uploadCadreAvatar(avatar) {
  const formData = new FormData()
  formData.append("avatar", avatar)

  return http.post("/admin/users/cadre/avatar", formData)
}

export function deleteAdminUser(id) {
  return http.delete(`/admin/users/${id}`)
}
