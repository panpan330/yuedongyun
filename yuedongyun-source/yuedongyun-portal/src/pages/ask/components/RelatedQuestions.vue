<!-- 相关问题 -->
<template>
  <div class="relatedQuestions">
    <div class="but">
      <span class="bt bt-round" @click="() => { $router.push({ path: '/ask', query: { id, title } }); }">
        发布新问题
      </span>
    </div>
    <div class="tit">相关问题</div>
    <div>
      <p>新手做 HIIT 时心率上升太快，应该怎么调整节奏？</p>
      <p>居家训练只有弹力带和瑜伽垫，适合先做哪些塑形动作？</p>
      <p>深蹲时总感觉膝盖压力大，是动作问题还是力量不够？</p>
      <p>跟练一周后恢复慢、腿部酸胀明显，应该如何安排休息日？</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  id: {
    type: String,
    default: "",
  },
  title: {
    type: String,
    default: "",
  },
});
</script>

<style lang="scss" scoped>
.relatedQuestions {
  width: 344px;
  height: fit-content;
  background: #ffffff;
  border-radius: 8px;
  padding: 30px;
  line-height: 24px;
  margin-top: 30px;
  font-size: 14px;

  .but {
    border-bottom: solid 1px var(--color-background1);
    padding: 0 50px 30px 50px;
    margin-bottom: 30px;
  }

  p {
    margin: 20px 0;
  }

  .tit {
    font-weight: 600;
    font-size: 20px;
  }
}
</style>
