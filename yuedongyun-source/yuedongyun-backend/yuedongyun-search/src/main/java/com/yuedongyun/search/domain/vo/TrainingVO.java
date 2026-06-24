package com.yuedongyun.search.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "训练信息")
public class TrainingVO {
    @ApiModelProperty(value = "训练id", example = "1")
    private Long id;
    @ApiModelProperty(value = "训练名称", example = "Java")
    private String name;
    @ApiModelProperty(value = "训练价格，单位分", example = "32900")
    private Long price;
    @ApiModelProperty(value = "训练难度，1：入门，2：进阶，3：高阶", example = "1")
    private Integer difficulty;
    @ApiModelProperty(value = "训练重点部位", example = "核心")
    private String trainPart;
    @ApiModelProperty(value = "预计热量消耗，单位kcal", example = "380")
    private Integer calorieBurn;
    @ApiModelProperty(value = "教练名字", example = "张教练")
    private String coach;
    @ApiModelProperty(value = "教练头像", example = "default-user-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "训练时长，单位秒", example = "3280")
    private Integer duration;
    @ApiModelProperty(value = "训练封面地址", example = "default-cover-url.jpg")
    private String coverUrl;
    @ApiModelProperty(value = "训练章节数量", example = "25")
    private Integer sessions;
    @ApiModelProperty(value = "训练报名人数（销量）", example = "3920")
    private Integer sold;

    public static final String[] EXCLUDE_FIELDS =
            {"categoryIdLv1", "categoryIdLv2", "categoryIdLv3", "free",
                    "publishTime", "type", "status", "score"};
}

