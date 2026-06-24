package com.yuedongyun.trade.service;

import com.yuedongyun.trade.domain.po.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.trade.domain.vo.CartVO;

import java.util.List;

/**
 * <p>
 * 购物车条目信息，也就是购物车中的训练 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-28
 */
public interface ICartService extends IService<Cart> {

    void addTraining2Cart(Long trainingId);

    List<CartVO> getMyCarts();

    void deleteCartById(Long id);

    void deleteCartByIds(List<Long> ids);

    void deleteCartByUserAndTrainingIds(Long userId, List<Long> trainingIds);
}

