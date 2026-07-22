const DEFAULT_API_BASE_URL = "/api";

function normalizeBaseUrl(value) {
  if (value === undefined) {
    return DEFAULT_API_BASE_URL;
  }

  const normalizedValue = String(value).trim();
  if (!normalizedValue || normalizedValue === "/") {
    return "";
  }

  return normalizedValue.replace(/\/+$/, "");
}

/** 开发和生产都统一走 /api 前缀，避免页面路由和后端接口路径冲突 */
export const apiBaseUrl = normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL);

export { DEFAULT_API_BASE_URL };
