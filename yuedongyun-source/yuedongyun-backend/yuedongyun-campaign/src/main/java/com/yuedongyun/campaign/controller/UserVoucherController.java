package com.yuedongyun.campaign.controller;

import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import com.yuedongyun.api.dto.campaign.OrderVoucherDTO;
import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.campaign.domain.query.UserVoucherQuery;
import com.yuedongyun.campaign.domain.vo.VoucherVO;
import com.yuedongyun.campaign.service.IDiscountService;
import com.yuedongyun.campaign.service.IUserVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 控制器
 * </p>
 *
 * @author 虎哥
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-vouchers")
@Api(tags = "优惠券相关接口")
public class UserVoucherController {

    private final IUserVoucherService userVoucherService;

    private final IDiscountService discountService;

    @ApiOperation("领取优惠券接口")
    @PostMapping("/{voucherId}/receive")
    public void receiveVoucher(@PathVariable("voucherId") Long voucherId){
        userVoucherService.receiveVoucher(voucherId);
    }

    @ApiOperation("兑换码兑换优惠券接口")
    @PostMapping("/{code}/exchange")
    public void exchangeVoucher(@PathVariable("code") String code){
        userVoucherService.exchangeVoucher(code);
    }

    @ApiOperation("分页查询我的优惠券接口")
    @GetMapping("page")
    public PageDTO<VoucherVO> queryMyVoucherPage(UserVoucherQuery query){
        return userVoucherService.queryMyVoucherPage(query);
    }

    @ApiOperation("查询我的优惠券可用方案")
    @PostMapping("/available")
    public List<VoucherDiscountDTO> findDiscountSolution(@RequestBody List<OrderTrainingDTO> orderTrainings){
        return discountService.findDiscountSolution(orderTrainings);
    }

    @ApiOperation("根据券方案计算订单优惠明细")
    @PostMapping("/discount")
    public VoucherDiscountDTO queryDiscountDetailByOrder(@RequestBody OrderVoucherDTO orderVoucherDTO){
        return discountService.queryDiscountDetailByOrder(orderVoucherDTO);
    }

    @ApiOperation("核销指定优惠券")
    @PutMapping("/use")
    public void writeOffVoucher(@ApiParam("用户优惠券id集合") @RequestParam("voucherIds") List<Long> userVoucherIds){
        userVoucherService.writeOffVoucher(userVoucherIds);
    }

    @ApiOperation("退还指定优惠券")
    @PutMapping("/refund")
    public void refundVoucher(@ApiParam("用户优惠券id集合") @RequestParam("voucherIds") List<Long> userVoucherIds){
        userVoucherService.refundVoucher(userVoucherIds);
    }

    @ApiOperation("分页查询我的优惠券接口")
    @GetMapping("/rules")
    public List<String> queryDiscountRules(
            @ApiParam("用户优惠券id集合") @RequestParam("voucherIds") List<Long> userVoucherIds){
        return userVoucherService.queryDiscountRules(userVoucherIds);
    }
}

