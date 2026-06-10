import http from "@/shared/api/http";

export function fetchVillagePopulationPage(params = {}) {
  return http.get("/cadre/village-population", { params });
}

export function fetchVillagePopulationDetail(id) {
  return http.get(`/cadre/village-population/${id}`);
}

export function createVillagePopulation(payload) {
  return http.post("/cadre/village-population", payload);
}

export function updateVillagePopulation(id, payload) {
  return http.put(`/cadre/village-population/${id}`, payload);
}

export function deleteVillagePopulation(id) {
  return http.delete(`/cadre/village-population/${id}`);
}
