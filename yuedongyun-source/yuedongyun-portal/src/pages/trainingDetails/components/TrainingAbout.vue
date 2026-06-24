<!-- 训练介绍 -->
<template>
  <div class="trainingAbout">
    <div class="title">训练简介</div>
    <div class="marg-bt-20">{{baseDetailsData.introduce}}</div>
    <div class="metaCards">
      <div class="metaCard">
        <div class="metaLabel">训练难度</div>
        <div class="metaValue">{{ formatDifficulty(baseDetailsData.difficulty) }}</div>
      </div>
      <div class="metaCard">
        <div class="metaLabel">重点部位</div>
        <div class="metaValue">{{ baseDetailsData.trainPart || "全身训练" }}</div>
      </div>
      <div class="metaCard">
        <div class="metaLabel">预计消耗</div>
        <div class="metaValue">{{ baseDetailsData.calorieBurn || 0 }} kcal</div>
      </div>
    </div>
    <div class="title">适合人群</div>
    <div class="marg-bt-20">{{baseDetailsData.usePeople}}</div>
    <div class="title">教练团队</div>
    <div class="classCoach">
      <div class="navigat">
        <div class="prev"><span class="iconfont zhy-a-shouqi2x"></span></div>
        <div class="next"><span class="iconfont zhy-a-shouqi2x"></span></div>  
      </div>
      <swiper
          :centeredSlides="true"
          :pagination="{
            clickable: true,
          }"
          :navigation="{nextEl: '.next', prevEl: '.prev',}"
          :modules="[ Navigation]"
          :space-between="20"
          :controller="{ control: secondSwiper }"
          >
          <swiper-slide
            class="swiper-slide"
            v-for="(item, i) in baseTrainingCoach"
            :key="i"
          >
          <div class="cards fx">
            <div class="coachInfo" v-for="(it, ind) in item" :key="ind">
              <div class="teach">
                <img :src="it.icon || '/img-tx/default-user-icon.jpg'" alt="">
                <div>
                  <div class="name">{{it.name}}</div> 
                  <div>{{it.job}}</div>
                </div>
              </div>
              <div class="about">
                {{it.introduce}}
              </div>
            </div>
          </div>
          </swiper-slide>
        </swiper>
    </div>
    <div class="title">训练详情</div>
    <div class="marg-bt-20">{{baseDetailsData.detail}}</div>
  </div>
</template>
<script setup>
import { ref } from 'vue';
// 教练信息轮播插件
import { Swiper, SwiperSlide } from "swiper/vue";
import {  Navigation } from "swiper";
import "swiper/css";
import "swiper/css/navigation";
// 引入父级传参
defineProps({
  baseDetailsData:{
    type: Object,
    default:{}
  },
  baseTrainingCoach:{
    type: Object,
    default:{}
  }
})
const secondSwiper = ref(null);
const difficultyLabelMap = {
  1: "入门",
  2: "进阶",
  3: "高阶",
}
const formatDifficulty = (value) => difficultyLabelMap[value] || "未设置";
</script>
<style lang="scss" scoped>
.trainingAbout{
    margin-top: 40px;
    font-weight: 400;
    font-size: 14px;
    line-height: 30px;
    .title{
        font-weight: 600;
        font-size: 20px; 
        margin: 10px 0;
    }
    .metaCards{
        display: flex;
        gap: 16px;
        margin: 16px 0 24px;
        .metaCard{
            flex: 1;
            min-width: 0;
            background: #f7fbff;
            border-radius: 8px;
            padding: 16px 20px;
            .metaLabel{
                color: #7f8ea3;
                font-size: 13px;
                line-height: 22px;
            }
            .metaValue{
                color: #1f2d3d;
                font-size: 18px;
                font-weight: 600;
                line-height: 28px;
                margin-top: 4px;
            }
        }
    }
    .classCoach{
        position: relative;
        // width: calc(80vw - 425px);
        margin-bottom: 30px;
        left: 50%;
        transform: translate(-50%);
        width: 795px;
        max-width: 1015px;
        min-width: 795px; 
        margin-top: 20px;
        .coachInfo{
            height: 255px;
            overflow: hidden;
            width: 50%;
            background-color: var(--color-background5);
            padding: 20px 30px;
            margin-right: 20px;
            border-radius: 8px;
            .teach{
                display: flex;
                margin-bottom: 10px;
                img{
                    width: 64px;
                    height: 64px;
                    border-radius: 64px;
                    margin-right: 15px;
                }
                .name{
                    font-weight: 600;
                }
            }
            .about{
                font-size: 14px;
            }
            &:last-child{
                margin-right: 0;
            }
        }
    }
    .navigat{
      position: absolute;
      top: -40px;
      right: 0;
      .next, .prev{
        position: relative;
        display: inline-block;
        width: 21px;
        height: 21px;
        border-radius: 21px;
        margin-left: 17px;
        box-shadow: 0px 0px 10px rgba($color: #d3d3d3, $alpha: 1.0);
        cursor: pointer;
        color: var(--color-main);
        span{
          position: relative;
          display: inline-block;
          left: 1px;
          top: -5px;
          width: 20px;
          height: 20px;
          font-size: 12px;
          text-align: center;
        }
      }
      .prev{
        transform: rotate(180deg);
      }
      .swiper-button-disabled{
        color:#e1eaf6;
      }
    }
}

</style>
