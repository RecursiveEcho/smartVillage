<script setup>
import { RouterView } from "vue-router";
import { flashMessage } from "@/shared/ui/flash";
</script>

<template>
  <div>
    <RouterView />
    <Transition name="fade">
      <div v-if="flashMessage" class="sv-toast">{{ flashMessage }}</div>
    </Transition>
  </div>
</template>

<style>
@import "@/styles/theme.css";

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
