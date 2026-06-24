<!-- 训练目录模块 -->
<template>
      <div class="trainingList">
        <el-collapse v-model="activeNames" accordion @change="handleChange">
        <el-collapse-item v-for="(item, index) in trainingListData" :key="index">
          <template v-slot:title>
            <div class="titText">
              <span class="icon" v-if="item.sessions.length > 0"></span>
              <div class="textL padl">
                <span
                  ><span v-if="index + 1 > 9">{{ index + 1 }}</span
                  ><span v-else>{{ "0" + (index + 1) }}</span></span
                >
                <span v-if="!item.isEdit"
                  >{{ item.name }}</span>
                <el-input
                  v-else
                  v-model="item.name"
                  @blur="handleBlur(item, i)"
                ></el-input>
              </div>
              <div class="textR">
                <span class="textForbidden" aria-disabled="true"
                  >章排序</span
                ><span class="textForbidden"
                  >添加小节</span
                ><span class="textForbidden"
                  >删除本章</span
                >
              </div>
            </div>
          </template>
          <div class="itemCon" v-if="item.sessions.length > 0">
            <div class="headTitle">
              <span>序号</span>
              <span class="padl">小节名称</span>
              <span>排序</span>
              <span>操作</span>
            </div>
            <div class="item">
              <ul>
                <li v-for="(val, i) in item.sessions" :key="i">
                  <div class="leftLine"></div>
                  <div class="con">
                    <!-- 序号 -->
                    <div>
                      <span v-if="i + 1 > 9">{{ i + 1 }}</span
                      ><span v-else>{{ "0" + (i + 1) }}</span>
                    </div>

                    <div class="padl">
                      <span v-if="!val.isEdit"
                        >{{ val.name }}</span>
                      <el-input
                        :autofocus="i === editActive"
                        v-else
                        v-model="val.name"
                      ></el-input>
                    </div>
                    <div class="sortIcon">
                      <span
                        class="upforbid"
                      ></span>
                      <span
                        class="downforbid "
                      ></span>
                    </div>
                    <div
                      class="textForbidden"
                    >
                      删除本节
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
</template>
<script setup>
import { ref } from "vue";

// 引入父级传参
defineProps({
  trainingListData:{
    type: Object,
    default:{}
  },
})
const activeNames = ref([0])
const handleChange = (val) => {
}
</script>

<style src="./../index.scss" lang="scss" scoped></style>
<style lang="scss" scoped>
// .trainingList{
//   margin-top: 40px;
// }
// .trainingList .titText .textL span{
//   font-size: 16px;
//   color: #332929;
// }
// .headTitle{
//   color: #332929;
// }
.textR{
  font-size: 14px;
  font-family: PingFangSC-Regular;
  font-weight: 400;
}
.paixu{
  display: inline-block;
  width: 21px;
  height: 20px;
  margin: 0 8px;
}
</style>

