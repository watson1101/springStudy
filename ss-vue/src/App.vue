<!-- <script setup lang="ts">
// import HelloWorld from './components/HelloWorld.vue'
// import TheWelcome from './components/TheWelcome.vue'
</script> -->

<template>
  <!-- <header>
    <img alt="Vue logo" class="logo" src="./assets/logo.svg" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="You did it!" />
    </div>
  </header>

  <main>
    <TheWelcome />
  </main> -->

  <div>
    姓名：<input type="text" v-model="userName" /><br>
    薪水：<input type="number" v-model="salary" /><br>
    岗位：<select name="" id="" v-model="job">
      <option value="开发">开发</option>
      <option value="测试">测试</option>
      <option value="运维">运维</option>
    </select><br>
  <button @click="handleClick">提交</button>
</div>

</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  data() {
    return {
      userName: 'zhangsan',
      salary: 10000,
      job: '开发',
    }
  },
  methods: {
    handleClick() {
      this.salary += 1000;
      console.log(this.userName, this.salary, this.job)
    },
  },
})
</script>

<style scoped>

</style>
