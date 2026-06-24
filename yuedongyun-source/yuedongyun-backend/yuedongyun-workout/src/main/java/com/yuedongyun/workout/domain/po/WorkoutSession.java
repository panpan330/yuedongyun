package com.yuedongyun.workout.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuedongyun.workout.enums.SessionStatus;
import com.yuedongyun.workout.enums.PlanStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 会员训练计划
 * </p>
 *
 * @author 虎哥
 * @since 2022-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("workout_session")
public class WorkoutSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会员id
     */
    private Long userId;

    /**
     * 训练id
     */
    private Long trainingId;

    /**
     * 训练状态，0-未跟练，1-跟练中，2-已学完，3-已失效
     */
    private SessionStatus status;

    /**
     * 每周跟练频率，每周3天，每天2节，则频率为6
     */
    private Integer weekFreq;

    /**
     * 训练计划状态，0-没有计划，1-计划进行中
     */
    private PlanStatus planStatus;

    /**
     * 已跟练小节数量
     */
    private Integer learnedSessions;

    /**
     * 最近一次跟练的小节id
     */
    private Long latestSessionId;

    /**
     * 最近一次跟练的时间
     */
    private LocalDateTime latestLearnTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}

