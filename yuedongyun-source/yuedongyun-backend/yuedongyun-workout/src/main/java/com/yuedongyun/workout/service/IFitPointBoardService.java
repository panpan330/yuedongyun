package com.yuedongyun.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.workout.domain.po.FitPointBoard;
import com.yuedongyun.workout.domain.query.FitPointBoardQuery;
import com.yuedongyun.workout.domain.vo.FitPointBoardVO;

import java.util.List;

/**
 * <p>
 * 学霸天梯榜 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IFitPointBoardService extends IService<FitPointBoard> {
    FitPointBoardVO queryFitPointBoardBySeason(FitPointBoardQuery query);

    void createFitPointBoardTableBySeason(Integer season);

    List<FitPointBoard> queryCurrentBoardList(String key, Integer pageNo, Integer pageSize);
}

