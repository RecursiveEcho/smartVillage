const TOKEN_STORAGE_KEY = "smartvillages.token"

export function getToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_STORAGE_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_STORAGE_KEY)
}

export function hasToken() {
  return Boolean(getToken())
}

export { TOKEN_STORAGE_KEY }
