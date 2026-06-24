package com.yuedongyun.training.domain.dto;

import com.yuedongyun.common.validate.annotations.EnumValid;
import com.yuedongyun.training.constants.TrainingErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 训练分类启用或停用模型
 *
 * @author wusongsong
 * @since 2022/7/10 15:24
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练分类启用/禁用")
public class CategoryDisableOrEnableDTO {
    @ApiModelProperty("训练分类id")
    @NotNull(message = TrainingErrorInfo.Msg.CATEGORY_ID_NOT_NULL)
    private Long id;
    @ApiModelProperty("训练分类状态，1：启用，0：禁用")
    @EnumValid(enumeration = {0,1}, message = TrainingErrorInfo.Msg.CATEGORY_DISABLE_ENABLE_STATUS_ENUM)
    private Integer status;
}

