package com.yuedongyun.workout.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "积分榜单汇总信息")
public class FitPointBoardVO {
    @ApiModelProperty("我的榜单排名")
    private Integer rank;
    @ApiModelProperty("我的积分值")
    private Integer fitpoints;
    @ApiModelProperty("前100名上榜人信息")
    private List<FitPointBoardItemVO> boardList;
}

