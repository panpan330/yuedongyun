package com.yuedongyun.trade.controller;


import com.yuedongyun.api.dto.training.TrainingPurchaseInfoDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.trade.domain.query.OrderDetailPageQuery;
import com.yuedongyun.trade.domain.vo.OrderDetailAdminVO;
import com.yuedongyun.trade.domain.vo.OrderDetailPageVO;
import com.yuedongyun.trade.service.IOrderDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单明细 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-29
 */

@Api(tags = "订单明细相关接口")
@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService detailService;

    @ApiOperation("分页查询订单明细")
    @GetMapping("/page")
    public PageDTO<OrderDetailPageVO> queryDetailForPage(OrderDetailPageQuery pageQuery) {
        return detailService.queryDetailForPage(pageQuery);
    }

    @ApiOperation("根据订单明细id获取详细信息")
    @GetMapping("/{id}")
    public OrderDetailAdminVO queryOrdersDetailProgress( @ApiParam(value = "订单明细id")@PathVariable("id") Long id) {
        return detailService.queryOrdersDetailProgress(id);
    }

    @ApiOperation("校验训练是否购买，是否过期")
    @GetMapping("/training/{id}")
    public Boolean checkTrainingOrderInfo(@PathVariable("id") Long trainingId){
        return detailService.checkTrainingOrderInfo(trainingId);
    }

    @ApiOperation("统计训练报名人数")
    @GetMapping("/enrollNum")
    public Map<Long, Integer> countEnrollNumOfTraining(@RequestParam("trainingIdList") List<Long> trainingIdList){
        return detailService.countEnrollNumOfTraining(trainingIdList);
    }

    @ApiOperation("统计会员报名训练数量")
    @GetMapping("/enrollTraining")
    public Map<Long, Integer> countEnrollTrainingOfMember(@RequestParam("memberIds") List<Long> memberIds){
        return detailService.countEnrollTrainingOfMember(memberIds);
    }

    @GetMapping("purchaseInfo")
    public TrainingPurchaseInfoDTO getPurchaseInfoOfTraining(@RequestParam("trainingId") Long trainingId){
        return detailService.getPurchaseInfoOfTraining(trainingId);
    }
}

