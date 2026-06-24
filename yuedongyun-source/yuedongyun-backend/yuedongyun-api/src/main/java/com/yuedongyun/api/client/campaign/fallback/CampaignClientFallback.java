package com.yuedongyun.api.client.campaign.fallback;

import com.yuedongyun.api.client.campaign.CampaignClient;
import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import com.yuedongyun.api.dto.campaign.OrderVoucherDTO;
import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.common.exceptions.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class CampaignClientFallback implements FallbackFactory<CampaignClient> {
    @Override
    public CampaignClient create(Throwable cause) {
        log.error("查询促销服务出现异常，", cause);
        return new CampaignClient() {
            @Override
            public List<VoucherDiscountDTO> findDiscountSolution(List<OrderTrainingDTO> orderTrainings) {
                return Collections.emptyList();
            }

            @Override
            public VoucherDiscountDTO queryDiscountDetailByOrder(OrderVoucherDTO orderVoucherDTO) {
                return null;
            }

            @Override
            public void writeOffVoucher(List<Long> userVoucherIds) {
                throw new BizIllegalException(500, "核销优惠券异常", cause);
            }

            @Override
            public void refundVoucher(List<Long> userVoucherIds) {
                throw new BizIllegalException(500, "退还优惠券异常", cause);
            }

            @Override
            public List<String> queryDiscountRules(List<Long> userVoucherIds) {
                return Collections.emptyList();
            }
        };
    }
}

