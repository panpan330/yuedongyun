package com.yuedongyun.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.api.dto.training.TrainingPurchaseInfoDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.trade.domain.po.Order;
import com.yuedongyun.trade.domain.po.OrderDetail;
import com.yuedongyun.trade.domain.po.RefundApply;
import com.yuedongyun.trade.domain.query.OrderDetailPageQuery;
import com.yuedongyun.trade.domain.vo.OrderDetailAdminVO;
import com.yuedongyun.trade.domain.vo.OrderDetailPageVO;
import com.yuedongyun.trade.domain.vo.OrderProgressNodeVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单明细 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-29
 */
public interface IOrderDetailService extends IService<OrderDetail> {

    void updateStatusByOrderId(Long orderId, Integer status);

    List<OrderDetail> queryByOrderIds(List<Long> orderIds);

    List<OrderDetail> queryByOrderId(Long orderId);

    PageDTO<OrderDetailPageVO> queryDetailForPage(OrderDetailPageQuery pageQuery);

    OrderDetailAdminVO queryOrdersDetailProgress(Long id);

    List<OrderProgressNodeVO> packageProgressNodes(Order order, RefundApply refundApply);

    void markDetailSuccessByOrderId(Long id, String payChannel, LocalDateTime successTime);

    void updateRefundStatusById(Long orderDetailId, int status);

    List<Long> queryTrainingIdsByOrderId(Long orderId);

    Boolean checkTrainingOrderInfo(Long trainingId);

    Map<Long, Integer> countEnrollNumOfTraining(List<Long> trainingIdList);

    Map<Long, Integer> countEnrollTrainingOfMember(List<Long> memberIds);

    TrainingPurchaseInfoDTO getPurchaseInfoOfTraining(Long trainingId);
}

