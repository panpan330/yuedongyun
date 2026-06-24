package com.yuedongyun.training.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 训练-题目关系表草稿
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("training_outline_assessment_draft")
public class TrainingOutlineAssessmentDraft implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 小节题目关系id
     */
    private Long id;

    private Long trainingId;

    /**
     * 小节id
     */
    private Long outlineId;

    /**
     * 题目id
     */
    private Long assessmentId;

    private LocalDateTime createTime;


}

