package com.yuedongyun.workout.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yuedongyun.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum SessionType implements BaseEnum {
    VIDEO(1, "视频"),
    EXAM(2, "考核"),
    ;
    @JsonValue
    @EnumValue
    int value;
    String desc;

    SessionType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SessionType of(Integer value){
        if (value == null) {
            return null;
        }
        for (SessionType status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}

