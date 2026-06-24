package com.yuedongyun.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "下单模型")
public class PlaceOrderDTO {
    @ApiModelProperty("要购买的训练id列表，可以只买单个训练")
    @NotNull(message = "你还没有选好训练")
    @Size(min = 1, message = "你还没有选好训练")
    @Size(max = 10, message = "一次最多选购10门训练")
    private List<Long> trainingIds;

    @ApiModelProperty("该订单使用的优惠券id列表，可以为空")
    private List<Long> voucherIds;

    @ApiModelProperty("订单id")
    @NotNull(message = "订单id不能为空")
    private Long orderId;
}
