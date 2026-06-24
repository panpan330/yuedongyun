package com.yuedongyun.workout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.workout.domain.po.FitPointBoard;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 学霸天梯榜 Mapper 接口
 * </p>
 *
 * @author 虎哥
 */
public interface FitPointBoardMapper extends BaseMapper<FitPointBoard> {

    void createFitPointBoardTable(@Param("tableName") String tableName);
}

