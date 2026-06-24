import request from "@/utils/request.js"

// 训练分类
export const getTrainingCategorys = (params) =>
	request({
		url: `/cs/categorys/all`,
		method: 'get',
		params
	})
 