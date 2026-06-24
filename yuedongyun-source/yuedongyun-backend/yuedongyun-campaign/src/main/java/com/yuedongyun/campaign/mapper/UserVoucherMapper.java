package com.yuedongyun.campaign.mapper;

import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.po.UserVoucher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.campaign.enums.UserVoucherStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 Mapper 接口
 * </p>
 *
 * @author 虎哥
 */
public interface UserVoucherMapper extends BaseMapper<UserVoucher> {

    List<Voucher> queryMyVouchers(@Param("userId") Long userId);

    List<Voucher> queryVoucherByUserVoucherIds(
            @Param("userVoucherIds") List<Long> userVoucherIds,@Param("status")  UserVoucherStatus status);
}

