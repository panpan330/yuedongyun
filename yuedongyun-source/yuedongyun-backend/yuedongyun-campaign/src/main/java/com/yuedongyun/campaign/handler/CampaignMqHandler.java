package com.yuedongyun.campaign.handler;

import com.yuedongyun.campaign.domain.dto.UserVoucherDTO;
import com.yuedongyun.campaign.service.IUserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.yuedongyun.common.constants.MqConstants.Exchange.CAMPAIGN_EXCHANGE;
import static com.yuedongyun.common.constants.MqConstants.Key.VOUCHER_RECEIVE;

@RequiredArgsConstructor
@Component
public class CampaignMqHandler {

    private final IUserVoucherService userVoucherService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "voucher.receive.queue", durable = "true"),
            exchange = @Exchange(name = CAMPAIGN_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = VOUCHER_RECEIVE
    ))
    public void listenVoucherReceiveMessage(UserVoucherDTO uc){
        userVoucherService.checkAndCreateUserVoucher(uc);
    }
}

