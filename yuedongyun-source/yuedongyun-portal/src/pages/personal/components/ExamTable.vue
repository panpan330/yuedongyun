<!-- 我的训练考核列表 -->
<template>
  <div class="trainingCards fx-1">
    <el-table class="table" :data="data">
      <el-table-column prop="trainingName" center label="训练">
        <template #default="scope">
          <div>{{ scope.row.trainingName }}</div>
        </template>
      </el-table-column>
      <el-table-column center prop="sessionName" label="小节" align="center" min-width="160" />
      <el-table-column prop="commitTime" align="center" label="提交时间" width="180">
        <template #default="scope">
          <span>{{ scope.row.commitTime || "--" }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="commitTime" align="center" label="类型" width="100">
        <template #default="scope">
          <span>{{ scope.row.type == 0 ? "自测" : "考核" }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="score" align="center" label="得分" width="60">
        <template #default="scope">
          <span>{{ scope.row.score || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template #default="scope">
          <div class="font-bt1" @click="() => $router.push({ path: 'myExamdetails', query: scope.row })">查看</div>
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
.trainingCards {
  margin-top: 20px;
  line-height: 30px;

  .table {
    border: solid 1px #ebeef5;
    border-bottom: none;
  }

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

