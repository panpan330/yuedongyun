<!-- 我的训练考核 -->
<template>
  <div class="myExamWrapper">
    <div v-if="myExamData != null" class="personalCards">
      <CardsTitle class="marg-bt-20" title="我的训练考核" />
      <div v-if="count == 0" class="nodata">
        <Empty />
      </div>
      <ExamTable v-if="count > 0" :data="myExamData" />
      <div v-if="count > 0" class="pageination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="count"
          class="mt-4"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getExamList } from "@/api/training.js";
import CardsTitle from "./components/CardsTitle.vue";
import ExamTable from "./components/ExamTable.vue";
import Empty from "@/components/Empty.vue";

const myExamData = ref(null);
const count = ref(0);
const params = reactive({
  pageNo: 1,
  pageSize: 10,
});

onMounted(() => {
  getExamListData();
});

const getExamListData = async () => {
  await getExamList(params)
    .then((res) => {
      if (res.code === 200 && res.data != null) {
        myExamData.value = res.data.list;
        count.value = Number(res.data.total);
      }
    })
    .catch(() => {
      ElMessage({
        message: "训练考核数据请求出错！",
        type: "error",
      });
    });
};

const handleSizeChange = (val) => {
  params.pageSize = val;
  getExamListData();
};

const handleCurrentChange = (val) => {
  params.pageNo = val;
  getExamListData();
};
</script>

<style lang="scss" src="./index.scss"></style>
