package com.yuedongyun.campaign.strategy.discount;

import com.yuedongyun.common.utils.NumberUtils;
import com.yuedongyun.common.utils.StringUtils;
import com.yuedongyun.campaign.domain.po.Voucher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateDiscount implements Discount {

    private static final String RULE_TEMPLATE = "满{}打{}折，上限{}元";

    @Override
    public boolean canUse(int totalAmount, Voucher voucher) {
        return totalAmount >= voucher.getThresholdAmount();
    }

    @Override
    public int calculateDiscount(int totalAmount,  Voucher voucher) {
        // 计算折扣，扩大100倍计算，向下取整，单位是分
        return Math.min(voucher.getMaxDiscountAmount(), totalAmount * (100 - voucher.getDiscountValue()) / 100);
    }

    @Override
    public String getRule( Voucher voucher) {
        return StringUtils.format(
                RULE_TEMPLATE,
                NumberUtils.scaleToStr(voucher.getThresholdAmount(), 2),
                NumberUtils.scaleToStr(voucher.getDiscountValue(), 1),
                NumberUtils.scaleToStr(voucher.getMaxDiscountAmount(), 2)
        );
    }
}

