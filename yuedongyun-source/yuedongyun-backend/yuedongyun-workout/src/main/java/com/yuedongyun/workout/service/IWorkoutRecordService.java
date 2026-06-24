package com.yuedongyun.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.api.dto.workout.WorkoutSessionDTO;
import com.yuedongyun.workout.domain.dto.WorkoutRecordFormDTO;
import com.yuedongyun.workout.domain.po.WorkoutRecord;

/**
 * <p>
 * 跟练记录表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-10
 */
public interface IWorkoutRecordService extends IService<WorkoutRecord> {

    WorkoutSessionDTO queryWorkoutRecordByTraining(Long trainingId);

    void addWorkoutRecord(WorkoutRecordFormDTO formDTO);
}

