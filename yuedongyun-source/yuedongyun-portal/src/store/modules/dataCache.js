import { defineStore } from 'pinia';

export const dataCacheStore = defineStore('notification', {
  state: () => ({
    trainingClassDataes: [], // 存储训练分类
    searchKey: '', // 搜索关键词存储
    askDetails: '22',
    learingDataes:{ // 跟练相关信息存储 
      trainingDetailsData:{}, //训练详情的信息 - 打开训练详情时写入 
      coachData:{}, // 教练信息 - 打开训练详情时写入
      classSessionMap:{},// 小节信息
      trainingOutlineLogs:{}, // 目录信息
      planData:{} // 训练计划信息 
    },
    currentPlayData:{}, // 视频的当前播放数据存储
    orderClassInfo:{}, //订单的训练信息
    currentTrainingInfo:{}, // 当前浏览的训练信息
    myLearnClassInfo:{}, // 当前跟练训练的信息
  }),
  getters: {
    // 获取对应的state的值
    getAskDetails: state => state.askDetails,
    getSearchKey: state => state.searchKey,
    getLearingDataes: state => state.learingDataes,
    getCurrentPlayData: state => state.currentPlayData,
    getOrderClassInfo: state => state.orderClassInfo,
    getTrainingClassDataes: state => state.trainingClassDataes,
    getCurrentTrainingInfo: state => state.currentTrainingInfo,
    getMyLearnClassInfo: state => state.myLearnClassInfo
  },
  actions: {
    setCurrentTrainingInfo(data){
      this.currentTrainingInfo = data;
    },
    // 设置state对应的值
    setAskDetails(data) {
      this.askDetails = data;
    },
    setSearchKey(data) {
      this.searchKey = data;
    },
    setLearingDataes(data) {
      this.learingDataes = data;
    },
    setCurrentPlayData(data) {
      this.currentPlayData = data;
    },
    setOrderClassInfo(data){
      this.orderClassInfo = data;
    },
    setTrainingCategoryDataes(data){
      this.trainingClassDataes = data;
    },
    setMyLearnClassInfo(data){
      this.myLearnClassInfo = data;
    },
  },
  persist: {
    enabled: true,
    encryptionKey: 'data-catch',
  }
});

