package com.yuedongyun.campaign.service.impl;

import com.yuedongyun.api.dto.campaign.VoucherDiscountDTO;
import com.yuedongyun.api.dto.campaign.OrderVoucherDTO;
import com.yuedongyun.api.dto.campaign.OrderTrainingDTO;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.campaign.domain.po.Voucher;
import com.yuedongyun.campaign.domain.po.VoucherScope;
import com.yuedongyun.campaign.enums.UserVoucherStatus;
import com.yuedongyun.campaign.mapper.UserVoucherMapper;
import com.yuedongyun.campaign.service.IVoucherScopeService;
import com.yuedongyun.campaign.service.IDiscountService;
import com.yuedongyun.campaign.strategy.discount.Discount;
import com.yuedongyun.campaign.strategy.discount.DiscountStrategy;
import com.yuedongyun.campaign.utils.PermuteUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements IDiscountService {

    private final UserVoucherMapper userVoucherMapper;

    private final IVoucherScopeService scopeService;

    private final Executor discountSolutionExecutor;

    @Override
    public List<VoucherDiscountDTO> findDiscountSolution(List<OrderTrainingDTO> orderTrainings) {
        // 1.查询我的所有可用优惠券
        List<Voucher> vouchers = userVoucherMapper.queryMyVouchers(UserContext.getUser());
        if (CollUtils.isEmpty(vouchers)) {
            return CollUtils.emptyList();
        }
        // 2.初筛
        // 2.1.计算订单总价
        int totalAmount = orderTrainings.stream().mapToInt(OrderTrainingDTO::getPrice).sum();
        // 2.2.筛选可用券
        List<Voucher> availableVouchers = vouchers.stream()
                .filter(c -> DiscountStrategy.getDiscount(c.getDiscountType()).canUse(totalAmount, c))
                .collect(Collectors.toList());
        if (CollUtils.isEmpty(availableVouchers)) {
            return CollUtils.emptyList();
        }
        // 3.排列组合出所有方案
        // 3.1.细筛（找出每一个优惠券的可用的训练，判断训练总价是否达到优惠券的使用需求）
        Map<Voucher, List<OrderTrainingDTO>> availableVoucherMap = findAvailableVoucher(availableVouchers, orderTrainings);
        if (CollUtils.isEmpty(availableVoucherMap)) {
            return CollUtils.emptyList();
        }
        // 3.2.排列组合
        availableVouchers = new ArrayList<>(availableVoucherMap.keySet());
        List<List<Voucher>> solutions = PermuteUtil.permute(availableVouchers);
        // 3.3.添加单券的方案
        for (Voucher c : availableVouchers) {
            solutions.add(List.of(c));
        }
        // 4.计算方案的优惠明细
        List<VoucherDiscountDTO> list = Collections.synchronizedList(new ArrayList<>(solutions.size()));
        // 4.1.定义闭锁
        CountDownLatch latch = new CountDownLatch(solutions.size());
        for (List<Voucher> solution : solutions) {
            // 4.2.异步计算
            CompletableFuture
                    .supplyAsync(
                            () -> calculateSolutionDiscount(availableVoucherMap, orderTrainings, solution),
                            discountSolutionExecutor
                    ).thenAccept(dto -> {
                // 4.3.提交任务结果
                list.add(dto);
                latch.countDown();
            });
        }
        // 4.4.等待运算结束
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("优惠方案计算被中断，{}", e.getMessage());
        }
        // 5.筛选最优解
        return findBestSolution(list);
    }

    @Override
    public VoucherDiscountDTO queryDiscountDetailByOrder(OrderVoucherDTO orderVoucherDTO) {
        // 1.查询用户优惠券
        List<Long> userVoucherIds = orderVoucherDTO.getUserVoucherIds();
        List<Voucher> vouchers = userVoucherMapper.queryVoucherByUserVoucherIds(userVoucherIds, UserVoucherStatus.UNUSED);
        if (CollUtils.isEmpty(vouchers)) {
            return null;
        }
        // 2.查询优惠券对应训练
        Map<Voucher, List<OrderTrainingDTO>> availableVoucherMap = findAvailableVoucher(vouchers, orderVoucherDTO.getTrainingList());
        if (CollUtils.isEmpty(availableVoucherMap)) {
            return null;
        }
        // 3.查询优惠券规则
        return calculateSolutionDiscount(availableVoucherMap, orderVoucherDTO.getTrainingList(), vouchers);
    }

    private List<VoucherDiscountDTO> findBestSolution(List<VoucherDiscountDTO> list) {
        // 1.准备Map记录最优解
        Map<String, VoucherDiscountDTO> moreDiscountMap = new HashMap<>();
        Map<Integer, VoucherDiscountDTO> lessVoucherMap = new HashMap<>();
        // 2.遍历，筛选最优解
        for (VoucherDiscountDTO solution : list) {
            // 2.1.计算当前方案的id组合
            String ids = solution.getIds().stream()
                    .sorted(Long::compare).map(String::valueOf).collect(Collectors.joining(","));
            // 2.2.比较用券相同时，优惠金额是否最大
            VoucherDiscountDTO best = moreDiscountMap.get(ids);
            if (best != null && best.getDiscountAmount() >= solution.getDiscountAmount()) {
                // 当前方案优惠金额少，跳过
                continue;
            }
            // 2.3.比较金额相同时，用券数量是否最少
            best = lessVoucherMap.get(solution.getDiscountAmount());
            int size = solution.getIds().size();
            if (size > 1 && best != null && best.getIds().size() <= size) {
                // 当前方案用券更多，放弃
                continue;
            }
            // 2.4.更新最优解
            moreDiscountMap.put(ids, solution);
            lessVoucherMap.put(solution.getDiscountAmount(), solution);
        }
        // 3.求交集
        Collection<VoucherDiscountDTO> bestSolutions = CollUtils
                .intersession(moreDiscountMap.values(), lessVoucherMap.values());
        // 4.排序，按优惠金额降序
        return bestSolutions.stream()
                .sorted(Comparator.comparingInt(VoucherDiscountDTO::getDiscountAmount).reversed())
                .collect(Collectors.toList());
    }

    private VoucherDiscountDTO calculateSolutionDiscount(
            Map<Voucher, List<OrderTrainingDTO>> voucherMap, List<OrderTrainingDTO> trainings, List<Voucher> solution) {
        // 1.初始化DTO
        VoucherDiscountDTO dto = new VoucherDiscountDTO();
        // 2.初始化折扣明细的映射
        Map<Long, Integer> detailMap = trainings.stream().collect(Collectors.toMap(OrderTrainingDTO::getId, oc -> 0));
        dto.setDiscountDetail(detailMap);
        // 3.计算折扣
        for (Voucher voucher : solution) {
            // 3.1.获取优惠券限定范围对应的训练
            List<OrderTrainingDTO> availableTrainings = voucherMap.get(voucher);
            // 3.2.计算训练总价(训练原价 - 折扣明细)
            int totalAmount = availableTrainings.stream()
                    .mapToInt(oc -> oc.getPrice() - detailMap.get(oc.getId())).sum();
            // 3.3.判断是否可用
            Discount discount = DiscountStrategy.getDiscount(voucher.getDiscountType());
            if (!discount.canUse(totalAmount, voucher)) {
                // 券不可用，跳过
                continue;
            }
            // 3.4.计算优惠金额
            int discountAmount = discount.calculateDiscount(totalAmount, voucher);
            // 3.5.计算优惠明细
            calculateDiscountDetails(detailMap, availableTrainings, totalAmount, discountAmount);
            // 3.6.更新DTO数据
            dto.getIds().add(voucher.getCreater());
            dto.getRules().add(discount.getRule(voucher));
            dto.setDiscountAmount(discountAmount + dto.getDiscountAmount());
        }
        return dto;
    }

    private void calculateDiscountDetails(Map<Long, Integer> detailMap, List<OrderTrainingDTO> trainings,
                                          int totalAmount, int discountAmount) {
        int times = 0;
        int remainDiscount = discountAmount;
        for (OrderTrainingDTO training : trainings) {
            // 更新训练已计算数量
            times++;
            int discount = 0;
            // 判断是否是最后一个训练
            if (times == trainings.size()) {
                // 是最后一个训练，总折扣金额 - 之前所有商品的折扣金额之和
                discount = remainDiscount;
            } else {
                // 计算折扣明细（训练价格在总价中占的比例，乘以总的折扣）
                discount = discountAmount * training.getPrice() / totalAmount;
                remainDiscount -= discount;
            }
            // 更新折扣明细
            detailMap.put(training.getId(), discount + detailMap.get(training.getId()));
        }
    }

    private Map<Voucher, List<OrderTrainingDTO>> findAvailableVoucher(
            List<Voucher> vouchers, List<OrderTrainingDTO> trainings) {
        Map<Voucher, List<OrderTrainingDTO>> map = new HashMap<>(vouchers.size());
        for (Voucher voucher : vouchers) {
            // 1.找出优惠券的可用的训练
            List<OrderTrainingDTO> availableTrainings = trainings;
            if (voucher.getSpecific()) {
                // 1.1.限定了范围，查询券的可用范围
                List<VoucherScope> scopes = scopeService.lambdaQuery().eq(VoucherScope::getVoucherId, voucher.getId()).list();
                // 1.2.获取范围对应的分类id
                Set<Long> scopeIds = scopes.stream().map(VoucherScope::getBizId).collect(Collectors.toSet());
                // 1.3.筛选训练
                availableTrainings = trainings.stream()
                        .filter(c -> scopeIds.contains(c.getCateId())).collect(Collectors.toList());
            }
            if (CollUtils.isEmpty(availableTrainings)) {
                // 没有任何可用训练，抛弃
                continue;
            }
            // 2.计算训练总价
            int totalAmount = availableTrainings.stream().mapToInt(OrderTrainingDTO::getPrice).sum();
            // 3.判断是否可用
            Discount discount = DiscountStrategy.getDiscount(voucher.getDiscountType());
            if (discount.canUse(totalAmount, voucher)) {
                map.put(voucher, availableTrainings);
            }
        }
        return map;
    }
}

