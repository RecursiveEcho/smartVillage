<template>
  <div>
    <div class="page-header">
      <h2>📋 民生服务</h2>
      <p>在线申办、进度查询、结果公示</p>
    </div>
    <div class="empty-state" style="padding: var(--space-2xl)">
      <div class="icon">📋</div>
      <h3 style="margin-bottom: var(--space-sm)">民生服务</h3>
      <p class="text-secondary">民生服务模块即将上线，敬请期待</p>
      <RouterLink to="/village" class="btn btn-outline" style="margin-top: var(--space-md)">返回村民中心</RouterLink>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from "vue-router"
</script>
