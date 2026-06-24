package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/8/15 16:04
 * @version 1.0.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlineSimpleAssessmentVO {
    @ApiModelProperty("小节或练习id")
    private Long outlineId;
    @ApiModelProperty("题目id")
    private List<AssessmentInfo> assessments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssessmentInfo{
        private Long id;
        private String name;
    }
}

