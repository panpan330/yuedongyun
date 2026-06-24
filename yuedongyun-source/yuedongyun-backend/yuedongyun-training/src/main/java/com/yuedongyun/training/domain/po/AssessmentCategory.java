package com.yuedongyun.training.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 训练分类关系表
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("assessment_category")
public class AssessmentCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目id
     */
    private Long assessmentId;

    /**
     * 一级训练分类id
     */
    private Long firstCateId;

    /**
     * 二级训练分类id
     */
    private Long secondCateId;

    /**
     * 三级训练分类id
     */
    private Long thirdCateId;


}

