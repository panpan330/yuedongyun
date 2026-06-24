package com.yuedongyun.api.dto.campaign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "订单中的训练信息")
public class OrderTrainingDTO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("训练的三级分类id")
    private Long cateId;
    @ApiModelProperty("训练价格")
    private Integer price;
}

