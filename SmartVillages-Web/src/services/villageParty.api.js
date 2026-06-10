import http from "@/shared/api/http";

export function fetchVillagePartyPage(params = {}) {
  return http.get("/cadre/village-party", { params });
}

export function fetchVillagePartyDetail(id) {
  return http.get(`/cadre/village-party/${id}`);
}

export function createVillageParty(payload) {
  return http.post("/cadre/village-party", payload);
}

export function updateVillageParty(id, payload) {
  return http.put(`/cadre/village-party/${id}`, payload);
}

export function deleteVillageParty(id) {
  return http.delete(`/cadre/village-party/${id}`);
}
