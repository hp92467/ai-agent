<template>
  <div class="home">
    <h1>欢迎使用AI应用</h1>
    <p>请选择一个应用：</p>
    <router-link to="/love-app">
      <button>AI 旋转机械故障诊断大师</button>
    </router-link>
    <router-link to="/manus">
      <button>AI 超级智能体</button>
    </router-link>
  </div>
</template>

<script>
export default {
  name: 'Home'
};
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
}
button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}
</style>