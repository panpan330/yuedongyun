package com.yuedongyun.api.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/11 16:42
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练目录")
public class OutlineDTO {
    @ApiModelProperty("章、节、练习id")
    private Long id;
    @ApiModelProperty("序号")
    private Integer index;
    @ApiModelProperty("章节练习名称")
    private String name;
    @ApiModelProperty("训练总时长,单位秒")
    private Integer mediaDuration;
    @ApiModelProperty("是否支持免费试看")
    private Boolean trailer;
    @ApiModelProperty("媒资名称")
    private String mediaName;
    @ApiModelProperty("媒资id")
    private Long mediaId;
    @ApiModelProperty("目录类型1：章，2：节，3：考核")
    private Integer type;
    @ApiModelProperty("题目数量")
    private Integer assessmentNum;
    @ApiModelProperty("题目总分")
    private Integer totalScore;
    @ApiModelProperty("是否可以修改,默认不能修改")
    private Boolean canUpdate = false;
    @ApiModelProperty("该章的所有小节和练习")
    private List<OutlineDTO> sessions;
}

