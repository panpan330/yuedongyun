package com.yuedongyun.campaign.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.po.ExchangeCode;
import com.yuedongyun.campaign.domain.query.CodeQuery;
import com.yuedongyun.campaign.domain.vo.ExchangeCodeVO;

/**
 * <p>
 * 兑换码 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IExchangeCodeService extends IService<ExchangeCode> {
    void asyncGenerateCode(Voucher voucher);

    boolean updateExchangeMark(long serialNum, boolean mark);

    PageDTO<ExchangeCodeVO> queryCodePage(CodeQuery query);

    Long exchangeTargetId(long serialNum);
}

