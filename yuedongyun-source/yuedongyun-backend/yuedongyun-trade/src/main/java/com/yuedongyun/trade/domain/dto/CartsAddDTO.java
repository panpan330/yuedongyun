package com.yuedongyun.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "训练加入购物车")
public class CartsAddDTO {
    @ApiModelProperty("要加入购物车的训练id")
    @NotNull(message = "训练id不能为空")
    private Long trainingId;
}
