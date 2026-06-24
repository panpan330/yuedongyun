package com.yuedongyun.search.mq;

import com.yuedongyun.search.service.ITrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yuedongyun.common.constants.MqConstants.Exchange.TRAINING_EXCHANGE;
import static com.yuedongyun.common.constants.MqConstants.Key.*;

@Slf4j
@Component
public class TrainingEventListener {

    @Autowired
    private ITrainingService trainingService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.training.up.queue", durable = "true"),
            exchange = @Exchange(name = TRAINING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = TRAINING_UP_KEY
    ))
    public void listenTrainingUp(Long trainingId){
        log.debug("监听到训练{}上架", trainingId);
        trainingService.handleTrainingUp(trainingId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.training.down.queue", durable = "true"),
            exchange = @Exchange(name = TRAINING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = TRAINING_DOWN_KEY
    ))
    public void listenTrainingDown(Long trainingId){
        log.debug("监听到训练{}下架", trainingId);
        trainingService.handleTrainingDelete(trainingId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.training.expire.queue", durable = "true"),
            exchange = @Exchange(name = TRAINING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = TRAINING_EXPIRE_KEY
    ))
    public void listenTrainingExpire(Long trainingId){
        trainingService.handleTrainingDelete(trainingId);
    }
}

