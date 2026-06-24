package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wusongsong
 * @since 2022/7/11 11:59
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练基本信息")
public class TrainingBaseInfoVO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("一级分类id")
    private Long firstCateId;
    @ApiModelProperty("二级分类id")
    private Long secondCateId;
    @ApiModelProperty("三级分类id")
    private Long thirdCateId;
    @ApiModelProperty("训练创建人")
    private String createrName;
    private Long creater;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("封面url")
    private String coverUrl;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("更新人名称")
    private String updaterName;
    private Long updater;
    @ApiModelProperty("课时总数量,去掉章，测试，用于编辑回显时该值为空")
    private Integer outlineTotalNum;
    @ApiModelProperty("训练评分，用于编辑回显时该值为空")
    private Double coureScore = 0d;
    @ApiModelProperty("训练评分")
    private Integer score;
    @ApiModelProperty("报名人数，用于编辑回显时该值为空")
    private Integer enrollNum = 0;
    @ApiModelProperty("跟练人数，用于编辑回显时该值为空")
    private Integer studyNum = 0;
    @ApiModelProperty("退款人数，用于编辑回显时该值为空")
    private Integer refundNum = 0;
    @ApiModelProperty("实付总金额，用于编辑回显时该值为空")
    private Integer realPayAmount = 0;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("训练分类名称，中间使用/隔开")
    private String cateNames;
    @ApiModelProperty("训练价格")
    private Integer price;
    @ApiModelProperty("训练难度，1：入门，2：进阶，3：高阶")
    private Integer difficulty;
    @ApiModelProperty("训练重点部位")
    private String trainPart;
    @ApiModelProperty("预计热量消耗，单位kcal")
    private Integer calorieBurn;
    @ApiModelProperty("购买有效期开始")
    private LocalDateTime purchaseStartTime;
    @ApiModelProperty
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("有效期")
    private Integer validDuration;
    @ApiModelProperty("训练介绍")
    private String introduce;
    @ApiModelProperty("使用人群")
    private String usePeople;
    @ApiModelProperty("详情")
    private String detail;
    //
    @ApiModelProperty("是否可以修改，默认不能修改")
    private Boolean canUpdate = false;
    @ApiModelProperty("是否免费")
    private Boolean free;
    @ApiModelProperty("步骤,1:已保存基本信息，2：已保存训练目录，3：已保存训练视频，4：已保存训练考核，5：已保存训练教练")
    private Integer step;
    @ApiModelProperty("训练状态，1：待上架，2：已上架，3：下架，4：已完结")
    private Integer status;

}

