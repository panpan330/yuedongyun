package com.yuedongyun.workout.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yuedongyun.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum FitPointRecordType implements BaseEnum {
    WORKOUT(1, "训练跟练", 50),
    SIGN(2, "每日签到", 0),
    QA(3, "训练问答", 20),
    NOTE(4, "训练笔记", 20),
    COMMENT(5, "训练评价", 0),
    ;
    @EnumValue
    @JsonValue
    int value;
    String desc;
    int maxFitPoint;

    FitPointRecordType(int value, String desc, int maxFitPoint) {
        this.value = value;
        this.desc = desc;
        this.maxFitPoint = maxFitPoint;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FitPointRecordType of(Integer value){
        if (value == null) {
            return null;
        }
        for (FitPointRecordType status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
