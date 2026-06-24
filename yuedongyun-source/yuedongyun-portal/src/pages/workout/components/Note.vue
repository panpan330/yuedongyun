<!-- 训练笔记 -->
<template>
  <div class="learnNoteWrapper">
    <div class="tabCheck">
      <span class="fx-1 cur-pt" :class="{ act: actIndex === 1 }" @click="activeHandle(1)">我的训练笔记</span>
      <span class="line"></span>
      <span class="fx-1 cur-pt" :class="{ act: actIndex === 2 }" @click="activeHandle(2)">全部训练笔记</span>
    </div>

    <div v-if="noteListsDataes.length > 0" class="noteCont">
      <div v-for="item in noteListsDataes" :key="item.id" class="noteLists">
        <div class="userInfo fx-sb">
          <div class="fx ft-cl-wt">
            <img :src="item.authorIcon" alt="" srcset="" />
            {{ item.authorName }}
          </div>
          <div>
            <i class="iconfont zhy-a-icon-sp22x"></i>
            {{ formatMoment(item.noteMoment) }}
          </div>
        </div>

        <div class="note">
          <div class="tit ft-14">{{ item.content }}</div>
        </div>

        <div class="time fx-sb">
          <div class="tm">{{ item.createTime }}</div>
          <div class="actBut">
            <span
              v-if="userInfo && userInfo.id === item.authorId"
              class="marg-rt-10"
              @click="editNoteHandle(item)"
            >
              <i class="iconfont zhy-a-icon-xiugai22x"></i> 编辑
            </span>
            <span
              v-if="userInfo && userInfo.id !== item.authorId"
              :class="{ activeLiked: item.isGathered }"
              @click="gathersHandle(item)"
            >
              <i class="iconfont zhy-a-ico-caiji2x"></i> {{ item.isGathered ? "已收藏" : "收藏" }}
            </span>
            <span v-if="userInfo && userInfo.id === item.authorId" @click="delNoteHandle(item)">
              <i class="iconfont zhy-a-icon-delete22x" style="font-size: 23px; top: 3px"></i> 删除
            </span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="noData">
      <Empty :type="true" />
    </div>

    <div class="questCont">
      <el-input
        v-model="noteParams.content"
        rows="4"
        resize="none"
        type="textarea"
        maxlength="500"
        placeholder="记录本次训练感受、动作要点或节奏提醒"
        show-word-limit
        @input="ruleshandle"
      />
      <div class="fx-sb fx-al-ct" style="margin-top: 12px">
        <div>
          <el-checkbox v-model="noteParams.isPrivate" label="仅自己可见" size="large" />
        </div>
        <div class="subCont">
          <span class="bt ft-14" :class="{ 'bt-dis': !isSend }" @click="submitForm()">保存笔记</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watchEffect } from "vue";
import { ElMessage } from "element-plus";
import Empty from "@/components/Empty.vue";
import { dataCacheStore, useUserStore } from "@/store";
import {
  addNotes,
  delNote,
  getAllNotes,
  notesGathers,
  unNotesGathers,
  updateNotes,
} from "@/api/notes.js";

const currentPlayData = dataCacheStore().getCurrentPlayData || {};
const store = useUserStore();

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
  currentTime: {
    type: Number,
    default: 0,
  },
});

const userInfo = ref(null);
const isSend = ref(false);
const actIndex = ref(1);
const noteListsDataes = ref([]);
const total = ref(0);
const subType = ref("add");
const editParams = ref({});

const noteParams = reactive({
  isPrivate: false,
  sessionId: currentPlayData.sessionId,
  trainingId: currentPlayData.trainingId,
  phaseId: currentPlayData.phaseId,
  content: "",
  noteMoment: 0,
});

const params = ref({
  admin: false,
  isAsc: false,
  pageNo: 1,
  pageSize: 1000,
  sessionId: currentPlayData.sessionId,
  trainingId: currentPlayData.trainingId,
  sortBy: "",
  onlyMine: true,
});

onMounted(() => {
  userInfo.value = store.getUserInfo;
  getNoteList();
});

watchEffect(() => {
  noteParams.noteMoment = props.currentTime;
});

const formatMoment = (moment = 0) => {
  const minute = Math.floor(Number(moment || 0) / 60);
  const second = Math.floor(Number(moment || 0) % 60)
    .toString()
    .padStart(2, "0");
  return `${minute}:${second}`;
};

const activeHandle = (value) => {
  actIndex.value = value;
  params.value.onlyMine = value === 1;
  getNoteList();
};

const getNoteList = async () => {
  await getAllNotes(params.value).then((res) => {
    if (res.code === 200) {
      noteListsDataes.value = res.data.list || [];
      total.value = Number(res.data.total || 0);
      return;
    }
    ElMessage({
      message: res.msg || "训练笔记加载失败",
      type: "error",
    });
  });
};

const ruleshandle = () => {
  isSend.value = noteParams.content.trim() !== "";
};

const gathersHandle = async (item) => {
  if (item.isGathered) {
    await unNotesGathersData(item);
    return;
  }
  await notesGathersData(item);
};

const notesGathersData = async (item) => {
  await notesGathers(item.id).then((res) => {
    if (res.code === 200) {
      ElMessage({
        message: "笔记收藏成功",
        type: "success",
      });
      item.isGathered = true;
      getNoteList();
      return;
    }
    ElMessage({
      message: res.msg || "笔记收藏失败",
      type: "error",
    });
  });
};

const unNotesGathersData = async (item) => {
  await unNotesGathers(item.id)
    .then((res) => {
      if (res.code === 200) {
        ElMessage({
          message: "已取消收藏",
          type: "success",
        });
        item.isGathered = false;
        getNoteList();
        return;
      }
      ElMessage({
        message: res.msg || "取消收藏失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "训练笔记操作失败，请稍后重试",
        type: "error",
      });
    });
};

const delNoteHandle = async (item) => {
  await delNote(item.id)
    .then((res) => {
      if (res.code === 200) {
        ElMessage({
          message: "训练笔记删除成功",
          type: "success",
        });
        getNoteList();
        return;
      }
      ElMessage({
        message: res.msg || "删除失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "训练笔记操作失败，请稍后重试",
        type: "error",
      });
    });
};

const submitForm = async () => {
  if (!isSend.value) {
    ElMessage({
      message: "请先输入训练笔记内容",
      type: "warning",
    });
    return;
  }

  const query = subType.value === "add" ? addNotes : updateNotes;
  const submitParams =
    subType.value === "edit"
      ? {
          ...editParams.value,
          content: noteParams.content,
          isPrivate: noteParams.isPrivate,
        }
      : {
          ...noteParams,
          noteMoment: props.currentTime,
        };

  await query(submitParams)
    .then((res) => {
      if (res.code === 200) {
        subType.value = "add";
        noteParams.content = "";
        noteParams.isPrivate = false;
        isSend.value = false;
        getNoteList();
        ElMessage({
          message: "训练笔记已保存",
          type: "success",
        });
        return;
      }
      ElMessage({
        message: res.msg || "笔记保存失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "训练笔记保存失败，请稍后重试",
        type: "error",
      });
    });
};

const editNoteHandle = (item) => {
  noteParams.content = item.content;
  noteParams.isPrivate = !!item.isPrivate;
  subType.value = "edit";
  editParams.value = {
    id: item.id,
    content: item.content,
    isPrivate: !!item.isPrivate,
  };
  isSend.value = true;
};
</script>

<style lang="scss" scoped>
.learnNoteWrapper {
  .tabCheck {
    margin: 10px auto 20px auto;
    display: flex;
    justify-content: center;
    width: 204px;
    height: 32px;
    line-height: 32px;
    border: 1px solid #a0a9b2;
    color: #a0a9b2;
    border-radius: 16px;
    font-size: 14px;
    text-align: center;

    .line {
      position: relative;
      top: 10px;
      height: 12px;
      width: 1px;
      background-color: #a0a9b2;
    }
  }

  .act {
    color: #fff;
  }

  .noteCont {
    height: calc(100vh - 378px);
    overflow-y: scroll;

    .noteLists {
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

        i {
          position: relative;
          top: 2px;
          font-size: 18px;
        }
      }

      .note {
        color: #a0a9b2;
        margin-top: 0;

        .tit {
          line-height: 24px;
          margin-top: 0;
        }
      }

      .time {
        color: var(--color-font3);
        padding-bottom: 16px;
        margin-bottom: 19px;
        border-bottom: 1px solid #000000;
        line-height: 20px;

        .tm {
          color: var(--color-ft2);
        }

        .actBut {
          color: #a0a9b2;
          cursor: pointer;

          .iconfont {
            position: relative;
            color: #a0a9b2;
            font-size: 20px;
            top: 2px;
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
  }

  .noData {
    height: calc(100vh - 488px);
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

    :deep(.el-textarea__inner) {
      color: #fff;
    }
  }

  .activeLiked,
  .activeLiked .iconfont {
    color: var(--color-main) !important;
  }
}
</style>

