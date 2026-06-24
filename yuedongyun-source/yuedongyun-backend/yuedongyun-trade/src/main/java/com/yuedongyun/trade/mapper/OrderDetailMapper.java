package com.yuedongyun.trade.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.api.dto.IdAndNumDTO;
import com.yuedongyun.trade.domain.po.OrderDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 订单明细 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-29
 */
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    @Select("SELECT training_id FROM order_detail WHERE order_id = #{orderId}")
    List<Long> queryTrainingIdsByOrderId(Long orderId);

    List<IdAndNumDTO> countEnrollNumOfTraining(@Param("ew") QueryWrapper<OrderDetail> wrapper);

    List<IdAndNumDTO> countEnrollTrainingOfMember(@Param("ew") QueryWrapper<OrderDetail> wrapper);

    @Select("SELECT SUM(real_pay_amount) FROM order_detail WHERE training_id = #{trainingId}")
    int countRealPayAmountByTrainingId(Long trainingId);
}

