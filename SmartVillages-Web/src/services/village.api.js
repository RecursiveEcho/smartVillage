import http from "@/shared/api/http";

export function createServiceTicket(payload) {
  return http.post("/villager/management/services", payload);
}

export function fetchMyServiceTickets(params) {
  return http.get("/villager/management/services/my", { params });
}

export function fetchMyServiceTicketDetail(id) {
  return http.get(`/villager/management/services/my/${id}`);
}

export function closeMyServiceTicket(id) {
  return http.put(`/villager/management/services/my/${id}/close`);
}

export function fetchCadreServiceTickets(params) {
  return http.get("/cadre/management/services", { params });
}

export function fetchCadreServiceTicketDetail(id) {
  return http.get(`/cadre/management/services/${id}`);
}

export function acceptCadreServiceTicket(id) {
  return http.put(`/cadre/management/services/${id}/processing`);
}

export function completeCadreServiceTicket(id) {
  return http.put(`/cadre/management/services/${id}/done`);
}

export function closeCadreServiceTicket(id) {
  return http.put(`/cadre/management/services/${id}/close`);
}
