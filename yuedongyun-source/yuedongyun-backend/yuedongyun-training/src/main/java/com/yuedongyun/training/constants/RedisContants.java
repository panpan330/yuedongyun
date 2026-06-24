package com.yuedongyun.training.constants;

/**
 * @ClassName RedisContants
 * @Author wusongsong
 * @Date 2022/7/17 17:20
 * @Version
 **/
public class RedisContants {

    //一级二级分类拥有的三级分类的数量
    public static final String REDIS_KEY_CATEGORY_THIRD_NUMBER = "CATEGORY:THIRD_NUMBER";

    public static class Formatter {
        public static final String STATISTICS_EXAMINFO = "TRAINING:ASSESSMENT:ANSWER_PROCESS_#{examDetailInfoDTO.recordId}";
        public static final String STATISTICS_TRAINING_NUM_CATE = "TRAINING:TRAINING_NUM_CATEGORY";
        public static final String CATEGORY_ID_LIST_HAVE_TRAINING = "TRAINING:CATEGORY_ID_WITH_TRAINING";
    }
}

