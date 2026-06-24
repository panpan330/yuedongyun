<!-- 首页 -->
<template>
  <div class="mainWrapper">
    <div class="container banner fx" style="overflow: hidden;">
      <div class="categorys bg-wt">
        <!-- 头部分类 -->
        <TrainingCategory :data="trainingCategories"></TrainingCategory>
      </div>
      <!-- 头部幻灯片 -->
      <Swiper :data="imags"></Swiper>
    </div>
    <!-- 兴趣模块  -->
    <div class="container" v-if="interest.size > 0">
      <Interest :data="interest" :key="intKey" @setInterest="setInterest"></Interest>
    </div>
    <!-- 兴趣模块 -->
    <!-- 直播公开课 -->
    <!-- <div class="bg-wt pd-tp-30">
      <OpenTraining
        title="直播公开课"
        class="container bg-wt"
        :data="freeTrainingData"
      ></OpenTraining>
    </div> -->
    <!-- 热门训练 -->
    <div class="pd-tp-30 bg-wt">
      <OpenTraining
        title="热门训练"
        class="container"
        :data="freeTrainingData"
      ></OpenTraining>
    </div>
    <!-- 广告位 -->
    <div class="globalTopBanner" style="display: block;">
      <img src="/src/assets/adv.png" />
    </div>
    <!-- 人气训练 -- start -->
    <div class="pd-tp-30">
      <OpenTraining
      title="人气训练"
        class="container"
        :data="goodTrainingData"
      ></OpenTraining>
    </div>
    <!-- 人气训练 -- end -->
    <!-- 新上架训练 -- start -->
    <div class="pd-tp-30">
      <OpenTraining
          title="新上架训练"
          class="container"
          :data="newTrainingData"
      ></OpenTraining>
    </div>
    <!-- 新上架训练 -- end -->
    <!-- 兴趣选择设置 -- start -->
    <div class="interest">
      <el-dialog v-model="interestDialog" width="80%" :show-close="false">
        <template #header="{ close, titleId }">
          <div class="dialogHead fx-sb">
            <div>
              <span class="titleText marg-rt-10">设置运动兴趣</span>
              <span class="ft-cl-des">打造你的专属健身与互动平台</span>
            </div>
            <div class="butCont fx">
              <span class="bt-grey marg-rt-15" @click="close">下次再选</span>
              <span class="bt" @click="saveInterest">保存</span>
            </div>
          </div>
        </template>
        <CheckInterest :data="trainingCategories" :initValue="interest" @setInterestList="setInterestList"></CheckInterest>
      </el-dialog>
    </div>
    <div class="floatCont fx-fd-col fx-ct">
      <router-link to="/main/voucher">
        <img src="@/assets/coup.png" voucher alt="" />
      </router-link>
      <div class="cont">
        <img src="@/assets/btn_backtop.png" alt="" />
      </div>
    </div>
  </div>
</template>
<script setup>
/** 数据导入 **/

import { onMounted, ref } from "vue";
import { isLogin, dataCacheStore } from "@/store";
import { ElMessage } from "element-plus";
import { getTrainingCategorys, getRecommendTrainingList, setInterests, getInterests } from "@/api/training.js";
import TrainingCategory from "@/components/TrainingCategory.vue";
import CheckInterest from "./components/CheckInterest.vue";
import Interest from "./components/Interest.vue";
import OpenTraining from "./components/OpenTraining.vue";
import Swiper from "./components/Swiper.vue";
import banner1 from "@/assets/banner1.jpg";
import banner2 from "@/assets/banner2.jpg";
import banner3 from "@/assets/banner3.jpg";
const dataCache = dataCacheStore();
// 分类数据
const trainingCategories = ref([]);
// banner幻灯片图片
const imags = [banner1, banner2, banner3];
// 精品公开课的数据
const freeTrainingData = ref([]);
// 兴趣弹窗
const interestDialog = ref(false);
// 精品好课数据
const goodTrainingData = ref([]);
// 精品新课数据
const newTrainingData = ref([]);
// mounted生命周期
onMounted(async () => {
  // 获取三级分类信息
  getTrainingCategoryData();
  // 获取精品公开课
  getFreeTrainingListData();
  getGoodTrainingListData();
  getNewTrainingListData();
  // 获取兴趣列表 （二级分类）
  if (await isLogin()) {
    getInterestData();
  }
});

/** 方法定义 **/

// 获取一、二、三级分类信息
const getTrainingCategoryData = async () => {
  await getTrainingCategorys()
    .then((res) => {
      if (res.code === 200) {
        trainingCategories.value = res.data;
        dataCache.setTrainingCategoryDataes(res.data)
      } else {
        ElMessage({
          message: res.data.msg,
          type: "error",
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "分类请求出错！",
        type: "error",
      });
    });
};
// 打开修改兴趣弹窗
const setInterest = (val) => {
  interestCatch.value = interest.value;
  interestDialog.value = val;
};
// 精品公开课接口 - 公开课取消
const getFreeTrainingListData = async () => {
  await getRecommendTrainingList("free")
    .then((res) => {
      if (res.code === 200) {
        freeTrainingData.value = res.data;
      } else {
        ElMessage({
          message: res.data.msg,
          type: "error",
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "分类请求出错！",
        type: "error",
      });
    });
};
// 精品公开课接口
const getGoodTrainingListData = async () => {
  await getRecommendTrainingList('best')
    .then((res) => {
      if (res.code === 200) {
        goodTrainingData.value = res.data;
      } else {
        ElMessage(res.meg);
      }
    })
    .catch(() => {
      ElMessage("分类请求出错！");
    });
};
// 新课推荐
const getNewTrainingListData = async () => {
  await getRecommendTrainingList('new')
    .then((res) => {
      if (res.code === 200) {
        newTrainingData.value = res.data;
      } else {
        ElMessage(res.meg);
      }
    })
    .catch(() => {
      ElMessage({
        message: "分类请求出错！",
        type: "error",
      });
    });
};
// 保存设置的兴趣变量
const interest = ref(new Set());
const intKey = ref(1);
// 获取兴趣
const getInterestData = async () => {
  await getInterests()
    .then((res) => {
      if (res.code === 200) {
        if (res.data.length === 0) {
          interestDialog.value = true;
        }
        interest.value = new Set(res.data);
      } else {
        console.log(res.msg)
        interestDialog.value(true);
      }
    })
    .catch(() => {
      ElMessage({
        message: "兴趣列表获取失败！",
        type: "error",
      });
    });
};

// 更改兴趣的时候记录
const interestCatch = ref(new Set());
const setInterestList = (list) => {
  interestCatch.value = list;
};

// 保存兴趣
const saveInterest = async () => {
  let str = "";
  for (let val of interestCatch.value) {
    if (typeof val == "string") {
      str == "" ? (str = val) : (str += `,${val}`);
    } else {
      str == "" ? (str = val.id) : (str += `,${val.id}`);
    }
  }
  if (str == "") {
    ElMessage({
      message: "您还没有选择兴趣，请先选择兴趣后再保存！",
      type: "success",
    });
    return;
  }
  await setInterests({ interestedIds: str })
    .then((res) => {
      if (res.code === 200) {
        ElMessage({
          message: "兴趣保存成功！",
          type: "success",
        });
        getInterestData();
        intKey.value++;
        interestDialog.value = false;
      } else {
        ElMessage({
          message: res.data.msg,
          type: "error",
        });
      }
    })
    .catch(() => { });
};
</script>
<style lang="scss" src="./index.scss">
</style>

