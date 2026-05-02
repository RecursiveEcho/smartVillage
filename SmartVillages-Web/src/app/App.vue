<template>
  <main>
    <section>
      <h1>SmartVillages 登录联调页</h1>
      <form @submit.prevent="handleLogin">
        <label>
          <span>用户名</span>
          <input v-model.trim="form.username" type="text" placeholder="请输入用户名" />
        </label>

        <label>
          <span>密码</span>
          <input v-model="form.password" type="password" placeholder="请输入密码" />
        </label>

        <div>
          <button type="submit" :disabled="loading.login">
            {{ loading.login ? '登录中' : '登录并保存token' }}
          </button>
        </div>
      </form>

      <p v-if="message">{{ message }}</p>
      <p v-if="errorMessage">{{ errorMessage }}</p>

      <section>
        <h2>当前 token</h2>
        <pre>{{ tokenPreview }}</pre>
      </section>

      <section>
        <h2>登录结果</h2>
        <pre>{{ loginResultText }}</pre>
      </section>
    </section>
  </main>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'

import { login } from '@/services/auth.api'
import { getToken, setToken } from '@/shared/auth/token'

const form = reactive({
  username: '',
  password: '',
})

const loading = reactive({
  login: false,
})

const message = ref('')
const errorMessage = ref('')
const loginResult = ref(null)
const tokenValue = ref(getToken())

const tokenPreview = computed(() => tokenValue.value || '当前还没有 token')
const loginResultText = computed(() => formatJson(loginResult.value))

function formatJson(value) {
  if (!value) {
    return '暂无数据'
  }

  return JSON.stringify(value, null, 2)
}

function clearFeedback() {
  message.value = ''
  errorMessage.value = ''
}

function syncToken() {
  tokenValue.value = getToken()
}

function getErrorMessage(error) {
  return error?.message || '发生了未知错误'
}

async function handleLogin() {
  clearFeedback()

  if (!form.username || !form.password) {
    errorMessage.value = '请先输入用户名和密码'
    return
  }

  loading.login = true

  try {
    const result = await login({
      username: form.username,
      password: form.password,
    })

    loginResult.value = result
    setToken(result.token)
    syncToken()
    message.value = '登录成功'
  } catch (error) {
    loginResult.value = null
    errorMessage.value = getErrorMessage(error)
  } finally {
    loading.login = false
  }
}
</script>
