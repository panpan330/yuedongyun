package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 训练简单信息
 * @author wusongsong
 * @since 2022/7/11 20:56
 * @version 1.0.0
 **/
@Data
@ApiModel("训练简单信息")
public class TrainingSimpleInfoVO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("封面url")
    private String coverUrl;
    @ApiModelProperty("价格")
    private Integer price;
    @ApiModelProperty("一级训练分类id")
    private Long firstCateId;
    @ApiModelProperty("二级训练分类id")
    private Long secondCateId;
    @ApiModelProperty("三级训练分类id")
    private Long thirdCateId;

    @ApiModelProperty("章节数量")
    private Integer sessionNum;
    @ApiModelProperty("训练有效期")
    private Integer validDuration;
    @ApiModelProperty("训练过期时间")
    private LocalDateTime purchaseEndTime;
}

