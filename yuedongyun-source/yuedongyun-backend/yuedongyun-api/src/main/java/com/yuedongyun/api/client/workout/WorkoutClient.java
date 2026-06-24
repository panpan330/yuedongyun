package com.yuedongyun.api.client.workout;

import com.yuedongyun.api.client.workout.fallback.WorkoutClientFallback;
import com.yuedongyun.api.dto.workout.WorkoutSessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "workout-service", fallbackFactory = WorkoutClientFallback.class)
public interface WorkoutClient {

    /**
     * 统计训练跟练人数
     * @param trainingId 训练id
     * @return 跟练人数
     */
    @GetMapping("/sessions/{trainingId}/count")
    Integer countWorkoutSessionByTraining(@PathVariable("trainingId") Long trainingId);

    /**
     * 校验当前用户是否可以跟练当前训练
     * @param trainingId 训练id
     * @return true：训练有效，false：训练无效，不能跟练
     */
    @GetMapping("/sessions/{trainingId}/valid")
    Long isSessionValid(@PathVariable("trainingId") Long trainingId);

    /**
     * 查询当前用户指定训练的跟练进度
     * @param trainingId 训练id
     * @return 训练表信息、跟练记录及进度信息
     */
    @GetMapping("/workout-records/training/{trainingId}")
    WorkoutSessionDTO queryWorkoutRecordByTraining(@PathVariable("trainingId") Long trainingId);

}

