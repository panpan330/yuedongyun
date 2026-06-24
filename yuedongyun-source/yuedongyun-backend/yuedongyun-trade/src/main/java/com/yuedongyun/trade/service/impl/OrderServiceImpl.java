package com.yuedongyun.trade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.client.campaign.CampaignClient;
import com.yuedongyun.api.constants.TrainingStatus;
import com.yuedongyun.api.dto.training.TrainingSimpleInfoDTO;
import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import com.yuedongyun.api.dto.campaign.OrderVoucherDTO;
import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.api.dto.trade.OrderBasicDTO;
import com.yuedongyun.common.autoconfigure.mq.RabbitMqHelper;
import com.yuedongyun.common.constants.MqConstants;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.pay.sdk.dto.PayResultDTO;
import com.yuedongyun.trade.config.TradeProperties;
import com.yuedongyun.trade.constants.OrderCancelReason;
import com.yuedongyun.trade.constants.OrderStatus;
import com.yuedongyun.trade.constants.RefundStatus;
import com.yuedongyun.trade.constants.TradeErrorInfo;
import com.yuedongyun.trade.domain.dto.PlaceOrderDTO;
import com.yuedongyun.trade.domain.po.Order;
import com.yuedongyun.trade.domain.po.OrderDetail;
import com.yuedongyun.trade.domain.query.OrderPageQuery;
import com.yuedongyun.trade.domain.vo.*;
import com.yuedongyun.trade.mapper.OrderMapper;
import com.yuedongyun.trade.service.ICartService;
import com.yuedongyun.trade.service.IOrderDetailService;
import com.yuedongyun.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yuedongyun.common.constants.ErrorInfo.Msg.OPERATE_FAILED;
import static com.yuedongyun.trade.constants.TradeErrorInfo.ORDER_ALREADY_FINISH;
import static com.yuedongyun.trade.constants.TradeErrorInfo.ORDER_NOT_EXISTS;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-29
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final TrainingClient trainingClient;
    private final IOrderDetailService detailService;
    private final ICartService cartService;
    private final TradeProperties tradeProperties;
    private final RabbitMqHelper rabbitMqHelper;
    private final CampaignClient campaignClient;

    @Override
    @Transactional
    public PlaceOrderResultVO placeOrder(PlaceOrderDTO placeOrderDTO) {
        Long userId = UserContext.getUser();
        // 1.查询训练费用信息，如果不可购买，这里直接报错
        List<TrainingSimpleInfoDTO> trainingInfos = getOnShelfTraining(placeOrderDTO.getTrainingIds());
        // 2.封装订单信息
        Order order = new Order();
        // 2.1.计算订单金额
        Integer totalAmount = trainingInfos.stream()
                .map(TrainingSimpleInfoDTO::getPrice).reduce(Integer::sum).orElse(0);
        // 2.2.计算优惠金额
        order.setDiscountAmount(0);
        List<Long> voucherIds = placeOrderDTO.getVoucherIds();
        VoucherDiscountDTO discount = null;
        if (CollUtils.isNotEmpty(voucherIds)) {
            List<OrderTrainingDTO> orderTrainings = trainingInfos.stream()
                    .map(c -> new OrderTrainingDTO().setId(c.getId()).setCateId(c.getThirdCateId()).setPrice(c.getPrice()))
                    .collect(Collectors.toList());
            discount = campaignClient.queryDiscountDetailByOrder(new OrderVoucherDTO(voucherIds, orderTrainings));
            if(discount != null) {
                order.setDiscountAmount(discount.getDiscountAmount());
                order.setVoucherIds(discount.getIds());
            }
        }
        Integer realAmount = totalAmount - order.getDiscountAmount();
        // 2.3.封装其它信息
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setRealAmount(realAmount);
        order.setStatus(OrderStatus.NO_PAY.getValue());
        order.setMessage(OrderStatus.NO_PAY.getProgressName());
        // 2.4.订单id
        Long orderId = placeOrderDTO.getOrderId();
        order.setId(orderId);

        // 3.封装订单详情
        List<OrderDetail> orderDetails = new ArrayList<>(trainingInfos.size());
        for (TrainingSimpleInfoDTO trainingInfo : trainingInfos) {
            Integer discountValue = discount == null ?
                    0 : discount.getDiscountDetail().getOrDefault(trainingInfo.getId(), 0);
            orderDetails.add(packageOrderDetail(trainingInfo, order, discountValue));
        }

        // 4.写入数据库
        saveOrderAndDetails(order, orderDetails);

        // 5.删除购物车数据
        cartService.deleteCartByUserAndTrainingIds(userId, placeOrderDTO.getTrainingIds());

        // 6.核销优惠券
        campaignClient.writeOffVoucher(voucherIds);

        // 7.构建下单结果
        return PlaceOrderResultVO.builder()
                .orderId(orderId)
                .payAmount(realAmount)
                .status(order.getStatus())
                .payOutTime(LocalDateTime.now().plusMinutes(tradeProperties.getPayOrderTTLMinutes()))
                .build();
    }

    private List<TrainingSimpleInfoDTO> getOnShelfTraining(List<Long> trainingIds) {
        // 1.查询训练
        List<TrainingSimpleInfoDTO> trainingInfos = trainingClient.getSimpleInfoList(trainingIds);
        LocalDateTime now = LocalDateTime.now();
        // 2.判断状态
        for (TrainingSimpleInfoDTO trainingInfo : trainingInfos) {
            // 2.1.检查训练是否上架
            if(!TrainingStatus.SHELF.equalsValue(trainingInfo.getStatus())){
                throw new BizIllegalException(TradeErrorInfo.TRAINING_NOT_FOR_SALE);
            }
            // 2.2.检查训练是否过期
            if(trainingInfo.getPurchaseEndTime().isBefore(now)){
                throw new BizIllegalException(TradeErrorInfo.TRAINING_EXPIRED);
            }
        }
        return trainingInfos;
    }


    @Override
    @Transactional
    public PlaceOrderResultVO enrolledFreeTraining(Long trainingId) {
        Long userId = UserContext.getUser();
        // 1.查询训练信息
        List<Long> cIds = CollUtils.singletonList(trainingId);
        List<TrainingSimpleInfoDTO> trainingInfos = getOnShelfTraining(cIds);
        if (CollUtils.isEmpty(trainingInfos)) {
            // 训练不存在
            throw new BizIllegalException(TradeErrorInfo.TRAINING_NOT_EXISTS);
        }
        TrainingSimpleInfoDTO trainingInfo = trainingInfos.get(0);
        if(!trainingInfo.getFree()){
            // 非免费训练，直接报错
            throw new BizIllegalException(TradeErrorInfo.TRAINING_NOT_FREE);
        }
        // 2.创建订单
        Order order = new Order();
        // 2.1.基本信息
        order.setUserId(userId);
        order.setTotalAmount(0);
        order.setDiscountAmount(0);
        order.setRealAmount(0);
        order.setStatus(OrderStatus.ENROLLED.getValue());
        order.setFinishTime(LocalDateTime.now());
        order.setMessage(OrderStatus.ENROLLED.getProgressName());
        // 2.2.订单id
        Long orderId = IdWorker.getId(order);
        order.setId(orderId);

        // 3.订单详情
        OrderDetail detail = packageOrderDetail(trainingInfo, order, 0);

        // 4.写入数据库
        saveOrderAndDetails(order, CollUtils.singletonList(detail));

        // 5.发送MQ消息，通知报名成功
        rabbitMqHelper.send(
                MqConstants.Exchange.ORDER_EXCHANGE,
                MqConstants.Key.ORDER_PAY_KEY,
                OrderBasicDTO.builder()
                        .orderId(orderId)
                        .userId(userId)
                        .trainingIds(cIds)
                        .finishTime(order.getFinishTime())
                        .build()
        );
        // 6.返回vo
        return PlaceOrderResultVO.builder()
                .orderId(orderId)
                .payAmount(0)
                .status(order.getStatus())
                .build();
    }

    @Override
    public OrderConfirmVO prePlaceOrder(List<Long> trainingIds) {
        // 1.查询训练信息
        List<TrainingSimpleInfoDTO> trainingInfos = trainingClient.getSimpleInfoList(trainingIds);
        if (CollUtils.isEmpty(trainingInfos)) {
            throw new BizIllegalException(TradeErrorInfo.TRAINING_NOT_EXISTS);
        }
        List<OrderTrainingVO> trainings = BeanUtils.copyList(trainingInfos, OrderTrainingVO.class);
        // 2.计算总价
        int total = trainingInfos.stream().mapToInt(TrainingSimpleInfoDTO::getPrice).sum();
        // 3.计算折扣
        List<OrderTrainingDTO> orderTrainings = trainingInfos.stream()
                .map(ci -> new OrderTrainingDTO().setId(ci.getId()).setCateId(ci.getThirdCateId()).setPrice(ci.getPrice()))
                .collect(Collectors.toList());
        List<VoucherDiscountDTO> discountSolution = campaignClient.findDiscountSolution(orderTrainings);
        // 4.生成订单id
        long orderId = IdWorker.getId();
        // 5.组织返回
        OrderConfirmVO vo = new OrderConfirmVO();
        vo.setOrderId(orderId);
        vo.setTotalAmount(total);
        vo.setDiscounts(discountSolution);
        vo.setTrainings(trainings);
        return vo;
    }

    private OrderDetail packageOrderDetail(TrainingSimpleInfoDTO trainingInfo, Order order, Integer discountValue) {
        OrderDetail detail = new OrderDetail();
        detail.setUserId(order.getUserId());
        detail.setOrderId(order.getId());
        detail.setStatus(order.getStatus());
        detail.setTrainingId(trainingInfo.getId());
        detail.setPrice(trainingInfo.getPrice());
        detail.setCoverUrl(trainingInfo.getCoverUrl());
        detail.setName(trainingInfo.getName());
        detail.setValidDuration(trainingInfo.getValidDuration());
        detail.setDiscountAmount(discountValue);
        detail.setRealPayAmount(trainingInfo.getPrice() - detail.getDiscountAmount());
        return detail;
    }

    @Override
    @Transactional
    public void saveOrderAndDetails(Order order, List<OrderDetail> orderDetails) {
        // 4.1.写订单
        boolean success = save(order);
        if (!success) {
            throw new DbException(TradeErrorInfo.PLACE_ORDER_FAILED);
        }
        // 4.2.写订单详情
        if(orderDetails.size() == 1){
            success = detailService.save(orderDetails.get(0));
        }else {
            success = detailService.saveBatch(orderDetails);
        }
        if (!success) {
            throw new DbException(TradeErrorInfo.PLACE_ORDER_FAILED);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, OrderCancelReason cancelReason) {
        Long userId = UserContext.getUser();
        // 1.查询订单
        Order order = getById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 2.判断订单状态是否已经取消，幂等判断
        if(OrderStatus.CLOSED.equalsValue(order.getStatus())){
           // 订单已经取消，无需重复操作
           return;
        }
        // 3.判断订单是否未支付，只有未支付订单才可以取消
        if(!OrderStatus.NO_PAY.equalsValue(order.getStatus())){
            throw new BizIllegalException(ORDER_ALREADY_FINISH);
        }
        // 4.可以更新订单状态为取消了
        boolean success = lambdaUpdate()
                .set(Order::getStatus, OrderStatus.CLOSED.getValue())
                .set(Order::getMessage, cancelReason.getMsg())
                .set(Order::getCloseTime, LocalDateTime.now())
                .eq(Order::getStatus, OrderStatus.NO_PAY.getValue())
                .eq(Order::getId, orderId)
                .update();
        if (!success) {
            return;
        }
        // 5.更新订单条目的状态
        detailService.updateStatusByOrderId(orderId, OrderStatus.CLOSED.getValue());

        // 6.退还优惠券
        campaignClient.refundVoucher(order.getVoucherIds());
    }

    @Override
    public void deleteOrder(Long id) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.查询订单
        Order order = getById(id);
        if (order == null) {
            return;
        }
        // 3.判断订单所属用户与当前登录用户是否一致
        if(!order.getUserId().equals(userId)){
            // 不一致，说明不是当前用户的订单，结束
            throw new BadRequestException("不能删除他人订单");
        }
        // 4.删除订单
        boolean success = removeById(id);
        if (!success) {
            throw new DbException(OPERATE_FAILED);
        }
    }

    @Override
    public PageDTO<OrderPageVO> queryMyOrderPage(OrderPageQuery pageQuery) {
        Long userId = UserContext.getUser();
        // 1.分页排序条件
        Page<Order> p = pageQuery.toMpPageDefaultSortByCreateTimeDesc();
        // 2.分页查询订单
        Integer status = pageQuery.getStatus();
        Page<Order> page = lambdaQuery()
                .eq(status != null, Order::getStatus, status)
                .eq(Order::getUserId, userId)
                .page(p);
        // 3.数据判断
        List<Order> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(p);
        }
        // 4.查询订单明细信息
        List<Long> orderIds = records.stream().map(Order::getId).collect(Collectors.toList());
        // 4.1.根据订单id查询订单明细
        List<OrderDetail> details = detailService.queryByOrderIds(orderIds);
        // 4.2.将订单明细分组，key是订单id，值是订单下的所有detail
        Map<Long, List<OrderDetailVO>> detailMap = details.stream()
                .map(od -> BeanUtils.copyBean(od, OrderDetailVO.class))
                .collect(Collectors.groupingBy(OrderDetailVO::getOrderId));
        // 5.转换VO
        List<OrderPageVO> list = new ArrayList<>(orderIds.size());
        for (Order record : records) {
            // 5.1.转换订单
            OrderPageVO v = BeanUtils.toBean(record, OrderPageVO.class);
            list.add(v);
            // 5.2.写入vo
            v.setDetails(detailMap.get(record.getId()));
            v.setStatusDesc(OrderStatus.desc(v.getStatus()));
        }
        return PageDTO.of(page, list);
    }

    @Override
    public OrderVO queryOrderById(Long id) {
        // 1.查询订单
        Order order = getById(id);
        if (order == null) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 2.查询订单详情
        List<OrderDetail> details = detailService.queryByOrderId(id);
        // 3.转换VO
        // 3.1.订单
        OrderVO vo = BeanUtils.toBean(order, OrderVO.class);
        // 3.2.订单详情
        List<OrderDetailVO> dvs = BeanUtils.copyList(details, OrderDetailVO.class, (d, v) -> v.setCanRefund(
                // 订单已经支付，且 退款没有在进行中，标记为可退款状态
                OrderStatus.canRefund(d.getStatus()) && !RefundStatus.inProgress(v.getRefundStatus())
        ));
        vo.setDetails(dvs);
        // 3.3.订单进度
        vo.setProgressNodes(detailService.packageProgressNodes(order, null));

        // 3.4.优惠明细
        List<String> rules = campaignClient.queryDiscountRules(order.getVoucherIds());
        vo.setVoucherDesc(String.join("/", rules));
        return vo;
    }

    @Override
    public PlaceOrderResultVO queryOrderStatus(Long orderId) {
        // 1.查询订单
        Order order = getById(orderId);
        if (order == null) {
            throw new BizIllegalException(ORDER_NOT_EXISTS);
        }
        // 2.计算超时时间
        LocalDateTime outTime = null;
        if(OrderStatus.NO_PAY.equalsValue(order.getStatus())){
            outTime = order.getCreateTime().plusMinutes(tradeProperties.getPayOrderTTLMinutes());
        }
        // 3.封装结果
        return PlaceOrderResultVO.builder()
                .orderId(orderId)
                .payAmount(order.getRealAmount())
                .status(order.getStatus())
                .payOutTime(outTime)
                .build();
    }

    @Override
    @Transactional
    public void handlePaySuccess(PayResultDTO payResult) {
        // 1.查询订单
        Order order = getById(payResult.getBizOrderId());
        if (order == null) {
            return;
        }
        // 2.更新订单状态
        Order o = new Order();
        o.setId(order.getId());
        o.setStatus(OrderStatus.PAYED.getValue());
        o.setPayTime(payResult.getSuccessTime());
        o.setPayChannel(payResult.getPayChannel());
        o.setPayOrderNo(payResult.getPayOrderNo());
        o.setMessage("用户支付成功");
        updateById(o);
        // 3.更新订单条目
        detailService.markDetailSuccessByOrderId(o.getId(), payResult.getPayChannel(), payResult.getSuccessTime());
        // 4.查询订单包含的训练信息
        List<Long> cIds = detailService.queryTrainingIdsByOrderId(o.getId());
        // 5.发送MQ消息，通知报名成功
        rabbitMqHelper.send(
                MqConstants.Exchange.ORDER_EXCHANGE,
                MqConstants.Key.ORDER_PAY_KEY,
                OrderBasicDTO.builder()
                        .orderId(o.getId()).userId(order.getUserId()).trainingIds(cIds)
                        .finishTime(o.getPayTime())
                        .build()
        );
    }

}

