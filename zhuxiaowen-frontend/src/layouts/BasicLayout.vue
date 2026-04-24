<template>
  <div id="basicLayout">
    <div class="layout-shell">
      <div class="header">
        <global-header />
      </div>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<style scoped>
#basicLayout {
  min-height: 100vh;
  padding: 24px 20px 32px;
}

.layout-shell {
  max-width: 1280px;
  margin: 0 auto;
}

.header {
  position: sticky;
  top: 16px;
  z-index: 10;
  margin-bottom: 28px;
}

.content {
  min-height: calc(100vh - 120px);
}

@media (max-width: 768px) {
  #basicLayout {
    padding: 16px 12px 24px;
  }

  .header {
    top: 10px;
    margin-bottom: 20px;
  }
}
</style>
