package com.yuedongyun.training.mapper;

import com.yuedongyun.training.domain.po.TrainingAssessment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 训练题目关系列表 Mapper 接口
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-17
 */
public interface TrainingAssessmentMapper extends BaseMapper<TrainingAssessment> {

    @Insert("<script>insert into training_assessment (training_id,assessment_id) " +
            "value <foreach collection='trainingAssessments' item='cs' separator=','>(#{cs.trainingId},#{cs.assessmentId})</foreach></script>")
    int batchInsert(@Param("trainingAssessments")List<TrainingAssessment> trainingAssessments);
}

