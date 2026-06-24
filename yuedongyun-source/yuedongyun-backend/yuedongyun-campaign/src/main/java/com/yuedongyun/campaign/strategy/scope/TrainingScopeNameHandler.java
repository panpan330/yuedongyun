package com.yuedongyun.campaign.strategy.scope;

import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.dto.training.TrainingSimpleInfoDTO;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.campaign.constants.ScopeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component(ScopeType.TRAINING_HANDLER_NAME)
public class TrainingScopeNameHandler implements ScopeNameHandler {

    private final TrainingClient trainingClient;

    @Override
    public List<String> getNameByIds(List<Long> scopeIds) {
        List<TrainingSimpleInfoDTO> infos = trainingClient.getSimpleInfoList(scopeIds);
        if (CollUtils.isEmpty(infos)) {
            throw new BizIllegalException("训练信息不存在");
        }
        return infos.stream().map(TrainingSimpleInfoDTO::getName).collect(Collectors.toList());
    }
}

