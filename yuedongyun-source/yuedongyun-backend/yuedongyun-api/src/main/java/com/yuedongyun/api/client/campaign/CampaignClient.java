package com.yuedongyun.api.client.campaign;

import com.yuedongyun.api.client.campaign.fallback.CampaignClientFallback;
import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import com.yuedongyun.api.dto.campaign.OrderVoucherDTO;
import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "campaign-service", fallbackFactory = CampaignClientFallback.class)
public interface CampaignClient {

    @PostMapping("/user-vouchers/available")
    List<VoucherDiscountDTO> findDiscountSolution(@RequestBody List<OrderTrainingDTO> orderTrainings);

    @ApiOperation("根据券方案计算订单优惠明细")
    @PostMapping("/user-vouchers/discount")
    VoucherDiscountDTO queryDiscountDetailByOrder(@RequestBody OrderVoucherDTO orderVoucherDTO);

    @ApiOperation("核销指定优惠券")
    @PutMapping("/user-vouchers/use")
    void writeOffVoucher(@ApiParam("用户优惠券id集合") @RequestParam("voucherIds") List<Long> userVoucherIds);

    @ApiOperation("退还指定优惠券")
    @PutMapping("/user-vouchers/refund")
    void refundVoucher(@ApiParam("用户优惠券id集合") @RequestParam("voucherIds") List<Long> userVoucherIds);

    @ApiOperation("分页查询我的优惠券接口")
    @GetMapping("/user-vouchers/rules")
    List<String> queryDiscountRules(@ApiParam("用户优惠券id集合") @RequestParam("voucherIds") List<Long> userVoucherIds);
}

