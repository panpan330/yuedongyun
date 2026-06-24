package com.yuedongyun.workout.controller;


import com.yuedongyun.api.dto.workout.WorkoutSessionDTO;
import com.yuedongyun.workout.domain.dto.WorkoutRecordFormDTO;
import com.yuedongyun.workout.service.IWorkoutRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 跟练记录表 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-10
 */
@RestController
@RequestMapping("/workout-records")
@Api(tags = "跟练记录的相关接口")
@RequiredArgsConstructor
public class WorkoutRecordController {

    private final IWorkoutRecordService recordService;

    @ApiOperation("查询指定训练的跟练记录")
    @GetMapping("/training/{trainingId}")
    public WorkoutSessionDTO queryWorkoutRecordByTraining(
            @ApiParam(value = "训练id", example = "2") @PathVariable("trainingId") Long trainingId){
        return recordService.queryWorkoutRecordByTraining(trainingId);
    }

    @ApiOperation("提交跟练记录")
    @PostMapping
    public void addWorkoutRecord(@RequestBody WorkoutRecordFormDTO formDTO){
        recordService.addWorkoutRecord(formDTO);
    }
}

