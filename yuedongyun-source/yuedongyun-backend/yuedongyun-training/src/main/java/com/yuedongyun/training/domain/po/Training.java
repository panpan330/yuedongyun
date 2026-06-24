package com.yuedongyun.training.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 训练正式表
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("training")
public class Training implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 训练id
     */
    private Long id;

    /**
     * 训练名称
     */
    private String name;

    /**
     * 训练类型，1：直播训练，2：录播训练
     */
    private Integer trainingType;

    /**
     * 封面链接
     */
    private String coverUrl;

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

    /**
     * 售卖方式0付费，1：免费
     */
    private Integer free;

    /**
     * 训练价格，单位为分
     */
    private Integer price;

    /**
     * 训练难度，1：入门，2：进阶，3：高阶
     */
    private Integer difficulty;

    /**
     * 训练重点部位
     */
    private String trainPart;

    /**
     * 预计热量消耗，单位kcal
     */
    private Integer calorieBurn;

    /**
     * 模板类型，1：固定模板，2：自定义模板
     */
    private Integer templateType;

    /**
     * 自定义模板的连接
     */
    private String templateUrl;

    /**
     * 训练状态，1：待上架，2：已上架，3：下架，4：已完结
     */
    private Integer status;

    /**
     * 训练购买有效期开始时间
     */
    private LocalDateTime purchaseStartTime;

    /**
     * 训练购买有效期结束时间
     */
    private LocalDateTime purchaseEndTime;

    /**
     * 信息填写进度
     */
    private Integer step;

    /**
     * 训练评价得分，45代表4.5星
     */
    private Integer score;

    /**
     * 训练总时长
     */
    private Integer mediaDuration;

    /**
     * 训练有效期，单位月
     */
    private Integer validDuration;

    /**
     * 训练总节数，包括练习
     */
    private Integer sessionNum;

    /**
     * 部门id
     */
    private Long depId;

    /**
     * 发布次数
     */
    private Integer publishTimes;

    /**
     * 最近一次发布时间
     */
    private LocalDateTime publishTime;

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

