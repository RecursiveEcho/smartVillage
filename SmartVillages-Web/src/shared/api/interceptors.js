import { unwrapResult } from "@/shared/api/result";
import { showFlash } from "@/shared/ui/flash";
import { getToken, removeToken } from "@/shared/auth/token";

let unauthorizedHandler = null;

export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

function attachToken(config) {
  const token = getToken();

  if (!token) {
    return config;
  }

  config.headers = config.headers ?? {};
  /* 规范：Authorization Bearer；后端 JwtSecurityFilter 仍读 header「token」，双写兼容 */
  config.headers.Authorization = `Bearer ${token}`;
  config.headers.token = token;

  return config;
}

function isAuthLoginRequest(config) {
  const url = String(config?.url ?? "");
  return url.includes("/auth/login");
}

function normalizeHttpError(error) {
  const status = error?.response?.status;
  const cfg = error?.config;

  if (status === 401 && !isAuthLoginRequest(cfg)) {
    removeToken();
    unauthorizedHandler?.();
    error.message = "登录状态已失效，请重新登录";
  } else if (status === 403) {
    error.message = "暂无权限访问";
    showFlash("暂无权限访问");
  } else if (status === 401 && isAuthLoginRequest(cfg)) {
    error.message = error?.response?.data?.message || "用户名或密码错误";
  } else if (!error?.response) {
    error.message = "网络异常或服务不可用";
    showFlash("网络异常或服务不可用");
  }

  return Promise.reject(error);
}

export function setupInterceptors(http) {
  http.interceptors.request.use(attachToken, Promise.reject);

  http.interceptors.response.use(
    (response) => unwrapResult(response.data),
    normalizeHttpError,
  );

  return http;
}
