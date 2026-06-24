package com.yuedongyun.workout.domain.vo;

import com.yuedongyun.workout.enums.SessionStatus;
import com.yuedongyun.workout.enums.PlanStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "训练计划信息")
public class WorkoutSessionVO {

    @ApiModelProperty("主键sessionId")
    private Long id;

    @ApiModelProperty("训练id")
    private Long trainingId;

    @ApiModelProperty("训练名称")
    private String trainingName;

    @ApiModelProperty("训练封面")
    private String trainingCoverUrl;

    @ApiModelProperty("训练章节数量")
    private Integer sessions;

    @ApiModelProperty("训练状态，0-未跟练，1-跟练中，2-已学完，3-已失效")
    private SessionStatus status;

    @ApiModelProperty("总已跟练章节数")
    private Integer learnedSessions;

    @ApiModelProperty("总已报名训练数")
    private Integer trainingAmount;

    @ApiModelProperty("训练购买时间")
    private LocalDateTime createTime;

    @ApiModelProperty("训练过期时间，如果为null代表训练永久有效")
    private LocalDateTime expireTime;

    @ApiModelProperty("计划的跟练频率")
    private Integer weekFreq;

    @ApiModelProperty("习计划状态，0-没有计划，1-计划进行中")
    private PlanStatus planStatus;

    @ApiModelProperty("最近跟练的小节名")
    private String latestSessionName;

    @ApiModelProperty("最近跟练的小节编号")
    private Integer latestSessionIndex;
}

