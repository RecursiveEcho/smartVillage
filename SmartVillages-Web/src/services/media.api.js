import http from "@/shared/api/http";

export function fetchMediaPage(params) {
  return http.get("/media/page", { params });
}

/** multipart：字段 file、fileType（image|video|document）、category；勿手写 Content-Type，以便带上 boundary */
export function uploadMedia(formData) {
  return http.post("/media/upload", formData, { timeout: 120000 });
}

export function deleteMedia(id) {
  return http.delete(`/media/cadre/${id}`);
}

export function updateMediaStatus(id, status) {
  return http.put(`/media/cadre/${id}/status`, null, { params: { status } });
}

export function auditMedia(id, status) {
  return http.put(`/media/cadre/${id}/audit`, null, { params: { status } });
}

export function fetchPendingMediaPage(params) {
  return http.get("/media/cadre/pending", { params });
}

export function fetchAuditedMediaPage(params) {
  return http.get("/media/cadre/audited", { params });
}
