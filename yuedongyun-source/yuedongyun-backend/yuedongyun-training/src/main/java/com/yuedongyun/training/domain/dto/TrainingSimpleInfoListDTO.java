package com.yuedongyun.training.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/26 9:26
 * @version 1.0.0
 **/
@Data
public class TrainingSimpleInfoListDTO {

    @ApiModelProperty("三级分类id列表")
    private List<Long> thirdOutlineIds;

    @ApiModelProperty("训练id列表")
    private List<Long> ids;
}

