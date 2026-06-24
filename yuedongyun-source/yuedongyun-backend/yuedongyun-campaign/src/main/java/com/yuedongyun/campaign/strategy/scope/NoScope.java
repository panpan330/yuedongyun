package com.yuedongyun.campaign.strategy.scope;

import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.campaign.constants.ScopeType;

import java.util.List;

public class NoScope implements Scope{

    @Override
    public boolean canUse(OrderTrainingDTO training) {
        return true;
    }

    @Override
    public ScopeType getType() {
        return ScopeType.ALL;
    }

    @Override
    public List<Long> getScopeIds() {
        return null;
    }

}

