package com.yuedongyun.workout.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 学霸天梯榜
 * </p>
 *
 * @author 虎哥
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fitpoints_board")
public class FitPointBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 榜单id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 会员id
     */
    private Long userId;

    /**
     * 积分值
     */
    private Integer fitpoints;

    /**
     * 名次，只记录赛季前100
     */
    @TableField(exist = false)
    private Integer rank;

    /**
     * 赛季id
     */
    @TableField(exist = false)
    private Integer season;


}

