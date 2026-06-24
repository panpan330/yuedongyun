<!-- 训练搜索页 -->
<template>
  <div class="trainingSearchWrapper">
    <div class="container searchBar">
      <div class="result" v-if="isLogin() && isShow && searchParams.keyword">
        <span class="searchKey">
          关键词：<em>{{ searchParams.keyword }}</em>
          <i class="close iconfont zhy-btn_qingchu1" @click="clearSearchKey"></i>
        </span>
        共找到 <em>{{ count }}</em> 项与 <em>{{ searchParams.keyword }}</em> 相关的训练内容
      </div>

      <div class="title">全部训练</div>
      <SearchKey :data="searchType" @searchKey="searchKey" :active="activeId" :key="activeId"></SearchKey>
      <SearchKey :data="searchCost" @searchKey="searchKey"></SearchKey>
      <SearchKey :data="searchDifficulty" @searchKey="searchKey"></SearchKey>
      <SearchKey :data="searchTrainPart" @searchKey="searchKey"></SearchKey>
    </div>

    <div class="searchContain bg-wt">
      <div class="container">
        <div class="fx-sb marg-bt-20">
          <SortBar :data="sortBar" @sortHandle="sortHandle"></SortBar>
          <div class="pageAction fx" v-if="count > 0">
            <img
              src="@/assets/page_act.png"
              class="iconTurn"
              v-if="page > 1"
              @click="pagesHandle('reduce')"
              alt=""
            />
            <img src="@/assets/page_act_nor.png" v-if="page === 1" alt="" />
            <span v-if="count > 0"><em>{{ page }}</em> / {{ Math.ceil(count / searchParams.pageSize) }}</span>
            <img
              src="@/assets/page_act.png"
              v-if="page < Math.ceil(count / searchParams.pageSize)"
              @click="pagesHandle('add')"
              alt=""
            />
            <img
              src="@/assets/page_act_nor.png"
              class="iconTurn"
              v-if="page === Math.ceil(count / searchParams.pageSize)"
              alt=""
            />
          </div>
        </div>

        <div class="content fx-wp" v-if="count > 0">
          <TrainingCards
            type="search"
            class="items marg-bt-20"
            v-for="(item, index) in searchResultData"
            :data="item"
            :key="index"
          ></TrainingCards>
        </div>

        <div class="content fx-ct noData" v-else>搜索结果为空，请调整筛选条件后重试</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watchEffect } from "vue";
import { ElMessage } from "element-plus";
import { getTrainingCategorys, trainingSearch } from "@/api/training.js";
import SearchKey from "./components/SearchKey.vue";
import SortBar from "./components/SortBar.vue";
import TrainingCards from "@/components/TrainingCards.vue";
import { useRoute } from "vue-router";
import { isLogin, dataCacheStore } from "@/store";

const dataCache = dataCacheStore();
const route = useRoute();

const searchType = ref({
  subKey: "categoryIdLv1",
  title: "训练分类",
  searchKeys: [{ id: "all", name: "全部" }, ...(dataCache.getTrainingClassDataes || [])],
});
const searchCost = ref({
  subKey: "free",
  title: "收费方式",
  searchKeys: [
    { id: "all", name: "全部" },
    { id: "1", name: "免费" },
    { id: "0", name: "付费" },
  ],
});
const searchDifficulty = ref({
  subKey: "difficulty",
  title: "训练难度",
  searchKeys: [
    { id: "all", name: "全部" },
    { id: "1", name: "入门" },
    { id: "2", name: "进阶" },
    { id: "3", name: "高阶" },
  ],
});
const searchTrainPart = ref({
  subKey: "trainPart",
  title: "训练部位",
  searchKeys: [
    { id: "all", name: "全部" },
    { id: "全身", name: "全身" },
    { id: "臀腿", name: "臀腿" },
    { id: "胸肩", name: "胸肩" },
    { id: "核心", name: "核心" },
    { id: "拉伸", name: "拉伸" },
  ],
});
const sortBar = ref([
  { key: "推荐", value: "all" },
  { key: "最新", value: "publishTime" },
  { key: "最热", value: "sold" },
]);

const searchParams = ref({
  keyword: "",
  categoryIdLv1: "",
  categoryIdLv2: "",
  categoryIdLv3: "",
  difficulty: undefined,
  trainPart: undefined,
  free: undefined,
  pageNo: 1,
  pageSize: 12,
});

const searchResultData = ref([]);
const count = ref(0);
const page = ref(1);
const isShow = ref(true);
const fullPath = ref(route.fullPath);
const activeId = ref("all");

onMounted(() => {
  searchParams.value.keyword = dataCache.getSearchKey;
  if (route.query.type) {
    searchParams.value[route.query.type] = route.query.id;
  }
  getTrainingCategoryData();
  search();
});

onUnmounted(() => {
  dataCache.setSearchKey("");
});

watchEffect(() => {
  if (dataCache.getSearchKey !== "") {
    isShow.value = true;
  }
  if (route.fullPath !== fullPath.value) {
    fullPath.value = route.fullPath;
    if (route.query.type === "categoryIdLv1") {
      searchParams.value.categoryIdLv1 = route.query.id;
    }
    searchParams.value.categoryIdLv2 = route.query.type === "categoryIdLv2" ? route.query.id : undefined;
    searchParams.value.categoryIdLv3 = route.query.type === "categoryIdLv3" ? route.query.id : undefined;
    activeId.value = route.query.id;
    search();
  }
});

watchEffect(() => {
  if (searchParams.value.keyword !== dataCache.getSearchKey) {
    searchParams.value.keyword = dataCache.getSearchKey;
    initPage();
    search();
  }
});

const getTrainingCategoryData = async () => {
  try {
    const res = await getTrainingCategorys();
    if (res.code === 200) {
      searchType.value.searchKeys = [{ id: "all", name: "全部" }, ...(res.data || [])];
      return;
    }
    ElMessage.error(res.msg || res.meg || "训练分类加载失败");
  } catch (error) {
    ElMessage.error("训练分类请求出错，请稍后重试");
  }
};

const clearSearchKey = () => {
  searchParams.value.keyword = "";
  isShow.value = false;
  dataCache.setSearchKey("");
  initPage();
  search();
};

const searchKey = async (item) => {
  if (!isLogin()) {
    isShow.value = false;
    dataCache.setSearchKey("");
  }

  if (item.key === "difficulty") {
    searchParams.value.difficulty = item.value !== "all" ? Number(item.value) : undefined;
  }
  if (item.key === "trainPart") {
    searchParams.value.trainPart = item.value !== "all" ? item.value : undefined;
  }
  if (item.key === "free") {
    searchParams.value.free = item.value !== "all" ? Boolean(+item.value) : undefined;
  }
  if (item.key === "categoryIdLv1") {
    searchParams.value.categoryIdLv1 = item.value !== "all" ? item.value : undefined;
    searchParams.value.categoryIdLv2 = undefined;
    searchParams.value.categoryIdLv3 = undefined;
  }

  initPage();
  search();
};

const sortHandle = (item) => {
  searchParams.value.sortBy = item !== "all" ? item : undefined;
  initPage();
  search();
};

const initPage = () => {
  page.value = 1;
  searchParams.value.pageNo = page.value;
};

const pagesHandle = (type) => {
  page.value = type === "add" ? page.value + 1 : page.value - 1;
  searchParams.value.pageNo = page.value;
  search();
};

const search = async () => {
  try {
    const params = JSON.parse(JSON.stringify({ ...searchParams.value }));
    const res = await trainingSearch(params);
    if (res.code === 200) {
      searchResultData.value = res.data.list || [];
      count.value = Number(res.data.total || 0);
      return;
    }
      ElMessage.error(res.msg || res.meg || "训练搜索失败");
  } catch (error) {
    ElMessage.error("训练请求出错，请稍后重试");
  }
};
</script>

<style lang="scss" src="./index.scss"></style>

