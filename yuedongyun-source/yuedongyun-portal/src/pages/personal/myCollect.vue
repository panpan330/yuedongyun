<!-- 我的收藏 -->
<template>
  <div class="myCollect bg-wt">
    <div class="content">功能研发中, 敬请期待！！</div>
  </div>
</template>
<script setup>

/** 数据导入 **/
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getTrainingDetails, getTrainingCoaches, getTrainingList } from "@/api/trainingDetails.js";
import { getTrainingWorkout } from "@/api/training.js";
import { useRoute } from "vue-router";
import { dataCacheStore } from "@/store"
import LeftNav from "./components/LeftNav.vue";

// 组件导入
// import LeftNav from "./components/LeftNav.jsx";

const route = useRoute()
const store = dataCacheStore()

// 训练目录
const trainingListData = ref([])
const baseTrainingCoachData = ref([])

// mounted生命周期
onMounted(async () => {
 
});

/** 方法定义 **/

</script>
<style lang="scss" src="./index.scss"> </style>

