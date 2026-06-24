package com.yuedongyun.workout.domain.query;

import com.yuedongyun.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "跟练笔记分页查询条件")
public class NotePageQuery extends PageQuery {
    // 用户端查询条件
    @ApiModelProperty(value = "训练id")
    private Long trainingId;
    @ApiModelProperty(value = "小节id", example = "1")
    private Long sessionId;
    @ApiModelProperty(value = "是否只查询我的笔记", example = "1")
    private Boolean onlyMine = false;
}

