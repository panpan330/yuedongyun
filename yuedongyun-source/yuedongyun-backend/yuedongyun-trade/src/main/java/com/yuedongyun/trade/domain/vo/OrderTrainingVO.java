package com.yuedongyun.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "订单中训练信息")
public class OrderTrainingVO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("训练封面url")
    private String coverUrl;
    @ApiModelProperty("训练价格，单位元")
    private Integer price;
}

