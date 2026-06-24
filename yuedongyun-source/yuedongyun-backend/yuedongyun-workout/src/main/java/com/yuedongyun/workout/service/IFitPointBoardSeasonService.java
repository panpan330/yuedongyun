package com.yuedongyun.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.workout.domain.po.FitPointBoardSeason;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IFitPointBoardSeasonService extends IService<FitPointBoardSeason> {

    Integer querySeasonByTime(LocalDateTime time);
}

