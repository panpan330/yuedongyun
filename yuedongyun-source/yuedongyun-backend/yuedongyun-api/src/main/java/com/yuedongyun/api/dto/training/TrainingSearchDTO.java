package com.yuedongyun.api.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 训练信息
 * @ClassName TrainingDTO
 * @author wusongsong
 * @since 2022/7/18 13:12
 * @version 1.0.0
 **/
@ApiModel(description = "训练信息")
@Data
public class TrainingSearchDTO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("一级训练分类id")
    private Long categoryIdLv1;
    @ApiModelProperty("二级训练分类id")
    private Long categoryIdLv2;
    @ApiModelProperty("三级训练分类id")
    private Long categoryIdLv3;
    @ApiModelProperty("训练封面")
    private String coverUrl;
    @ApiModelProperty("价格")
    private Integer price;
    @ApiModelProperty("训练难度，1：入门，2：进阶，3：高阶")
    private Integer difficulty;
    @ApiModelProperty("训练重点部位")
    private String trainPart;
    @ApiModelProperty("预计热量消耗，单位kcal")
    private Integer calorieBurn;
    @ApiModelProperty("是否免费")
    private Boolean free;
    @ApiModelProperty("发布时间")
    private LocalDateTime publishTime;
    @ApiModelProperty("章节数")
    private Integer sessions;
    @ApiModelProperty("训练时长")
    private Integer duration;
    @ApiModelProperty("教练id")
    private Long coach;
    @ApiModelProperty("训练类型，1：直播训练，2：录播训练")
    private Integer trainingType;
    @ApiModelProperty(value = "训练报名人数（销量）", example = "3920")
    private Integer sold = 0;
    @ApiModelProperty(value = "训练评价得分，45代表4.5星", example = "35")
    private Integer score = 0;
}

