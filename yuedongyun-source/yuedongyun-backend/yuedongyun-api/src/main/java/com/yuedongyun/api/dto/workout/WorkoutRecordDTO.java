package com.yuedongyun.api.dto.workout;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "小节信息及跟练进度")
public class WorkoutRecordDTO {
    @ApiModelProperty("对应节的id")
    private Long sessionId;
    @ApiModelProperty("视频的当前观看时长，单位秒")
    private Integer moment;
    @ApiModelProperty("是否完成跟练，默认false")
    private Boolean finished;
}

