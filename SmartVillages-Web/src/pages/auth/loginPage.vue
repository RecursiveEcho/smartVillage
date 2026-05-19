<template>
  <main>
    <section>
      <h1>SmartVillages 登录</h1>
      <p>用于验证登录、token 保存、自动请求头和当前用户接口。</p>

      <form @submit.prevent="handleLogin">
        <div>
          <label for="username">用户名</label>
          <input
            id="username"
            v-model.trim="form.username"
            autocomplete="username"
            placeholder="请输入用户名"
            type="text"
          />
        </div>

        <div>
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            autocomplete="current-password"
            placeholder="请输入密码"
            type="password"
          />
        </div>

        <div>
          <button type="submit" :disabled="loading.login">
            {{ loading.login ? "登录中" : "登录" }}
          </button>
          <button type="button" :disabled="loading.currentUser" @click="handleGetCurrentUser">
            {{ loading.currentUser ? "获取中" : "获取当前用户" }}
          </button>
          <button type="button" @click="handleEnterSystem">进入系统</button>
          <button type="button" @click="handleClearLocalState">清空本地状态</button>
        </div>
      </form>
    </section>

    <section aria-live="polite">
      <h2>操作状态</h2>
      <p v-if="message">{{ message }}</p>
      <p v-if="errorMessage">{{ errorMessage }}</p>
      <p v-if="!message && !errorMessage">暂无操作结果</p>
    </section>

    <section>
      <h2>联调预览</h2>

      <article>
        <h3>当前 token</h3>
        <pre>{{ tokenPreview }}</pre>
      </article>

      <article>
        <h3>登录结果</h3>
        <pre>{{ loginResultText }}</pre>
      </article>

      <article>
        <h3>当前用户</h3>
        <pre>{{ currentUserText }}</pre>
      </article>
    </section>
  </main>
</template>

<script setup>
import { computed, reactive, ref } from "vue"

import { useRoute, useRouter } from "vue-router"

import { getCurrentUser, login } from "@/services/auth.api"
import { normalizeRole } from "@/shared/auth/guards"
import { getToken, removeSavedUser, removeToken, setSavedUser, setToken } from "@/shared/auth/token"

const route = useRoute()
const router = useRouter()

const form = reactive({
  username: "",
  password: "",
})

const loading = reactive({
  login: false,
  currentUser: false,
})

const message = ref("")
const errorMessage = ref("")
const loginResult = ref(null)
const currentUser = ref(null)
const tokenValue = ref(getToken())

const tokenPreview = computed(() => tokenValue.value || "当前还没有 token")
const loginResultText = computed(() => formatJson(loginResult.value))
const currentUserText = computed(() => formatJson(currentUser.value))

function formatJson(value) {
  if (!value) {
    return "暂无数据"
  }

  return JSON.stringify(value, null, 2)
}

function clearFeedback() {
  message.value = ""
  errorMessage.value = ""
}

function syncToken() {
  tokenValue.value = getToken()
}

function getErrorMessage(error) {
  return error?.message || "发生了未知错误"
}

async function handleLogin() {
  clearFeedback()

  if (!form.username || !form.password) {
    errorMessage.value = "请先输入用户名和密码"
    return
  }

  loading.login = true

  try {
    const result = await login({
      username: form.username,
      password: form.password,
    })

    loginResult.value = result
    currentUser.value = null
    setToken(result.token)
    setSavedUser(result)
    syncToken()
    message.value = "登录成功，token 已保存"
    await router.push(getTargetRoute(result.role))
  } catch (error) {
    loginResult.value = null
    errorMessage.value = getErrorMessage(error)
  } finally {
    loading.login = false
  }
}

async function handleGetCurrentUser() {
  clearFeedback()

  if (!getToken()) {
    errorMessage.value = "请先登录并保存 token"
    return
  }

  loading.currentUser = true

  try {
    currentUser.value = await getCurrentUser()
    setSavedUser(currentUser.value)
    message.value = "当前用户获取成功，请检查 Network 面板中的 token 请求头"
  } catch (error) {
    currentUser.value = null
    errorMessage.value = getErrorMessage(error)
  } finally {
    loading.currentUser = false
    syncToken()
  }
}

function handleClearLocalState() {
  removeToken()
  removeSavedUser()
  syncToken()
  loginResult.value = null
  currentUser.value = null
  clearFeedback()
  message.value = "本地 token 和页面预览数据已清空"
}

async function handleEnterSystem() {
  const redirect = typeof route.query.redirect === "string" ? route.query.redirect : getTargetRoute(loginResult.value?.role)
  await router.push(redirect)
}

function getTargetRoute(role) {
  const normalizedRole = normalizeRole(role)

  if (normalizedRole === "ADMIN") {
    return "/admin"
  }

  if (normalizedRole === "CADRE") {
    return "/cadre"
  }

  return "/"
}
</script>
