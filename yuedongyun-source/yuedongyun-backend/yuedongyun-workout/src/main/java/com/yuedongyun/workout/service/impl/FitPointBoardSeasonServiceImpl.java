package com.yuedongyun.workout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.workout.domain.po.FitPointBoardSeason;
import com.yuedongyun.workout.mapper.FitPointBoardSeasonMapper;
import com.yuedongyun.workout.service.IFitPointBoardSeasonService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class FitPointBoardSeasonServiceImpl extends ServiceImpl<FitPointBoardSeasonMapper, FitPointBoardSeason> implements IFitPointBoardSeasonService {

    @Override
    public Integer querySeasonByTime(LocalDateTime time) {
        Optional<FitPointBoardSeason> optional = lambdaQuery()
                .le(FitPointBoardSeason::getBeginTime, time)
                .ge(FitPointBoardSeason::getEndTime, time)
                .oneOpt();
        return optional.map(FitPointBoardSeason::getId).orElse(null);
    }
}

