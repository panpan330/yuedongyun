package com.yuedongyun.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "订单条目中的训练信息")
public class OrderDetailVO {
    @ApiModelProperty("订单条目id")
    private Long id;
    @ApiModelProperty("总订单id")
    private Long orderId;
    @ApiModelProperty("训练id")
    private Long trainingId;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("封面")
    private String coverUrl;
    @ApiModelProperty("训练价格")
    private Integer price;
    @ApiModelProperty("实付金额")
    private Integer realPayAmount;
    @ApiModelProperty("退款状态")
    private Integer refundStatus;
    @ApiModelProperty("优惠券规则")
    private String voucherDesc;
    @ApiModelProperty("是否可以退款")
    private Boolean canRefund;
}

