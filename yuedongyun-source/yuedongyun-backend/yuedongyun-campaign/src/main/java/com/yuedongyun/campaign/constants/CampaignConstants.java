package com.yuedongyun.campaign.constants;

public interface CampaignConstants {
    String VOUCHER_CODE_SERIAL_KEY = "voucher:code:serial";
    String VOUCHER_CODE_MAP_KEY = "voucher:code:map";
    String VOUCHER_CACHE_KEY_PREFIX = "prs:voucher:";
    String USER_VOUCHER_CACHE_KEY_PREFIX = "prs:user:voucher:";
    String VOUCHER_RANGE_KEY = "voucher:code:range";

    String[] RECEIVE_VOUCHER_ERROR_MSG = {
            "活动未开始",
            "库存不足",
            "活动已经结束",
            "领取次数过多",
    };
    String[] EXCHANGE_VOUCHER_ERROR_MSG = {
            "兑换码已兑换",
            "无效兑换码",
            "活动未开始",
            "活动已经结束",
            "领取次数过多",
    };
}

