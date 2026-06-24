package com.yuedongyun.api.dto.training;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName CategoryDTO
 * @author wusongsong
 * @since 2022/7/21 14:51
 * @version 1.0.0
 **/
@Data
@ApiModel("训练分类")
public class CategoryDTO {
    @ApiModelProperty("训练分类id")
    private Long id;
    @ApiModelProperty("训练分类名称")
    private String name;
    @ApiModelProperty("三级分类数量")
    private Integer thirdCategoryNum;
    @ApiModelProperty("训练数量")
    private Integer trainingNum;
    @ApiModelProperty("状态：1：正常，2：禁用")
    private Integer status;
    @ApiModelProperty("状态描述")
    private String statusDesc;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty("排序")
    private Integer index;
    @ApiModelProperty("父id")
    private Long parentId;
    @ApiModelProperty("级别")
    private Integer level;
}

