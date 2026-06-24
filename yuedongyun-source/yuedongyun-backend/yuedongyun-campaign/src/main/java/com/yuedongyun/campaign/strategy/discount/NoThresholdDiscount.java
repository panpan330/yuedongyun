package com.yuedongyun.campaign.strategy.discount;

import com.yuedongyun.common.utils.NumberUtils;
import com.yuedongyun.common.utils.StringUtils;
import com.yuedongyun.campaign.domain.po.Voucher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoThresholdDiscount implements Discount{

    private static final String RULE_TEMPLATE = "无门槛抵{}元";

    @Override
    public boolean canUse(int totalAmount, Voucher voucher) {
        return totalAmount > voucher.getDiscountValue();
    }

    @Override
    public int calculateDiscount(int totalAmount, Voucher voucher) {
        return voucher.getDiscountValue();
    }

    @Override
    public String getRule(Voucher voucher) {
        return StringUtils.format(RULE_TEMPLATE, NumberUtils.scaleToStr(voucher.getDiscountValue(), 2));
    }
}

