package com.yuedongyun.api.dto.campaign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "订单中训练及优惠券信息")
public class OrderVoucherDTO {
    @ApiModelProperty("用户优惠券id")
    private List<Long> userVoucherIds;
    @ApiModelProperty("订单中的训练列表")
    private List<OrderTrainingDTO> trainingList;
}

