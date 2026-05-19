import http from "@/shared/api/http"

export function createInteractionMessage(payload) {
  return http.post("/interactions/messages", payload)
}

export function getInteractionMessagePage(params = {}) {
  return http.get("/interactions/messages", { params })
}

export function getMyInteractionMessagePage(params = {}) {
  return http.get("/interactions/messages/my", { params })
}

export function getMyInteractionMessageDetail(id) {
  return http.get(`/interactions/messages/my/${id}`)
}

export function withdrawMyInteractionMessage(id) {
  return http.post(`/interactions/messages/my/${id}/withdraw`)
}

export function getCadreInteractionMessagePage(params = {}) {
  return http.get("/cadre/interactions/messages", { params })
}

export function getCadreInteractionMessageDetail(id) {
  return http.get(`/cadre/interactions/messages/${id}`)
}

export function replyCadreInteractionMessage(id, payload) {
  return http.put(`/cadre/interactions/messages/${id}/replies`, payload)
}

export function markCadreInteractionMessageProcessing(id) {
  return http.post(`/cadre/interactions/messages/${id}/processing`)
}
