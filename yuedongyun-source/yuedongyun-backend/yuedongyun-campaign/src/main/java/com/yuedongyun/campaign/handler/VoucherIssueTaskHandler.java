package com.yuedongyun.campaign.handler;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.enums.VoucherStatus;
import com.yuedongyun.campaign.service.IVoucherService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoucherIssueTaskHandler {

    private final IVoucherService voucherService;

    @XxlJob("voucherIssueJobHandler")
    public void handleVoucherIssueJob(){
        // 1.获取分片信息，作为页码，每页最多查询 20条
        int index = XxlJobHelper.getShardIndex() + 1;
        int size = Integer.parseInt(XxlJobHelper.getJobParam());
        // 2.查询<<未开始>>的优惠券
        Page<Voucher> page = voucherService.lambdaQuery()
                .eq(Voucher::getStatus, VoucherStatus.UN_ISSUE)
                .le(Voucher::getTermBeginTime, LocalDateTime.now())
                .page(new Page<>(index, size));
        // 3.发放优惠券
        List<Voucher> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return;
        }
        voucherService.beginIssueBatch(records);
    }
}

