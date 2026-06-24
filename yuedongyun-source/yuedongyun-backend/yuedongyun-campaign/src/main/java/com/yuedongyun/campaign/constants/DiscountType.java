package com.yuedongyun.campaign.constants;

import com.yuedongyun.common.enums.BaseEnum;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.strategy.discount.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscountType implements BaseEnum {
    PER_PRICE_DISCOUNT(1, "每满减"){
        @Override
        public Discount getDiscount(Voucher voucher) {
            return new PerPriceDiscount(
                    voucher.getDiscountValue(), voucher.getThresholdAmount(), voucher.getMaxDiscountAmount());
        }
    },
    RATE_DISCOUNT(2, "折扣"){
        @Override
        public Discount getDiscount(Voucher voucher) {
            return new RateDiscount(
                    voucher.getDiscountValue(), voucher.getThresholdAmount(), voucher.getMaxDiscountAmount());
        }
    },
    NO_THRESHOLD(3, "无门槛"){
        @Override
        public Discount getDiscount(Voucher voucher) {
            return new NoThresholdDiscount(voucher.getDiscountValue());
        }
    },
    PRICE_DISCOUNT(4, "满减"){
        @Override
        public Discount getDiscount(Voucher voucher) {
            return new PriceDiscount(voucher.getDiscountValue(), voucher.getThresholdAmount());
        }
    },
    ;
    private final int value;
    private final String desc;

    public static DiscountType of(Integer value) {
        if (value == null) {
            return null;
        }
        for (DiscountType status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

    public abstract Discount getDiscount(Voucher voucher);
}

