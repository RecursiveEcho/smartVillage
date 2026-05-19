<template>
  <section>
    <h2>村民互动</h2>
    <p>当前路由：{{ route.fullPath }}</p>
    <p>后续这里接入公开留言列表、我的留言和留言详情。</p>
  </section>
</template>

<script setup>
import { useRoute } from "vue-router"

const route = useRoute()
</script>
