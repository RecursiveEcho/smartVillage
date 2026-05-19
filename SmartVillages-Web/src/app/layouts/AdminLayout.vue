<template>
  <main>
    <header>
      <h1>智慧乡村管理端</h1>
      <nav>
        <RouterLink to="/admin">管理员首页</RouterLink>
        <RouterLink to="/admin/users">用户管理</RouterLink>
        <RouterLink to="/admin/announcements">公告管理</RouterLink>
        <RouterLink to="/admin/media">媒体管理</RouterLink>
        <RouterLink to="/cadre">干部首页</RouterLink>
        <RouterLink to="/cadre/announcements">干部公告</RouterLink>
        <RouterLink to="/cadre/interactions">留言处理</RouterLink>
        <RouterLink to="/">返回公共端</RouterLink>
      </nav>
    </header>

    <RouterView />
  </main>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router"
</script>
