package com.yuedongyun.training.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 训练-题目关系表
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("training_outline_assessment")
public class TrainingOutlineAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 小节题目关系id
     */
    private Long id;

    /**
     * 训练id
     */
    private Long trainingId;

    /**
     * 小节id
     */
    private Long outlineId;

    /**
     * 题目id
     */
    private Long assessmentId;


}

