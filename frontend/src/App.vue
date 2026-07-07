<template>
  <RouterView />
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style>
:root {
  --esun-green: #1a6c3f;
  --esun-green-dark: #145230;
  --esun-green-light: #e8f3ec;
  --bg: #f4f6f8;
  --card-bg: #ffffff;
  --text: #2c3e50;
  --text-light: #7a8a99;
  --border: #e2e8f0;
  --danger: #e05252;
  --radius: 10px;
  --shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  --shadow-hover: 0 4px 20px rgba(0, 0, 0, 0.1);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  min-height: 100vh;
}

body {
  font-family: -apple-system, "Segoe UI", "Microsoft JhengHei", sans-serif;
  background: var(--bg);
  color: var(--text);
  line-height: 1.6;
}

#app {
  display: block;
}

button {
  font-family: inherit;
}

a { color: inherit; }
</style>