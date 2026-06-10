export function formatDate(value) {
  if (value == null || value === "") return "";
  const s = String(value);
  if (s.length >= 10 && s[4] === "-" && s[7] === "-") {
    return s.slice(0, 10);
  }
  const d = new Date(s);
  if (Number.isNaN(d.getTime())) return "";
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export function textExcerpt(raw, max = 140) {
  if (raw == null) return "";
  let t = String(raw).replace(/<[^>]+>/g, " ");
  t = t.replace(/\s+/g, " ").trim();
  if (t.length <= max) return t;
  return `${t.slice(0, max)}…`;
}
