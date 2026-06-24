package com.yuedongyun.user.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "分页教练信息")
public class CoachPageVO {
    @ApiModelProperty(value = "教练id，也是用户id", example = "1")
    private Long id;
    @ApiModelProperty(value = "教练名称", example = "罗教练")
    private String name;
    @ApiModelProperty(value = "头像", example = "default-user-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "手机号", example = "13980019001")
    private String cellPhone;
    @ApiModelProperty(value = "岗位", example = "教练")
    private String job;
    @ApiModelProperty(value = "介绍", example = "悦动云体能教练")
    private String intro;
    @ApiModelProperty(value = "负责的训练数量", example = "10")
    private Integer trainingAmount;
    @ApiModelProperty(value = "出题数量", example = "18")
    private Integer examQuestionAmount;
    @ApiModelProperty(value = "注册时间", example = "2022-07-12")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "账户状态，0-禁用，1-正常", example = "1")
    private Integer status;
    @ApiModelProperty(value = "形象照片地址", example = "default-user-icon.jpg")
    private String photo;
}

