package com.yuedongyun.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.exceptions.BadRequestException;
import lombok.Getter;

@Getter
public enum UserType implements BaseEnum{
    STAFF(1, "其他员工"),
    MEMBER(2, "会员"),
    COACH(3, "教练"),
    ;
    @EnumValue
    int value;
    String desc;

    UserType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserType of(int value) {
        for (UserType type : UserType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new BadRequestException(ErrorInfo.Msg.INVALID_USER_TYPE);
    }
}
