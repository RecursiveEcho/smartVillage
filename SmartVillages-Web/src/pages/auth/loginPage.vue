<template>
  <main class="page-container" style="max-width: 420px; margin: 60px auto;">
    <div class="card" style="padding: var(--space-xl);">
      <div style="text-align: center; margin-bottom: var(--space-lg);">
        <div style="font-size: 48px; margin-bottom: var(--space-sm);">🌿</div>
        <h2 style="margin-bottom: var(--space-xs);">智慧乡村</h2>
        <p class="text-secondary text-sm">综合管理系统 · 登录</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model.trim="form.username"
            autocomplete="username"
            placeholder="请输入用户名"
            type="text"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            autocomplete="current-password"
            placeholder="请输入密码"
            type="password"
            class="form-input"
          />
        </div>

        <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

        <button type="submit" class="btn btn-primary btn-lg" style="width:100%; margin-top: var(--space-md);" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>

      <div style="margin-top: var(--space-lg); padding-top: var(--space-md); border-top: 1px solid var(--border-light);">
        <p class="text-xs text-secondary" style="margin-bottom: var(--space-sm);">测试账号（密码均为 123456）</p>
        <div class="text-xs" style="display: grid; grid-template-columns: 1fr 1fr; gap: 4px;">
          <span style="color: var(--text-secondary);">管理员：admin</span>
          <span style="color: var(--text-secondary);">村干部：cadre_wang</span>
          <span style="color: var(--text-secondary);">村民：zhang_san</span>
          <span style="color: var(--text-secondary);">村民：li_si</span>
        </div>
      </div>
    </div>

    <!-- 登录调试面板 -->
    <details style="margin-top: var(--space-lg); opacity: 0.5; font-size: var(--font-size-xs);">
      <summary>调试信息</summary>
      <pre style="margin-top: var(--space-sm); background: #f5f5f5; padding: var(--space-sm); border-radius: var(--radius-sm); overflow-x: auto;">{{ debugText }}</pre>
    </details>
  </main>
</template>

<script setup>
import { computed, reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { login } from "@/services/auth.api"
import { getToken, setToken, setSavedUser } from "@/shared/auth/token"

const router = useRouter()

const form = reactive({ username: "", password: "" })
const loading = ref(false)
const errorMessage = ref("")
const loginResult = ref(null)

const debugText = computed(() => JSON.stringify({ token: getToken() || null, result: loginResult.value }, null, 2))

async function handleLogin() {
  errorMessage.value = ""
  if (!form.username || !form.password) {
    errorMessage.value = "请先输入用户名和密码"
    return
  }
  loading.value = true
  try {
    const result = await login({ username: form.username, password: form.password })
    loginResult.value = result
    setToken(result.token)
    setSavedUser(result)

    const role = result.role?.toLowerCase()
    if (role === "admin") await router.push("/admin")
    else if (role === "cadre") await router.push("/cadre")
    else if (role === "villager") await router.push("/village")
    else await router.push("/")
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || error?.message || "登录失败，请检查网络或账号密码"
  } finally {
    loading.value = false
  }
}
</script>
