package com.yuedongyun.workout.domain.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuedongyun.common.domain.dto.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "训练计划分页统计结果")
public class WorkoutPlanPageVO extends PageDTO<WorkoutPlanVO> {
    @ApiModelProperty("本周积分值")
    private Integer weekFitPoint;
    @ApiModelProperty("本周完成的计划数量")
    private Integer weekFinished;
    @ApiModelProperty("总的计划跟练数量")
    private Integer weekTotalPlan;

    public WorkoutPlanPageVO() {
    }

    public WorkoutPlanPageVO pageInfo(Long total, Long pages, List<WorkoutPlanVO> list) {
        this.total = total;
        this.pages = pages;
        this.list = list;
        return this;
    }

    public WorkoutPlanPageVO emptyPage(Page<?> page) {
        this.total = page.getTotal();
        this.pages = page.getPages();
        return this;
    }

}
