package com.yuedongyun.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuedongyun.training.domain.po.OutlineIdAndSubScore;
import com.yuedongyun.training.domain.po.TrainingOutlineAssessmentDraft;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 训练-题目关系表草稿 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface TrainingOutlineAssessmentDraftMapper extends BaseMapper<TrainingOutlineAssessmentDraft> {

    @Insert("<script>insert into training_outline_assessment_draft(training_id,outline_id,assessment_id) value " +
            "<foreach collection='pos' item='po' separator=','>(#{po.trainingId},#{po.outlineId},#{po.assessmentId})</foreach></script>")
    int batchInsert(@Param("pos")List<TrainingOutlineAssessmentDraft> trainingOutlineAssessmentDrafts);

    /**
     * 根据id删除训练对应的题目
     * @param trainingId
     * @return
     */
    @Delete("delete from training_outline_assessment_draft where training_id=#{trainingId}")
    int deleteByTrainingId(@Param("trainingId") Long trainingId);

    @Select("select training_id,outline_id,assessment_id from training_outline_assessment_draft where training_id=#{trainingId}")
    @ResultMap("BaseResultMap")
    List<TrainingOutlineAssessmentDraft> getByTrainingId(@Param("trainingId") Long trainingId);

    @Insert("insert into training_outline_assessment_draft (training_id,outline_id,assessment_id) " +
            "(select training_id,outline_id,assessment_id from training_outline_assessment where training_id=#{trainingId})")
    int insertFromTrainingOutlineAssessment(@Param("trainingId") Long trainingId);

    @Select("select ccs.outline_id as outlineId,ccs.assessment_id as assessmentId, s.score from training_outline_assessment_draft ccs " +
            "left join assessment s on ccs.assessment_id=s.id where ccs.training_id=#{trainingId}")
    List<OutlineIdAndSubScore> queryOutlineIdAndScoreByCorseId(@Param("trainingId") Long trainingId);

}

