<!-- 训练展示卡片 - 首页、搜索页 -->
<template>
  <div class="trainingCards" @click="goDetails(data.id)">
    <div class="image">
      <img :src="data.coverUrl" alt="" />
    </div>
    <div class="pd-10">
      <div class="title marg-bt-10 ft-14" v-html="data.name"></div>

      <div class="ft-cl-des" v-if="type === 'default' || type === 'search'">
        <span>教练：</span> {{ data.coach || "教练团队" }}
      </div>

      <div class="ft-cl-des" v-if="type === 'default' || type === 'search'">
        {{ formatDifficulty(data.difficulty) }}
        <span v-if="data.trainPart"> · {{ data.trainPart }}</span>
        <span v-if="data.calorieBurn"> · 约{{ data.calorieBurn }} kcal</span>
      </div>

      <div class="ft-cl-des" v-if="type === 'default'">
        <span>共</span> {{ data.sessions || 0 }} <span>节</span>
      </div>

      <div class="ft-cl-des fx-sb" v-if="type === 'default'">
        <span>{{ data.sold || 0 }} 人正在训练</span>
        <span class="ft-16 ft-cl-err">{{ formatPrice(data.price) }}</span>
      </div>

      <div class="ft-cl-des fx-sb" v-if="type === 'search'">
        <span>共 {{ data.sessions || 0 }} 节<em> · </em>{{ data.sold || 0 }} 人正在训练</span>
        <span class="ft-16 ft-cl-err">{{ formatPrice(data.price) }}</span>
      </div>

      <div class="ft-cl-des" v-if="type === 'like'"><span>训练评分：</span> 4.9</div>

      <div class="ft-cl-des fx-sb" v-if="type === 'like'">
        <span>{{ data.sessions || 0 }} 次跟练</span>
        <span class="ft-16 ft-cl-err">{{ formatPrice(data.price) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import router from "../router";

defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
  type: {
    type: String,
    default: "default",
  },
});

const difficultyLabelMap = {
  1: "入门训练",
  2: "进阶训练",
  3: "高阶训练",
};

const formatDifficulty = (value) => difficultyLabelMap[value] || "训练";

const formatPrice = (price) => {
  if (!price || Number(price) === 0) {
    return "免费";
  }
  return `¥${(Number(price) / 100).toFixed(2)}`;
};

const goDetails = (id) => {
  router.push({ path: "/details", query: { id } });
};
</script>

<style lang="scss" scoped>
.trainingCards {
  position: relative;
  width: 23%;
  background: #ffffff;
  border: 1px solid #eeeeee;
  border-radius: 8px;
  font-size: 12px;
  line-height: 24px;
  cursor: pointer;

  &:hover {
    box-shadow: 0 4px 6px 2px rgba(108, 112, 118, 0.17);
    top: -3px;
  }

  .title {
    line-height: 22px;

    :deep(em) {
      font-style: normal;
      color: var(--color-main);
    }
  }

  .image {
    width: 100%;
    height: 160px;
    overflow: hidden;
    position: relative;

    img {
      width: 100%;
      border-radius: 8px 8px 0 0;
    }
  }

  em {
    position: relative;
    top: -1px;
    font-style: normal;
  }
}
</style>
