package com.yuedongyun.trade.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "购物车条目信息")
public class CartVO {
    @ApiModelProperty("购物车中条目id")
    private Long id;
    @ApiModelProperty("训练id")
    private Long trainingId;
    @ApiModelProperty("训练名称")
    private String trainingName;
    @ApiModelProperty("训练封面url")
    private String coverUrl;
    @ApiModelProperty("加入购物车时的训练价格，单位元")
    private Integer price;
    @ApiModelProperty("现在的训练价格，单位元")
    private Integer nowPrice;
    @ApiModelProperty("训练是否已经过期")
    private Boolean expired;
    @JsonIgnore
    @ApiModelProperty(value = "训练有效期", hidden = true)
    private LocalDateTime trainingValidDate;
}
