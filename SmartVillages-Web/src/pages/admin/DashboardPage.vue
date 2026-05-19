<template>
  <section>
    <h2>管理首页</h2>
    <p>当前路由：{{ route.fullPath }}</p>
    <p>后续这里接入后台统计、快捷入口和待办事项。</p>
  </section>
</template>

<script setup>
import { useRoute } from "vue-router"

const route = useRoute()
</script>
