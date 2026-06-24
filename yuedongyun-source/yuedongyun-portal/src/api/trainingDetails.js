import request from "@/utils/request.js"
// 训练详情页面接口
const TRAINING_API_PREFIX = "/cs"
const WORKOUT_API_PREFIX = "/ls"
// 训练分类
export const getTrainingDetails = (id) =>
	request({
		url: `${TRAINING_API_PREFIX}/trainings/baseInfo/${id}`,
		method: 'get',
	})
// 查询训练教练
export const getTrainingCoaches = (id) =>
	request({
		url: `${TRAINING_API_PREFIX}/trainings/coaches/${id}`,
		method: 'get',
	})
// 查询训练目录
export const getTrainingList = (id) =>
	request({
		url: `${TRAINING_API_PREFIX}/trainings/outlines/${id}`,
		method: 'get'
	})	
// 获取训练章节 下拉数据展示
export const getTrainingOutlines = (id) =>
request({
	url: `${TRAINING_API_PREFIX}/trainings/outlines/${id}`,
	method: 'get'
})	
// 获取训练小节 - 问答详情使用
export const getTrainingPhases = (id) =>
	request({
		url: `${TRAINING_API_PREFIX}/trainings/outlines/index/list/${id}`,
		method: 'get'
	})	

// 问答相关
// 获取问答列表-全部
export const getAskList = (params) =>
	request({
		url: `${WORKOUT_API_PREFIX}/questions/page`,
		method: 'get',
		params,
	})	

// 新增提问
export const postQuestions = (params) =>
	request({
		url: `${WORKOUT_API_PREFIX}/questions`,
		method: 'post',
		data:params,
	})	
// 获取问题详情
export const getQuestionsDetails = (id) =>
request({
	url: `${WORKOUT_API_PREFIX}/questions/${id}`,
	method: 'get',
})	
// 编辑提问
export const putQuestions = (params) =>
	request({
		url: `${WORKOUT_API_PREFIX}/questions/${id}`,
		method: 'put',
		params,
	})			
// 删除提问
export const delQuestions = (id) =>
	request({
		url: `${WORKOUT_API_PREFIX}/questions/${id}`,
		method: 'delete',
	})		
// 回复
export const postAnswers = params =>
	request({
		url: `${WORKOUT_API_PREFIX}/replies`,
		method: 'post',
		data:params
	})			

// 分页查询回复、回答列表
export const getReply = params =>
request({
	url: `${WORKOUT_API_PREFIX}/replies/page`,
	method: 'get',
	params
})

// 点赞接口
export const putLiked = params =>
request({
	url: `/rs/likes`,
	method: 'post',
	data: params
})
