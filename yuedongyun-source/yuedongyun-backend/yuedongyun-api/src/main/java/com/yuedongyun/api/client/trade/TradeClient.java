package com.yuedongyun.api.client.trade;

import com.yuedongyun.api.client.trade.fallback.TradeClientFallback;
import com.yuedongyun.api.dto.training.TrainingPurchaseInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "trade-service", fallbackFactory = TradeClientFallback.class)
public interface TradeClient {
    /**
     * 统计指定训练的报名人数
     * @param trainingIdList 训练id集合
     * @return 统计结果
     */
    @GetMapping("/order-details/enrollNum")
    Map<Long, Integer> countEnrollNumOfTraining(@RequestParam("trainingIdList") List<Long> trainingIdList);

    /**
     * 统计指定会员的报名训练数量
     * @param memberIds 会员id集合
     * @return 统计结果
     */
    @GetMapping("/order-details/enrollTraining")
    Map<Long, Integer> countEnrollTrainingOfMember(@RequestParam("memberIds") List<Long> memberIds);

    /**
     * 检查当前用户是否报名指定训练
     * @param id 训练id
     * @return 是否报名
     */
    @GetMapping("/order-details/training/{id}")
    Boolean checkMySession(@PathVariable("id") Long id);

    /**
     * 统计训练购买、退款状态
     * @param trainingId 训练id
     * @return 统计结果
     */
    @GetMapping("/order-details/purchaseInfo")
    TrainingPurchaseInfoDTO getPurchaseInfoOfTraining(@RequestParam("trainingId") Long trainingId);
}

