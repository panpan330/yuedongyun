package com.yuedongyun.user.constants;

import java.time.Duration;

public interface UserConstants {
    String DEFAULT_PASSWORD = "123456";

    Long MEMBER_ROLE_ID = 2L;
    String MEMBER_ROLE_NAME = "会员";

    Long COACH_ROLE_ID = 3L;
    String COACH_ROLE_NAME = "教练";

    // 验证码的Redis key前缀
    String USER_VERIFY_CODE_KEY = "sms:user:code:phone:";
    // 验证码有效期，5分钟
    Duration USER_VERIFY_CODE_TTL = Duration.ofMinutes(5);
}
