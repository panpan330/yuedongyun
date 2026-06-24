package com.yuedongyun.workout.mq;

import com.yuedongyun.common.constants.MqConstants;
import com.yuedongyun.workout.enums.FitPointRecordType;
import com.yuedongyun.workout.mq.message.SignInMessage;
import com.yuedongyun.workout.service.IFitPointRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkoutFitPointListener {

    private final IFitPointRecordService recordService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "qa.fitpoints.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.WORKOUT_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.WRITE_REPLY
    ))
    public void listenWriteReplyMessage(Long userId){
        recordService.addFitPointRecord(userId, 5, FitPointRecordType.QA);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sign.fitpoints.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.WORKOUT_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.SIGN_IN
    ))
    public void listenSignInMessage(SignInMessage message){
        recordService.addFitPointRecord(message.getUserId(), message.getFitPoint(), FitPointRecordType.SIGN);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "workout.fitpoints.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.WORKOUT_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.LEARN_SECTION
    ))
    public void listenLearnSessionMessage(Long userId){
        recordService.addFitPointRecord(userId, 10, FitPointRecordType.WORKOUT);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "note.new.fitpoints.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.WORKOUT_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.WRITE_NOTE
    ))
    public void listenWriteNodeMessage(Long userId){
        recordService.addFitPointRecord(userId, 3, FitPointRecordType.NOTE);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "note.gathered.fitpoints.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.WORKOUT_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.NOTE_GATHERED
    ))
    public void listenNodeGatheredMessage(Long userId){
        recordService.addFitPointRecord(userId, 2, FitPointRecordType.NOTE);
    }
}

