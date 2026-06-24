package com.yuedongyun.trade.service;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.pay.sdk.dto.PayResultDTO;
import com.yuedongyun.trade.constants.OrderCancelReason;
import com.yuedongyun.trade.domain.dto.PlaceOrderDTO;
import com.yuedongyun.trade.domain.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.trade.domain.po.OrderDetail;
import com.yuedongyun.trade.domain.query.OrderPageQuery;
import com.yuedongyun.trade.domain.vo.OrderConfirmVO;
import com.yuedongyun.trade.domain.vo.OrderPageVO;
import com.yuedongyun.trade.domain.vo.OrderVO;
import com.yuedongyun.trade.domain.vo.PlaceOrderResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-29
 */
public interface IOrderService extends IService<Order> {

    PlaceOrderResultVO placeOrder(PlaceOrderDTO placeOrderDTO);

    @Transactional
    void saveOrderAndDetails(Order order, List<OrderDetail> orderDetails);

    void cancelOrder(Long orderId, OrderCancelReason cancelReason);

    void deleteOrder(Long id);

    PageDTO<OrderPageVO> queryMyOrderPage(OrderPageQuery pageQuery);

    OrderVO queryOrderById(Long id);

    PlaceOrderResultVO queryOrderStatus(Long orderId);

    void handlePaySuccess(PayResultDTO payResult);

    PlaceOrderResultVO enrolledFreeTraining(Long trainingId);

    OrderConfirmVO prePlaceOrder(List<Long> trainingIds);

}

