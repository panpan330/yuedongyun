package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wusongsong
 * @since 2022/7/13 11:26
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练保存结果")
@AllArgsConstructor
@NotNull
@Builder
public class TrainingSaveVO {
    @ApiModelProperty("训练id")
    private Long id;
}

