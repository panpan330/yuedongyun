<!-- 我的商城券 - 使用说明 -->
<template>
  <div class="voucherWrapperExplain">
    <div class="personalCards">
        <BreadCrumb></BreadCrumb>
        <div class="title">商城券使用说明</div>
        <div class="cont">
          <p class="tit">1.怎样获取商城券？</p>
          <p>悦动云会不定期推出训练内容、训练营和运动商城活动，通过活动可以领取商城券。</p>
          <p class="tit">2.怎样使用商城券？</p>
          <p>先领取一张商城券，然后在对应的训练内容或运动商品页面点击“立即购买”，在支付页面中选择需要使用的商城券。使用后，结算金额会自动变化。</p>
          <p class="tit">3.如果商城券面额大于订单金额，可以使用吗？</p>
          <p>不可以，商城券面额≤订单金额时才能使用，购买时最低需支付0.02元。</p>
          <p class="tit">4.每个人可以领取多少张商城券？</p>
          <p>每个人可以领取多张商城券，但同一活动下相同的商城券，一个用户只能领取一张。</p>
        </div>
    </div>
  </div>
</template>
<script setup>

/** 数据导入 **/
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getMyVoucher } from "@/api/training.js";
import { useRoute } from "vue-router";
import { dataCacheStore } from "@/store"

// 组件导入
import BreadCrumb from './components/BreadCrumb.vue'

const route = useRoute()
const store = dataCacheStore()

const tableBar = [
  {id: 1, name: '未使用'}, 
  {id: 2, name: '已使用'}, 
  {id: 3, name: '已过期'},
]

// tab切换
const actId = ref(1)
const changeTable = id => {
  actId.value = id
  getMyVoucherData()
}

// mounted生命周期
onMounted(async () => {
 getMyVoucherData()
});

/** 方法定义 **/
// 我的优惠券数据获取
const myVoucher = ref([])
const getMyVoucherData =  async () => {
  const params = {
    status: actId.value,
    pageNo: 1,
    pageSize:1000
  }
  await getMyVoucher(params)
    .then((res) => {
      if (res.code == 200 ){
        myVoucher.value = res.data.list
      } else {
        ElMessage({
        message: res.msg,
        type: 'error'
      });
      }
    })
    .catch(() => {
      ElMessage({
          message: "商城券列表请求失败！",
        type: 'error'
      });
    });
}

</script>
<style lang="scss" src="./index.scss"> </style>
