const DEFAULT_API_BASE_URL = "http://127.0.0.1:8090";

function normalizeBaseUrl(value) {
  if (!value) {
    return DEFAULT_API_BASE_URL;
  }
  return value.replace(/\/+$/, "");
}

/** 开发环境走 Vite 代理（同源），避免用局域网 IP 打开页面时跨域失败 */
export const apiBaseUrl = import.meta.env.DEV
  ? ""
  : normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL);

export { DEFAULT_API_BASE_URL };
