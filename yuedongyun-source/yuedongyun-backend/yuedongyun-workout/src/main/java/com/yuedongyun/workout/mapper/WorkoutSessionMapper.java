package com.yuedongyun.workout.mapper;

import com.yuedongyun.workout.domain.po.WorkoutSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 会员训练计划 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-02
 */
public interface WorkoutSessionMapper extends BaseMapper<WorkoutSession> {

    Integer queryTotalPlan(@Param("userId") Long userId);
}

