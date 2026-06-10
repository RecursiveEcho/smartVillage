/**
 * 民生工单草稿（仅浏览器 localStorage，不涉及后端）
 */
const NS = "smartvillages.ticketDrafts.v1";
const WIP_KEY = `${NS}.wip`;
const LIST_KEY = `${NS}.list`;
const MAX_DRAFTS = 15;

function safeParse(json, fallback) {
  try {
    return JSON.parse(json);
  } catch {
    return fallback;
  }
}

export function readWip() {
  if (typeof localStorage === "undefined") return null;
  const raw = localStorage.getItem(WIP_KEY);
  if (!raw) return null;
  return safeParse(raw, null);
}

export function writeWip(payload) {
  if (typeof localStorage === "undefined") return;
  if (!payload || isEmptyPayload(payload)) {
    localStorage.removeItem(WIP_KEY);
    return;
  }
  localStorage.setItem(
    WIP_KEY,
    JSON.stringify({ ...payload, updatedAt: Date.now() }),
  );
}

export function clearWip() {
  if (typeof localStorage === "undefined") return;
  localStorage.removeItem(WIP_KEY);
}

function isEmptyPayload(p) {
  return (
    !(p.title && String(p.title).trim()) &&
    !(p.detail && String(p.detail).trim()) &&
    !(p.contactPhone && String(p.contactPhone).trim())
  );
}

export function readDraftList() {
  if (typeof localStorage === "undefined") return [];
  const raw = localStorage.getItem(LIST_KEY);
  const list = safeParse(raw, []);
  return Array.isArray(list) ? list : [];
}

function writeDraftList(list) {
  if (typeof localStorage === "undefined") return;
  localStorage.setItem(LIST_KEY, JSON.stringify(list.slice(0, MAX_DRAFTS)));
}

export function generateDraftId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;
}

/**
 * 存入草稿库：新增一条，或覆盖同 id（编辑已有草稿时）
 */
export function saveDraftEntry({
  id,
  serviceType,
  title,
  detail,
  contactPhone,
}) {
  const list = readDraftList();
  const row = {
    id: id || generateDraftId(),
    serviceType: serviceType || "other",
    title: title?.trim() || "",
    detail: detail?.trim() || "",
    contactPhone: contactPhone?.trim() || "",
    savedAt: Date.now(),
  };
  const idx = list.findIndex((x) => x.id === row.id);
  if (idx >= 0) {
    list[idx] = row;
  } else {
    list.unshift(row);
  }
  list.sort((a, b) => b.savedAt - a.savedAt);
  writeDraftList(list.slice(0, MAX_DRAFTS));
  return row.id;
}

export function deleteDraftEntry(id) {
  const list = readDraftList().filter((x) => x.id !== id);
  writeDraftList(list);
}

export function getDraftEntry(id) {
  return readDraftList().find((x) => x.id === id) ?? null;
}

export function draftCount() {
  return readDraftList().length;
}
