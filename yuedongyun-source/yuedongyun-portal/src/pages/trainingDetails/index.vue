<!-- 训练详情 -->
<template>
  <div class="trainingDetailsWrapper">
    <div class="detailHead">
      <div class="backGround"><img :src="baseDetailsData.coverUrl" width="100%" alt=""/></div>
      <div class="container">
        <Breadcrumb :data="baseDetailsData.cateNames && baseDetailsData.cateNames.split('/').at(-1)"></Breadcrumb>
        <div class="topInfo fx">
          <div class=""><img :src="baseDetailsData.coverUrl" width="380" height="234" alt="" /></div>
          <div class="fx-1">
              <div class="title">{{baseDetailsData.name}}</div>
              <div class="item fx">
                <div class="card">
                  <div class="tit">训练节数</div>
                  <div class="info">{{baseDetailsData.outlineTotalNum}}节</div>
                </div>
                <div class="card">
                  <div class="tit">有效期</div>
                  <div class="info">{{baseDetailsData.validDuration > 99 ? '永久有效' : `${baseDetailsData.validDuration}月`}}</div>
                </div>
                <div class="card bd-non">
                  <div class="tit">评分</div>
                  <div class="info">{{baseDetailsData.score/10}}</div>
                </div>
              </div>
              <div class="fx">
                <div @click="collectionHandle" class="bt-wt bt-round marg-rt-15 ft-14" :class="{isCollection:isCollection}"> <i :class="{iconfont:true, 'zhy-btn_shoucang':!isCollection, 'zhy-btn_yishoucang':isCollection}"></i> 收藏</div>
                <div class="bt-wt bt-round ft-14"><weixin class="wx"></weixin> 分享</div>
              </div>
          </div>
        </div>
        <div v-if="baseDetailsData">
          <div class="buyCont fx-sb" v-if="baseDetailsData.price != '0'" >
          <div class="fx-ct">
            <span class="price">￥</span>
            <span class="price">{{(baseDetailsData.price / 100).toFixed(2) }}</span>
             <span class="desc">训练服务保障 · 购买后可立即开练</span> 
          </div>
          <div class="buy" v-if="!isSignUp">
            <span class="bt-red1 bt-round marg-rt-20" @click="addCarts()">加入购物车</span>
            <span class="bt-red bt-round" @click="payHandle()" >立即购买</span>
          </div>
          <div class="buy" v-else @click="goWorkout">
             <span class="bt-red bt-round">马上开练</span>
          </div>
        </div>
        <div class="buyCont fx-sb" v-else >
          <div class="fx-ct">
            <span class="price">免费</span>
          </div>
          <div class="buy" v-if="!isSignUp" @click="signUpHandle">
             <span class="bt-red bt-round">立即加入</span>
          </div>
          <div class="buy" v-else @click="goWorkout">
             <span class="bt-red bt-round">马上开练</span>
          </div>
        </div>
        </div>

      </div>
    </div>
    <!-- 主体部分 - start -->
    <div class="DetailsContent container fx">
      <div class="leftCont bg-wt">
        <TableSwitchBar :data="tableBar" @changeTable="changeTable"></TableSwitchBar>
        <!-- 训练介绍 -->
        <TrainingAbout v-show="actId == 1" :baseDetailsData="baseDetailsData" :baseTrainingCoach="baseTrainingCoach"></TrainingAbout>
        <!-- 训练目录 -->
        <TrainingOutline v-show="actId == 2" :data="trainingListData" :isSignUp="isSignUp"  :id="detailsId"></TrainingOutline>
        <!-- 社区问答 -->
        <TrainingAsk v-if="isLogin() && isSignUp" v-show="actId == 3" :id="detailsId" :title="baseDetailsData.name"></TrainingAsk>
        <!-- 训练笔记 -->
        <Note  v-if="isLogin() && isSignUp" v-show=" actId == 4" :id="detailsId"></Note>
        <div class="fx-ct ft-cl-des" style="height: 400px;" v-show="actId == 5" :id="detailsId">
          暂无数据！
        </div>
      </div>
      <div class="ritCont">
        <!-- 训练常见问题 -->
        <Ask :data="trainingAskData"></Ask>
        <!-- 推荐训练 -->
        <LikeCards :data="recommendTrainingData"></LikeCards>
      </div>
    </div>
    <!-- 主体部分 - end -->
  </div>
</template>
<script setup>

/** 数据导入 **/
import { computed, onMounted, ref } from "vue";
import { ElMessage,ElMessageBox } from "element-plus";
import { getTrainingDetails, getTrainingCoaches, getTrainingList } from "@/api/trainingDetails.js";
import { enrolledFreeTraining, putCarts } from "@/api/order.js";

import { getTrainingWorkout } from "@/api/training.js";
import { useRoute, useRouter } from "vue-router";
import { dataCacheStore } from "@/store"

// 组件导入
import Breadcrumb from "@/components/Breadcrumb.vue";
import TableSwitchBar from "@/components/TableSwitchBar.vue";
import Empty from "@/components/Empty.vue";
import TrainingAbout from "./components/TrainingAbout.vue";
import LikeCards from "./components/LikeCards.vue";
import Ask from "./components/Ask.vue";
import TrainingAsk from "./components/TrainingAsk.vue";
import Note from "./components/Note.vue";
import TrainingOutline from "./components/TrainingOutline.vue";
import { isLogin } from "@/store";
import weixin from '@/assets/icon_weixin.svg'

const route = useRoute()
const router= useRouter()
const store = dataCacheStore()

// 结果 - 详情Id
const detailsId = ref()

// 训练信息及教练信息
const baseDetailsData = ref({})
const baseTrainingCoach = ref([])

// table切换数据 - 静态数据
const logData = [{id: 1, name: '训练介绍'}, {id: 2, name: '训练目录'},{id: 3, name: '社区问答'},{id: 4, name: '训练笔记'}, {id: 5, name: '用户评价'}]
const noLogData = [{id: 1, name: '训练介绍'}, {id: 2, name: '训练目录'}, {id: 5, name: '用户评价'}]
const tableBar = computed(() => {
  return isLogin() && isSignUp.value ? logData : noLogData
})

// 猜你喜欢 - 静态数据
const LikeData = [
  {
    sold: 234, 
    icon: "sit enim sunt", 
    sessions: 45,
    coverUrl: "http://img-qn-3.51miz.com/preview/muban/00/00/50/44/M-504460-F3103C10.jpg!/quality/90/unsharp/true/compress/true/fw/640/clip/640x500a0a0",
    duration: 444,
    icon: "sit enim sunt",
    id: 46000019721003770,
    name: "居家燃脂入门训练",
    price: 64540,
    sessions: 45,
    sold: 234,
    coach: "李燃教练"
  },
  {
    sold: 234, 
    icon: "sit enim sunt", 
    sessions: 45,
    coverUrl: "http://img-qn-3.51miz.com/preview/muban/00/00/50/44/M-504460-F3103C10.jpg!/quality/90/unsharp/true/compress/true/fw/640/clip/640x500a0a0",
    duration: 444,
    icon: "sit enim sunt",
    id: 46000019721003770,
    name: "晨间塑形跟练课",
    price: 64540,
    sessions: 45,
    sold: 234,
    coach: "林夏教练"
  }
]

const isCollection = ref(false);

const recommendTrainingData = [
  {
    sold: 412,
    sessions: 28,
    coverUrl: "/img-tx/default-cover-url.jpg",
    duration: 35,
    id: 46000019721003770,
    name: "燃脂 HIIT 入门课",
    price: 0,
    coach: "周燃教练",
    difficulty: 1,
    trainPart: "全身",
    calorieBurn: 260,
  },
  {
    sold: 189,
    sessions: 36,
    coverUrl: "/img-tx/default-cover-url.jpg",
    duration: 45,
    id: 46000019721003771,
    name: "臀腿塑形进阶营",
    price: 19900,
    coach: "林夏教练",
    difficulty: 2,
    trainPart: "臀腿",
    calorieBurn: 420,
  },
]

const trainingAskData = [
  {
    ask: "如何查看我已加入的训练？",
    answer: "登录后进入【我的训练】即可查看已购训练、免费加入训练和最近跟练记录。",
  },
  {
    ask: "免费训练和会员训练有什么区别？",
    answer: "免费训练可直接加入跟练，会员训练通常会提供更完整的训练内容、计划安排和持续更新服务。",
  },
  {
    ask: "训练节奏跟不上怎么办？",
    answer: "建议先从基础难度开始，按章节顺序逐步跟练，也可以暂停视频后按自己的节奏完成动作。",
  },
  {
    ask: "训练有效期结束后还能继续看吗？",
    answer: "会员训练会按有效期提供观看权限，请在有效期内完成训练安排；永久有效的训练内容不受影响。",
  },
]

// 当前table选项
const actId = ref(1)

// 常见问题 - 静态数据
const askData = [
  { ask: "如何查看已加入的训练？", answer: "请使用当前账号登录，进入【我的训练】页面查看。" },
  { ask: "训练购买后可以更换吗？", answer: "如需更换训练内容，请联系平台客服确认是否支持调整。" },
  { ask: "无法登录怎么办？", answer: "建议先检查账号状态，或尝试更换浏览器后重新登录。" },
  { ask: "训练到期后怎么办？", answer: "训练到期后将无法继续观看，请在有效期内完成跟练安排。" },
]
// 训练目录
const trainingListData = ref([])
const baseTrainingCoachData = ref([])

// mounted生命周期
onMounted(async () => {
  detailsId.value = route.query.id
  //TODO 详情、教练信息、跟练进度相关信息
  //TODO 相关联的接口： 小节列表、目录、问答（我的、全部）、笔记（我的全部）
  // 获取训练信息 - 详情
  await getTrainingDetailsData()
  // 获取训练教练信息
  await getTrainingCoachesData()
  // 获取本节课的训练计划
  // await getClassPlan()
  // 获取训练目录
  await getTrainingListData()
  // 获取当前训练的跟练情况 
  if(await isLogin()){
    await getTrainingWorkoutData()
  }

  store.setLearingDataes({
    trainingDetailsData:baseDetailsData.value, // 训练信息
    coachData:baseTrainingCoachData.value, // 教练信息
    planData: planData.value // 跟练计划信息
  })
});

/** 方法定义 **/
// 获取详情数据
const getTrainingDetailsData = async () => {
  await getTrainingDetails(detailsId.value)
    .then((res) => {
      if (res.code === 200) {
        baseDetailsData.value = res.data
      } else {
        ElMessage({
          message:res.data.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "请求出错！",
        type: 'error'
      });
    });
};

// 获取训练教练详情数据
const getTrainingCoachesData = async () => {
  await getTrainingCoaches(detailsId.value)
    .then((res) => {
      if (res.code == 200) {
        // 过滤可展示项
        const data = res.data.filter(n => n.isShow);
        baseTrainingCoachData.value = data
        let catchArr = []
        // 数据重组 展示幻灯片用
        data.forEach((n, i) => {
          if(catchArr.length == 0){
            catchArr.push([n])
          } else {
            const lastTag = catchArr.at(-1);
            if(lastTag.length == 2){
              catchArr.push([n])
            } else {
              lastTag.push(n)
              catchArr.push([n])
            }
          }
        })
        baseTrainingCoach.value = catchArr
      } else {
        ElMessage({
          message:res.data.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "请求出错！",
        type: 'error'
      });
    });
};

// 获取训练目录
const getTrainingListData = async () => {
  await getTrainingList(detailsId.value)
    .then((res) => {
      if (res.code == 200) {
        trainingListData.value = res.data
      } else {
        ElMessage({
          message:res.data.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练目录数据请求出错！",
        type: 'error'
      });
    });
};
// table切换 当前展示信息 训练介绍、训练目录
const changeTable = id => {
  actId.value = id
  switch (id) {
    case 2 : {
      break;
    } 
    case 3 : {
      break;
    } 
    case 4 : {
      break;
    } 
  }
}

//收藏
const collectionHandle = () => {
  isCollection.value = !isCollection.value
}
const isSignUp = ref(false)
// 立即报名
const signUpHandle = async () => {
  // 校验是否登录
  if(!validation()){
    return;
  }
  // 尝试报名
  await enrolledFreeTraining(detailsId.value)
  .then((res) => {
    if (res.code === 200) {
      ElMessage({
        message:'报名成功',
        type: 'success'
      });
      isSignUp.value = true
    } else {
      ElMessage({
        message:res.data.msg,
        type: 'error'
      });
    }
  })
  .catch(() => {
    ElMessage({
      message: "报名失败，请联系管理员",
      type: 'error'
    });
  });
}
// 马上跟练
const goWorkout = () => {
  router.push({path: '/workout', query: {id: detailsId.value}})
}

// 查询当前用户跟练的指定训练信息，返回null则代表没有购买
const planData = ref()
const getTrainingWorkoutData = async () => {
  await getTrainingWorkout(detailsId.value)
  .then((res) => {
    const { data } = res
    if (res.code === 200 && data) {
      isSignUp.value = true
      planData.value = data
    } else {
      isSignUp.value = false
      console.log(res.msg);
    }
  })
  .catch(() => {
    ElMessage({
      message: "用户训练信息数据请求出错！",
      type: 'error'
    });
  });
}
// 立即购买
const payHandle = () => {
  if(!validation()){
    return;
  }
  store.setOrderClassInfo([baseDetailsData.value])
  router.push({path: '/pay/settlement'})
}
// TODO 没有效验 松松那边没弄好 
// 未登录处理购买、加入购物车点击问题
const validation = () => {
  if ( !isLogin()) {
    ElMessageBox.confirm(
        `您还没有登录 请先去登录`,
        '确认登录',
        {
          confirmButtonText: '登录购买',
          cancelButtonText: '继续浏览',
          type: 'warning',
        }
      )
      .then(() => {
        router.push({path:'/login'})
      })
    return false;
  }
  return true;
}

// 加入购物车
const addCarts = () => {
  if(!validation()){
    return;
  }
  putCarts({trainingId: detailsId.value})
  .then((res) => {
    if (res.code === 200) {
     ElMessage({
        message:'已加入购物车',
        type: 'success'
      });
    } else {
      ElMessage({
        message:res.msg,
        type: 'error'
      });
    }
  })
  .catch(() => {
    ElMessage({
      message: "添加购物车请求出错！",
      type: 'error'
    });
  });
}
</script>
<style lang="scss" src="./index.scss"> </style>

