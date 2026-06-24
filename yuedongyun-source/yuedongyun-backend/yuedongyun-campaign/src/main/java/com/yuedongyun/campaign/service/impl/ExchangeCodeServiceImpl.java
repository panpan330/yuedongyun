package com.yuedongyun.campaign.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.po.ExchangeCode;
import com.yuedongyun.campaign.domain.query.CodeQuery;
import com.yuedongyun.campaign.domain.vo.ExchangeCodeVO;
import com.yuedongyun.campaign.mapper.ExchangeCodeMapper;
import com.yuedongyun.campaign.service.IExchangeCodeService;
import com.yuedongyun.campaign.utils.CodeUtil;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.yuedongyun.campaign.constants.CampaignConstants.*;

/**
 * <p>
 * 兑换码 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode> implements IExchangeCodeService {

    private final StringRedisTemplate redisTemplate;
    private final BoundValueOperations<String, String> serialOps;

    public ExchangeCodeServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.serialOps = redisTemplate.boundValueOps(VOUCHER_CODE_SERIAL_KEY);
    }

    @Override
    @Async("generateExchangeCodeExecutor")
    public void asyncGenerateCode(Voucher voucher) {
        // 发放数量
        Integer totalNum = voucher.getTotalNum();
        // 1.获取Redis自增序列号
        Long result = serialOps.increment(totalNum);
        if (result == null) {
            return;
        }
        int maxSerialNum = result.intValue();
        List<ExchangeCode> list = new ArrayList<>(totalNum);
        for (int serialNum = maxSerialNum - totalNum + 1; serialNum <= maxSerialNum; serialNum++) {
            // 2.生成兑换码
            String code = CodeUtil.generateCode(serialNum, voucher.getId());
            ExchangeCode e = new ExchangeCode();
            e.setCode(code);
            e.setId(serialNum);
            e.setExchangeTargetId(voucher.getId());
            e.setExpiredTime(voucher.getIssueEndTime());
            list.add(e);
        }
        // 3.保存数据库
        saveBatch(list);

        // 4.写入Redis缓存，member：voucherId，score：兑换码的最大序列号
        redisTemplate.opsForZSet().add(VOUCHER_RANGE_KEY, voucher.getId().toString(), maxSerialNum);
    }

    @Override
    public boolean updateExchangeMark(long serialNum, boolean mark) {
        Boolean boo = redisTemplate.opsForValue().setBit(VOUCHER_CODE_MAP_KEY, serialNum, mark);
        return boo != null && boo;
    }

    @Override
    public PageDTO<ExchangeCodeVO> queryCodePage(CodeQuery query) {
        // 1.分页查询兑换码
        Page<ExchangeCode> page = lambdaQuery()
                .eq(ExchangeCode::getStatus, query.getStatus())
                .eq(ExchangeCode::getExchangeTargetId, query.getVoucherId())
                .page(query.toMpPage());
        // 2.返回数据
        return PageDTO.of(page, c -> new ExchangeCodeVO(c.getId(), c.getCode()));
    }

    @Override
    public Long exchangeTargetId(long serialNum) {
        // 1.查询score值比当前序列号大的第一个优惠券
        Set<String> results = redisTemplate.opsForZSet().rangeByScore(
                VOUCHER_RANGE_KEY, serialNum, serialNum + 5000, 0L, 1L);
        if (CollUtils.isEmpty(results)) {
            return null;
        }
        // 2.数据转换
        String next = results.iterator().next();
        return Long.parseLong(next);
    }
}

