package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "训练信息")
public class TrainingPageVO {
    @ApiModelProperty(value = "训练id", example = "1")
    private Long id;
    @ApiModelProperty(value = "训练名称", example = "燃脂 HIIT 入门课")
    private String name;
    @ApiModelProperty(value = "训练价格，单位分", example = "32900")
    private Long price;
    @ApiModelProperty(value = "训练难度，1：入门，2：进阶，3：高阶", example = "1")
    private Integer difficulty;
    @ApiModelProperty(value = "训练重点部位", example = "核心")
    private String trainPart;
    @ApiModelProperty(value = "预计热量消耗，单位kcal", example = "380")
    private Integer calorieBurn;
    @ApiModelProperty(value = "训练封面地址", example = "default-cover-url.jpg")
    private String coverUrl;
    @ApiModelProperty(value = "训练分类，三级分类，以/隔开")
    private String categories;
    @ApiModelProperty(value = "训练章节数量", example = "25")
    private Integer sessions;
    @ApiModelProperty(value = "训练报名人数（销量）", example = "3920")
    private Integer sold;
    @ApiModelProperty(value = "训练评价得分，45代表4.5星", example = "35")
    private Integer score;
    @ApiModelProperty(value = "训练状态，1：待上架，2：已上架，3：已下架，4：已完结", example = "1")
    private Integer status;
    @ApiModelProperty(value = "更新人名字", example = "32900")
    private String updaterName;
    @ApiModelProperty(value = "更新时间", example = "2022-7-18 19:52:36")
    private LocalDateTime updateTime;
    @ApiModelProperty("训练编辑进度：1：基本信息已经保存，2：训练目录已经保存，3：训练视频已保存，4：训练考核已保存，5：训练教练已经保存")
    private Integer step;
    @ApiModelProperty("训练发布时间")
    private LocalDateTime publishTime;
    @ApiModelProperty("下架时间")
    private LocalDateTime purchaseEndTime;

    public static final String[] EXCLUDE_FIELDS =
            {"free", "type", "coach","duration","publishTime"};
}

