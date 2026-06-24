<!-- 发布问题 -->
<template>
  <div class="askWrapper">
    <div class="container">
      <div class="askCont fx-sb">
        <div class="formCont bg-wt marg-rt-20 fx-1">
          <div class="title">发布问题</div>
          <el-form
            ref="ruleFormRef"
            :model="ruleForm"
            :rules="rules"
            status-icon
            label-width="80px"
            class="demo-ruleForm"
            label-position="left"
          >
            <el-form-item label="所属训练:">
              <span>{{ trainingInfo.title }}</span>
            </el-form-item>
            <el-form-item label="训练小节:" prop="pass">
              <div>
                <el-cascader v-model="value" :options="options" :props="props" @change="handleChange" />
                <div class="desc">
                  选择整套训练时，问题将归属于整个训练；选择具体小节时，问题将归属于对应训练小节。
                </div>
              </div>
            </el-form-item>
            <el-form-item label="问题标题:" prop="title">
              <el-input
                v-model="ruleForm.title"
                maxlength="64"
                placeholder="请输入训练问题标题"
                show-word-limit
                @input="ruleshandle"
              />
            </el-form-item>
            <el-form-item label="问题描述:" prop="description">
              <el-input
                v-model="ruleForm.description"
                rows="11"
                type="textarea"
                maxlength="500"
                placeholder="补充训练背景、动作感受或当前卡点"
                show-word-limit
                @input="ruleshandle"
              />
            </el-form-item>
            <el-form-item>
              <div class="fx-sb fx-al-ct">
                <div>
                  <el-checkbox v-model="ruleForm.anonymity" label="匿名提问" size="large" />
                </div>
                <div class="subCont">
                  <span class="bt ft-14" :class="{ 'bt-dis': !isSend }" @click="submitForm(ruleFormRef)">
                    发布问题
                  </span>
                </div>
              </div>
            </el-form-item>
          </el-form>
        </div>
        <AskCareful />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import AskCareful from "./components/AskCareful.vue";
import {
  getTrainingOutlines,
  getQuestionsDetails,
  postQuestions,
  putQuestions,
} from "@/api/trainingDetails.js";

const route = useRoute();
const router = useRouter();
const trainingInfo = route.query;

const type = ref("add");
const ruleFormRef = ref();
const value = ref([]);
const options = ref([]);
const isSend = ref(false);

const props = {
  expandTrigger: "hover",
};

const ruleForm = reactive({
  trainingId: "",
  phaseId: "",
  sessionId: "",
  title: "",
  anonymity: false,
  description: "",
});

const rules = reactive({});

onMounted(() => {
  getTrainingOutlinesDataes();
  ruleForm.trainingId = trainingInfo.id;
  if (trainingInfo.type === "edit") {
    type.value = "edit";
    getQuestionsDetailsData();
  }
});

const getTrainingOutlinesDataes = async () => {
  await getTrainingOutlines(trainingInfo.id)
    .then((res) => {
      if (res.code === 200) {
        options.value = (res.data || []).map((phase) => ({
          value: phase.id,
          label: phase.name,
          children: (phase.sessions || []).map((session) => ({
            value: session.id,
            label: session.name,
          })),
        }));
        return;
      }
      ElMessage({
        message: res.msg || "训练小节加载失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "训练小节数据请求出错！",
        type: "error",
      });
    });
};

const getQuestionsDetailsData = async () => {
  await getQuestionsDetails(trainingInfo.queryId)
    .then((res) => {
      if (res.code === 200) {
        const { data } = res;
        ruleForm.trainingId = data.trainingId;
        ruleForm.phaseId = data.phaseId;
        ruleForm.sessionId = data.sessionId;
        ruleForm.title = data.title;
        ruleForm.anonymity = data.anonymity;
        ruleForm.description = data.description;
        ruleForm.id = data.id;
        value.value = [data.phaseId, data.sessionId];
        ruleshandle();
        return;
      }
      ElMessage({
        message: res.msg || "问题详情加载失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "训练问题数据请求出错！",
        type: "error",
      });
    });
};

const ruleshandle = () => {
  const ruleData = { ...ruleForm };
  let valid = true;
  for (const key in ruleData) {
    if (ruleData[key] === "" && key !== "anonymity" && key !== "id") {
      valid = false;
    }
  }
  isSend.value = valid;
};

const handleChange = (val) => {
  if (type.value === "edit") {
    ruleForm.id = route.query.queryId;
  }
  ruleForm.phaseId = val[0];
  ruleForm.sessionId = val[1];
  ruleshandle();
};

const submitForm = (formEl) => {
  if (!isSend.value) {
    return;
  }
  formEl.validate(async (valid) => {
    if (!valid) {
      return false;
    }

    const submitFn = type.value === "edit" ? putQuestions : postQuestions;
    await submitFn(ruleForm)
      .then((res) => {
        if (res.code === 200) {
          router.push({ path: "/result/success", query: "查看我的问题" });
          return;
        }
        ElMessage({
          message: res.msg || "问题提交失败",
          type: "error",
        });
      })
      .catch(() => {
        ElMessage({
          message: type.value === "edit" ? "问题修改出错！" : "问题发布出错！",
          type: "error",
        });
      });
  });
};
</script>

<style lang="scss" src="./index.scss"></style>

