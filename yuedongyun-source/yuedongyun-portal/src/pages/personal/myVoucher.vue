<!-- 我的商城券 -->
<template>
  <div class="voucherWrapper">
    <div class="personalCards">
      <div class="headTop fx-sb">
        <CardsTitle class="marg-bt-20" title="我的商城券"/>
        <div class="fx">
          <span class="font-bt ft-14" @click="() => dialogVisible = true">兑换商城券&gt;</span>
          <span @click="() => $router.push({path: '/personal/main/myVoucherExplain'})"
                class="font-bt ft-14">商城券使用说明&gt;</span>
        </div>
      </div>
      <div>
        <TableSwitchBar :data="tableBar" @changeTable="changeTable"></TableSwitchBar>
      </div>
      <div class="myVoucherItems">
        <VoucherCards v-if="myVoucher.length > 0" :data="myVoucher" :type="actId"></VoucherCards>
        <div v-else class="nodata">
          <Empty></Empty>
        </div>
      </div>
    </div>
    <el-dialog
        v-model="dialogVisible"
        title="兑换商城券"
        width="30%"
    >
      <div class="exchangeVoucher fx"><span>商城券码：</span>
        <el-input type="text" v-model="inputVal" placeholder="请输入兑换码"/>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <div class="bt bt-grey" @click="dialogVisible = false">取消</div>
          <div class="bt" type="primary" @click="exchangeVoucherHandle">兑换</div>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>

/** 数据导入 **/
import {onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import {getMyVoucher, exchangeVoucher, formatRule} from "@/api/training.js";
import {useRoute} from "vue-router";
import {dataCacheStore} from "@/store"

// 组件导入
import CardsTitle from "./components/CardsTitle.vue";
import TableSwitchBar from "./components/TableSwitch.vue";
import VoucherCards from "@/components/VoucherCards.vue";
import Empty from "@/components/Empty.vue";

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
const getMyVoucherData = async () => {
  const params = {
    status: actId.value,
    pageNo: 1,
    pageSize: 1000
  }
  await getMyVoucher(params)
      .then((res) => {
        if (res.code == 200) {
          res.data.list.forEach(d => d.rule = formatRule(d));
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

// 兑换商城券
const dialogVisible = ref(false)
const inputVal = ref('')

const exchangeVoucherHandle = async () => {
  if (inputVal.value == "") {
    ElMessage({message: '请输入正确的商城券码'});
    return false
  }
  const params = {code: inputVal.value}
  // TODO 接口404
  await exchangeVoucher(params)
      .then((res) => {
        if (res.code == 200) {
          ElMessage({
            message: '商城券兑换成功',
            type: 'success'
          });
          dialogVisible.value = false
          getMyVoucherData();
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
<style lang="scss" src="./index.scss"></style>
