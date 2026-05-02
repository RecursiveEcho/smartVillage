import { unwrapResult } from "@/shared/api/result"
import { getToken, removeToken } from "@/shared/auth/token"

function attachToken(config) {
  const token = getToken()

  if (!token) {
    return config
  }

  config.headers = config.headers ?? {}
  config.headers.token = token

  return config
}

function normalizeHttpError(error) {
  const status = error?.response?.status

  if (status === 401) {
    removeToken()
    error.message = "登录状态已失效，请重新登录"
  } else if (status === 403) {
    error.message = "您没有权限执行此操作"
  } else if (!error?.response) {
    error.message = "网络异常或服务不可用"
  }

  return Promise.reject(error)
}

export function setupInterceptors(http) {
  http.interceptors.request.use(attachToken, Promise.reject)

  http.interceptors.response.use(
    (response) => unwrapResult(response.data),
    normalizeHttpError,
  )

  return http
}
