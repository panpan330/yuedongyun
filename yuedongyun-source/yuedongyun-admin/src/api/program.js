import request from "@/utils/request.js";
// 获取训练分类列表
export const getProgramType = (params) =>
  request({
    url: `/cs/categorys/list`,
    method: "get",
    params,
  });
// 获取详情
export const getDetails = (id) =>
  request({
    url: `/cs/categorys/${id}`,
    method: "get",
  });
// 新增分类
export const addProgramType = (params) =>
  request({
    url: `/cs/categorys/add`,
    method: "post",
    data: params,
  });
// 编辑分类
export const editProgramType = (params) =>
  request({
    url: `/cs/categorys/update`,
    method: "put",
    data: params,
  });
// 训练分类停用或启用
export const editProgramStatus = (params) =>
  request({
    url: `/cs/categorys/disableOrEnable`,
    method: "put",
    data: params,
  });
// 删除分类信息
export const deleteType = (id) =>
  request({
    url: `/cs/categorys/${id}`,
    method: "delete",
  });
// 删除分类信息
export const deleteTrainings = (id) =>
  request({
    url: `/cs/trainings/delete/${id}`,
    method: "delete",
  });
// 获取训练管理列表
export const getTrainingsPage = (params) =>
  request({
    url: `/cs/trainings/page`,
    method: "get",
    params,
  });
// 获取训练管理列表详情
export const getTrainingsDetail = (id) =>
  request({
    url: `/cs/trainings/baseInfo/${id}?see=0`,
    method: "get",
  });
// 获取训练详情
export const getTrainingDetail = (id) =>
  request({
    url: `/cs/trainings/baseInfo/${id}?see=1`,
    method: "get",
  });
// 保存训练基本信息
export const baseInfoSave = (params) =>
  request({
    url: `/cs/trainings/baseInfo/save`,
    method: "post",
    data: params,
  });
// 获取小节或练习中的题目
export const getTrainingsOutline = (params) =>
  request({
    url: `/cs/trainings/outlines/${params.id}?see=0`,
    method: "get",
    params,
  });
// 详情页获取小节或练习中的题目
export const getTrainingOutline = (params) =>
  request({
    url: `/cs/trainings/outlines/${params.id}?see=1`,
    method: "get",
    params,
  });
// 获取训练详情页训练目录
export const gettrainingsListData = (id) =>
  request({
    url: `/cs/trainings/outlines/${id}?see=1&withPractice=0`,
    method: "get",
  });
// 获取训练小节内的题目列表
export const getAssessments = (params) =>
request({
  url: `/cs/trainings/assessments/get/${params.id}?see=0`,
  method: "get",
  params,
});
// 获取训练目录小节中的题目
export const getAssessmentsList = (Id) =>
request({
  url: `/es/questions/listOfBiz?bizId=${id}`,
  method: "get",
});
// 保存题目
export const baseOutlineSave = (params) =>
  request({
    url: `/cs/trainings/outlines/save/${params.id}/${params.step}`,
    method: "post",
    data: params.datas,
  });
// 添加阶段测试
export const addTopic = () =>
  request({
    url: `/cs/trainings/generator`,
    method: "get"
  });
// 保存训练视频
export const baseVideoSave = (params) =>
  request({
    url: `/cs/trainings/media/save/${params.id}`,
    method: "post",
    data: params.datas,
  });
// 保存小节或练习中的题目
export const baseAssessmentSave = (params) =>
  request({
    url: `/cs/trainings/assessments/save/${params.id}`,
    method: "post",
    data: params.datas,
  });
// 获取教练列表
export const getCoaches = (params) =>
  request({
    url: `/cs/trainings/coaches/${params.id}?see=0`,
    method: "get",
    params,
  });
// 获取详情页教练信息
export const getTrainingCoaches = (id) =>
  request({
    url: `/cs/trainings/coaches/${id}?see=0`,
    method: "get",
  });
    // 获取详情教练信息
    export const gettrainingCoach = (id) =>
    request({
      url: `/cs/trainings/coaches/${id}?see=1`,
      method: "get",
    });
// 保存训练教练接口
export const baseCoachesSave = (params) =>
  request({
    url: `/cs/trainings/coaches/save`,
    method: "post",
    data: params,
  });
// 训练上架
export const baseUpShelf = (params) =>
  request({
    url: `/cs/trainings/upShelf`,
    method: "post",
    data: params,
  });
// 校验训练是否可以上架
export const baseBeforeUpShelf = (id) =>
  request({
    url: `/cs/trainings/checkBeforeUpShelf/${id}`,
    method: "get",
  });
// 训练下架
export const baseDownShelf = (params) =>
  request({
    url: `/cs/trainings/downShelf`,
    method: "post",
    data: params,
  });
// 训练名称是否存在校验
export const setTrainingsName = (params) =>
request({
  url: `/cs/trainings/checkName`,
  method: "get",
  params
});
