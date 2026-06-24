<!-- 我的训练卡片 -->
<template>
  <div class="trainingCards fx-sb fx-ct">
    <div class="marg-rt-20">
      <img :src="data.trainingCoverUrl" alt="" @click="$router.push({ path: '/details/index', query: { id: data.trainingId } })" />
    </div>
    <div class="info fx-1">
      <div class="tit">{{ data.trainingName }}</div>
      <div>
        <span>有效期：</span>
        {{
          data.expireTime == null
            ? "永久有效"
            : new Date(data.createTime).toLocaleDateString() + " - " + new Date(data.expireTime).toLocaleDateString()
        }}
      </div>
      <div><span>已完成：</span><em>{{ data.learnedSessions }}</em> / {{ data.sessions }}</div>
      <div v-if="type === '1'"><span>正在跟练：</span>第{{ data.latestSessionIndex }}节 {{ data.latestSessionName }}</div>
    </div>
    <div class="btnCont">
      <div v-if="type === '1'" class="btn" @click="() => $router.push({ path: '/workout/index', query: { id: data.trainingId } })">
        <span class="bt bt-round">继续跟练</span>
      </div>
      <div
        v-if="type === '2' && data.status != 3"
        class="btn"
        @click="() => $router.push({ path: '/workout/index', query: { id: data.trainingId } })"
      >
        <span v-if="data.status == 0" class="bt bt-round">马上开练</span>
        <span v-if="data.status == 1" class="bt bt-round">继续跟练</span>
        <span v-if="data.status == 2" class="bt bt-round">重新开练</span>
      </div>
      <div v-if="type === '2' && data.status != 3 && data.planStatus == 0" class="btn" @click="planActive(data, 'add')">
        <span class="bt-grey bt-round">创建计划</span>
      </div>
      <div v-if="type === '2' && data.status != 3 && data.planStatus == 1" class="btn" @click="planActive(data, 'edit')">
        <span class="bt-grey bt-round">调整计划</span>
      </div>
      <div v-if="type === '2' && data.status == 3" class="btn" @click="planActive(data, 'del')">
        <span class="bt-grey bt-round">删除训练</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
  type: {
    type: String,
    default: "1",
  },
});

const emit = defineEmits(["planHandle"]);

const planActive = (data, type) => {
  emit("planHandle", { data, type });
};
</script>

<style lang="scss" scoped>
.trainingCards {
  img {
    width: 236px;
    height: 132px;
    border-radius: 8px;
  }

  .info {
    line-height: 30px;
    font-size: 14px;

    em {
      font-style: normal;
      color: var(--color-main);
    }

    span {
      display: inline-block;
      color: var(--color-font3);
      min-width: 85px;
    }

    .tit {
      font-size: 20px;
      font-weight: 500;
      line-height: 40px;
    }
  }

  .btnCont {
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;

    .btn {
      width: 114px;
      height: 40px;
      display: flex;
      align-items: center;
      margin: 10px 0;
    }
  }
}
</style>

