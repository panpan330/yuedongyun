package com.yuedongyun.campaign.strategy.discount;

import com.yuedongyun.common.utils.NumberUtils;
import com.yuedongyun.common.utils.StringUtils;
import com.yuedongyun.campaign.domain.po.Voucher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerPriceDiscount implements Discount {

    private final static String RULE_TEMPLATE = "每满{}减{}，上限{}";

    @Override
    public boolean canUse(int totalAmount, Voucher voucher) {
        return totalAmount >= voucher.getThresholdAmount();
    }

    @Override
    public int calculateDiscount(int totalAmount, Voucher voucher) {
        int discount = 0;
        Integer thresholdAmount = voucher.getThresholdAmount();
        Integer discountValue = voucher.getDiscountValue();
        while (totalAmount >= thresholdAmount) {
            discount += discountValue;
            totalAmount -= thresholdAmount;
        }
        return Math.min(discount, voucher.getMaxDiscountAmount());
    }

    @Override
    public String getRule(Voucher voucher) {
        return StringUtils.format(
                RULE_TEMPLATE,
                NumberUtils.scaleToStr(voucher.getThresholdAmount(), 2),
                NumberUtils.scaleToStr(voucher.getDiscountValue(), 2),
                NumberUtils.scaleToStr(voucher.getMaxDiscountAmount(), 2));
    }
}

