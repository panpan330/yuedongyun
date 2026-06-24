package com.yuedongyun.workout.constants;

public interface RedisConstants {
    /**
     * 签到记录的Key的前缀：sign:uid:110:202301
     */
    String SIGN_RECORD_KEY_PREFIX = "sign:uid:";
    /**
     * 积分排行榜的Key的前缀：boards:202301
     */
    String FITPOINTS_BOARD_KEY_PREFIX = "boards:";
}

