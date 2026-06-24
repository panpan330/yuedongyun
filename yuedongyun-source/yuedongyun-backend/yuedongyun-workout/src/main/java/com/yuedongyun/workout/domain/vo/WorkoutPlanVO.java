package com.yuedongyun.workout.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "训练计划信息")
public class WorkoutPlanVO {

    @ApiModelProperty("主键sessionId")
    private Long id;

    @ApiModelProperty("训练id")
    private Long trainingId;

    @ApiModelProperty("训练名称")
    private String trainingName;

    @ApiModelProperty("每周计划跟练章节数")
    private Integer weekFreq;

    @ApiModelProperty("训练章节数量")
    private Integer sessions;

    @ApiModelProperty("本周已跟练章节数")
    private Integer weekLearnedSessions;

    @ApiModelProperty("总已跟练章节数")
    private Integer learnedSessions;

    @ApiModelProperty("最近一次跟练时间")
    private LocalDateTime latestLearnTime;
}

