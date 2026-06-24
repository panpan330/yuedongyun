<!-- 训练列表 -->
<template>
  <div class="mainWrapper">
    <div class="container banner fx">
      <div class="categorys bg-wt">
        <TrainingCategory :data="trainingCategories"></TrainingCategory>
      </div>
      <Swiper :data="images"></Swiper>
    </div>

    <div class="bg-wt pd-tp-30">
      <OpenTraining
        title="热门训练"
        class="container bg-wt"
        :data="hotClassData"
      ></OpenTraining>
    </div>

    <div class="pd-tp-30">
      <OpenTraining
      title="新上训练"
        class="container"
        :data="newTrainingData"
      ></OpenTraining>
    </div>

    <div class="globalTopBanner" style="display: block;">
      <img src="@/assets/adv.png" />
    </div>

    <div class="bg-wt pd-tp-30">
      <OpenTraining
        title="人气推荐"
        class="container bg-wt"
        :data="popularClassData"
      ></OpenTraining>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getTrainingCategorys, getFreeTrainingList } from "@/api/training.js";
import TrainingCategory from "@/components/TrainingCategory.vue";
import OpenTraining from "./components/OpenTraining.vue";
import Swiper from "./components/Swiper.vue";
import banner1 from "@/assets/banner1.jpg";
import banner2 from "@/assets/banner2.jpg";
import banner3 from "@/assets/banner3.jpg";

const images = [banner1, banner2, banner3];
const trainingCategories = ref([]);
const classData = ref([]);

const pickTrainings = (start, size = 4) => {
  const list = classData.value || [];
  if (list.length <= size) {
    return list;
  }
  return Array.from({ length: size }, (_, index) => list[(start + index) % list.length]).filter(Boolean);
};

const hotClassData = computed(() => pickTrainings(0));
const newTrainingData = computed(() => pickTrainings(2));
const popularClassData = computed(() => pickTrainings(1));

onMounted(() => {
  getTrainingCategoryData();
  getTrainingListData();
});

const getTrainingCategoryData = async () => {
  try {
    const res = await getTrainingCategorys();
    if (res.code === 200) {
      trainingCategories.value = res.data || [];
      return;
    }
    ElMessage.error(res.msg || res.meg || "训练分类加载失败");
  } catch (error) {
    ElMessage.error("训练分类请求出错，请稍后重试");
  }
};

const getTrainingListData = async () => {
  try {
    const res = await getFreeTrainingList();
    if (res.code === 200) {
      classData.value = res.data || [];
      return;
    }
      ElMessage.error(res.msg || res.meg || "训练加载失败");
  } catch (error) {
    ElMessage.error("训练请求出错，请稍后重试");
  }
};
</script>

<style lang="scss" scoped>
.mainWrapper {
  .banner {
    padding: 20px 0;

    .categorys {
      position: relative;
      width: 236px;
      height: 388px;
      border-radius: 8px;
      z-index: 9;
    }
  }

  .globalTopBanner {
    width: 100%;
    min-width: 1152px;
    max-width: 2560px;
    height: 72px;
    overflow: hidden;
    cursor: pointer;
    position: relative;

    img {
      height: 100%;
      display: block;
      position: absolute;
      top: 0;
      left: 50%;
      transform: translateX(-50%);
    }
  }
}
</style>

