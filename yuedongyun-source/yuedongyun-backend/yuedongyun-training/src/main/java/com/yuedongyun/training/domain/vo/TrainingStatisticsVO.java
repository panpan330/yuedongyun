package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 训练数据统计
 * @author wusongsong
 * @since 2022/7/10 15:36
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练统计数据")
public class TrainingStatisticsVO {
    @ApiModelProperty("训练总数量")
    private Integer totalNum;
    @ApiModelProperty("上架训练数量")
    private Integer onSaleNum;
    @ApiModelProperty("下架训练数量")
    private Integer offShelfNum;
    @ApiModelProperty("待上架训练数量")
    private Integer noSaleNum;
    @ApiModelProperty("完结训练数量")
    private Integer finishedNum;
    @ApiModelProperty("录播训练数量")
    private Integer recordNum;
    @ApiModelProperty("直播训练数")
    private Integer liveNum;

}

