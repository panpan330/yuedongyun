package com.yuedongyun.api.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 训练支付相关信息 训练状态
 * @author wusongsong
 * @since 2022/7/26 20:41
 * @version 1.0.0
 **/
@Data
@ApiModel("训练购买信息")
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPurchaseInfoDTO {
    @ApiModelProperty("报名人数")
    private Integer enrollNum;
    @ApiModelProperty("退款人数")
    private Integer refundNum;
    @ApiModelProperty("实付总金额")
    private Integer realPayAmount;
}

