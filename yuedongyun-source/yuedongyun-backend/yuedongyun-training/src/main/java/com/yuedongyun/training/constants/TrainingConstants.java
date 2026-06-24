package com.yuedongyun.training.constants;

/**
 * @author wusongsong
 * @since 2022/7/14 13:44
 * @version 1.0.0 1.0
 **/
public class TrainingConstants {

    public static final long CATEGORY_ROOT = 0;

    public class TrainingStep {
        public static final int BASE_INFO = 1; //基本信息
        public static final int OUTLINE = 2; //目录
        public static final int MEDIA = 3; //视频
        public static final int ASSESSMENT = 4; //题目
        public static final int COACH = 5; //教练
    }

    //目录类型
    public class OutlineType{
        public static final int PHASE = 1; //章
        public static final int SESSION = 2; //节
        public static final int PRATICE = 3; //练习或测试
    }

}

