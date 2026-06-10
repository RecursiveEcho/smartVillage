import http from "@/shared/api/http";

export function fetchFeaturePage(params) {
  return http.get("/features", { params });
}

export function fetchFeatureDetail(id) {
  return http.get(`/features/${id}`);
}

export function fetchCadreFeaturePage(params) {
  return http.get("/cadre/features", { params });
}

export function fetchCadreFeatureDetail(id) {
  return http.get(`/cadre/features/${id}`);
}

export function updateCadreFeature(id, payload) {
  return http.put(`/cadre/features/${id}`, payload);
}
