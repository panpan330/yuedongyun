<!-- 训练自测 -->
<template>
  <div class="practiseWrapper">
    <div class="preview">
      <div class="previewTit">自测进度</div>
      <div class="previewList">
        <span
          v-for="(item, index) in assessmentList"
          :key="index"
          :class="{ act: hasAnswer(item.answers) }"
        >
          {{ index + 1 }}
        </span>
      </div>
      <div class="previewSub" @click="submit">
        <span class="bt">提交结果</span>
      </div>
    </div>

    <div class="ExQuestions" v-infinite-scroll="load" style="overflow: auto">
      <div v-for="(item, index) in assessmentList" :key="index" class="questions">
        <div class="title fx" v-html="`<p>${index + 1}. ${assessmentTypeText(item.type)}</p>${item.name}`"></div>

        <div v-if="item.type === 1">
          <el-radio-group v-model="item.answers" class="ml-4">
            <el-radio
              v-for="(it, ind) in item.options"
              :key="it"
              :label="ind + 1"
              size="large"
            >
              <div class="fx"><span>{{ upperAlpha(ind) }}</span><span v-html="it"></span></div>
            </el-radio>
          </el-radio-group>
        </div>

        <div v-if="item.type === 2">
          <el-checkbox-group v-model="item.answers">
            <el-checkbox
              v-for="(it, ind) in item.options"
              :key="it"
              :label="ind + 1"
            >
              <div class="fx"><span>{{ upperAlpha(ind) }}</span><span v-html="it"></span></div>
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <div v-if="item.type === 3">
          <el-checkbox-group v-model="item.answers">
            <el-checkbox
              v-for="(it, ind) in item.options"
              :key="it"
              :label="ind + 1"
            >
              <div class="fx"><span>{{ upperAlpha(ind) }}</span><span v-html="it"></span></div>
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <div v-if="item.type === 4">
          <el-radio-group v-model="item.answers" class="ml-4">
            <el-radio :label="true" size="large">A. 正确</el-radio>
            <el-radio :label="false" size="large">B. 错误</el-radio>
          </el-radio-group>
        </div>

        <div v-if="item.type === 5">
          <el-input
            v-model="item.answers"
            type="textarea"
            class="textArea"
            rows="5"
            maxlength="200"
            placeholder="请输入你的训练理解或动作要点"
            show-word-limit
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAssessment, postAssessment } from "@/api/assessment.js";
import { dataCacheStore } from "@/store";

const store = dataCacheStore();
const currentPlayData = ref({});

defineProps({
  id: {
    type: String,
    default: "",
  },
  examInfo: {
    type: Object,
    default: () => ({}),
  },
});

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
  examInfo: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(["goHandle", "playHadle"]);

const assessmentList = ref([]);
const examId = ref("");
const isSubmit = ref(false);

onMounted(() => {
  currentPlayData.value = JSON.parse(JSON.stringify(store.getCurrentPlayData || {}));
  currentPlayData.value.name = currentPlayData.value.sessionName;
  getAssessmentList();
});

onBeforeUnmount(() => {
  leaveConfirm();
});

const assessmentTypeText = (type) => {
  switch (type) {
    case 1:
      return "(单选题)";
    case 2:
      return "(多选题)";
    case 3:
      return "(不定项选择)";
    case 4:
      return "(判断题)";
    case 5:
      return "(主观题)";
    default:
      return "";
  }
};

const upperAlpha = (num) => {
  const chars = ["A.", "B.", "C.", "D.", "E.", "F.", "G."];
  return chars[num] || "";
};

const hasAnswer = (answer) => {
  if (Array.isArray(answer)) {
    return answer.length > 0;
  }
  return answer !== "" && answer !== undefined && answer !== null;
};

const getAssessmentList = async () => {
  await getAssessment(props.examInfo)
    .then((res) => {
      if (res.code === 200) {
        examId.value = res.data.id;
        assessmentList.value = res.data.questions || [];
      } else {
        ElMessage({
          message: res.msg,
          type: "error",
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练自测加载失败，请稍后重试",
        type: "error",
      });
    });
};

const normalizeAnswer = (answer) => {
  if (Array.isArray(answer)) {
    return answer
      .slice()
      .sort((a, b) => Number(a) - Number(b))
      .join(",");
  }
  if (typeof answer === "boolean") {
    return String(answer);
  }
  return answer ?? "";
};

const submit = () => {
  const answered = assessmentList.value.filter((item) => hasAnswer(item.answers));
  if (answered.length < assessmentList.value.length) {
    ElMessageBox.confirm(
      "当前仍有未完成的题目，是否确认提交本次自测结果？",
      "确认提交",
      {
        confirmButtonText: "确认提交",
        cancelButtonText: "我再看看",
        type: "warning",
      }
    ).then(() => {
      postAssessmentHandle();
    });
    return;
  }
  postAssessmentHandle();
};

const postAssessmentHandle = async () => {
  const examDetails = assessmentList.value.map((item) => ({
    questionId: item.id,
    answer: normalizeAnswer(item.answers),
    questionType: item.type,
  }));

  await postAssessment({ examDetails, id: examId.value })
    .then((res) => {
      if (res.code === 200) {
        assessmentList.value = res.data || assessmentList.value;
        isSubmit.value = true;
        ElMessage({
          message: "自测结果提交成功，请前往个人中心查看",
          type: "success",
        });
        emit("playHadle", { item: currentPlayData.value, tp: "9" });
      } else {
        ElMessage({
          message: res.msg,
          type: "error",
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练自测提交失败，请稍后重试",
        type: "error",
      });
    });
};

function leaveConfirm() {
  if (isSubmit.value) {
    return false;
  }
  const answered = assessmentList.value.filter((item) => hasAnswer(item.answers));
  if (answered.length > 0) {
    ElMessageBox.confirm(
      "你还有未提交的自测结果，是否现在提交？",
      "确认提交",
      {
        confirmButtonText: "确认提交",
        cancelButtonText: "我再看看",
        type: "warning",
      }
    ).then(() => {
      postAssessmentHandle();
    });
  }
  return false;
}

const load = () => {};
</script>

<style lang="scss" scoped>
.practiseWrapper {
  background-color: black;
  color: black;
  height: calc(100vh - 60px);
  padding: 30px;
  display: flex;

  .preview {
    width: 188px;
    height: max-content;
    background: #ffffff;
    margin-right: 20px;
    padding: 15px;

    .previewTit {
      font-size: 14px;
      color: var(--color-font1);
      margin-bottom: 20px;
    }

    .previewList {
      display: flex;
      flex-wrap: wrap;
      justify-content: flex-start;
      margin-bottom: 30px;

      span {
        display: inline-block;
        width: 22px;
        line-height: 22px;
        border: 1px solid #80878c;
        color: #80878c;
        border-radius: 2px;
        margin-right: 10px;
        margin-bottom: 10px;
        text-align: center;
      }

      span:nth-child(5n) {
        margin-right: 0;
      }

      .act {
        color: #fff;
        background: #27ba9b;
        border: 1px solid #27ba9b;
      }
    }

    .previewSub {
      width: 80px;
      height: 28px;
      border-radius: 3px;
      margin: 0 auto;

      .bt {
        font-size: 14px;
        line-height: 28px;
      }
    }
  }

  .ExQuestions {
    background-color: #ffffff;
    flex: 1;
    padding: 30px;

    .textArea {
      width: 100%;
    }

    .title {
      margin: 16px 0;
    }
  }
}
</style>
