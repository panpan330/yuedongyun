package com.yuedongyun.data.model.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TrainingInfo
 * @Author wusongsong
 * @Date 2022/10/10 19:33
 * @Version
 **/
@Data
public class TrainingInfo implements Serializable {
    private String category;
    private String name;
    private Integer newStuNum;
    private Double orderAmount;
}

