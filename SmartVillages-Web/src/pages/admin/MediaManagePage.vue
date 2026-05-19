<template>
  <section>
    <h2>媒体管理</h2>
    <p>当前路由：{{ route.fullPath }}</p>
    <p>后续这里接入媒体上传、分页、详情和状态管理。</p>
  </section>
</template>

<script setup>
import { useRoute } from "vue-router"

const route = useRoute()
</script>
