<!-- 项目入口文件 -->
<template>
  <el-config-provider :size="size" :z-index="zIndex">
    <RouterView />
  </el-config-provider>
</template>

<script setup>
import { ElConfigProvider } from 'element-plus'

const zIndex = 3000
const size = 'default' // 也可以是 'large' | 'default' | 'small'
</script>
