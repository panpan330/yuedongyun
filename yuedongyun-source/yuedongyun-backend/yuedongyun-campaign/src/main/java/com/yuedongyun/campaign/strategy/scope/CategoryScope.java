package com.yuedongyun.campaign.strategy.scope;

import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.campaign.constants.ScopeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CategoryScope implements Scope {

    private final List<Long> scopeIds;

    @Override
    public boolean canUse(OrderTrainingDTO training) {
        return scopeIds.contains(training.getCateId());
    }

    @Override
    public ScopeType getType() {
        return ScopeType.CATEGORY;
    }
}

