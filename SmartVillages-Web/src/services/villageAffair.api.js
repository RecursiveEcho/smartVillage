import http from "@/shared/api/http";

export function fetchPublicAffairPage(params) {
  return http.get("/public/village-affairs", { params });
}

export function fetchPublicAffairDetail(id) {
  return http.get(`/public/village-affairs/${id}`);
}

export function fetchCadreAffairPage(params) {
  return http.get("/cadre/village-affairs", { params });
}

export function fetchCadreAffairDetail(id) {
  return http.get(`/cadre/village-affairs/${id}`);
}

export function createCadreAffair(payload) {
  return http.post("/cadre/village-affairs", payload);
}

export function updateCadreAffair(id, payload) {
  return http.put(`/cadre/village-affairs/${id}`, payload);
}

export function deleteCadreAffair(id) {
  return http.delete(`/cadre/village-affairs/${id}`);
}

export function auditCadreAffair(id, payload) {
  return http.post(`/cadre/village-affairs/${id}/audit`, payload);
}
