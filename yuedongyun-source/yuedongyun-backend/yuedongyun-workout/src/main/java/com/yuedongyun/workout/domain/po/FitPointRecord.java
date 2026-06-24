package com.yuedongyun.workout.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuedongyun.workout.enums.FitPointRecordType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 跟练积分记录，每个月底清零
 * </p>
 *
 * @author 虎哥
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fitpoints_record")
public class FitPointRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 积分记录表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 积分方式：1-训练跟练，2-每日签到，3-训练问答， 4-训练笔记，5-训练评价
     */
    private FitPointRecordType type;

    /**
     * 积分值
     */
    private Integer fitpoints;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}

