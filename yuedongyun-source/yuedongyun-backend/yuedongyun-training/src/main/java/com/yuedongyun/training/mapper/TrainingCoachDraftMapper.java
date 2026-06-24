package com.yuedongyun.training.mapper;

import com.yuedongyun.training.domain.po.TrainingCoachDraft;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 训练教练关系表草稿 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface TrainingCoachDraftMapper extends BaseMapper<TrainingCoachDraft> {

    String COLUMNS = "id, training_id, coach_id, is_show, c_index, dep_id, create_time, update_time, creater, updater, deleted";
    /**
     * 根据训练id删除训练中的教练
     * @param trainingId
     * @return
     */
    @Delete("delete from training_coach_draft where training_id=#{trainingId}")
    int deleteByTrainingId(@Param("trainingId") Long trainingId);

    @Insert("insert into training_coach_draft (" + COLUMNS + ") (select " + COLUMNS + " from training_coach " +
            "where training_id= #{trainingId} and deleted = 0)")
    int insertFromTrainingCoach(@Param("trainingId") Long trainingId);


}

