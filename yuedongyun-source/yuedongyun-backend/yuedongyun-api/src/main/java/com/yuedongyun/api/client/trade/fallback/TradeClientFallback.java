package com.yuedongyun.api.client.trade.fallback;

import com.yuedongyun.api.client.trade.TradeClient;
import com.yuedongyun.api.dto.training.TrainingPurchaseInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TradeClientFallback implements FallbackFactory<TradeClient> {

    @Override
    public TradeClient create(Throwable cause) {
        log.error("查询交易服务异常", cause);
        return new TradeClient() {

            @Override
            public Map<Long, Integer> countEnrollNumOfTraining(List<Long> trainingIdList) {
                return new HashMap<>();
            }

            @Override
            public Map<Long, Integer> countEnrollTrainingOfMember(List<Long> memberIds) {
                return new HashMap<>();
            }

            @Override
            public Boolean checkMySession(Long id) {
                return false;
            }

            @Override
            public TrainingPurchaseInfoDTO getPurchaseInfoOfTraining(Long trainingId) {
                return new TrainingPurchaseInfoDTO();
            }
        };
    }
}

