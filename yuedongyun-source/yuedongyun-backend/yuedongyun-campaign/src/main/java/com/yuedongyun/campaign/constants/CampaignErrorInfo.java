package com.yuedongyun.campaign.constants;

public interface CampaignErrorInfo {
    String VOUCHER_NOT_EXISTS = "优惠券不存在或活动已结束";
    String VOUCHER_EXPIRED = "优惠券已经过期";
    String VOUCHER_NOT_ENOUGH = "优惠券被领完了";

    String INVALID_VOUCHER_CODE = "兑换码无效或格式错误";
    String VOUCHER_ISSUE_EXPIRED = "优惠券已经停止发放";
    String INVALID_USER_VOUCHER = "用户优惠券不存在或已过期";
}

