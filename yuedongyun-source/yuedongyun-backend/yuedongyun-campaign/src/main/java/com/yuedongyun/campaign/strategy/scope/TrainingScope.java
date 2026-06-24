package com.yuedongyun.campaign.strategy.scope;

import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.campaign.constants.ScopeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TrainingScope implements Scope {

    private final List<Long> scopeIds;

    @Override
    public boolean canUse(OrderTrainingDTO training) {
        return scopeIds.contains(training.getId());
    }

    @Override
    public ScopeType getType() {
        return ScopeType.TRAINING;
    }
}

