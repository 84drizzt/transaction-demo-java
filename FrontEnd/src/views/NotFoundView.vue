<template>
  <div>
    <el-result icon="error" title="Error Tip" sub-title="Please follow the instructions">
      <template #extra>
        <el-button type="primary" @click="goBackHomePage">Back</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
const goBackHomePage = () => {
  // 跳转到首页
  confirm('你确定要返回首页吗？') && (location.href = '/')
}
</script>
