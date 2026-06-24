package com.yuedongyun.campaign.service;

import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import com.yuedongyun.api.dto.campaign.OrderVoucherDTO;
import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;

import java.util.List;

public interface IDiscountService {
    List<VoucherDiscountDTO> findDiscountSolution(List<OrderTrainingDTO> orderTrainings);

    VoucherDiscountDTO queryDiscountDetailByOrder(OrderVoucherDTO orderVoucherDTO);
}

