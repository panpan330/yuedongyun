package com.yuedongyun.workout.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "积分榜单信息")
public class FitPointBoardItemVO {
    @ApiModelProperty("积分值")
    private Integer fitpoints;
    @ApiModelProperty("名次")
    private Integer rank;
    @ApiModelProperty("会员姓名")
    private String name;
}

