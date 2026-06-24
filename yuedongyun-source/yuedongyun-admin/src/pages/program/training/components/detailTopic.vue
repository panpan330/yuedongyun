<!--训练题目-->
<template>
  <div class="contentBox">
    <div class="trainingList">
      <el-collapse accordion v-model="activeNames">
        <el-collapse-item v-for="(item, index) in itemData.value" :key="index">
          <template v-slot:title>
            <div class="titText">
              <span class="icon" v-if="item.sessions.length > 0"></span>
              <div class="textL">
                <span
                  ><span v-if="index + 1 > 9">{{ index + 1 }}</span
                  ><span v-else>{{ "0" + (index + 1) }}</span></span
                >
                <span>{{ item.name }}</span>
              </div>
              <div class="textR">
            <span class="textForbidden">添加阶段考核</span>
              </div>
            </div>
          </template>
          <div class="itemCon" v-if="item.sessions.length > 0">
            <div class="headTitle">
              <span>序号</span>
              <span>小节名称</span>
              <span>题目</span>
              <span>题目数目</span>
              <span>题目分数</span>
              <span>操作</span>
            </div>
            <div class="item">
              <ul>
                <li v-for="(val, i) in item.sessions" :key="i">
                  <div class="leftLine"></div>
                  <div class="con">
                    <!-- 序号 -->
                    <div>
                      <div v-if="val.type !== 3">
                        <span v-if="i + 1 > 9">{{ i + 1 }}</span
                        ><span v-else>{{ "0" + (i + 1) }}</span>
                      </div>
                    </div>
                    <div>
                      <span>{{ val.name }}</span>
                    </div>
                    <div>
                      <span
                        @click="handleWatch(val)"
                        :class="
                          val.assessmentNum > 0 ? 'textDefault' : 'textForbidden'
                        "
                      >
                        查看题目</span
                      >
                    </div>
                    <div>
                      {{ val.assessmentNum }}
                    </div>
                    <div>
                      {{ val.totalScore }}
                    </div>
                    <div>
                      <span class="textForbidden" v-if="val.type === 3"
                  >删除阶段考核</span
                      >
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <div class="cover"></div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <!-- 查看题目弹层 -->
    <detailwatchTopic
      :dialogVisible="dialogVisible"
      :jectIds="jectIds.value"
      :score="score"
      @detailwatchTopicInfo="detailwatchTopicInfo"
      @handleClose="handleWatchClose"
    ></detailwatchTopic>
    <!-- end -->
    <!-- 删除弹层 -->
    <!-- end -->
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, nextTick, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
// 接口
import { baseOutlineSave } from "@/api/program";
// 导入组件
// 查看题目弹层
import detailwatchTopic from "./detailwatchTopic.vue";
// ------定义变量------
// 获取父组件值、方法
const props = defineProps({
  trainingTopicData: {
    type: Object,
    default: {},
  },
});
const router = useRouter(); //获取全局
const route = useRoute(); //获取局部
let dialogVisible = ref(false); //弹层隐藏显示
let itemData = reactive([]); //目录数据
let activeNames = reactive(["1"]);
let phaseId = ref(null); //当前触发的章id
let trainingId = route.params.id;
let jectIds = reactive([]); //  设置题目弹层的回显内容
let score = ref(null); //当前小节的分数

watch("itemData", (newValue, oldValue) => {
  itemData.value = newValue;
});
watch(props, () => {
  getOutline();
  // console.log(props.trainingTopicData);
});
// ------定义方法------
// 获取目录数据
const getOutline = async () => {
  itemData.value = props.trainingTopicData;
  // 遍历章节
  itemData.value.map((val, index) => {
    val.sessions.map((obj, i) => {
      if (obj.type === 3) {
        val.isCheck = true;
      }
    });
  });
};

// 提交
const handleSubmit = async (str) => {
  let params = {
    datas: itemData.value,
    id: trainingId,
    step: 4,
  };
  await baseOutlineSave(params)
    .then((res) => {
      if (res.code === 200) {
        saveAssessment(); //保存小节或者练习中的题目
        if (str === "getback") {
          router.push({
            path: "/program/index",
          });
        } else {
          router.push({
            path: `/program/add/` + trainingId,
          });
        }
      } else {
        ElMessage({

          message: res.data.msg,
          type: "error",
          showClose: false,
        });
      }
    })
    .catch((err) => {});
};

// 获取设置题目的ids
const detailwatchTopicInfo = (data) => {
  itemData.value.map((val) => {
    val.sessions.map((ele) => {
      if (ele.id === phaseId.value) {
        ele.assessmentIds = data.value;
        ele.assessmentNum = data.value.length;
        ele.totalScore = data.totalScore;
        // assessmentData.push(JSON.parse(JSON.stringify(ele)))
        // arr.push(ele)
      }
    });
  });
  // assessmentData.value = arr;
  // console.log(itemData.value);
};
// 打开查看题目弹层
const handleWatch = (obj) => {
  // console.log(obj, "obj")
  if (obj.assessmentNum > 0) {
    jectIds.value = [];
    phaseId.value = obj.id;
    score.value = obj.totalScore; //分数
    dialogVisible.value = true;
    itemData.value.map((val) => {
      val.sessions.map((ele) => {
        if (ele.id === phaseId.value) {
          jectIds.value = ele.id;
        }
      });
    });
  }
};
// 关闭设置题目弹层
const handleWatchClose = () => {
  // jectIds.value = []
  dialogVisible.value = false;
};

// 向父组件暴露方法
defineExpose({
  handleSubmit,
});
</script>
<style lang="scss" scoped>
.trainingList .titText .textL span {
  font-size: 16px;
  color: #332929;
}
.headTitle {
  color: #332929;
}
.textR {
  font-size: 14px;
  font-family: PingFangSC-Regular;
  font-weight: 400;
}
</style>

