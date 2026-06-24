package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "训练和目录及跟练进度信息")
public class TrainingAndSessionVO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("训练封面")
    private String coverUrl;
    @ApiModelProperty("训练章节数量")
    private Integer sessions;
    @ApiModelProperty("教练头像")
    private String coachIcon;
    @ApiModelProperty("教练名称")
    private String coachName;
    @ApiModelProperty("训练表id")
    private Long sessionId;
    @ApiModelProperty("正在跟练的小节id")
    private Long latestSessionId;
    private List<PhaseVO> phases;
}

