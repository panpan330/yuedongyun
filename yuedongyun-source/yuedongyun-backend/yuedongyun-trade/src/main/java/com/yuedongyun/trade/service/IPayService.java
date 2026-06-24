package com.yuedongyun.trade.service;

import com.yuedongyun.trade.domain.dto.OrderDelayQueryDTO;
import com.yuedongyun.trade.domain.dto.PayApplyFormDTO;
import com.yuedongyun.trade.domain.vo.PayChannelVO;

import java.util.List;

public interface IPayService {
    List<PayChannelVO> queryPayChannels();

    String applyPayOrder(PayApplyFormDTO payApply);

    void queryPayResult(OrderDelayQueryDTO message);
}
