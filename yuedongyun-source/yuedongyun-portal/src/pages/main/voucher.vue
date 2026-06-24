<!-- 首页 - 选择优惠券 -->
<template>
  <div class="discountChoiceWrapper">
    <div class="container">
      <Breadcrumb data="商城券"></Breadcrumb>
    </div>
    <div class="voucherItems container bg-wt">
       <div class="mainVoucherCards fx" v-for="(item, index) in voucherData" :key="index">
          <div class="price ft-cl-wt" v-if="item.discountType == 4 || item.discountType == 1 || item.discountType == 3">
            <div>￥ <em>{{item.discountValue / 100}}</em></div>
            <div class="desc">{{item.rule}}</div>
          </div>
          <div class="price ft-cl-wt" v-if="item.discountType == 2 || item.discountType == 5">
            <div><em>{{item.discountValue / 10}}</em> 折</div>
            <div class="desc">{{item.rule}}</div>
          </div>
          <div class="info">
            <div class="tit">{{item.name}}</div>
        <div><em>适用范围：</em>{{item.specific ? '指定训练内容' : '全部训练内容'}}</div>
            <div><em>有效日期：</em> {{item.termDays ? `${item.termDays}天` : moment(item.termEndTime).format('YYYY-MM-DD hh:mm:ss')}}</div>
          </div>
          <div class="butCont fx-ct" v-if="item.received">
            <span  @click="() => $router.push('/search/index')" class="bt">去使用</span>
          </div>
          <div class="butCont fx-ct" v-else>
            <span v-if="item.available" @click="getVoucherData(item)" class="bt">立即领取</span>
            <span v-else class="bt bt-grey">已领完</span>
          </div>
       </div>
    </div>
  </div>
</template>
<script setup>
/** 数据导入 **/

import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import moment from 'moment';
import { getCollectableVoucher, getVoucher,formatRule } from "@/api/training.js";
import { isLogin } from '@/store'
// 组件导入
import Breadcrumb from "@/components/Breadcrumb.vue";
const voucherData = ref([]);

// mounted生命周期
onMounted(() => {
  // 获取可领优惠券（超值优惠券）
  getCollectableVoucherData()
});

/** 方法定义 **/
// 获取可领优惠券（超值优惠券）
const getCollectableVoucherData = async () => {
  await getCollectableVoucher()
  .then((res) => {
      if (res.code == 200) {
        res.data.forEach(d => d.rule = formatRule(d));
        voucherData.value = res.data;
      } else {
        ElMessage({
          message:res.data.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "商城券加载失败！",
        type: 'error'
      });
    });
}

// 优惠券领取
const getVoucherData = async (item) => {
  const id = item.id
  await getVoucher({id:id})
  .then((res) => {
      if (res.code == 200) {
        // 领取成功之后重新刷新列表
        getCollectableVoucherData()
        item.recieveStatus = 2
        ElMessage({
          message:'商城券领取成功!',
          type: 'success'
        });
      } else {
        ElMessage({
          message:res.data.msg,
          type: 'error'
        });
      }
    })
    .catch(() => {
      ElMessage({
        message: "商城券领取失败！",
        type: 'error'
      });
    });
}

</script>
<style lang="scss" src="./index.scss"> </style>

<style lang="scss"> 
.discountChoiceWrapper{
  .voucherItems{
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    padding: 50px 50px 20px 50px;
  }
  .mainVoucherCards{
    width: calc(50% - 20px);
    background: #FFFFFF;
    border: 1px solid #EEEEEE;
    border-left:none;
    border-radius: 8px;
    margin-bottom: 30px;
    margin-right: 40px;
    &:nth-child(2n){
      margin-right: 0;
    }
    .price{
      width: 167px;
      height: 150px;
      background:url('@/assets/bg_yhq1.png') center center no-repeat;
      background-size: cover;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      font-weight: 400;
      font-size: 14px;
      em{
        font-style: normal;
        font-family: PingFangSC-Semibold;
        font-weight: 600;
        font-size: 42px;
      }
      .desc{
        padding: 0 15px;
        font-size: 12px;
        text-align: center;
      }
    }
    .info{
      display: flex;
      padding-left: 30px;
      text-align: left;
      flex-direction: column;
      justify-content: center;
      font-size: 14px;
      flex: 1;
      line-height: 28px;
      .tit{
        font-weight: 500;
        font-size: 20px;  
        margin-bottom: 15px;
      }
      em{
        font-style: normal;
        color: var(--color-font3);
      }
    }
    .butCont{
      padding: 20px;
      span{
        display: inline-block;
        width: 104px;
        text-align: center;
        height: 40px;
        border-radius: 20px;
      }
    }
  }
}
</style>
