package com.yuedongyun.workout.domain.dto;

import com.yuedongyun.common.validate.annotations.EnumValid;
import com.yuedongyun.workout.enums.SessionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "跟练记录")
public class WorkoutRecordFormDTO {

    @ApiModelProperty("小节类型：1-视频，2-考核")
    @NotNull(message = "小节类型不能为空")
    @EnumValid(enumeration = {1, 2}, message = "小节类型错误，只能是：1-视频，2-考核")
    private SessionType sessionType;

    @ApiModelProperty("课表id")
    @NotNull(message = "课表id不能为空")
    private Long sessionId;

    @ApiModelProperty("对应节的id")
    @NotNull(message = "节的id不能为空")
    private Long sessionId;

    @ApiModelProperty("视频总时长，单位秒")
    private Integer duration;

    @ApiModelProperty("视频的当前观看时长，单位秒，第一次提交填0")
    private Integer moment;

    @ApiModelProperty("提交时间")
    private LocalDateTime commitTime;
}
