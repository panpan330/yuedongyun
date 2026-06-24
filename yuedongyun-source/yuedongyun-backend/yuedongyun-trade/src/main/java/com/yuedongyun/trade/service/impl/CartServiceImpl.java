package com.yuedongyun.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.dto.training.TrainingFullInfoDTO;
import com.yuedongyun.api.dto.training.TrainingSimpleInfoDTO;
import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.exceptions.BizIllegalException;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.StringUtils;
import com.yuedongyun.common.utils.UserContext;
import com.yuedongyun.trade.config.TradeProperties;
import com.yuedongyun.trade.domain.po.Cart;
import com.yuedongyun.trade.domain.vo.CartVO;
import com.yuedongyun.trade.mapper.CartMapper;
import com.yuedongyun.trade.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yuedongyun.trade.constants.TradeErrorInfo.*;

/**
 * <p>
 * 购物车条目信息，也就是购物车中的训练 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private final TrainingClient trainingClient;
    private final TradeProperties tradeProperties;

    @Override
    public void addTraining2Cart(Long trainingId) {
        Long userId = UserContext.getUser();
        log.debug("加入购物车请求：用户：{}，训练：{}", userId, trainingId);
        // 1.查询该训练是否已经在购物车
        if (checkTrainingExists(trainingId, userId)) {
            return;
        }
        // 2.查询购物车中训练是否超出上限
        checkCartsFull(userId);

        // 3.根据id查询训练信息
        TrainingFullInfoDTO trainingInfo = trainingClient.getTrainingInfoById(trainingId, false, false);

        // 4.判断是否为空
        if (trainingInfo == null) {
            throw new BadRequestException(TRAINING_NOT_EXISTS);
        }

        // 5.判断是否过期
        if (trainingInfo.getPurchaseEndTime().isBefore(LocalDateTime.now())) {
            // 已经过期，无法购买
            throw new BadRequestException(TRAINING_EXPIRED);
        }
        // 5.写入购物车
        Cart cart = new Cart();
        cart.setId(IdWorker.getId()); //购物车中的id
        cart.setTrainingId(trainingId); //训练id
        cart.setTrainingName(trainingInfo.getName());
        cart.setUserId(UserContext.getUser());
        cart.setCoverUrl(trainingInfo.getCoverUrl());
        cart.setPrice(trainingInfo.getPrice());
        save(cart);
        log.debug("加入购物车成功！用户：{}，训练：{}", userId, trainingId);
    }

    private void checkCartsFull(Long userId) {
        int count = lambdaQuery().eq(Cart::getUserId, userId).count();
        if (count >= tradeProperties.getMaxTrainingAmount()) {
            throw new BizIllegalException(
                    StringUtils.format(CARTS_FULL, tradeProperties.getMaxTrainingAmount()));
        }
    }

    private boolean checkTrainingExists(Long trainingId, Long userId) {
        int count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getTrainingId, trainingId)
                .count();
        return count > 0;
    }

    @Override
    public List<CartVO> getMyCarts() {
        // 1.获取用户
        Long userId = UserContext.getUser();
        // 2.查询我的购物车
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, userId).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }
        // 3.查询购物车中的训练
        List<Long> trainingIds = carts.stream().map(Cart::getTrainingId).collect(Collectors.toList());
        List<TrainingSimpleInfoDTO> trainingSimpleInfos = trainingClient.getSimpleInfoList(trainingIds);
        Map<Long, TrainingSimpleInfoDTO> map = trainingSimpleInfos.stream()
                .collect(Collectors.toMap(TrainingSimpleInfoDTO::getId, c -> c));
        // 4.组织 vo
        List<CartVO> list = new ArrayList<>(carts.size());
        for (Cart cart : carts) {
            // 4.1.转换VO
            CartVO vo = BeanUtils.toBean(cart, CartVO.class);
            list.add(vo);
            // 4.2.获取新的训练信息
            TrainingSimpleInfoDTO info = map.get(cart.getTrainingId());
            vo.setNowPrice(info.getPrice());
            vo.setExpired(info.getPurchaseEndTime().isBefore(LocalDateTime.now()));
            vo.setTrainingValidDate(info.getPurchaseEndTime());
        }
        // 5.排序
        return list.stream().sorted(
                Comparator.comparing(CartVO::getExpired).reversed() // 先看是否过期，未过期的在前，已过期的在后
                        .thenComparingLong(CartVO::getId).reversed() // 再看id，id大的是新加入的
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteCartById(Long id) {
        // 1.获取用户
        Long userId = UserContext.getUser();
        // 2.删除
        remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getId, id)
                .eq(Cart::getUserId, userId)
        );
    }

    @Override
    public void deleteCartByIds(List<Long> ids) {
        Long userId = UserContext.getUser();
        remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .in(Cart::getId, ids)
        );
    }

    @Override
    public void deleteCartByUserAndTrainingIds(Long userId, List<Long> trainingIds) {
        log.debug("尝试从购物车删除用户已购买的训练，用户id：{}，训练id：{}", userId, trainingIds);
        try {
            if(CollUtils.isEmpty(trainingIds) || userId == null){
                return;
            }
            remove(new LambdaQueryWrapper<Cart>()
                    .eq(Cart::getUserId, userId)
                    .in(Cart::getTrainingId, trainingIds)
            );
        } catch (Exception e) {
            log.error("从购物车删除用户已购买的训练发生异常，用户id：{}，训练id：{}", userId, trainingIds, e);
        }
    }
}

