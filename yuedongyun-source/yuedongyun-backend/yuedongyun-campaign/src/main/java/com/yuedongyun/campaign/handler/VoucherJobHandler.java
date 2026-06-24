package com.yuedongyun.campaign.handler;

import com.yuedongyun.common.utils.NumberUtils;
import com.yuedongyun.campaign.service.IVoucherService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherJobHandler {

    private final IVoucherService voucherService;

    @XxlJob("voucherIssueJobHandler")
    public void handleVoucherIssueJob() {
        // 1.获取分片信息，作为分页信息
        int page = XxlJobHelper.getShardIndex();
        String param = XxlJobHelper.getJobParam();
        int size = NumberUtils.parseInt(param);
        log.debug("准备开始处理优惠券发放任务，page={},size={}", page, size);
        // 2.分页处理待发放状态的优惠券
        voucherService.issueVoucherByPage(page, size);
    }
}

