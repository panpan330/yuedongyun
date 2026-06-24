package com.yuedongyun.campaign.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.cache.CategoryCache;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.utils.*;
import com.yuedongyun.campaign.constants.CampaignConstants;
import com.yuedongyun.campaign.domain.dto.VoucherFormDTO;
import com.yuedongyun.campaign.domain.dto.VoucherIssueFormDTO;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.po.VoucherScope;
import com.yuedongyun.campaign.domain.po.UserVoucher;
import com.yuedongyun.campaign.domain.query.VoucherQuery;
import com.yuedongyun.campaign.domain.vo.VoucherDetailVO;
import com.yuedongyun.campaign.domain.vo.VoucherPageVO;
import com.yuedongyun.campaign.domain.vo.VoucherScopeVO;
import com.yuedongyun.campaign.domain.vo.VoucherVO;
import com.yuedongyun.campaign.enums.VoucherStatus;
import com.yuedongyun.campaign.enums.ObtainType;
import com.yuedongyun.campaign.enums.UserVoucherStatus;
import com.yuedongyun.campaign.mapper.VoucherMapper;
import com.yuedongyun.campaign.service.IVoucherScopeService;
import com.yuedongyun.campaign.service.IVoucherService;
import com.yuedongyun.campaign.service.IExchangeCodeService;
import com.yuedongyun.campaign.service.IUserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yuedongyun.campaign.enums.VoucherStatus.*;

/**
 * <p>
 * 优惠券的规则信息 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
@RequiredArgsConstructor
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    private final IVoucherScopeService scopeService;

    private final IExchangeCodeService codeService;

    private final IUserVoucherService userVoucherService;

    private final CategoryCache categoryCache;

    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void saveVoucher(VoucherFormDTO dto) {
        // 1.保存优惠券
        // 1.1.转PO
        Voucher voucher = BeanUtils.copyBean(dto, Voucher.class);
        // 1.2.保存
        save(voucher);

        if (!dto.getSpecific()) {
            // 没有范围限定
            return;
        }
        Long voucherId = voucher.getId();
        // 2.保存限定范围
        List<Long> scopes = dto.getScopes();
        if (CollUtils.isEmpty(scopes)) {
            throw new BadRequestException("限定范围不能为空");
        }
        // 2.1.转换PO
        List<VoucherScope> list = scopes.stream()
                .map(bizId -> new VoucherScope().setBizId(bizId).setVoucherId(voucherId))
                .collect(Collectors.toList());
        // 2.2.保存
        scopeService.saveBatch(list);
    }

    @Override
    public PageDTO<VoucherPageVO> queryVoucherByPage(VoucherQuery query) {
        Integer status = query.getStatus();
        String name = query.getName();
        Integer type = query.getType();
        // 1.分页查询
        Page<Voucher> page = lambdaQuery()
                .eq(type != null, Voucher::getDiscountType, type)
                .eq(status != null, Voucher::getStatus, status)
                .like(StringUtils.isNotBlank(name), Voucher::getName, name)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        // 2.处理VO
        List<Voucher> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        List<VoucherPageVO> list = BeanUtils.copyList(records, VoucherPageVO.class);
        // 3.返回
        return PageDTO.of(page, list);
    }

    @Transactional
    @Override
    public void beginIssue(VoucherIssueFormDTO dto) {
        // 1.查询优惠券
        Voucher voucher = getById(dto.getId());
        if (voucher == null) {
            throw new BadRequestException("优惠券不存在！");
        }
        // 2.判断优惠券状态，是否是暂停或待发放
        if(voucher.getStatus() != VoucherStatus.DRAFT && voucher.getStatus() != PAUSE){
            throw new BizIllegalException("优惠券状态错误！");
        }
        // 3.判断是否是立刻发放
        LocalDateTime issueBeginTime = dto.getIssueBeginTime();
        LocalDateTime now = LocalDateTime.now();
        boolean isBegin = issueBeginTime == null || !issueBeginTime.isAfter(now);
        // 4.更新优惠券
        // 4.1.拷贝属性到PO
        Voucher c = BeanUtils.copyBean(dto, Voucher.class);
        // 4.2.更新状态
        if (isBegin) {
            c.setStatus(ISSUING);
            c.setIssueBeginTime(now);
        }else{
            c.setStatus(UN_ISSUE);
        }
        // 4.3.写入数据库
        updateById(c);

        // 5.添加缓存
        if (isBegin) {
            voucher.setIssueBeginTime(c.getIssueBeginTime());
            voucher.setIssueEndTime(c.getIssueEndTime());
            cacheVoucherInfo(voucher);
        }

        // 6.判断是否需要生成兑换码，优惠券类型必须是兑换码，优惠券状态必须是待发放
        if(voucher.getObtainWay() == ObtainType.ISSUE && voucher.getStatus() == VoucherStatus.DRAFT){
            voucher.setIssueEndTime(c.getIssueEndTime());
            codeService.asyncGenerateCode(voucher);
        }
    }

    private void cacheVoucherInfo(Voucher voucher) {
        // 1.组织数据
        Map<String, String> map = new HashMap<>(4);
        map.put("issueBeginTime", String.valueOf(DateUtils.toEpochMilli(voucher.getIssueBeginTime())));
        map.put("issueEndTime", String.valueOf(DateUtils.toEpochMilli(voucher.getIssueEndTime())));
        map.put("totalNum", String.valueOf(voucher.getTotalNum()));
        map.put("userLimit", String.valueOf(voucher.getUserLimit()));
        // 2.写缓存
        redisTemplate.opsForHash().putAll(CampaignConstants.VOUCHER_CACHE_KEY_PREFIX + voucher.getId(), map);
    }

    @Override
    public List<VoucherVO> queryIssuingVouchers() {
        // 1.查询发放中的优惠券列表
        List<Voucher> vouchers = lambdaQuery()
                .eq(Voucher::getStatus, ISSUING)
                .eq(Voucher::getObtainWay, ObtainType.PUBLIC)
                .list();
        if (CollUtils.isEmpty(vouchers)) {
            return CollUtils.emptyList();
        }
        // 2.统计当前用户已经领取的优惠券的信息
        List<Long> voucherIds = vouchers.stream().map(Voucher::getId).collect(Collectors.toList());
        // 2.1.查询当前用户已经领取的优惠券的数据
        List<UserVoucher> userVouchers = userVoucherService.lambdaQuery()
                .eq(UserVoucher::getUserId, UserContext.getUser())
                .in(UserVoucher::getVoucherId, voucherIds)
                .list();
        // 2.2.统计当前用户对优惠券的已经领取数量
        Map<Long, Long> issuedMap = userVouchers.stream()
                .collect(Collectors.groupingBy(UserVoucher::getVoucherId, Collectors.counting()));
        // 2.3.统计当前用户对优惠券的已经领取并且未使用的数量
        Map<Long, Long> unusedMap = userVouchers.stream()
                .filter(uc -> uc.getStatus() == UserVoucherStatus.UNUSED)
                .collect(Collectors.groupingBy(UserVoucher::getVoucherId, Collectors.counting()));
        // 3.封装VO结果
        List<VoucherVO> list = new ArrayList<>(vouchers.size());
        for (Voucher c : vouchers) {
            // 3.1.拷贝PO属性到VO
            VoucherVO vo = BeanUtils.copyBean(c, VoucherVO.class);
            list.add(vo);
            // 3.2.是否可以领取：已经被领取的数量 < 优惠券总数量 && 当前用户已经领取的数量 < 每人限领数量
            vo.setAvailable(
                    c.getIssueNum() < c.getTotalNum()
                    && issuedMap.getOrDefault(c.getId(), 0L) < c.getUserLimit()
            );
            // 3.3.是否可以使用：当前用户已经领取并且未使用的优惠券数量 > 0
            vo.setReceived(unusedMap.getOrDefault(c.getId(),  0L) > 0);
        }
        return list;
    }

    @Override
    @Transactional
    public void pauseIssue(Long id) {
        // 1.查询旧优惠券
        Voucher voucher = getById(id);
        if (voucher == null) {
            throw new BadRequestException("优惠券不存在");
        }

        // 2.当前券状态必须是未开始或进行中
        VoucherStatus status = voucher.getStatus();
        if (status != UN_ISSUE && status != ISSUING) {
            // 状态错误，直接结束
            return;
        }

        // 3.更新状态
        boolean success = lambdaUpdate()
                .set(Voucher::getStatus, PAUSE)
                .eq(Voucher::getId, id)
                .in(Voucher::getStatus, UN_ISSUE, ISSUING)
                .update();
        if (!success) {
            // 可能是重复更新，结束
            log.error("重复暂停优惠券");
        }

        // 4.删除缓存
        redisTemplate.delete(CampaignConstants.VOUCHER_CACHE_KEY_PREFIX + id);
    }

    @Override
    public void deleteById(Long id) {
        // 1.查询
        Voucher voucher = getById(id);
        if (voucher == null || voucher.getStatus() != DRAFT) {
            throw new BadRequestException("优惠券不存在或者优惠券正在使用中");
        }
        // 2.删除优惠券
        boolean success = remove(new LambdaQueryWrapper<Voucher>()
                .eq(Voucher::getId, id)
                .eq(Voucher::getStatus, DRAFT)
        );
        if (!success) {
            throw new BadRequestException("优惠券不存在或者优惠券正在使用中");
        }
        // 3.删除优惠券对应限定范围
        if(!voucher.getSpecific()){
            return;
        }
        scopeService.remove(new LambdaQueryWrapper<VoucherScope>().eq(VoucherScope::getVoucherId, id));
    }

    @Override
    public VoucherDetailVO queryVoucherById(Long id) {
        // 1.查询优惠券
        Voucher voucher = getById(id);
        // 2.转换VO
        VoucherDetailVO vo = BeanUtils.copyBean(voucher, VoucherDetailVO.class);
        if (vo == null || !voucher.getSpecific()) {
            // 数据不存在，或者没有限定范围，直接结束
            return vo;
        }
        // 3.查询限定范围
        List<VoucherScope> scopes = scopeService.lambdaQuery().eq(VoucherScope::getVoucherId, id).list();
        if (CollUtils.isEmpty(scopes)) {
            return vo;
        }
        List<VoucherScopeVO> scopeVOS = scopes.stream()
                .map(VoucherScope::getBizId)
                .map(cateId -> new VoucherScopeVO(cateId, categoryCache.getNameByLv3Id(cateId)))
                .collect(Collectors.toList());
        vo.setScopes(scopeVOS);
        return vo;
    }

    @Override
    public void beginIssueBatch(List<Voucher> vouchers) {
        // 1.更新券状态
        for (Voucher c : vouchers) {
            c.setStatus(VoucherStatus.ISSUING);
        }
        updateBatchById(vouchers);
        // 2.批量缓存
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection src = (StringRedisConnection) connection;
            for (Voucher voucher : vouchers) {
                // 2.1.组织数据
                Map<String, String> map = new HashMap<>(4);
                map.put("issueBeginTime", String.valueOf(DateUtils.toEpochMilli(voucher.getIssueBeginTime())));
                map.put("issueEndTime", String.valueOf(DateUtils.toEpochMilli(voucher.getIssueEndTime())));
                map.put("totalNum", String.valueOf(voucher.getTotalNum()));
                map.put("userLimit", String.valueOf(voucher.getUserLimit()));
                // 2.2.写缓存
                src.hMSet(CampaignConstants.VOUCHER_CACHE_KEY_PREFIX + voucher.getId(), map);
            }
            return null;
        });
    }
}

