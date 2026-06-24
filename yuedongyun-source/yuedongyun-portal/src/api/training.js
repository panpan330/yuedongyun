import request from "@/utils/request.js"
const SEARCH_API_PREFIX = "/ss"
const TRAINING_API_PREFIX = "/cs"
const WORKOUT_API_PREFIX = "/ls"
const MEDIA_API_PREFIX = "/ms"
const CAMPAIGN_API_PREFIX = "/prs"
const EXAM_API_PREFIX = "/es"
// 训练分类
export const getTrainingCategorys = (params) =>
	request({
		url: `${TRAINING_API_PREFIX}/categorys/all`,
		method: 'get',
		params
	})

// 获取训练推荐接口
export const getRecommendTrainingList = (type) =>
	request({
		url: `${SEARCH_API_PREFIX}/recommend/${type}`,
		method: 'get'
	})

// 获取训练列表 - 分类id 查询对应的列表 （倒序 十条）
export const getTrainingList = (id) =>
	request({
		url: `${SEARCH_API_PREFIX}/interests/${id}/trainings`,
		method: 'get'
	})

// 获取训练计划 getWorkoutPlan	
export const getWorkoutPlan = (id) =>
	request({
		url: `${WORKOUT_API_PREFIX}/plans`,
		method: 'get'
	})

// 查询当前用户跟练的指定训练信息，返回null则代表没有购买	
export const getTrainingWorkout = (trainingId) =>
request({
	url: `${WORKOUT_API_PREFIX}/sessions/${trainingId}`,
	method: 'get'
})	
// 训练搜索
export const trainingSearch = (params) =>
	request({
		url: `${SEARCH_API_PREFIX}/trainings/portal`,
		method: 'get',
		params
	})


// 跟练相关接口
export const getWorkoutTrainingDetails = (id) =>
	request({
		url: `${TRAINING_API_PREFIX}/trainings/${id}/outlinelogs`,
		method: 'get'
	})



// 兴趣接口

// 新增兴趣爱好
export const setInterests = (params) =>
	request({
		url: `${SEARCH_API_PREFIX}/interests`,
		method: 'post',
		data:params,
		params
	})	
// 查询我的兴趣爱好
export const getInterests = (params) =>
	request({
		url: `${SEARCH_API_PREFIX}/interests`,
		method: 'get',
		params
	})		

// 获取播放视频的授权签名
export const getMediasSignature = (params) =>
	request({
		url: `${MEDIA_API_PREFIX}/medias/signature/play`,
		method: 'get',
		params
	})	
// 优惠券 相关接口
// 格式化规则
export const formatRule = (d) => {
	let rule = "";
	let PER_PRICE_DISCOUNT = 1, RATE_DISCOUNT = 2,NO_THRESHOLD = 3, PRICE_DISCOUNT = 4
	switch (d.discountType) {
		case PER_PRICE_DISCOUNT:
			rule = `每满${d.thresholdAmount / 100}元减${d.discountValue / 100}元，不超过${d.maxDiscountAmount / 100}元`;
			break;
		case PRICE_DISCOUNT:
			rule = `满${d.thresholdAmount / 100}元减${d.discountValue / 100}元`;
			break;
		case NO_THRESHOLD:
			rule = `无门槛抵扣${d.discountValue / 100}元`;
			break;
		case RATE_DISCOUNT:
			rule = `满${ d.thresholdAmount / 100}元打${d.discountValue / 10}折，不超过${d.maxDiscountAmount / 100}元`
			break;
	}
	return rule;
}
// 可领优惠券（超值优惠券）
export const getCollectableVoucher = (params) =>
request({
	url: `${CAMPAIGN_API_PREFIX}/vouchers/list`,
	method: 'get',
	params
})		
// 我的优惠券（近一年）
export const getMyVoucher = (params) =>
request({
	url: `${CAMPAIGN_API_PREFIX}/user-vouchers/page`,
	method: 'get',
	params
})	
// 优惠券领取
export const getVoucher = (params) =>
request({
	url: `${CAMPAIGN_API_PREFIX}/user-vouchers/${params.id}/receive`,
	method: 'post'
})	
// 兑换码兑换优惠券
export const exchangeVoucher = (data) =>
request({
	url: `${CAMPAIGN_API_PREFIX}/user-vouchers/${data.code}/exchange`,
	method: 'post'
})	

// 训练计划管理接口

// 查询我的训练计划
export const getMysessions = () =>
request({
	url: `${WORKOUT_API_PREFIX}/sessions/page`,
	method: 'get',
})	
// 查询我正在跟练的训练
export const getMyWorkout = () =>
request({
	url: `${WORKOUT_API_PREFIX}/sessions/now`,
	method: 'get',
})

// 查询我的训练计划
export const getMyPlan = () =>
request({
	url: `${WORKOUT_API_PREFIX}/sessions/plans`,
	method: 'get',
})
/*
// 重新跟练训练
export const restartMySession = (trainingId) =>
	request({
		url: `${WORKOUT_API_PREFIX}/sessions/${trainingId}/restart`,
		method: 'PUT',
	})*/

// 报名免费训练
export const signUp = (trainingId) =>
request({
	url: `/ts/orders/freeTraining/${trainingId}`,
	method: 'psot',
})

// 将指定训练从训练计划移除
export const delMyTraining = (trainingId) =>
request({
	url: `${WORKOUT_API_PREFIX}/sessions/${trainingId}`,
	method: 'delete',
})

// 创建训练计划
export const creatPlans = (params) =>
request({
	url: `${WORKOUT_API_PREFIX}/sessions/plans`,
	method: 'post',
	data: params
})

// 考核相关
// 分页查询我的考核记录
export const getExamList = (params) =>
request({
	url: `${EXAM_API_PREFIX}/exams/page`,
	method: 'get',
	params
})
// 查询我的考核记录详情
export const getExamDetails = (id) =>
request({
	url: `${EXAM_API_PREFIX}/exams/${id}`,
	method: 'get',
})
// 提交考核答案，考核或测试提交时需要保存答案信息
export const submitExamRecords = (params) =>
request({
	url: `${EXAM_API_PREFIX}/exam-records/details`,
	method: 'post',
	data:params
})
// 新增考核记录，考核或测试开始时需要保存基本信息，返回记录id
export const addExamRecords = (params) =>
request({
	url: `${EXAM_API_PREFIX}/exam-records`,
	method: 'post',
	data:params
})

// 训练计划的信息

// 查询我的某个训练计划的跟练记录
export const getWorkoutLog = (sessionId) =>
request({
	url: `${WORKOUT_API_PREFIX}/workout-records/sessions/${sessionId}`,
	method: 'get'
})

// 新增跟练记录，在跟练某小节时提交
export const addPlayLog = (params) =>
request({
	url: `${WORKOUT_API_PREFIX}/workout-records`,
	method: 'post',
	data:params
})



/**  积分相关的接口 **/

// 签到打卡功能，返回本次签到的积分值
export const fitpointsSign = (params) =>
request({
	url: `${WORKOUT_API_PREFIX}/sign-records`,
	method: 'post',
	data:params
})
// 获取签到记录
export const getSignRecords = () =>
request({
	url: `${WORKOUT_API_PREFIX}/sign-records`,
	method: 'get',
})
// 获取签到记录
export const getTodayFitPoint = () =>
request({
	url: `${WORKOUT_API_PREFIX}/fitpoints/today`,
	method: 'get',
})
// 查询指定赛季信息
export const getSeasons = (params) =>
request({
	url: `${WORKOUT_API_PREFIX}/boards`,
	method: 'get',
	params
})
// 查询赛季信息列表
export const getSelectOptions = (params) =>
request({
	url: `${WORKOUT_API_PREFIX}/boards/seasons/list`,
	method: 'get',
	params
})


