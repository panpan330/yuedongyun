import request from "@/utils/request.js";
// 获取列表
export const getCoachList = (params) =>
  request({
    url: `/us/coaches/page`,
    method: "get",
    params,
  });