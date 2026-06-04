<template>
  <div>
    <div class="page-header">
      <h2>🖼️ 媒体管理</h2>
      <p>管理图片、视频等媒体资源</p>
    </div>

    <div class="empty-state" style="padding: var(--space-2xl)">
      <div class="icon">🖼️</div>
      <h3 style="margin-bottom: var(--space-sm)">媒体资源库</h3>
      <p class="text-secondary">媒体管理模块即将上线，支持图片上传、分类管理和资源预览</p>
      <div style="margin-top:var(--space-lg);display:flex;justify-content:center;gap:var(--space-md);flex-wrap:wrap">
        <div class="card" style="text-align:center;width:160px;padding:var(--space-lg)">
          <div style="font-size:32px;margin-bottom:var(--space-sm)">📸</div>
          <p class="text-sm text-secondary">图片管理</p>
        </div>
        <div class="card" style="text-align:center;width:160px;padding:var(--space-lg)">
          <div style="font-size:32px;margin-bottom:var(--space-sm)">🎬</div>
          <p class="text-sm text-secondary">视频管理</p>
        </div>
        <div class="card" style="text-align:center;width:160px;padding:var(--space-lg)">
          <div style="font-size:32px;margin-bottom:var(--space-sm)">📁</div>
          <p class="text-sm text-secondary">文件管理</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>
