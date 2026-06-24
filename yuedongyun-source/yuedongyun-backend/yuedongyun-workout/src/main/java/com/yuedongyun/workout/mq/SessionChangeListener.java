package com.yuedongyun.workout.mq;

import com.yuedongyun.api.dto.trade.OrderBasicDTO;
import com.yuedongyun.common.constants.MqConstants;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.workout.service.IWorkoutSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionChangeListener {

    private final IWorkoutSessionService sessionService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "workout.session.pay.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.ORDER_PAY_KEY
    ))
    public void listenSessionPay(OrderBasicDTO order){
        // 1.健壮性处理
        if(order == null || order.getUserId() == null || CollUtils.isEmpty(order.getTrainingIds())){
            // 数据有误，无需处理
            log.error("接收到MQ消息有误，订单数据为空");
            return;
        }
        // 2.添加训练
        log.debug("监听到用户{}的订单{}，需要添加训练{}到课表中", order.getUserId(), order.getOrderId(), order.getTrainingIds());
        sessionService.addUserSessions(order.getUserId(), order.getTrainingIds());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "workout.session.refund.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.ORDER_REFUND_KEY
    ))
    public void listenSessionRefund(OrderBasicDTO order){
        // 1.健壮性处理
        if(order == null || order.getUserId() == null || CollUtils.isEmpty(order.getTrainingIds())){
            // 数据有误，无需处理
            log.error("接收到MQ消息有误，订单数据为空");
            return;
        }
        // 2.添加训练
        log.debug("监听到用户{}的订单{}要退款，需要删除训练{}", order.getUserId(), order.getOrderId(), order.getTrainingIds());
        sessionService.deleteTrainingFromSession(order.getUserId(), order.getTrainingIds().get(0));
    }
}

