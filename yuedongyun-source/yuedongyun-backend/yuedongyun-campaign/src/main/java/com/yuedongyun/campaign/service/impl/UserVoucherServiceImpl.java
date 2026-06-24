package com.yuedongyun.campaign.service.impl;

import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.common.autoconfigure.mq.RabbitMqHelper;
import com.yuedongyun.common.constants.MqConstants;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.NumberUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.campaign.constants.CampaignConstants;
import com.yuedongyun.campaign.domain.dto.UserVoucherDTO;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.po.ExchangeCode;
import com.yuedongyun.campaign.domain.po.UserVoucher;
import com.yuedongyun.campaign.domain.query.UserVoucherQuery;
import com.yuedongyun.campaign.domain.vo.VoucherVO;
import com.yuedongyun.campaign.enums.ExchangeCodeStatus;
import com.yuedongyun.campaign.enums.UserVoucherStatus;
import com.yuedongyun.campaign.mapper.VoucherMapper;
import com.yuedongyun.campaign.mapper.UserVoucherMapper;
import com.yuedongyun.campaign.service.IExchangeCodeService;
import com.yuedongyun.campaign.service.IUserVoucherService;
import com.yuedongyun.campaign.strategy.discount.DiscountStrategy;
import com.yuedongyun.campaign.utils.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yuedongyun.campaign.constants.CampaignConstants.VOUCHER_CODE_MAP_KEY;
import static com.yuedongyun.campaign.constants.CampaignConstants.VOUCHER_RANGE_KEY;

/**
 * <p>
 * 用户领取优惠券的记录，是真正使用的优惠券信息 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
@RequiredArgsConstructor
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher> implements IUserVoucherService {

    private final VoucherMapper voucherMapper;

    private final IExchangeCodeService codeService;

    private final StringRedisTemplate redisTemplate;

    private final RabbitMqHelper mqHelper;

    private static final RedisScript<Long> RECEIVE_VOUCHER_SCRIPT;
    private static final RedisScript<String> EXCHANGE_VOUCHER_SCRIPT;

    static {
        RECEIVE_VOUCHER_SCRIPT = RedisScript.of(new ClassPathResource("lua/receive_voucher.lua"), Long.class);
        EXCHANGE_VOUCHER_SCRIPT = RedisScript.of(new ClassPathResource("lua/exchange_voucher.lua"), String.class);
    }

    @Override
    // @Lock(name = "lock:voucher:#{voucherId}")
    public void receiveVoucher(Long voucherId) {
        /*// 1.查询优惠券
        Voucher voucher = queryVoucherByCache(voucherId);
        if (voucher == null) {
            throw new BadRequestException("优惠券不存在");
        }
        // 2.校验发放时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getIssueBeginTime()) || now.isAfter(voucher.getIssueEndTime())) {
            throw new BadRequestException("优惠券发放已经结束或尚未开始");
        }
        // 3.校验库存
        if (voucher.getTotalNum() <= 0) {
            throw new BadRequestException("优惠券库存不足");
        }
        Long userId = UserContext.getUser();
        // 4.校验每人限领数量
        // 4.1.查询领取数量
        String key = CampaignConstants.USER_VOUCHER_CACHE_KEY_PREFIX + voucherId;
        Long count = redisTemplate.opsForHash().increment(key, userId.toString(), 1);
        // 4.2.校验限领数量
        if(count > voucher.getUserLimit()){
            throw new BadRequestException("超出领取数量");
        }
        // 5.扣减优惠券库存
        redisTemplate.opsForHash().increment(
                CampaignConstants.VOUCHER_CACHE_KEY_PREFIX + voucherId, "totalNum", -1);
*/
        // 1.执行LUA脚本，判断结果
        // 1.1.准备参数
        String key1 = CampaignConstants.VOUCHER_CACHE_KEY_PREFIX + voucherId;
        String key2 = CampaignConstants.USER_VOUCHER_CACHE_KEY_PREFIX + voucherId;
        Long userId = UserContext.getUser();
        // 1.2.执行脚本
        Long r = redisTemplate.execute(RECEIVE_VOUCHER_SCRIPT, List.of(key1, key2), userId.toString());
        int result = NumberUtils.null2Zero(r).intValue();
        if (result != 0) {
            // 结果大于0，说明出现异常
            throw new BizIllegalException(CampaignConstants.RECEIVE_VOUCHER_ERROR_MSG[result - 1]);
        }
        // 2.发送MQ消息
        UserVoucherDTO uc = new UserVoucherDTO();
        uc.setUserId(userId);
        uc.setVoucherId(voucherId);
        mqHelper.send(MqConstants.Exchange.CAMPAIGN_EXCHANGE, MqConstants.Key.VOUCHER_RECEIVE, uc);
    }

    private Voucher queryVoucherByCache(Long voucherId) {
        // 1.准备KEY
        String key = CampaignConstants.VOUCHER_CACHE_KEY_PREFIX + voucherId;
        // 2.查询
        Map<Object, Object> objMap = redisTemplate.opsForHash().entries(key);
        if (objMap.isEmpty()) {
            return null;
        }
        // 3.数据反序列化
        return BeanUtils.mapToBean(objMap, Voucher.class, false, CopyOptions.create());
    }

    @Transactional
    @Override
    public void checkAndCreateUserVoucher(UserVoucherDTO uc) {
        // 1.查询优惠券
        Voucher voucher = voucherMapper.selectById(uc.getVoucherId());
        if (voucher == null) {
            throw new BizIllegalException("优惠券不存在！");
        }
        // 2.更新优惠券的已经发放的数量 + 1
        int r = voucherMapper.incrIssueNum(voucher.getId());
        if (r == 0) {
            throw new BizIllegalException("优惠券库存不足！");
        }
        // 3.新增一个用户券
        saveUserVoucher(voucher, uc.getUserId());

        // 4.更新兑换码状态
        if (uc.getSerialNum() != null) {
            codeService.lambdaUpdate()
                    .set(ExchangeCode::getUserId, uc.getUserId())
                    .set(ExchangeCode::getStatus, ExchangeCodeStatus.USED)
                    .eq(ExchangeCode::getId, uc.getSerialNum())
                    .update();
        }
    }

    @Override
    // @Lock(name = "lock:voucher:#{T(com.yuedongyun.common.utils.UserContext).getUser()}")
    public void exchangeVoucher(String code) {
        // 1.校验并解析兑换码
        long serialNum = CodeUtil.parseCode(code);
        // 2.执行LUA脚本
        Long userId = UserContext.getUser();
        String result = redisTemplate.execute(
                EXCHANGE_VOUCHER_SCRIPT,
                List.of(VOUCHER_CODE_MAP_KEY, VOUCHER_RANGE_KEY),
                String.valueOf(serialNum), String.valueOf(serialNum + 5000), userId.toString());
        long r = NumberUtils.parseLong(result);
        if (r < 10) {
            // 异常结果应该是在1~5之间
            throw new BizIllegalException(CampaignConstants.EXCHANGE_VOUCHER_ERROR_MSG[(int) (r - 1)]);
        }
        // 3.发送MQ消息通知
        UserVoucherDTO uc = new UserVoucherDTO();
        uc.setUserId(userId);
        uc.setVoucherId(r);
        uc.setSerialNum((int) serialNum);
        mqHelper.send(MqConstants.Exchange.CAMPAIGN_EXCHANGE, MqConstants.Key.VOUCHER_RECEIVE, uc);
        /*// 2.校验是否已经兑换 SETBIT KEY 4 1
        boolean exchanged = codeService.updateExchangeMark(serialNum, true);
        if (exchanged) {
            throw new BizIllegalException("兑换码已经被兑换过了");
        }
        try {
            // 3.查询兑换码对应的优惠券id
            Long voucherId = codeService.exchangeTargetId(serialNum);
            if (voucherId == null) {
                throw new BizIllegalException("兑换码不存在！");
            }
            Voucher voucher = voucherMapper.selectById(voucherId);
            // 4.是否过期
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(voucher.getIssueEndTime()) || now.isBefore(voucher.getIssueBeginTime())) {
                throw new BizIllegalException("优惠券活动未开始或已经结束");
            }

            // 5.校验每人限领数量
            Long userId = UserContext.getUser();
            // 5.1.查询领取数量
            String key = CampaignConstants.USER_VOUCHER_CACHE_KEY_PREFIX + voucherId;
            Long count = redisTemplate.opsForHash().increment(key, userId.toString(), 1);
            // 5.2.校验限领数量
            if(count > voucher.getUserLimit()){
                throw new BadRequestException("超出领取数量");
            }

            // 6.发送MQ消息通知
            UserVoucherDTO uc = new UserVoucherDTO();
            uc.setUserId(userId);
            uc.setVoucherId(voucherId);
            uc.setSerialNum((int) serialNum);
            mqHelper.send(MqConstants.Exchange.CAMPAIGN_EXCHANGE, MqConstants.Key.VOUCHER_RECEIVE, uc);
        } catch (Exception e) {
            // 重置兑换的标记 0
            codeService.updateExchangeMark(serialNum, false);
            throw e;
        }*/
    }

    @Override
    public PageDTO<VoucherVO> queryMyVoucherPage(UserVoucherQuery query) {
        // 1.获取当前用户
        Long userId = UserContext.getUser();
        // 2.分页查询用户券
        Page<UserVoucher> page = lambdaQuery()
                .eq(UserVoucher::getUserId, userId)
                .eq(UserVoucher::getStatus, query.getStatus())
                .page(query.toMpPage(new OrderItem("term_end_time", true)));
        List<UserVoucher> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 3.获取优惠券详细信息
        // 3.1.获取用户券关联的优惠券id
        Set<Long> voucherIds = records.stream().map(UserVoucher::getVoucherId).collect(Collectors.toSet());
        // 3.2.查询
        List<Voucher> vouchers = voucherMapper.selectBatchIds(voucherIds);

        // 4.封装VO
        return PageDTO.of(page, BeanUtils.copyList(vouchers, VoucherVO.class));
    }

    @Override
    @Transactional
    public void writeOffVoucher(List<Long> userVoucherIds) {
        // 1.查询优惠券
        List<UserVoucher> userVouchers = listByIds(userVoucherIds);
        if (CollUtils.isEmpty(userVouchers)) {
            return;
        }
        // 2.处理数据
        List<UserVoucher> list = userVouchers.stream()
                // 过滤无效券
                .filter(voucher -> {
                    if (voucher == null) {
                        return false;
                    }
                    if (UserVoucherStatus.UNUSED != voucher.getStatus()) {
                        return false;
                    }
                    LocalDateTime now = LocalDateTime.now();
                    return !now.isBefore(voucher.getTermBeginTime()) && !now.isAfter(voucher.getTermEndTime());
                })
                // 组织新增数据
                .map(voucher -> {
                    UserVoucher c = new UserVoucher();
                    c.setId(voucher.getId());
                    c.setStatus(UserVoucherStatus.USED);
                    return c;
                })
                .collect(Collectors.toList());

        // 4.核销，修改优惠券状态
        boolean success = updateBatchById(list);
        if (!success) {
            return;
        }
        // 5.更新已使用数量
        List<Long> voucherIds = userVouchers.stream().map(UserVoucher::getVoucherId).collect(Collectors.toList());
        int c = voucherMapper.incrUsedNum(voucherIds, 1);
        if (c < 1) {
            throw new DbException("更新优惠券使用数量失败！");
        }
    }

    @Override
    @Transactional
    public void refundVoucher(List<Long> userVoucherIds) {
        // 1.查询优惠券
        List<UserVoucher> userVouchers = listByIds(userVoucherIds);
        if (CollUtils.isEmpty(userVouchers)) {
            return;
        }
        // 2.处理优惠券数据
        List<UserVoucher> list = userVouchers.stream()
                // 过滤无效券
                .filter(voucher -> voucher != null && UserVoucherStatus.USED == voucher.getStatus())
                // 更新状态字段
                .map(voucher -> {
                    UserVoucher c = new UserVoucher();
                    c.setId(voucher.getId());
                    // 3.判断有效期，是否已经过期，如果过期，则状态为 已过期，否则状态为 未使用
                    LocalDateTime now = LocalDateTime.now();
                    UserVoucherStatus status = now.isAfter(voucher.getTermEndTime()) ?
                            UserVoucherStatus.EXPIRED : UserVoucherStatus.UNUSED;
                    c.setStatus(status);
                    return c;
                }).collect(Collectors.toList());

        // 4.修改优惠券状态
        boolean success = updateBatchById(list);
        if (!success) {
            return;
        }
        // 5.更新已使用数量
        List<Long> voucherIds = userVouchers.stream().map(UserVoucher::getVoucherId).collect(Collectors.toList());
        int c = voucherMapper.incrUsedNum(voucherIds, -1);
        if (c < 1) {
            throw new DbException("更新优惠券使用数量失败！");
        }
    }

    @Override
    public List<String> queryDiscountRules(List<Long> userVoucherIds) {
        // 1.查询优惠券信息
        List<Voucher> vouchers = baseMapper.queryVoucherByUserVoucherIds(userVoucherIds, UserVoucherStatus.USED);
        if (CollUtils.isEmpty(vouchers)) {
            return CollUtils.emptyList();
        }
        // 2.转换规则
        return vouchers.stream()
                .map(c -> DiscountStrategy.getDiscount(c.getDiscountType()).getRule(c))
                .collect(Collectors.toList());
    }

    private void saveUserVoucher(Voucher voucher, Long userId) {
        // 1.基本信息
        UserVoucher uc = new UserVoucher();
        uc.setUserId(userId);
        uc.setVoucherId(voucher.getId());
        // 2.有效期信息
        LocalDateTime termBeginTime = voucher.getTermBeginTime();
        LocalDateTime termEndTime = voucher.getTermEndTime();
        if (termBeginTime == null) {
            termBeginTime = LocalDateTime.now();
            termEndTime = termBeginTime.plusDays(voucher.getTermDays());
        }
        uc.setTermBeginTime(termBeginTime);
        uc.setTermEndTime(termEndTime);
        // 3.保存
        save(uc);
    }
}

