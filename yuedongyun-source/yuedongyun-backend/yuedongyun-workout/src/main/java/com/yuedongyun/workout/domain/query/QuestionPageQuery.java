package com.yuedongyun.workout.domain.query;

import com.yuedongyun.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "互动问题分页查询条件")
public class QuestionPageQuery extends PageQuery {
    // 用户端查询条件
    @ApiModelProperty(value = "训练id")
    private Long trainingId;
    @ApiModelProperty(value = "小节id", example = "1")
    private Long sessionId;
    @ApiModelProperty(value = "是否只查询我的问题", example = "1")
    private Boolean onlyMine = false;
}

