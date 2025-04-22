<template>
  <div>
    <!-- 头部 start -->
    <div style="height: 60px; background-color: blue; display: flex; align-items: center">
      <div style="width: 360px; display: flex; padding-left: 10px; align-items: center; font-size: 20px; color: white">
        Transaction Management System
      </div>
      <div style="flex: 1; display: flex; justify-content: end"></div>
      <div style="width: fit-content; display: flex; align-items: center; padding-right: 10px">
        <el-icon size="40px" color="white"><User /></el-icon>

      </div>
    </div>
    <!-- 头部 end -->

    <!-- 下部 start -->
    <div style="display: flex">
      <!-- 左侧菜单 start -->
      
      <!-- 左侧菜单 end -->

      <!-- 右侧内容 start -->
      <div style="flex: 1; width: 0; background-color: #f8f8ff">
        <RouterView />
      </div>
      <!-- 右侧内容 end -->
    </div>
    <!-- 下部 end -->
  </div>
</template>

<script setup>
import router from '@/router/index.js'
</script>

<style scoped>
.el-menu .is-active {
  background-color: #e6ecf7;
}
</style>
