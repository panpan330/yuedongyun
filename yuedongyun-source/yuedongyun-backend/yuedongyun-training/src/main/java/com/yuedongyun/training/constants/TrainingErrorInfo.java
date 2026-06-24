package com.yuedongyun.training.constants;

/**
 * @author wusongsong
 * @since 2022/7/14 13:46
 * @version 1.0.0
 **/
public class TrainingErrorInfo {

    //错误信息
    public class Msg {
        public static final String CATEGORY_PARENT_NOT_FOUND = "训练分类父分类没有找到";
        public static final String CATEGORY_CREATE_ON_THIRD = "三级分类下不能再创建子分类";
        public static final String CATEGORY_SAME_NAME = "分类名称不能重复！";
        public static final String CATEGORY_NOT_FOUND = "该分类未找到";
        public static final String CATEGORY_HAVE_CHILD = "该训练分类有子分类，不能删除";
        public static final String CATEGORY_ADD_NAME_NOT_NULL = "分类名称不能为空！";
        public static final String CATEGORY_ADD_NAME_SIZE = "名称不能超过15个字符";
        public static final String CATEGORY_ADD_INDEX_MAX_MIN = "分类序号格式错误，请重新输入！";
        public static final String CATEGORY_ADD_INDEX_NOT_NULL = "分类序号不能为空！";
        public static final String CATEGORY_ADD_OVER_THIRD_LEVEL = "训练分类不支持三级以上的分类";
        public static final String CATEGORY_ID_NOT_NULL = "未选中训练分类";
        public static final String CATEGORY_DISABLE_ENABLE_STATUS_ENUM = "只有禁用或启用两种状态";
        public static final String CATEGORY_UPDATE_NAME_NOT_NULL = "分类名称不能为空！";
        public static final String CATEGORY_UPDATE_NAME_SIZE = "名称不能超过15个字符";
        public static final String CATEGORY_UPDATE_INDEX_MAX_MIN = "分类序号格式错误，请重新输入！";
        public static final String CATEGORY_UPDATE_INDEX_NOT_NULL = "分类序号不能为空！";
        public static final String CATEGORY_DELETE_HAVE_ASSESSMENT = "该分类下含有题目，无法删除";
        public static final String CATEGORY_DELETE_HAVE_TRAINING = "该分类下含有训练，无法删除";
        public static final String CATEGORY_QUERY_ID_NULL = "查询目录信息，id为空";
        public static final String CATEGORY_DELETE_FAILD = "训练分类删除失败";
        public static final String CATEGORY_ENABLE_CANNOT = "当前上级分类为禁用状态，无法启用";

        public static final String ASSESSMENT_NAME_EXISTS = "该题目已存在";
        public static final String ASSESSMENT_NO_DELETE_BY_USED = "当前题目已被引用，无法删除";
        public static final String ASSESSMENT_SAVE_CATEGORY_INCOMPLETE = "训练分类不完整";

        public static final String TRAINING_SAVE_FAILD = "训练基本信息保存失败";
        public static final String TRAINING_SAVE_CATEGORY_NULL = "训练分类为空，请选择训练分类";
        public static final String TRAINING_SAVE_CATEGORY_NOT_FOUND = "训练分类未找到";
        public static final String TRAINING_CATEGORY_NOT_FOUND = "训练分类未找到";
        public static final String TRAINING_SAVE_NAME_NULL = "训练名称为空，请填写训练名称";
        public static final String TRAINING_SAVE_COVER_URL_NULL = "训练封面为空，请上传训练封面";
        public static final String TRAINING_SAVE_FREE_NULL = "售卖模式为空，请选择售卖模式";
        public static final String TRAINING_SAVE_PURCHASE_TIME_NULL = "训练周期为空，请设置训练周期";
        public static final String TRAINING_SAVE_INTRODUCE_NULL = "训练介绍为空，请输入训练介绍";
        public static final String TRAINING_SAVE_USE_PEOPLE_NULL = "适用人群为空，请输入适用人群";
        public static final String TRAINING_SAVE_DETAIL_NULL = "训练详情为空，请输入训练详情";
        public static final String TRAINING_SAVE_DIFFICULTY_NULL = "训练难度为空，请选择训练难度";
        public static final String TRAINING_SAVE_DIFFICULTY_ILLEGAL = "训练难度不合法，请重新选择";
        public static final String TRAINING_SAVE_CALORIE_BURN_NULL = "预计热量消耗为空，请输入预计热量消耗";
        public static final String TRAINING_SAVE_CALORIE_BURN_ILLEGAL = "预计热量消耗必须为正整数";
        public static final String TRAINING_SAVE_TRAIN_PART_NULL = "训练重点部位为空，请输入训练重点部位";
        public static final String TRAINING_SAVE_DURATION_NULL = "跟练周期为空，请输入跟练周期";
        public static final String TRAINING_SAVE_PRICE_NULL = "训练价格为空，请输入训练价格";
        public static final String TRAINING_SAVE_PRICE_NEGATIVE = "训练价格为正数，请输入合法的训练价格";
        public static final String TRAINING_SAVE_PRICE_FREE = "免费训练没有价格，可以填0";
        public static final String TRAINING_SAVE_PURCHASE_ILLEGAL = "训练下架时间不得早于当前时间";
        public static final String TRAINING_SAVE_PURCHASE_ILLEGAL2 = "训练开始购买时间不得早于当前";
        public static final String TRAINING_SAVE_NAME_EXISTS = "训练名称重复，请重新输入";
        public static final String TRAINING_CATAS_SAVE_NAME_NULL = "章名称为空，请输入章名称";
        public static final String TRAINING_CATAS_SAVE_NAME_SIZE = "章名称格格式错误，请重新输入";
        public static final String TRAINING_CATAS_SAVE_NAME_SIZE2 = "小节名称格格式错误，请重新输入";
        public static final String TRAINING_CATAS_SAVE_NAME_NULL2 = "小节名称为空，请输入小节名称";
        public static final String TRAINING_CATAS_SAVE_CHAPTER_WITHOUT_SECTION = "章里必须有小节";
        public static final String TRAINING_CATAS_SAVE_CHAPTER_NAME_DELETED = "已经上架的{}目录被删除了";
        public static final String TRAINING_CATAS_SAVE_CHAPTER_NAME_MOVE = "已经上架的《%s》目录被移动了";
        public static final String TRAINING_CATAS_SAVE_INEDX = "目录的章中有序号是重复的";
        public static final String TRAINING_CATAS_SAVE_INEDX_JUMP = "目录的章中有序号不连续";
        public static final String TRAINING_CATAS_SAVE_CHAPTER_INDEX_REPEAT = "章序号有重复的";
        public static final String TRAINING_CATAS_SAVE_CHAPTER_INDEX_INTERRUPTED = "章序号填写有中断";
        public static final String TRAINING_CATA_NOT_EXISTS = "目录结构不存在";
        public static final String TRAINING_MEDIA_SAVE_ILLEGAL = "请求参数不合法";
        public static final String TRAINING_MEDIA_SAVE_SIZE_WRONG = "请检查所有的小节是否关联媒资或题目";
        public static final String TRAINING_MEDIA_SAVE_NO_EXECUTE = "媒资当前不能保存";
        public static final String TRAINING_MEDIA_SAVE_MEDIA_NULL = "部分章节未选择视频，请选择/上传视频";
        public static final String TRAINING_MEDIA_SAVE_TRAILER_NULL = "有训练还没有选择是否支持试看";
        public static final String TRAINING_SUBJECT_SAVE_SUBJECT_IDS_NULL = "阶段考核为空，请设置阶段考核题目";
        public static final String TRAINING_SUBJECT_SAVE_OUTLINE_ID_NULL = "题目未指定练习id";
        public static final String TRAINING_COACH_SAVE_TRAINING_ID_NULL = "训练id不能为空";
        public static final String TRAINING_COACH_SAVE_COACHS_NULL = "请至少设置一名教练";
//        public static final String TRAINING_COACH_SAVE_COACHS_NUM_MAX = "最多可设置5名训练教练";
        public static final String TRAINING_COACH_SAVE_COACHS_NUM_MAX = "必须设置教练1到5人";
        public static final String TRAINING_COACH_SAVE_COACH_SHOW = "教练用户端显示不能为空";
        public static final String TRAINING_COACH_SAVE_COACH_ID_NULL = "教练id不能为空";

        public static final String TRAINING_OPERATE_ID_NULL = "未选定训练";

    public static final String TRAINING_UP_SHELF_INFO_INCOMPLETE = "训练信息未填写完，无法上架";
        public static final String TRAINING_UP_SHELF_STATE_WRONG = "当前训练不能进行上架";
        public static final String TRAINING_UP_SHELF_PURCHASE_ILLEGAL = "下架时间需晚于当前时间";
        public static final String TRAINING_UP_SHELF_SECTION_WITHOUT_MEDIA = "小节《{}》未上传媒资";
        public static final String TRAINING_UP_SHELF_PRACTICE_WITHOUT_SUBJECT = "练习《{}》未上传题目";
        public static final String TRAINING_UP_SHELF_NOT_FOUND_TRAINING = "未找到对应的训练";
        public static final String TRAINING_UP_SHELF_AREADY = "训练已经上架，请勿重复操作";
        public static final String TRAINING_DOWN_SHELF_FAILD = "当前训练不能下架";
    public static final String TRAINING_CHECK_NOT_FOUND = "未查询到训练信息";
        public static final String TRAINING_CHECK_NOT_EXISTS = "某些训练不存在或已经删除";
        public static final String TRAINING_CHECK_DOWN_SHELF = "训练已经下架";
        public static final String TRAINING_CHECK_FINISHED = "训练已经完结";
        public static final String TRAINING_CHECK_NO_SALE = "训练还未开始销售";
    }
}

