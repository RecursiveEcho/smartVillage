<template>
  <section>
    <h2>公告管理</h2>
    <p>当前路由：{{ route.fullPath }}</p>
    <p>后续这里接入公告新增、编辑、上下架和审核。</p>
  </section>
</template>

<script setup>
import { useRoute } from "vue-router"

const route = useRoute()
</script>
