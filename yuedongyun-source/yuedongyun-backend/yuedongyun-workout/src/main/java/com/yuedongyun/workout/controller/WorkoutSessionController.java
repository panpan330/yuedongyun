package com.yuedongyun.workout.controller;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.domain.query.PageQuery;
import com.yuedongyun.workout.domain.dto.WorkoutPlanDTO;
import com.yuedongyun.workout.domain.vo.WorkoutSessionVO;
import com.yuedongyun.workout.domain.vo.WorkoutPlanPageVO;
import com.yuedongyun.workout.service.IWorkoutSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 我的训练表接口
 */
@RestController
@RequestMapping("/sessions")
@Api(tags = "我的训练表相关接口")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final IWorkoutSessionService sessionService;

    @GetMapping("/page")
    @ApiOperation("分页查询我的训练表")
    public PageDTO<WorkoutSessionVO> queryMySessions(PageQuery query) {
        return sessionService.queryMySessions(query);
    }

    @GetMapping("/now")
    @ApiOperation("查询我正在跟练的训练")
    public WorkoutSessionVO queryMyCurrentSession() {
        return sessionService.queryMyCurrentSession();
    }

    @GetMapping("/{trainingId}")
    @ApiOperation("查询指定训练信息")
    public WorkoutSessionVO querySessionByTrainingId(
            @ApiParam(value = "训练id", example = "1") @PathVariable("trainingId") Long trainingId) {
        return sessionService.querySessionByTrainingId(trainingId);
    }

    @DeleteMapping("/{trainingId}")
    @ApiOperation("删除指定训练信息")
    public void deleteTrainingFromSession(
            @ApiParam(value = "训练id", example = "1") @PathVariable("trainingId") Long trainingId) {
        sessionService.deleteTrainingFromSession(null, trainingId);
    }

    @ApiOperation("统计训练跟练人数")
    @GetMapping("/{trainingId}/count")
    public Integer countWorkoutSessionByTraining(
            @ApiParam(value = "训练id", example = "1") @PathVariable("trainingId") Long trainingId) {
        return sessionService.countWorkoutSessionByTraining(trainingId);
    }

    @ApiOperation("校验当前训练是否已加入")
    @GetMapping("/{trainingId}/valid")
    public Long isSessionValid(
            @ApiParam(value = "训练id", example = "1") @PathVariable("trainingId") Long trainingId) {
        return sessionService.isSessionValid(trainingId);
    }

    @ApiOperation("创建训练计划")
    @PostMapping("/plans")
    public void createWorkoutPlans(@Valid @RequestBody WorkoutPlanDTO planDTO) {
        sessionService.createWorkoutPlan(planDTO.getTrainingId(), planDTO.getFreq());
    }

    @ApiOperation("查询我的训练计划")
    @GetMapping("/plans")
    public WorkoutPlanPageVO queryMyPlans(PageQuery query) {
        return sessionService.queryMyPlans(query);
    }
}

