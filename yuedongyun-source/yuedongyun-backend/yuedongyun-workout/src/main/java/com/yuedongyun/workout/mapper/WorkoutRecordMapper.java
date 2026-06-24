package com.yuedongyun.workout.mapper;

import com.yuedongyun.api.dto.IdAndNumDTO;
import com.yuedongyun.workout.domain.po.WorkoutRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 跟练记录表 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-10
 */
public interface WorkoutRecordMapper extends BaseMapper<WorkoutRecord> {

    List<IdAndNumDTO> countLearnedSessions(
            @Param("userId") Long userId,
            @Param("begin") LocalDateTime begin,
            @Param("end") LocalDateTime end);
}

