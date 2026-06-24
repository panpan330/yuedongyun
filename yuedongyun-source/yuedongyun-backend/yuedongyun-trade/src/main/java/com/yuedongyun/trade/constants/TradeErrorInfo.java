package com.yuedongyun.trade.constants;

public interface TradeErrorInfo {

    String CARTS_FULL = "用户购物车训练不能超过{}";
    String TRAINING_NOT_EXISTS = "训练不存在";
    String TRAINING_EXPIRED = "训练已过期";
    String TRAINING_NOT_FOR_SALE = "训练无法购买";
    String TRAINING_NOT_FREE = "训练不是免费训练";

    String PLACE_ORDER_FAILED = "下单失败";
    String ORDER_NOT_EXISTS = "订单不存在";
    String ORDER_ALREADY_FINISH = "订单已经支付或退款";
    String ORDER_OVER_TIME = "订单已经超时";
    String ORDER_CANNOT_REFUND = "订单未支付或已关闭";

    String NO_AUTH_REFUND = "无权申请退款";
    String REFUND_TOO_MANY_TIMES = "退款次数太多";
    String REFUND_IN_PROGRESS = "有其它未完成的退款进程";
    String REFUND_NOT_EXISTS = "退款记录不存在";
    String REFUND_APPROVED = "退款申请已经处理过了";
    String FREE_TRAINING_CANNOT_REFUND = "免费训练不能退款";

}

