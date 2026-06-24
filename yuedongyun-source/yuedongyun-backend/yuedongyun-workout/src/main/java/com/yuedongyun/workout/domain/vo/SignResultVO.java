package com.yuedongyun.workout.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "签到结果")
public class SignResultVO {
    @ApiModelProperty("连续签到天数")
    private Integer signDays;
    @ApiModelProperty("签到得分")
    private Integer signFitPoint = 1;
    @ApiModelProperty("连续签到奖励积分，连续签到超过7天以上才有奖励")
    private Integer rewardFitPoint;

    @JsonIgnore
    public int totalFitPoint(){
        return signFitPoint + rewardFitPoint;
    }
}

