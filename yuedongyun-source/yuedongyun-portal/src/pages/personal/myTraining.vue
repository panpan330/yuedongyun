<!-- 个人中心 - 我的训练 -->
<template>
  <div class="myTrainingWrapper">
    <div>
      <div v-if="workoutData != null && typeof workoutData != 'string'" class="personalCards">
        <CardsTitle class="marg-bt-20" title="最近跟练" />
        <TrainingCards :data="workoutData" type="1" />
      </div>

      <div v-if="planData && typeof planData != 'string'" class="personalCards">
        <CardsTitle title="训练计划">
          <div class="ft-wt-400">
            <span class="marg-rt-20">本周训练：<em>{{ weekFinishedAmount || 0 }}</em> / {{ weekPlanAmount || 0 }}</span>
            <span>热力值奖励：<em>{{ totalFitPoint || 0 }}</em></span>
          </div>
        </CardsTitle>
        <PlanTable :data="planData" />
      </div>

      <div v-if="myTrainingData != null && myTrainingData.length > 0" id="allClass">
        <div class="personalCards">
          <CardsTitle class="marg-bt-20" title="全部训练" />
          <div><span></span></div>
          <div v-for="item in myTrainingData" :key="item.trainingId" class="item marg-bt-20">
            <TrainingCards :data="item" type="2" @planHandle="planHandle" />
          </div>
        </div>
        <div v-if="count > 10" class="fx-ct ft-18 ft-wt-600">查看全部</div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="title" width="30%">
      <div class="dialogCont">
        <div class="fx marg-bt-20">
          <span>每周训练次数:</span>
          <el-input v-model="number" min="1" type="number" @input="planDayHandle" />
        </div>
        <div class="fx">
          <span>预计完成时间:</span>
          <div class="lastTime">{{ lastTime }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialogFooter">
          <div @click="dialogVisible = false"><span class="bt bt-grey">取消</span></div>
          <div @click="createPlan"><span class="bt">确定</span></div>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import moment from "moment";
import { dataCacheStore } from "@/store";
import { creatPlans, delMyTraining, getMyWorkout, getMysessions, getMyPlan } from "@/api/training.js";
import CardsTitle from "./components/CardsTitle.vue";
import TrainingCards from "./components/TrainingCards.vue";
import PlanTable from "./components/PlanTable.vue";

const store = dataCacheStore();

const workoutData = ref(null);
const planData = ref([]);
const planTotal = ref(0);
const weekFinishedAmount = ref(0);
const totalFitPoint = ref(0);
const weekPlanAmount = ref(0);
const days = ref(0);
const number = ref(1);
const dialogVisible = ref(false);
const title = ref("创建训练计划");
const currentData = ref();
const myTrainingData = ref(null);
const count = ref(0);

const planParams = {
  page: 1,
  pageSize: 10,
};

const params = {
  page: 1,
  pageSize: 10,
};

onMounted(() => {
  getWorkoutData();
  getMysessionsData();
  getPlanData();
});

const getWorkoutData = async () => {
  await getMyWorkout()
    .then((res) => {
      if (res.code == 200 && res.data != null) {
        workoutData.value = res.data;
        store.setMyLearnClassInfo(res.data);
      }
    })
    .catch(() => {
      ElMessage({
        message: "最近跟练数据请求出错！",
        type: "error",
      });
    });
};

const getPlanData = async () => {
  await getMyPlan(planParams)
    .then((res) => {
      if (res.code == 200 && res.data != null) {
        planData.value = res.data.list;
        planTotal.value = res.data.total;
        weekFinishedAmount.value = res.data.weekFinished;
        totalFitPoint.value = res.data.weekFitPoint;
        weekPlanAmount.value = res.data.weekTotalPlan;
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练计划数据请求出错！",
        type: "error",
      });
    });
};

const lastTime = computed(() => {
  const num = Math.ceil(days.value / number.value) * 7;
  return number.value ? moment().add(num, "days").format("YYYY-MM-DD") : "";
});

const planDayHandle = (val) => {
  if (val !== "" && val < 1) {
    number.value = 1;
    return;
  }
  if (val !== "" && val > 50) {
    number.value = 50;
  }
};

const planHandle = (val) => {
  const { data, type } = val;
  dialogVisible.value = true;
  currentData.value = data;
  if (type == "edit") {
    number.value = data.weekFreq;
    days.value = data.sessions;
    title.value = "调整训练计划";
  } else if (type == "add") {
    days.value = data.sessions;
    title.value = "创建训练计划";
  } else if (type == "del") {
    delMyTrainingData(data.training.id);
  }
};

const createPlan = async () => {
  const params = {
    freq: number.value,
    trainingId: currentData.value ? currentData.value.trainingId : "",
  };
  await creatPlans(params)
    .then((res) => {
      if (res.code == 200) {
        getPlanData();
        ElMessage({
          message: `${title.value}成功`,
          type: "success",
        });
        dialogVisible.value = false;
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练计划数据请求出错！",
        type: "error",
      });
    });
};

const delMyTrainingData = async (id) => {
  await delMyTraining(id)
    .then((res) => {
      if (res.code == 200) {
        getMysessionsData();
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练数据请求出错！",
        type: "error",
      });
    });
};

const getMysessionsData = async () => {
  await getMysessions(params)
    .then((res) => {
      if (res.code == 200 && res.data != null) {
        myTrainingData.value = res.data.list;
        count.value = Number(res.data.total);
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练数据请求出错！",
        type: "error",
      });
    });
};
</script>

<style lang="scss" src="./index.scss"></style>

