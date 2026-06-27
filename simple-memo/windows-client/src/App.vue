<template>
  <div class="app" :class="{ 'is-dark': appStore.darkMode }">
    <router-view />
  </div>
</template>
<script setup lang="ts">
import { useAppStore } from '@/stores/app'
const appStore = useAppStore()
</script>
<style scoped>
.app { height: 100vh; background: var(--bg-primary); }
</style>
