package com.yuedongyun.training.mapper;

import com.yuedongyun.training.domain.po.OutlineIdAndSubScore;
import com.yuedongyun.training.domain.po.TrainingOutlineAssessment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 训练-题目关系表 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface TrainingOutlineAssessmentMapper extends BaseMapper<TrainingOutlineAssessment> {

    @Insert("<script>insert into training_outline_assessment (training_id,outline_id,assessment_id) value " +
            "<foreach collection='trainingOutlineAssessments' item='ccs' separator=','>" +
            "(#{ccs.trainingId},#{ccs.outlineId},#{ccs.assessmentId})" +
            "</foreach></script>")
    int batchInsert(@Param("trainingOutlineAssessments")List<TrainingOutlineAssessment> trainingOutlineAssessments);

    @Delete("delete from training_outline_assessment where training_id=#{trainingId}")
    int deleteByTrainingId(@Param("trainingId") Long trainingId);

    @Select("select ccs.outline_id as outlineId,ccs.assessment_id as assessmentId, s.score from training_outline_assessment ccs " +
            "left join assessment s on ccs.assessment_id=s.id where ccs.training_id=#{trainingId}")
    List<OutlineIdAndSubScore> queryOutlineIdAndScoreByCorseId(@Param("trainingId") Long trainingId);

    @Select("select assessment_id from training_outline_assessment where outline_id=#{outlineId}")
    List<Long> queryAssessmentIdByOutlineId(@Param("outlineId") Long outlineId);


}

