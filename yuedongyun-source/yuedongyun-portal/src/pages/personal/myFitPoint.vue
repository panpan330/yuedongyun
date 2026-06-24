<!-- 我的积分 -->
<template>
  <div class="myFitPointWrapper">
    <div class="personalCards">
      <CardsTitle class="marg-bt-20" title="我的积分" />
      <div class="title"></div>
      <!-- 打卡日历 -->
      <Calendar @fitpointsSign="fitpointsSignHandle"></Calendar>
      <!-- 积分获取 -->
      <div class="listCont fx-sb">
        <div class="list">
          <div class="tit">获取积分</div>
          <div class="tab">
            <div class="item fx-sb" v-for="item in access" :key="item.type">
              <span>{{item.type}}</span>
              <span>{{item.fitpoints}}/{{item.maxFitPoint}}</span>
            </div>
          </div>
        </div>
        <div class="listRt">
            <div class="tit fx-sb">
              <span>卡路里赛季榜</span>
              <span class="more font-bt" @click="() => $router.push({path: 'myFitPointRanking', query:{rank: seasonsData.rank, fitpoints:seasonsData.fitpoints}})">更多&gt;</span>
            </div>
            <FitPointRankTab :data="seasonsData"></FitPointRankTab>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>

/** 数据导入 **/
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getSeasons, getTodayFitPoint, fitpointsSign } from "@/api/training.js";
import { useRoute } from "vue-router";
import { dataCacheStore } from "@/store"

// 组件导入
import CardsTitle from './components/CardsTitle.vue'
import Calendar from './components/Calendar.vue'
import FitPointRankTab from './components/FitPointRankTab.vue'

const route = useRoute()
const store = dataCacheStore()

const value = ref(new Date())

const access = ref([
  {value: 1, 'type': '跟练完成', fitpoints:0, maxFitPoint: 50},
  {value: 2, 'type': '每日签到', fitpoints:0, maxFitPoint: 2},
  {value: 3, 'type': '社区问答', fitpoints:0, maxFitPoint: 20},
  {value: 4, 'type': '训练笔记', fitpoints:0, maxFitPoint: 20},
  {value: 5, 'type': '训练评价', fitpoints:0, maxFitPoint: 999},
])

// 训练目录
const trainingListData = ref([])
const baseTrainingCoachData = ref([])

// mounted生命周期
onMounted(async () => {
 // 积分榜信息查�? getSeasonsData()
 // 积分获得记录
 getSignRecordsHandle()
});

/** 方法定义 **/

// 积分榜信息查�?const seasonsData = ref([])
const getSeasonsData = () => {
  getSeasons({season:0, pageNo: 1, pageSize: 10})
    .then((res) => {
      if (res.code == 200 ){
        console.log(3333, res.data)
        seasonsData.value = res.data
      } else {
        ElMessage({
          message: res.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "赛季榜请求失败！",
        type: 'error'
      });
    });
}
// 用户本日积分情况查询失败
const toadyFitPointData = ref()
const getSignRecordsHandle = async () => {
  await getTodayFitPoint()
    .then((res) => {
      if (res.code == 200 ){
        access.value.map(n => {
          res.data.forEach( val => {
            if (val.type == n.type){
              n.fitpoints = val.fitpoints
            }
          })
        })
        toadyFitPointData.value = res.data
      } else {
        ElMessage({
          message: res.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "用户本日积分情况查询失败",
        type: 'error'
      });
    });
}
// 打卡 - 返回积分
const fitpointsSignHandle = async () => {
  await fitpointsSign()
    .then((res) => {
      if (res.code == 200 ){
        ElMessage({
          message: '签到成功�?,
          type: 'success'
        });
      } else {
        ElMessage({
          message: res.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "赛季榜请求失败！",
        type: 'error'
      });
    });
}

</script>
<style lang="scss" src="./index.scss"> </style>
