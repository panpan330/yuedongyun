package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 目录和习题模型
 * @author wusongsong
 * @since 2022/7/11 17:45
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练题目统计")
public class OutlineAssessmentVO {
    @ApiModelProperty("小节或测试id")
    private Long outlineId;
    @ApiModelProperty("小节或测试名称")
    private String outlineName;
    @ApiModelProperty("类型，2：节，3：测试")
    private Integer type;
    @ApiModelProperty("题目数量")
    private Integer assessmentNum;
    @ApiModelProperty("题目总分")
    private Integer assessmentScore;
}

