package com.yuedongyun.campaign.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.campaign.domain.po.Voucher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 Mapper 接口
 * </p>
 *
 * @author 虎哥
 */
public interface VoucherMapper extends BaseMapper<Voucher> {
    @Update("UPDATE voucher SET issue_num = issue_num + 1 WHERE id = #{voucherId} AND issue_num < total_num")
    int incrIssueNum(@Param("voucherId") Long voucherId);

    int incrUsedNum(List<Long> voucherIds, int amount);
}

