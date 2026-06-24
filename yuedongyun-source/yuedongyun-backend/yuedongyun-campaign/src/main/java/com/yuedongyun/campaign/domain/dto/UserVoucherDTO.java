package com.yuedongyun.campaign.domain.dto;

import lombok.Data;

@Data
public class UserVoucherDTO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 优惠券id
     */
    private Long voucherId;
    /**
     * 兑换码序列号（id）
     */
    private Integer serialNum;
}

