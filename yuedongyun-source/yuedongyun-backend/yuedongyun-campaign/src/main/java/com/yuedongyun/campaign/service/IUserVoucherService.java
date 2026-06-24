package com.yuedongyun.campaign.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.campaign.domain.dto.UserVoucherDTO;
import com.yuedongyun.campaign.domain.po.UserVoucher;
import com.yuedongyun.campaign.domain.query.UserVoucherQuery;
import com.yuedongyun.campaign.domain.vo.VoucherVO;

import java.util.List;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IUserVoucherService extends IService<UserVoucher> {
    void receiveVoucher(Long voucherId);

    void checkAndCreateUserVoucher(UserVoucherDTO uc);

    void exchangeVoucher(String code);

    PageDTO<VoucherVO> queryMyVoucherPage(UserVoucherQuery query);

    void writeOffVoucher(List<Long> userVoucherIds);

    void refundVoucher(List<Long> userVoucherIds);

    List<String> queryDiscountRules(List<Long> userVoucherIds);
}

