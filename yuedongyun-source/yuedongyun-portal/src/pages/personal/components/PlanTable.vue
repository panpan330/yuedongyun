<!-- 训练计划表 -->
<template>
  <div class="planTable fx-sb fx-1">
    <el-table :data="data" height="247" style="width: 100%">
      <el-table-column center label="训练">
        <template #default="scope">
          <div>{{ scope.row.trainingName }}</div>
        </template>
      </el-table-column>
      <el-table-column center label="本周进度" align="center" width="100">
        <template #default="scope">
          <div>{{ scope.row.weekLearnedSessions }}/{{ scope.row.weekFreq }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="address" align="center" label="总进度" width="100">
        <template #default="scope">
          <div>{{ scope.row.learnedSessions }}/{{ scope.row.sessions }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="address" align="center" label="最近跟练时间" width="180">
        <template #default="scope">
          <div>{{ scope.row.latestLearnTime }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template #default="scope">
          <div class="font-bt1" @click="() => $router.push({ path: '/workout/index', query: { id: scope.row.trainingId } })">
            去跟练
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
defineProps({
  data: {
    type: Array,
    default: () => [],
  },
});
</script>

<style lang="scss" scoped>
.planTable {
  margin-top: 30px;
  border: solid 1px #ebeef5;
  border-bottom: none;
  line-height: 30px;

  img {
    width: 236px;
    height: 132px;
    border-radius: 4px;
  }

  .info {
    line-height: 30px;
    font-size: 14px;

    .tit {
      font-size: 20px;
      font-weight: 500;
      line-height: 40px;
    }
  }
}
</style>

