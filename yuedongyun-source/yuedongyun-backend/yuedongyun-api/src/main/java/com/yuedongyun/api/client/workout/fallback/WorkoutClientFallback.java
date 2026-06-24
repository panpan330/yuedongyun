package com.yuedongyun.api.client.workout.fallback;

import com.yuedongyun.api.client.workout.WorkoutClient;
import com.yuedongyun.api.dto.workout.WorkoutSessionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class WorkoutClientFallback implements FallbackFactory<WorkoutClient> {

    @Override
    public WorkoutClient create(Throwable cause) {
        log.error("查询跟练服务异常", cause);
        return new WorkoutClient() {
            @Override
            public Integer countWorkoutSessionByTraining(Long trainingId) {
                return 0;
            }

            @Override
            public Long isSessionValid(Long trainingId) {
                return null;
            }

            @Override
            public WorkoutSessionDTO queryWorkoutRecordByTraining(Long trainingId) {
                return null;
            }
        };
    }
}

