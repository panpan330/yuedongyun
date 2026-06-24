package com.yuedongyun.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.domain.query.PageQuery;
import com.yuedongyun.workout.domain.po.WorkoutSession;
import com.yuedongyun.workout.domain.vo.WorkoutSessionVO;
import com.yuedongyun.workout.domain.vo.WorkoutPlanPageVO;

import java.util.List;

/**
 * <p>
 * 会员训练计划 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-02
 */
public interface IWorkoutSessionService extends IService<WorkoutSession> {

    void addUserSessions(Long userId, List<Long> trainingIds);

    PageDTO<WorkoutSessionVO> queryMySessions(PageQuery query);

    WorkoutSessionVO queryMyCurrentSession();

    WorkoutSessionVO querySessionByTrainingId(Long trainingId);

    void deleteTrainingFromSession(Long userId, Long trainingId);

    Integer countWorkoutSessionByTraining(Long trainingId);

    Long isSessionValid(Long trainingId);

    WorkoutSession queryByUserAndTrainingId(Long userId, Long trainingId);

    void createWorkoutPlan(Long trainingId, Integer freq);

    WorkoutPlanPageVO queryMyPlans(PageQuery query);
}

