<!-- 我的训练考核详情 -->
<template>
  <div class="myExamDetails">
    <BreadCrumb />
    <div class="examHeadle">
      <div class="tit">考核结果</div>
      <div class="table">
        <div class="fx-sb">
          <div class="td fx-1">
            <div class="marg-bt-10 ft-wt-600 ft-cl-1">所属训练</div>
            <div>{{ $route.query.trainingName }}</div>
          </div>
          <div class="td fx-1">
            <div class="marg-bt-10 ft-wt-600 ft-cl-1">考核名称</div>
            <div>{{ $route.query.sessionName }}</div>
          </div>
          <div class="td fx-1">
            <div class="marg-bt-10 ft-wt-600 ft-cl-1">会员昵称</div>
            <div>{{ store.getUserInfo.name }}</div>
          </div>
        </div>
        <div class="fx-sb">
          <div class="td fx-1">
            <div class="marg-bt-10 ft-wt-600 ft-cl-1">用时</div>
            <div>{{ $route.query.duration ? timeFormat($route.query.duration) : "00 : 00 : 00" }}</div>
          </div>
          <div class="td fx-1">
            <div class="marg-bt-10 ft-wt-600 ft-cl-1">提交时间</div>
            <div>{{ $route.query.commitTime }}</div>
          </div>
          <div class="td fx-1">
            <div class="marg-bt-10 ft-wt-600 ft-cl-1">总得分</div>
            <div>{{ $route.query.score || 0 }} / {{ total }}</div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="myExamDetails" class="answerCardTitle">结果概览</div>
    <div class="answerCards">
      <span
        v-for="(item, index) in myExamDetails"
        :key="item.id || index"
        :class="{ right: item.correct, wrong: !item.correct && item.answer != '' }"
      >
        {{ index + 1 }}
      </span>
    </div>

    <div class="examCont">
      <div v-for="(item, index) in myExamDetails" :key="item.id || index" class="item">
        <div class="examTitle">
          <div>
            <img v-if="item.correct" src="@/assets/icon_right.png" alt="" />
            <img v-else src="@/assets/icon_wrong.png" alt="" />
          </div>
          <div class="quest fx">{{ index + 1 }}. <span v-html="item.question.name"></span></div>
        </div>
        <div class="answer">
          <li v-for="it in item.question.options" :key="it"><span v-html="it"></span></li>
        </div>
        <div class="analysis">
          <div class="fx marg-bt-20">
            <div class="col ft-wt-600">你的答案：{{ answerChange(item.question.type, item.answer) }}</div>
            <div class="col rt ft-wt-600">正确答案：{{ answerChange(item.question.type, item.question.answer) }}</div>
            <div class="col">难度：{{ defficultyChange(item.question.difficulty) }}</div>
            <div>得分：{{ item.score }}</div>
          </div>
          <div v-if="item.question.analysis" class="fx">解析：<span v-html="item.question.analysis"></span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import { getExamDetails } from "@/api/training.js";
import { upperAlpha, timeFormat } from "@/utils/tool.js";
import { useUserStore } from "@/store";
import BreadCrumb from "./components/BreadCrumb.vue";

const store = useUserStore();
const route = useRoute();
const total = ref(0);
const myExamDetails = ref();

onMounted(() => {
  getExamDetailsData();
});

const getExamDetailsData = async () => {
  await getExamDetails(route.query.id)
    .then((res) => {
      if (res.code === 200 && res.data != null) {
        myExamDetails.value = res.data;
        res.data.forEach((item) => {
          total.value += item.question.score;
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练记录加载失败！",
        type: "error",
      });
    });
};

const answerChange = (type, val) => {
  let data = "";
  switch (parseInt(type)) {
    case 1:
      data = Number.isNaN(Number(val)) ? val : upperAlpha(Number(val));
      break;
    case 2:
    case 3: {
      const arr = typeof val === "string" ? val.split(",") : val;
      data = (arr || [])
        .map((item) => (Number.isNaN(Number(item)) ? item : upperAlpha(Number(item))))
        .join(",");
      break;
    }
    case 4:
      data = val ? "正确" : "错误";
      break;
    case 5:
      data = val;
      break;
    default:
      data = val;
  }
  return data;
};

const defficultyChange = (item) => {
  return item == 1 ? "基础" : item == 2 ? "进阶" : "高阶";
};
</script>

<style lang="scss" src="./index.scss"></style>

