package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "小节信息及跟练进度")
public class SessionVO {
    @ApiModelProperty("对应章节的id")
    private Long id;
    @ApiModelProperty("对应章节的名称")
    private String name;
    @ApiModelProperty("小节序号")
    private Integer index;
    @ApiModelProperty("对应章节的类型，2-视频（小节），3-考核")
    private Integer type;
    @ApiModelProperty("视频总时长，单位秒")
    private Integer mediaDuration;
    @ApiModelProperty("媒资id")
    private Long mediaId;
    @ApiModelProperty("是否支持免费试看")
    private Boolean trailer;
    @ApiModelProperty("题目数量")
    private Integer assessmentNum;
    @ApiModelProperty("是否包含小节测试")
    private Boolean hasTest;
    @ApiModelProperty("视频的当前观看时长，单位秒")
    private Integer moment = 0;
    @ApiModelProperty("是否完成跟练，默认false")
    private Boolean finished;

    public Boolean getHasTest() {
        return assessmentNum != null && assessmentNum > 0;
    }
}
