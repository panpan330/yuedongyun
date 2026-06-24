<!-- 发布成功页 -->
<template>
  <div class="success container bg-wt fx-fd-col fx-ct">
    <IconSuccess />
    <div class="title">发布成功</div>
    <div class="ft-14 marg-bt-20 ft-cl-des">
      请耐心等待，会有热心的运动伙伴回复你的问题。
    </div>
    <div class="ask bt ft-14" @click="() => $router.go(-2)">查看我的训练</div>
  </div>
</template>

<script setup>
import IconSuccess from "@/assets/icon_success.svg";
</script>

<style lang="scss">
.success {
  margin: 30px auto 40px auto;
  border-radius: 8px;

  .title {
    font-weight: 600;
    font-size: 24px;
    margin: 20px 20px 10px 20px;
  }

  .ask {
    width: 122px;
    height: 36px;
    line-height: 36px;
  }
}
</style>
