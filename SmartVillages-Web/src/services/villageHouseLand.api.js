import http from "@/shared/api/http";

export function fetchVillageHouseLandPage(params = {}) {
  return http.get("/cadre/village-house-land", { params });
}

export function fetchVillageHouseLandDetail(id) {
  return http.get(`/cadre/village-house-land/${id}`);
}

export function createVillageHouseLand(payload) {
  return http.post("/cadre/village-house-land", payload);
}

export function updateVillageHouseLand(id, payload) {
  return http.put(`/cadre/village-house-land/${id}`, payload);
}

export function deleteVillageHouseLand(id) {
  return http.delete(`/cadre/village-house-land/${id}`);
}
