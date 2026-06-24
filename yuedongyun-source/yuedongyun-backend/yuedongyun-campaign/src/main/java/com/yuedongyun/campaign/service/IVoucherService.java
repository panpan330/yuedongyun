package com.yuedongyun.campaign.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.campaign.domain.dto.VoucherFormDTO;
import com.yuedongyun.campaign.domain.dto.VoucherIssueFormDTO;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.query.VoucherQuery;
import com.yuedongyun.campaign.domain.vo.VoucherDetailVO;
import com.yuedongyun.campaign.domain.vo.VoucherPageVO;
import com.yuedongyun.campaign.domain.vo.VoucherVO;

import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IVoucherService extends IService<Voucher> {

    void saveVoucher(VoucherFormDTO dto);

    PageDTO<VoucherPageVO> queryVoucherByPage(VoucherQuery query);

    void beginIssue(VoucherIssueFormDTO dto);

    List<VoucherVO> queryIssuingVouchers();

    void pauseIssue(Long id);

    void deleteById(Long id);

    VoucherDetailVO queryVoucherById(Long id);

    void beginIssueBatch(List<Voucher> vouchers);
}

