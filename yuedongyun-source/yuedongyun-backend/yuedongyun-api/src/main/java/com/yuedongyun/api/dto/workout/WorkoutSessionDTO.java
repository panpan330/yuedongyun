package com.yuedongyun.api.dto.workout;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "我的训练表进度信息")
public class WorkoutSessionDTO {
    @ApiModelProperty("训练表id")
    private Long id;
    @ApiModelProperty("最近跟练的小节id")
    private Long latestSessionId;
    @ApiModelProperty("已跟练小节记录")
    private List<WorkoutRecordDTO> records;
}

