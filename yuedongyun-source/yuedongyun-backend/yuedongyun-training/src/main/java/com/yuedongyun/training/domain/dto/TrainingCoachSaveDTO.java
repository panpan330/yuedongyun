package com.yuedongyun.training.domain.dto;

import com.yuedongyun.training.constants.TrainingErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 保存教练训练关系
 * @ClassName TrainingCoachSaveDTO
 * @Author wusongsong
 * @Date 2022/7/13 14:59
 * @Version
 **/
@Data
@ApiModel("训练教练关系模型")
public class TrainingCoachSaveDTO {
    @ApiModelProperty("训练id")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_COACH_SAVE_TRAINING_ID_NULL)
    private Long id;
    @ApiModelProperty("教练id和用户端是否展示，该列表按照界面上的顺序")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_COACH_SAVE_COACHS_NULL)
//    @Min(value = 1, message = TrainingErrorInfo.Msg.TRAINING_COACH_SAVE_COACHS_NULL)
    @Size(min = 1, max = 5, message = TrainingErrorInfo.Msg.TRAINING_COACH_SAVE_COACHS_NUM_MAX )
    private List<CoachInfo> coaches;

    @Data
    @ApiModel("教练id和用户端是否显示")
    public static class CoachInfo{
        @ApiModelProperty("教练id")
        @NotNull(message = TrainingErrorInfo.Msg.TRAINING_COACH_SAVE_COACH_ID_NULL)
        private Long id;
        @ApiModelProperty("用户端是否展示")
        @NotNull(message = TrainingErrorInfo.Msg.TRAINING_COACH_SAVE_COACH_SHOW)
        private Boolean isShow;
    }
}

