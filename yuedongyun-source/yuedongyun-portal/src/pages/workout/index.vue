<!-- 训练跟练中心 -->
<template>
  <div class="workoutCenter">
    <div class="videoCont">
      <div class="head fx-sb">
        <div class="fx cur-pt" @click="goBack">
          <img src="@/assets/icon_back.png" alt="" />
          <div>返回 <span class="line">|</span> {{ currentPlayData.sessionName }}</div>
        </div>
      </div>
      <div class="videoCont">
        <div v-show="pageType === 1" class="video">
          <video id="videoRef" ref="videoRef"></video>
        </div>
        <Practise
          v-if="pageType === 2"
          :key="currentPlayData.sessionId"
          :examInfo="examInfo"
          @playHadle="playHadle"
        />
      </div>
    </div>

    <div class="learn" :class="{ close: isClose }">
      <div class="closeRt cur-pt" :class="{ close: isClose }" @click="close">
        <i class="iconfont zhy-a-shouqi2x"></i>
      </div>
      <div class="teachInfo fx">
        <img
          :src="workoutTrainingDetails && workoutTrainingDetails.coverUrl"
          alt=""
          @click="() => $router.push({ path: '/details/index', query: { id: workoutTrainingDetails.id } })"
        />
        <div>
          <div class="tit">{{ workoutTrainingDetails && workoutTrainingDetails.name }}</div>
          <div class="coach ft-14">教练：{{ workoutTrainingDetails && workoutTrainingDetails.coachName }}</div>
        </div>
      </div>
      <div class="cont">
        <div class="pd-lr-10">
          <TableSwitchBar :data="tableBar" @changeTable="changeTable" />
        </div>
        <div v-show="actId === 1" class="outline" v-infinite-scroll="load" style="overflow: auto">
          <Outline
            :data="phases"
            :finished="finished"
            :playId="playId"
            @openOutline="openOutline"
            @playHadle="playHadle"
          />
        </div>
        <div v-if="actId === 2" class="question" v-infinite-scroll="load" style="overflow: auto">
          <Question />
        </div>
        <div v-if="actId === 3" class="note" v-infinite-scroll="load" style="overflow: auto">
          <Note :currentTime="currentPlayTime" />
        </div>
      </div>
    </div>

    <div class="askAndNote" :class="{ close: !isClose }" @click="open">
      <div class="fx-cl-ct">
        <img src="../../assets/btn-wd.png" alt="" />
        <p>互动问答</p>
      </div>
      <div class="fx-cl-ct">
        <img src="../../assets/btn-bj.png" alt="" />
        <p>训练笔记</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, onMounted, onUnmounted, provide, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRoute } from "vue-router";
import { dataCacheStore } from "@/store";
import { addPlayLog, getWorkoutTrainingDetails, getMediasSignature } from "@/api/training.js";
import TableSwitchBar from "./components/TableSwitchBar.vue";
import Outline from "./components/Outline.vue";
import Question from "./components/Question.vue";
import Practise from "./components/Practise.vue";
import Note from "./components/Note.vue";
import icon from "@/assets/icon_good.png";
import router from "../../router";

const route = useRoute();
const store = dataCacheStore();

const pageType = ref(1);
const detailsId = ref(route.query.id || "");
const tableBar = [
  { id: 1, name: "跟练目录" },
  { id: 2, name: "互动问答" },
  { id: 3, name: "训练笔记" },
];

const videoRef = ref(null);
const playId = ref("");
const finished = ref(false);
const fileId = ref("");
const signature = ref("");
const currentPlayTime = ref(0);
const player = ref(null);
const examInfo = ref({});
const actId = ref(1);
const isClose = ref(false);
const workoutTrainingDetails = ref(null);
const phases = ref([]);
const sessionMap = ref({});

let timer = -1;
let playing = false;

const currentPlayData = reactive({
  trainingId: route.query.id || "",
  lastPlaySessionId: "",
  prevSessionId: "",
  phaseId: "",
  sessionId: "",
  sessionName: "",
  moment: 0,
  duration: 0,
  type: "",
  sessionId: "",
});

provide("currentPlayData", currentPlayData);

onMounted(async () => {
  await getWorkoutTrainingDetailsData();
  currentPlayData.sessionId = currentPlayData.sessionId || workoutTrainingDetails.value?.latestSessionId || "";
  currentPlayData.moment = currentPlayData.moment || workoutTrainingDetails.value?.moment || 0;
  if (currentPlayData.sessionId) {
    await getMediasSignatureData(currentPlayData.sessionId);
  }
});

onUnmounted(() => {
  window.clearInterval(timer);
});

const getWorkoutTrainingDetailsData = async () => {
  await getWorkoutTrainingDetails(detailsId.value)
    .then((res) => {
      if (res.code !== 200) {
        ElMessage({
          message: res.msg || "训练详情加载失败",
          type: "error",
        });
        return;
      }

      sessionMap.value = {};
      res.data.phases.forEach((phase) => {
        phase.sessions.forEach((session) => {
          sessionMap.value[session.id] = session;
          session.hasTest = !!session.assessmentNum;
        });
      });

      const fallbackSession = res.data.phases?.[0]?.sessions?.[0];
      const currentSessionId = route.query.sessionId || res.data.latestSessionId;
      const currentSession = sessionMap.value[currentSessionId] || fallbackSession;

      if (!currentSession) {
        return;
      }

      const currentPhase = res.data.phases.find((phase) =>
        (phase.sessions || []).some((session) => session.id === currentSession.id)
      );

      res.data.latestSessionMoment = currentSession.moment;
      res.data.latestSessionName = currentSession.name;
      workoutTrainingDetails.value = res.data;
      phases.value = res.data.phases;

      currentPlayData.duration = currentSession.mediaDuration || 0;
      currentPlayData.sessionId = currentSession.id;
      currentPlayData.moment = currentSession.moment || 0;
      currentPlayData.sessionName = currentSession.name || "";
      currentPlayData.type = currentSession.type || "";
      currentPlayData.sessionId = res.data.sessionId || "";
      currentPlayData.phaseId = currentPhase?.id || "";
      playId.value = currentPlayData.sessionId || "";
      store.setCurrentPlayData(currentPlayData);
    })
    .catch(() => {
      ElMessage({
        message: "训练详情加载失败，请稍后重试",
        type: "error",
      });
    });
};

const trainingFinished = () => {
  ElMessageBox({
    title: "",
    message: h("div", null, [
      h("div", { style: "display:flex" }, [
        h("img", {
          src: icon,
          style: "width:52px;height:52px;margin-right: 10px;",
        }),
        h("div", null, [
          h("div", { style: "font-size: 18px;font-weight: 500;" }, "你真棒！"),
          h("div", { style: "line-height:30px" }, "当前训练内容已全部完成，去看看新的训练计划吧~"),
        ]),
      ]),
    ]),
    showCancelButton: false,
    confirmButtonText: "我知道了",
  }).then(() => {
    router.push("/personal/main/myTraining");
  });
};

const initPlay = (currentFileId, currentSignature) => {
  player.value = new TCPlayer(videoRef.value, {
    appID: "1312394356",
    fileID: currentFileId,
    psign: currentSignature,
    posterImage: true,
    autoplay: true,
    preload: "auto",
    hlsConfig: {},
  });

  player.value.on("timeupdate", () => {
    currentPlayData.moment = player.value.currentTime();
    currentPlayTime.value = currentPlayData.moment;
  });

  player.value.on("pause", () => {
    window.clearInterval(timer);
    playing = false;
  });

  player.value.on("play", () => {
    if (playing) {
      return;
    }
    playing = true;
    finished.value = false;
    if (!currentPlayData.sessionId) {
      return;
    }
    addPlayLogHandle();
    timer = window.setInterval(addPlayLogHandle, 15000);
  });

  player.value.on("ended", () => {
    window.clearInterval(timer);
    finished.value = true;
  });

  player.value.ready(() => {
    window.clearInterval(timer);
    player.value.currentTime(currentPlayData.moment || 0);
    player.value.play();
  });
};

const load = () => {};

const t = (num) => (num < 10 ? `0${num}` : num);

const now = () => {
  const date = new Date();
  return `${date.getFullYear()}-${t(date.getMonth() + 1)}-${t(date.getDate())} ${t(
    date.getHours()
  )}:${t(date.getMinutes())}:${t(date.getSeconds())}`;
};

const addPlayLogHandle = () => {
  const { sessionId, sessionId, moment, duration } = currentPlayData;
  if (!sessionId) {
    return;
  }
  addPlayLog({ sessionId, sessionId, moment, duration, sessionType: 1, commitTime: now() })
    .then((res) => {
      if (res.code === 200) {
        console.log("播放记录提交成功:", res);
      }
    })
    .catch((err) => console.log(err));
};

const getMediasSignatureData = async (sessionId) => {
  const res = await getMediasSignature({ sessionId });
  if (res.code === 200) {
    fileId.value = res.data.fileId;
    signature.value = res.data.signature;
    if (player.value == null) {
      initPlay(res.data.fileId, res.data.signature);
    }
    return true;
  }

  ElMessage({
    message: res.msg,
    type: "error",
  });
  return false;
};

const playHadle = async (val) => {
  const { item, tp } = val;
  const actionType = Number(tp);

  if (actionType === 0) {
    finished.value = false;
    if (!item) {
      trainingFinished();
    } else {
      ElMessage.success("当前训练已完成，试试配套自测吧");
    }
    return;
  }

  if (!item) {
    return;
  }

  const previousSessionId = currentPlayData.sessionId;
  currentPlayData.sessionName = item.name;

  if (String(tp) === "9") {
    pageType.value = 1;
    return;
  }

  currentPlayData.sessionId = item.id;
  currentPlayData.moment = item.moment || 0;
  currentPlayData.duration = item.mediaDuration || 0;
  currentPlayData.phaseId = item.phaseId || currentPlayData.phaseId;

  if (actionType === 1) {
    pageType.value = 1;
    if (item.id === previousSessionId) {
      player.value && player.value.play();
    } else {
      const success = await getMediasSignatureData(item.id);
      if (!success) {
        return;
      }
      player.value.loadVideoByID({
        appID: "1312394356",
        fileID: fileId.value,
        psign: signature.value,
      });
      player.value.currentTime(item.latestSessionMoment || item.moment || 0);
      player.value.play();
    }
  } else if (actionType === 2 || actionType === 3) {
    player.value && player.value.pause();
    if (item.type !== 2) {
      ElMessageBox.confirm(
        "温馨提示：阶段考核仅保留一次提交机会，如中途退出或未提交结果，将无法再次参加。确认后请点击开始考核。",
        "确认开始考核",
        {
          confirmButtonText: "开始考核",
          cancelButtonText: "稍后再做",
          type: "warning",
        }
      ).then(() => {
        startExaminationHandle(item);
      });
    } else {
      startExaminationHandle(item);
    }
  }

  store.setCurrentPlayData(currentPlayData);
};

const startExaminationHandle = (item) => {
  examInfo.value = {
    sessionId: item.id,
    type: item.type - 1,
    trainingId: currentPlayData.trainingId,
  };
  pageType.value = 2;
};

const openOutline = (phaseId) => {
  currentPlayData.phaseId = phaseId;
};

const changeTable = (id) => {
  actId.value = id;
};

const close = () => {
  isClose.value = true;
};

const open = () => {
  isClose.value = false;
};

const goBack = () => {
  window.clearInterval(timer);
  timer = 0;
  router.go(-1);
};
</script>

<style lang="scss" src="./index.scss"></style>

