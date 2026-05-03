const DEFAULT_API_BASE_URL = "http://localhost:8080"

function normalizeBaseUrl(value) {
  if (!value) {
    return DEFAULT_API_BASE_URL
  }

  return value.replace(/\/+$/, "")
}

export const apiBaseUrl = normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL)

export { DEFAULT_API_BASE_URL }
