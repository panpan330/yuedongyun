package com.yuedongyun.api.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("训练信息")
@Data
public class TrainingDTO {
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
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("价格")
    private Integer price;
    @ApiModelProperty("训练难度，1：入门，2：进阶，3：高阶")
    private Integer difficulty;
    @ApiModelProperty("训练重点部位")
    private String trainPart;
    @ApiModelProperty("预计热量消耗，单位kcal")
    private Integer calorieBurn;
    @ApiModelProperty("视频播放时长")
    private Integer duration;
    @ApiModelProperty("训练有效期天数")
    private Integer validDuration;
    @ApiModelProperty("是否免费")
    private Boolean free;
    @ApiModelProperty("发布时间")
    private LocalDateTime publishTime;
    @ApiModelProperty("章节数")
    private Integer sessions;
    @ApiModelProperty("训练状态")
    private  Byte status;
    @ApiModelProperty("教练id")
    private Long coach;
    @ApiModelProperty("训练类型，1：直播训练，2：录播训练")
    private Integer trainingType;
    @ApiModelProperty("更新时间")
    private Long updater;
    @ApiModelProperty("训练进行到的步骤，1：基本信息，2：目录，3：训练视频，4：训练考核，5：训练教练")
    private Integer step;
    @ApiModelProperty(value = "训练报名人数（销量）", example = "3920")
    private Integer sold = 0;
    @ApiModelProperty(value = "训练评价得分，45代表4.5星", example = "35")
    private Integer score = 0;
    @ApiModelProperty("训练是否禁用,0:禁用，1：启用")
    private Integer enable;
}

