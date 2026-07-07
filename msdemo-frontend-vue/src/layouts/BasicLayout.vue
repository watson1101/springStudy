<template>
  <div class="basic-layout">
      <a-layout>
          <a-layout-header :style="headerStyle">Header</a-layout-header>


          <a-layout-content :style="contentStyle">
              <router-view></router-view>

          </a-layout-content>



          <a-layout-footer :style="footerStyle">Footer</a-layout-footer>
      </a-layout>
  </div>
</template>

<script setup></script>

<style scoped></style>
