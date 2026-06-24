<!-- 问题与回复详情 -->
<template>
  <div class="askDetailsWrapper">
    <div v-if="askInfo" class="container">
      <div class="fx-sb">
        <div class="fx-1 marg-rt-20">
          <div class="askCont bg-wt marg-bt-20">
            <div class="userInfo">
              <img v-if="askInfo.userIcon" :src="askInfo.userIcon" alt="" />
              <img v-else src="/src/assets/anonymity.png" alt="" />
              {{ askInfo.userName || "匿名用户" }}
            </div>
            <div class="askInfo">
              <div class="ft-20 ft-wt-600">{{ askInfo.title }}</div>
              <div class="ft-cl-des marg-bt-10">{{ askInfo.createTime }}</div>
              <div>{{ askInfo.description }}</div>
            </div>
          </div>

          <div class="answerCont bg-wt marg-bt-20">
            <div class="ft-20 ft-wt-600 marg-bt-20">我要回复</div>
            <div class="answer fx">
              <img :src="store.getUserInfo.icon" alt="" srcset="" />
              <div class="fx-1">
                <el-input
                  v-model="description"
                  rows="11"
                  type="textarea"
                  maxlength="500"
                  placeholder="请输入你的训练建议、动作纠正或经验分享"
                  show-word-limit
                  @input="ruleshandle"
                />
                <div class="fx-sb fx-al-ct">
                  <div>
                    <el-checkbox v-model="anonymity" label="匿名回复" size="large" />
                  </div>
                  <div class="subCont">
                    <span class="bt ft-14" :class="{ 'bt-dis': !isSend }" @click="answerHandle('first')">
                      发布回复
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="answerCont bg-wt marg-bt-20">
            <div class="ft-20 ft-wt-600 marg-bt-20">全部回复（{{ count }}）</div>
            <div class="answerItems">
              <div v-for="item in questionData" :key="item.id" class="items">
                <div class="fx-al-ct">
                  <img v-if="item.userIcon" class="img" :src="item.userIcon" alt="" />
                  <img v-else class="img" src="/src/assets/anonymity.png" alt="" />
                  <span class="ft-cl-des">{{ item.userName || "匿名用户" }}</span>
                </div>
                <div class="cont">
                  <div class="marg-bt-10">{{ item.content }}</div>
                  <div class="fx-sb">
                    <div class="ft-cl-des">{{ item.createTime }}</div>
                    <div>
                      <span class="marg-rt-10 cur-pt" @click="openReply(item)">
                        <i class="iconfont zhy-a-btn_pinglun_nor2x"></i> 回复（{{ item.replyTimes }}）
                      </span>
                      <span :class="{ 'cur-pt': true, activeLiked: item.liked }" @click="likedHandle(item)">
                        <i class="iconfont zhy-a-btn_zan_nor2x"></i> 点赞（{{ item.likedTimes }}）
                      </span>
                    </div>
                  </div>
                </div>
                <component
                  :is="openReplyFormId === item.id ? ReplayForm : null"
                  :key="item.id"
                  :name="item.userName || '匿名用户'"
                  :askInfoId="askInfo.id"
                  @commentHandle="commentHandle"
                />

                <div v-show="replyData && isReplay === item.id" class="replyCont">
                  <div v-for="it in replyData" :key="it.id" class="items">
                    <div class="fx-al-ct">
                      <img v-if="it.userIcon" class="img" :src="it.userIcon" alt="" />
                      <img v-else class="img" src="/src/assets/anonymity.png" alt="" />
                      <span class="ft-cl-des">
                        {{ it.userName || "匿名用户" }} 回复 {{ it.targetUserName || "匿名用户" }}
                      </span>
                    </div>
                    <div class="cont">
                      <div class="marg-bt-10">{{ it.content }}</div>
                      <div class="fx-sb">
                        <div class="ft-cl-des">{{ it.createTime }}</div>
                        <div>
                          <span class="marg-rt-10 cur-pt" @click="replayHandle(it, 'target')">
                            <i class="iconfont zhy-a-btn_pinglun_nor2x"></i> 回复
                          </span>
                          <span :class="{ 'cur-pt': true, activeLiked: it.liked }" @click="likedHandle(it)">
                            <i class="iconfont zhy-a-btn_zan_nor2x"></i> 点赞（{{ it.likedTimes }}）
                          </span>
                        </div>
                      </div>
                    </div>
                    <component
                      :is="openReplyFormId === it.id ? ReplayForm : null"
                      :key="it.id"
                      :name="it.userName || '匿名用户'"
                      :id="it.userId"
                      :askInfoId="askInfo.id"
                      @commentHandle="commentHandle"
                    />
                  </div>
                  <div
                    v-if="replyCont > 5"
                    class="fx-ct ft-14 ft-cl-des cur-pt"
                    @click="() => { dialogTableVisible = true; }"
                  >
                    点击查看全部 {{ replyCont }} 条回复
                  </div>
                </div>
              </div>
              <div></div>
              <p v-if="!noMore" class="fx-ct ft-14 ft-cl-des" @click="clickLoad">点击查看更多</p>
              <p v-if="noMore" class="fx-ct ft-14 ft-cl-des">没有更多了</p>
            </div>
          </div>
        </div>

        <RelatedQuestions :id="route.query.detailsId" :title="route.query.name" />
      </div>
    </div>

    <el-dialog v-model="dialogTableVisible" :title="`全部回复（${replyCont}）`" width="80%" top="5vh">
      <div
        class="dialogReplyCont"
        v-infinite-scroll="load"
        style="overflow: auto"
        :infinite-scroll-disabled="disabled"
      >
        <div v-for="it in replyData" :key="`ss${it.id}`" class="items">
          <div class="fx-al-ct">
            <img v-if="it.userIcon" class="img" :src="it.userIcon" alt="" />
            <img v-else class="img" src="/src/assets/anonymity.png" alt="" />
            <span class="ft-cl-des">
              {{ it.userName || "匿名用户" }} 回复 {{ it.targetUserName || "匿名用户" }}
            </span>
          </div>
          <div class="cont">
            <div class="marg-bt-10">{{ it.content }}</div>
            <div class="fx-sb">
              <div class="ft-cl-des">{{ it.createTime }}</div>
              <div>
                <span class="marg-rt-10 cur-pt" @click="replayHandle(it, 'target')">
                  <i class="iconfont zhy-a-btn_pinglun_nor2x"></i> 回复
                </span>
                <span :class="{ 'cur-pt': true, activeLiked: it.liked }" @click="likedHandle(it)">
                  <i class="iconfont zhy-a-btn_zan_nor2x"></i> 点赞 {{ it.likedTimes }}
                </span>
              </div>
            </div>
          </div>
          <component
            :is="openReplyFormId === it.id ? ReplayForm : null"
            :key="it.id"
            :name="it.userName || '匿名用户'"
            :id="it.userId"
            :askInfoId="askInfo.id"
            @commentHandle="commentHandle"
          />
        </div>
        <p v-if="replyLoding" class="fx-ct ft-14 ft-cl-des">Loading...</p>
        <p v-if="replaynoMore" class="fx-ct ft-14 ft-cl-des">没有更多了</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import { getQuestionsDetails, getReply, postAnswers, putLiked } from "@/api/trainingDetails.js";
import { useUserStore } from "@/store";
import RelatedQuestions from "./components/RelatedQuestions.vue";
import ReplayForm from "./components/ReplayForm.vue";

const store = useUserStore();
const route = useRoute();

const askInfo = ref();
const dialogTableVisible = ref(false);
const count = ref(0);
const loading = ref(false);
const questionData = ref([]);
const noMore = computed(() => questionData.value.length >= count.value);
const description = ref("");
const anonymity = ref(false);
const isSend = ref(false);
const isReplay = ref();
const openReplyFormId = ref();
const answerInfo = ref({ id: "" });
const targetInfo = ref({ id: "" });
const replyData = ref([]);
const replyCont = ref();
const replyLoding = ref(true);
const replaynoMore = computed(() => replyData.value.length >= replyCont.value);
const disabled = computed(() => replyLoding.value || replaynoMore.value);

const questParams = reactive({
  questionId: route.query.id,
  pageNo: 1,
  pageSize: 10,
});

const replyParams = reactive({
  pageNo: 1,
  pageSize: 5,
  answerId: "",
});

const params = reactive({
  questionId: "",
  targetReplyId: "",
  targetUserId: "",
  answerId: "",
  content: "",
  anonymity: "",
});

onMounted(() => {
  getQuestionsDetailsData();
  getAllQuestionsData();
});

const getQuestionsDetailsData = async () => {
  await getQuestionsDetails(route.query.id)
    .then((res) => {
      if (res.code === 200) {
        askInfo.value = res.data;
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

const clickLoad = () => {
  loading.value = false;
  questParams.pageNo++;
  getAllQuestionsData("more");
};

const getAllQuestionsData = async (mode) => {
  await getReply(questParams)
    .then((res) => {
      if (res.code === 200) {
        if (mode === "more") {
          questionData.value = questionData.value.concat(res.data.list || []);
        } else if (questParams.pageNo === 1) {
          questionData.value = res.data.list || [];
        } else {
          questionData.value = questionData.value
            .slice(0, (questParams.pageNo - 1) * questParams.pageSize)
            .concat(res.data.list || []);
        }
        count.value = Number(res.data.total || 0);
        loading.value = false;
        return;
      }
      ElMessage({
        message: res.msg || "回复列表加载失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "回复数据请求出错！",
        type: "error",
      });
    });
};

const ruleshandle = () => {
  isSend.value = description.value.trim() !== "";
};

const openReply = (item) => {
  if (item.id !== isReplay.value) {
    getReplyData(item.id, "one");
    replayHandle(item, "answer");
  }
};

const replayHandle = (item, type) => {
  openReplyFormId.value = item.id;
  targetInfo.value = item;
  if (type === "answer") {
    answerInfo.value = item;
  }
};

const load = () => {
  replyLoding.value = false;
  replyParams.pageNo++;
  getReplyData(replyParams.answerId);
};

const getReplyData = async (id, mode) => {
  replyLoding.value = true;
  replyParams.answerId = id;
  await getReply(replyParams)
    .then((res) => {
      if (res.code === 200) {
        replyLoding.value = false;
        replyData.value = mode === "one" ? res.data.list || [] : replyData.value.concat(res.data.list || []);
        replyCont.value = Number(res.data.total || 0);
        isReplay.value = id;
        return;
      }
      ElMessage({
        message: res.msg || "回复详情加载失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "回复数据请求出错！",
        type: "error",
      });
    });
};

function commentHandle(val) {
  params.content = val.content;
  params.anonymity = val.anonymity;
  answerHandle();
}

const answerHandle = async (type) => {
  params.questionId = askInfo.value.id;
  params.targetUserId = targetInfo.value.userId || askInfo.value.userId;
  if (params.content === "") {
    params.content = description.value;
    params.anonymity = anonymity.value;
  }
  params.answerId = answerInfo.value.id;
  params.targetReplyId = targetInfo.value.id;

  if (params.content === "") {
    ElMessage({
      message: "请输入内容后再提交",
      type: "warning",
    });
    return;
  }

  await postAnswers(params)
    .then((res) => {
      if (res.code === 200) {
        ElMessage({
          message: "回复成功！",
          type: "success",
        });

        if (type === "first") {
          getAllQuestionsData();
        } else if (dialogTableVisible.value) {
          getReplyData(isReplay.value, "one");
        } else {
          getReplyData(isReplay.value, "one");
        }

        params.content = "";
        description.value = "";
        params.anonymity = "";
        anonymity.value = "";
        isSend.value = false;
        return;
      }
      ElMessage({
        message: res.msg || "回复失败",
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

const likedHandle = async (item) => {
  await putLiked({ bizId: item.id, liked: !item.liked, bizType: "QA" })
    .then((res) => {
      if (res.code === 200) {
        item.liked = !item.liked;
        item.liked ? item.likedTimes++ : item.likedTimes--;
        return;
      }
      ElMessage({
        message: res.msg || "点赞失败",
        type: "error",
      });
    })
    .catch(() => {
      ElMessage({
        message: "点赞请求出错！",
        type: "error",
      });
    });
};
</script>

<style lang="scss" src="./index.scss"></style>
