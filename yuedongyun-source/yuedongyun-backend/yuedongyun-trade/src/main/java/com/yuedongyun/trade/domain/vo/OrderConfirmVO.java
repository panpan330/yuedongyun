package com.yuedongyun.trade.domain.vo;

import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "订单确认页信息")
public class OrderConfirmVO {
    @ApiModelProperty("订单id")
    private Long orderId;
    @ApiModelProperty("订单总金额")
    private Integer totalAmount;
    @ApiModelProperty("优惠折扣方案")
    private List<VoucherDiscountDTO> discounts;
    @ApiModelProperty("订单中包含的训练")
    private List<OrderTrainingVO> trainings;
}

