package com.yuedongyun.workout.controller;

import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.workout.domain.po.FitPointBoardSeason;
import com.yuedongyun.workout.domain.query.FitPointBoardQuery;
import com.yuedongyun.workout.domain.vo.FitPointBoardSeasonVO;
import com.yuedongyun.workout.domain.vo.FitPointBoardVO;
import com.yuedongyun.workout.service.IFitPointBoardSeasonService;
import com.yuedongyun.workout.service.IFitPointBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学霸天梯榜 控制器
 * </p>
 *
 * @author 虎哥
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Api(tags = "积分相关接口")
public class FitPointBoardController {

    private final IFitPointBoardService fitpointsBoardService;

    private final IFitPointBoardSeasonService seasonService;

    @GetMapping
    @ApiOperation("分页查询指定赛季的积分排行榜")
    public FitPointBoardVO queryFitPointBoardBySeason(FitPointBoardQuery query){
        return fitpointsBoardService.queryFitPointBoardBySeason(query);
    }

    @ApiOperation("查询赛季信息列表")
    @GetMapping("/seasons/list")
    public List<FitPointBoardSeasonVO> queryFitPointBoardSeasons(){
        // 1.获取时间
        LocalDateTime now = LocalDateTime.now();

        // 2.查询赛季列表，必须是当前赛季之前的（开始时间小于等于当前时间）
        List<FitPointBoardSeason> list =  seasonService.lambdaQuery()
                .le(FitPointBoardSeason::getBeginTime, now).list();
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 3.返回VO
        return BeanUtils.copyToList(list, FitPointBoardSeasonVO.class);
    }
}

