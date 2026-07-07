<script setup></script>

<template>
  <div class="home">
    <a-button type="primary" danger>Primary</a-button>
    <img alt="Vue logo" src="../assets/logo.png" />
    <HelloWorld msg="Welcome to your vue.js + ts app"></HelloWorld>
  </div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
// @ here is alias to /src
import HelloWorld from "@/components/HelloWorld.vue";

export default defineComponent({
  name: "HomeView",
  components: { HelloWorld },
});
</script>
