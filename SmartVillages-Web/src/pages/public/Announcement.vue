<template>
  <section>
    <h2>公告页面</h2>
    <p>当前路由：{{ route.fullPath }}</p>
    <p>后续这里接入公告列表和公告详情。</p>
  </section>
</template>

<script setup>
import { useRoute } from "vue-router"

const route = useRoute()
</script>
