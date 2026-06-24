package com.yuedongyun.api.dto.training;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 训练信息
 *
 * @author wusongsong
 * @since 2022/8/5 16:54
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "训练详细信息，包含训练、目录、教练")
public class TrainingFullInfoDTO {
    @ApiModelProperty("训练id")
    private Long id;
    @ApiModelProperty("训练名称")
    private String name;
    @ApiModelProperty("封面链接")
    private String coverUrl;
    @ApiModelProperty("价格")
    private Integer price;
    @ApiModelProperty("训练难度，1：入门，2：进阶，3：高阶")
    private Integer difficulty;
    @ApiModelProperty("训练重点部位")
    private String trainPart;
    @ApiModelProperty("预计热量消耗，单位kcal")
    private Integer calorieBurn;
    @ApiModelProperty("一级训练分类id")
    private Long firstCateId;
    @ApiModelProperty("二级训练分类id")
    private Long secondCateId;
    @ApiModelProperty("三级训练分类id")
    private Long thirdCateId;
    @ApiModelProperty("训练总节数")
    private Integer sessionNum;
    @ApiModelProperty("训练购买有效期结束时间")
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("训练有效期")
    private Integer validDuration;
    @ApiModelProperty("训练章信息")
    private List<OutlineDTO> phases;
    @ApiModelProperty("教练列表")
    private List<Long> coachIds;
    @JsonIgnore
    public List<Long> getCategoryIds(){
        return List.of(firstCateId, secondCateId, thirdCateId);
    }
}

