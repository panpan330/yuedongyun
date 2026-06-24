import request from "@/utils/request.js";
// 获取会员列表
export const getMembers = (params) =>
  request({
    url: `/us/members/page`,
    method: "get",
    params,
  });

  // 修改会员密码
export const editMembersPassword = (params) =>
  request({
    url: `/us/members/password`,
    method: "put",
    data:params,
  });
