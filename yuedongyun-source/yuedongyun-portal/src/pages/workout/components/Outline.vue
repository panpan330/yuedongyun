<!-- 跟练目录 -->
<template>
  <div class="outlineWrapper">
    <el-collapse accordion v-model="actIndex">
      <el-collapse-item :name="item.id" v-for="item in data" :key="item.id">
        <template #title>
          <div class="title"><span class="ft-wt-600">{{ item.name }}</span></div>
        </template>
        <div class="subTitle fx-sb" :class="isPlay(item.id, it)" v-for="it in item.sessions" :key="it.id">
          <i @click="play(it, it.type == 2 ? 1 : it.type)" :class="startIcon(it)"></i>
          <div class="subTit fx-1">
            <span @click="play(it, it.type == 2 ? 1 : it.type)" class="marg-rt-10">{{ it.name }}</span>
            <span v-if="it.hasTest" @click="play(it, 2)" class="phase">自测</span>
            <span v-if="it.trailer" class="trailer-font">试看</span>
          </div>
          <div>
            <span @click="play(it, it.type == 2 ? 1 : it.type)" v-if="it.mediaDuration != 0">
              {{ formatDuration(it.mediaDuration) }}
            </span>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { onMounted, ref, watchEffect, inject } from "vue";

const props = defineProps({
  data: {
    type: Array,
    default: [],
  },
  statusList: {
    type: Array,
    default: [],
  },
  playId: {
    type: String,
    default: "",
  },
  finished: {
    type: Boolean,
    default: false,
  },
});

const currentPlayData = inject("currentPlayData");
const playId = ref(props.playId);
const actIndex = ref("");

onMounted(() => {
  for (const phase of props.data) {
    phase.sessions.forEach((session) => {
      if (session.id == props.playId) {
        actIndex.value = phase.id;
      }
    });
  }
});

watchEffect(() => {
  playId.value = currentPlayData.sessionId || props.playId;
  if (props.finished) {
    next();
  }
});

const formatDuration = (duration = 0) => {
  const minute = Math.floor(Number(duration || 0) / 60);
  const second = Math.floor(Number(duration || 0) % 60)
    .toString()
    .padStart(2, "0");
  return `${minute}:${second}`;
};

const isPlay = (phaseId, session) => {
  const active = playId.value === session.id;
  if (active) {
    openItem(phaseId);
  }
  return { playAct: active };
};

const startIcon = (item) => {
  let data = "iconfont zhy-a-ico-sp-sei2x";

  if (item.type == "2") {
    if (item.finished != undefined && item.finished == false) {
      data = "iconfont zhy-a-ico-502x1";
    } else if (item.finished && item.finished == true) {
      data = "iconfont zhy-a-ico-wc2x";
    } else {
      data = "iconfont zhy-a-ico-sp-sei2x";
    }
  } else if (item.type == "3") {
    data = "iconfont zhy-a-ico-jdks2x";
  }
  return data;
};

const emit = defineEmits(["sortHandle", "playHadle", "openOutline"]);

const openItem = (val) => {
  emit("openOutline", val);
};

const play = (item, tp) => {
  playId.value = item.id;
  emit("playHadle", { item, tp });
};

const next = () => {
  const phases = props.data || [];
  let currentFound = false;
  let nextItem = null;

  outer: for (const phase of phases) {
    for (const session of phase.sessions || []) {
      if (currentFound) {
        nextItem = session;
        break outer;
      }
      if (session.id === playId.value) {
        currentFound = true;
      }
    }
  }

  if (!nextItem || nextItem.type === 3) {
    emit("playHadle", { item: nextItem, tp: 0 });
    return;
  }
  playId.value = nextItem.id;
  emit("playHadle", { item: nextItem, tp: 1 });
};
</script>

<style lang="scss" scoped>
.outlineWrapper {
  padding: 0 10px;

  ::deep(.el-collapse-item__header) {
    background: transparent;
  }

  .title {
    font-size: 16px;
    height: 40px;
    line-height: 40px;
    width: 280px;
    overflow: hidden;

    span {
      display: inline-block;
      height: 40px;
    }
  }

  .subTitle {
    position: relative;
    line-height: 20px;
    cursor: pointer;
    margin: 5px 0 20px 0;
    color: #a0a9b2;

    &::before {
      position: relative;
      z-index: 2;
    }

    &::after {
      content: "";
      position: absolute;
      left: 7px;
      top: 21px;
      border-left: 1px dashed #667280;
      height: calc(100% - 2px);
    }

    i {
      position: relative;
      top: 1px;
      margin-right: 4px;
    }

    .subTit {
      width: 230px;
      line-height: 20px;

      .phase {
        display: inline-block;
        width: 32px;
        text-align: center;
        line-height: 15.5px;
        font-weight: 400;
        font-size: 12px;
        border-radius: 2px;
        background: #a0a9b2;
        color: #1b2127;
      }
    }

    &:hover {
      color: #fff;

      .phase {
        background: #ffffff !important;
        color: #1b2127 !important;
      }
    }
  }

  :deep(.el-collapse-item__content) {
    padding-bottom: 5px;
  }

  .subTitle:last-child {
    margin-bottom: 0;

    &::after {
      display: none;
    }
  }

  .playAct {
    color: var(--color-main);

    &::after {
      border-color: var(--color-main);
    }

    .phase {
      background: var(--color-main) !important;
      color: #ffffff !important;
    }
  }

  .trailer-font {
    padding: 3px 12px;
    font-size: 12px;
    border-radius: 12px;
    color: #f20d0d;
    font-weight: 700;
    line-height: 20px;
  }
}
</style>
