package com.yuedongyun.workout.controller;

import com.yuedongyun.workout.domain.vo.FitPointStatisticsVO;
import com.yuedongyun.workout.service.IFitPointRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 跟练积分记录，每个月底清零 控制器
 * </p>
 *
 * @author 虎哥
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/fitpoints")
@Api(tags = "积分相关接口")
public class FitPointRecordController {

    private final IFitPointRecordService fitpointsRecordService;

    @ApiOperation("查询我的今日积分")
    @GetMapping("today")
    public List<FitPointStatisticsVO> queryMyFitPointToday(){
        return fitpointsRecordService.queryMyFitPointToday();
    }
}

