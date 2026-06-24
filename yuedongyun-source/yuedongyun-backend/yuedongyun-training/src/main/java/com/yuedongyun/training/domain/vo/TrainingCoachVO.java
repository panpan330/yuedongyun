package com.yuedongyun.training.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 训练相关教练信息
 * @ClassName TrainingCoachVO
 * @Author wusongsong
 * @Date 2022/7/11 18:17
 * @Version
 **/
@Data
@ApiModel("教练训练信息")
public class TrainingCoachVO {
    @ApiModelProperty("教练训练关系id")
    private Long id;
    @ApiModelProperty("教练头像")
    private String icon;
    @ApiModelProperty("形象照")
    private String photo;
    @ApiModelProperty("教练姓名")
    private String name;
    @ApiModelProperty("教练介绍")
    private String introduce;
    @ApiModelProperty("用户端是否显示")
    private Boolean isShow;
    @ApiModelProperty("职位")
    private String job;

}

