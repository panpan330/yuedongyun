package com.yuedongyun.workout.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yuedongyun.workout.domain.po.FitPointRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 跟练积分记录，每个月底清零 Mapper 接口
 * </p>
 *
 * @author 虎哥
 */
public interface FitPointRecordMapper extends BaseMapper<FitPointRecord> {

    @Select("SELECT SUM(fitpoints) FROM fitpoints_record ${ew.customSqlSegment}")
    Integer queryUserFitPointByTypeAndDate(@Param(Constants.WRAPPER) QueryWrapper<FitPointRecord> wrapper);

    @Select("SELECT type, SUM(fitpoints) AS fitpoints FROM fitpoints_record ${ew.customSqlSegment} GROUP BY type")
    List<FitPointRecord> queryUserFitPointByDate(@Param(Constants.WRAPPER) QueryWrapper<FitPointRecord> wrapper);
}

