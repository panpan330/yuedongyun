package com.yuedongyun.training.domain.dto;

import com.yuedongyun.common.exceptions.BadRequestException;
import com.yuedongyun.common.utils.DateUtils;
import com.yuedongyun.common.validate.Checker;
import com.yuedongyun.training.constants.TrainingErrorInfo;
import com.yuedongyun.training.utils.TrainingSaveBaseGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 训练基本信息
 *
 * @ClassName TrainingBaseInfoSaveDTO
 * @author wusongsong
 * @since 2022/7/11 11:39
 * @version 1.0.0
 **/

@Data
@ApiModel(description = "训练基本信息保存")
public class TrainingBaseInfoSaveDTO implements Checker {
    @ApiModelProperty("训练id，新训练该值不能传，老训练必填")
    private Long id;
    @ApiModelProperty("训练名称")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_NAME_NULL)
    private String name;
    @ApiModelProperty("三级训练分类id")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_CATEGORY_NULL)
    private Long thirdCateId;
    @ApiModelProperty("封面链接url")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_COVER_URL_NULL, groups = TrainingSaveBaseGroup.class)
    private String coverUrl;
    @ApiModelProperty("是否是免费")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_FREE_NULL)
    private Boolean free;
    @ApiModelProperty("训练价格")
    @Min(value = 0, message = TrainingErrorInfo.Msg.TRAINING_SAVE_PRICE_NEGATIVE)
    private Integer price;
    @ApiModelProperty("训练难度，1：入门，2：进阶，3：高阶")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_DIFFICULTY_NULL)
    @Min(value = 1, message = TrainingErrorInfo.Msg.TRAINING_SAVE_DIFFICULTY_ILLEGAL)
    @Max(value = 3, message = TrainingErrorInfo.Msg.TRAINING_SAVE_DIFFICULTY_ILLEGAL)
    private Integer difficulty;
    @ApiModelProperty("训练重点部位")
    @NotBlank(message = TrainingErrorInfo.Msg.TRAINING_SAVE_TRAIN_PART_NULL)
    private String trainPart;
    @ApiModelProperty("预计热量消耗，单位kcal")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_CALORIE_BURN_NULL)
    @Min(value = 1, message = TrainingErrorInfo.Msg.TRAINING_SAVE_CALORIE_BURN_ILLEGAL)
    private Integer calorieBurn;
//    @ApiModelProperty("购买周期开始时间")
//    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_PURCHASE_TIME_NULL)
    private LocalDateTime purchaseStartTime;
    @ApiModelProperty("购买周期结束时间")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_PURCHASE_TIME_NULL)
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("训练介绍")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_INTRODUCE_NULL, groups = TrainingSaveBaseGroup.class)
    private String introduce;
    @ApiModelProperty("使用人群")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_USE_PEOPLE_NULL, groups = TrainingSaveBaseGroup.class)
    private String usePeople;
    @ApiModelProperty("详情")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_DETAIL_NULL, groups = TrainingSaveBaseGroup.class)
    private String detail;
    @ApiModelProperty("跟练周期，0或不传表示没有期限，其他表示月数")
    @NotNull(message = TrainingErrorInfo.Msg.TRAINING_SAVE_DURATION_NULL)
    private Integer validDuration;

    @Override
    public void check() {
        if(!free) { //非免费
            if(price == null) { //付费训练未设置价格
                throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_PRICE_NULL);
            }
            if(price <= 0){ //付费训练设置价格小于0
                throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_PRICE_NEGATIVE);
            }
        }else { //免费
            if(price != null && price > 0){ //免费训练设置了价格
                throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_PRICE_FREE);
            }
        }
        if(purchaseEndTime.isBefore(DateUtils.now())){
            throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_PURCHASE_ILLEGAL);
        }
//        if (purchaseStartTime.isAfter(purchaseEndTime)) {
//            throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_PURCHASE_ILLEGAL);
//        }
//        if(id == null && purchaseStartTime.isBefore(LocalDateTime.now())){
//            throw new BadRequestException(TrainingErrorInfo.Msg.TRAINING_SAVE_PURCHASE_ILLEGAL2);
//        }
    }
}

