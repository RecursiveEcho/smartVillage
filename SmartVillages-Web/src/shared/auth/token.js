const TOKEN_STORAGE_KEY = "smartvillages.token"
const USER_STORAGE_KEY = "smartvillages.user"

export function getToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_STORAGE_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_STORAGE_KEY)
}

export function getSavedUser() {
  const rawUser = localStorage.getItem(USER_STORAGE_KEY)

  if (!rawUser) {
    return null
  }

  try {
    return JSON.parse(rawUser)
  } catch {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

export function setSavedUser(user) {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
}

export function removeSavedUser() {
  localStorage.removeItem(USER_STORAGE_KEY)
}

export function hasToken() {
  return Boolean(getToken())
}

export { TOKEN_STORAGE_KEY, USER_STORAGE_KEY }
