package com.yuedongyun.workout.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "训练计划表单实体")
public class WorkoutPlanDTO {
    @NotNull
    @ApiModelProperty("训练计划id")
    @Min(1)
    private Long trainingId;
    @NotNull
    @Range(min = 1, max = 50)
    @ApiModelProperty("每周跟练频率")
    private Integer freq;
}

