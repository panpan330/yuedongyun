package com.yuedongyun.training.domain.dto;

import com.yuedongyun.training.constants.TrainingErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 训练视频模型
 *
 * @author wusongsong
 * @since 2022/7/11 17:12
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练视频模型")
public class TrainingMediaDTO {
    @ApiModelProperty("目录id")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    private Long outlineId;
    @ApiModelProperty("媒资id")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    private Long mediaId;
    @ApiModelProperty("是否支持试看")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    private Boolean trailer;
    @ApiModelProperty("媒资名称")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    @Length(min = 1, message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    private String videoName;
    @ApiModelProperty("媒资时长，单位s")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    @Min(value = 1, message = TrainingErrorInfo.Msg.TRAINING_MEDIA_SAVE_MEDIA_NULL)
    private Integer mediaDuration;
}

