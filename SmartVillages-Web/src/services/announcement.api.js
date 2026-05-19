import http from "@/shared/api/http"

export function getAnnouncementPage(params = {}) {
  return http.get("/announcements", { params })
}

export function getHotAnnouncements(params = {}) {
  return http.get("/announcements/hot", { params })
}

export function getAnnouncementDetail(id) {
  return http.get(`/announcements/${id}`)
}

export function createCadreAnnouncement(payload) {
  return http.post("/cadre/announcements", payload)
}

export function getCadreAnnouncementPage(params = {}) {
  return http.get("/cadre/announcements", { params })
}

export function getCadreAnnouncementDetail(id) {
  return http.get(`/cadre/announcements/${id}`)
}

export function updateCadreAnnouncement(id, payload) {
  return http.put(`/cadre/announcements/${id}`, payload)
}

export function updateCadreAnnouncementStatus(id, status) {
  return http.put(`/cadre/announcements/${id}/status`, null, {
    params: { status },
  })
}

export function deleteCadreAnnouncement(id) {
  return http.delete(`/cadre/announcements/${id}`)
}

export function getPendingCadreAnnouncementPage(params = {}) {
  return http.get("/cadre/announcements/pending", { params })
}

export function auditCadreAnnouncement(id, status) {
  return http.put(`/cadre/announcements/${id}/audit`, null, {
    params: { status },
  })
}

export function getAuditedCadreAnnouncementPage(params = {}) {
  return http.get("/cadre/announcements/audited", { params })
}
