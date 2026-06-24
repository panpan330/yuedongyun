package com.yuedongyun.campaign.strategy.scope;

import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.campaign.constants.ScopeType;

import java.util.List;

public interface Scope {

    boolean canUse(OrderTrainingDTO training);

    ScopeType getType();

    List<Long> getScopeIds();
}

