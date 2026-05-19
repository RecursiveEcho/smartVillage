<template>
  <section>
    <h2>管理页面</h2>
    <p>当前路由：{{ route.fullPath }}</p>
    <p>管理员路由下用于用户管理，干部路由下用于留言或村务管理。</p>
  </section>
</template>

<script setup>
import { useRoute } from "vue-router"

const route = useRoute()
</script>
