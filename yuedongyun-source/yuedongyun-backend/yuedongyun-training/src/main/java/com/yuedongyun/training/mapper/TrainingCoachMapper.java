package com.yuedongyun.training.mapper;

import com.yuedongyun.training.domain.po.TrainingCoach;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 训练教练关系表 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface TrainingCoachMapper extends BaseMapper<TrainingCoach> {

    @Delete("delete from training_coach where training_id=#{trainingId}")
    int deleteByTrainingId(@Param("trainingId") Long trainingId);

}

