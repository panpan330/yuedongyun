<!-- 训练互动模块 -->
<template>
  <div class="questionWrapper marg-bt-20">
    <div class="askCont">
      <div v-for="item in askListsDataes" :key="item.id" class="askLists">
        <div class="userInfo fx">
          <img :src="item.userIcon" alt="" @error="onerrorImg(item)" />
          {{ item.userName || "匿名用户" }}
        </div>
        <div class="ask">
          <div class="tit ft-14">{{ item.title }}</div>
          <div
            v-if="item.latestAnswer && item.latestAnswer.content"
            class="font-bt2"
            @click="goDetails(item)"
          >
            最新回复：{{ item.latestAnswer.replier.name }}
          </div>
        </div>
        <div class="time fx-sb">
          <div>{{ item.createTime }}</div>
          <div class="actBut">
            <span class="marg-rt-10" @click="goDetails(item)">回复 {{ item.answerTimes }}</span>
          </div>
        </div>
      </div>
      <div v-if="askListsDataes && askListsDataes.length <= 0" class="noData">
        <Empty :type="true" />
      </div>
    </div>

    <div class="questCont">
      <el-input
        v-model="quest.title"
        class="title"
        maxlength="64"
        placeholder="输入训练问题标题"
        show-word-limit
        @input="ruleshandle"
      />
      <el-input
        v-model="quest.description"
        rows="4"
        resize="none"
        type="textarea"
        maxlength="500"
        placeholder="描述动作、节奏或训练中遇到的问题"
        show-word-limit
        @input="ruleshandle"
      />
      <div class="fx-sb fx-al-ct" style="margin-top: 4px">
        <div>
          <el-checkbox v-model="quest.anonymity" label="匿名发布问题" size="large" />
        </div>
        <div class="subCont">
          <span class="bt ft-14" :class="{ 'bt-dis': !isSend }" @click="submitForm()">发布问题</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import defaultImage from "@/assets/icon.jpeg";
import Empty from "@/components/Empty.vue";
import { getAskList, postQuestions } from "@/api/trainingDetails.js";
import { dataCacheStore, isLogin, useUserStore } from "@/store";

const router = useRouter();
const userStore = useUserStore();
const dataCache = dataCacheStore();
const currentPlayData = dataCache.getCurrentPlayData || {};

const userInfo = ref(null);
const isSend = ref(false);
const askListsDataes = ref([]);
const total = ref(0);

const params = ref({
  isAsc: true,
  pageNo: 1,
  pageSize: 1000,
  sessionId: currentPlayData.sessionId,
  sortBy: "",
  onlyMine: false,
});

const quest = reactive({
  sessionId: currentPlayData.sessionId,
  trainingId: currentPlayData.trainingId,
  phaseId: currentPlayData.phaseId,
  title: "",
  anonymity: false,
  description: "",
});

onMounted(() => {
  if (isLogin()) {
    userInfo.value = userStore.getUserInfo;
  }
  getAskListsDataes();
});

const onerrorImg = (item) => {
  item.userIcon = defaultImage;
};

const getAskListsDataes = async () => {
  await getAskList(params.value)
    .then((res) => {
      if (res.code === 200) {
        askListsDataes.value = res.data.list || [];
        total.value = Number(res.data.total || 0);
      }
    })
    .catch(() => {
      ElMessage({
        message: "互动问答加载失败，请稍后重试",
        type: "error",
      });
    });
};

const goDetails = (item) => {
  dataCache.setAskDetails(item);
  router.push({ path: "/askDetails", query: { id: item.id } });
};

const submitForm = async () => {
  if (!isLogin()) {
    ElMessage({
      message: "请先登录再发布问题",
      type: "warning",
    });
    return;
  }

  if (!isSend.value) {
    ElMessage({
      message: "请输入标题和问题描述",
      type: "error",
    });
    return;
  }

  await postQuestions(quest)
    .then((res) => {
      if (res.code === 200) {
        quest.title = "";
        quest.description = "";
        isSend.value = false;
        getAskListsDataes();
        ElMessage({
          message: "训练问题发布成功",
          type: "success",
        });
      } else {
        ElMessage({
          message: res.msg || "问题发布失败",
          type: "error",
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练问题发布失败，请稍后重试",
        type: "error",
      });
    });
};

const ruleshandle = () => {
  isSend.value = quest.title.trim() !== "" && quest.description.trim() !== "";
};
</script>

<style lang="scss" scoped>
.questionWrapper {
  height: calc(100vh - 415px);
  margin-top: 15px;

  .askCont {
    .askLists {
      line-height: 40px;
      font-size: 14px;

      .userInfo {
        line-height: 20px;
        font-size: 12px;
        color: var(--color-font3);

        img {
          width: 20px;
          height: 20px;
          border-radius: 26px;
          margin-right: 10px;
        }
      }

      .ask {
        color: #a0a9b2;

        .tit {
          line-height: 24px;
          margin-top: 6px;
        }
      }

      .time {
        color: var(--color-font3);
        padding-bottom: 10px;
        margin-bottom: 19px;
        border-bottom: 1px solid #000000;
        line-height: 20px;

        .actBut {
          color: #a0a9b2;
          cursor: pointer;

          i {
            display: inline-block;
            position: relative;
            width: 20px;
            height: 20px;
          }

          .iconfont {
            position: relative;
            color: #a0a9b2;
            font-size: 20px;
            top: 2px;
          }

          .zhy-a-btn_zan_sel2x {
            color: var(--color-main);
            font-size: 18px;
            top: 0;
          }

          .btnIcon {
            color: #a0a9b2;
            width: 21px;
            height: 21px;
            position: relative;
            top: 5px;
          }
        }
      }
    }

    .noData {
      height: calc(100vh - 488px);
    }
  }

  .questCont {
    position: absolute;
    width: 100%;
    background-color: #292f37;
    bottom: 0;
    left: 0;
    padding: 15px;

    .title {
      margin-bottom: 10px;
    }

    .subCont {
      .bt {
        width: 80px;
        height: 28px;
        line-height: 28px;
      }
    }

    input::-webkit-input-placeholder {
      color: #000000;
    }

    :deep(.el-input__inner) {
      color: #fff;
    }

    :deep(.el-textarea__inner) {
      color: #fff;
    }

    :deep(.el-input__count) {
      color: #7a838a;
    }
  }
}
</style>

<style>
input::-webkit-input-placeholder {
  color: #fff;
}
</style>

