package com.yuedongyun.training.domain.dto;

import com.yuedongyun.training.constants.TrainingErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wusongsong
 * @since 2022/7/20 16:50
 * @version 1.0.0
 **/
@ApiModel(description = "训练id")
@Data
public class TrainingIdDTO {
    @ApiModelProperty("训练id")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_OPERATE_ID_NULL)
    private Long id;
}

