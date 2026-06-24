package com.yuedongyun.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.workout.domain.po.FitPointRecord;
import com.yuedongyun.workout.domain.vo.FitPointStatisticsVO;
import com.yuedongyun.workout.enums.FitPointRecordType;

import java.util.List;

/**
 * <p>
 * 跟练积分记录，每个月底清零 服务类
 * </p>
 *
 * @author 虎哥
 */
public interface IFitPointRecordService extends IService<FitPointRecord> {
    void addFitPointRecord(Long userId, int fitpoints, FitPointRecordType type);

    List<FitPointStatisticsVO> queryMyFitPointToday();

}

