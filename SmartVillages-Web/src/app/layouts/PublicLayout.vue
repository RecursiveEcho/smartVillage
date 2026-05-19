<template>
  <main>
    <header>
      <h1>智慧乡村公共端</h1>
      <nav>
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/announcements">公告</RouterLink>
        <RouterLink to="/features">特色服务</RouterLink>
        <RouterLink to="/interactions">村民互动</RouterLink>
        <RouterLink to="/my/interactions">我的留言</RouterLink>
        <RouterLink to="/login">登录</RouterLink>
      </nav>
    </header>

    <RouterView />
  </main>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router"
</script>
