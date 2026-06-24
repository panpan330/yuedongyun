package com.yuedongyun.training.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 训练教练关系表
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("training_coach")
public class TrainingCoach implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 训练教练关系id
     */
    private Long id;

    /**
     * 训练id
     */
    private Long trainingId;

    /**
     * 教练id
     */
    private Long coachId;

    /**
     * 用户端是否展示
     */
    private Integer isShow;

    /**
     * 序号
     */
    private Integer cIndex;

    /**
     * 部门id
     */
    private Long depId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long creater;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 逻辑删除
     */
    private Integer deleted;


}

