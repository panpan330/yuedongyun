package com.yuedongyun.api.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 教练负责的训练数和出题数目的集合
 * @ClassName SubNumAndTrainingNumDTO
 * @author wusongsong
 * @since 2022/7/18 15:12
 * @version 1.0.0
 **/
@Data
@AllArgsConstructor
@NotNull
@ApiModel("教练id和教练对应的训练数、出题数")
public class SubNumAndTrainingNumDTO {
    @ApiModelProperty("教练id")
    private Long coachId;
    @ApiModelProperty("教练负责的训练数")
    private Integer trainingNum;
    @ApiModelProperty("教练出题数")
    private Integer assessmentNum;
}

