import request from "@/utils/request.js";
// 获取训练分类列表
export const getTypeAll = (params) =>
  request({
    url: `/cs/categorys/all`,
    method: "get",
    params,
  });
// 获取训练分类列表
export const getTrainingsAll = (params) =>
  request({
    url: `/cs/trainings/simpleInfo/list`,
    method: "get",
    params,
  });
