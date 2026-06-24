<!-- 推荐训练模块 -->
<template>
    <div class="like bg-wt">
        <div class="title">推荐训练</div>
        <div class="likeCards">
        <TrainingCards class="marg-bt-20" type="like" style="width:100%" v-for="(item, index) in data" :data="item" :key="index"></TrainingCards>
        </div>
    </div>
</template>
<script setup>
import TrainingCards from "@/components/TrainingCards.vue"

// 引入父级传参
defineProps({
  data:{
    type: Object,
    default:{}
  }
})
</script>
<style lang="scss" scoped>
.like{
    padding: 30px;
    border-radius: 8px;
    .title{
        font-weight: 600;
        font-size: 20px;
        margin-bottom: 10px;
    }
    .likeCards{
        .title{
            font-size: 14px;
            font-weight: 400;
            margin-bottom: 20px;
        }
    }
}
</style>
